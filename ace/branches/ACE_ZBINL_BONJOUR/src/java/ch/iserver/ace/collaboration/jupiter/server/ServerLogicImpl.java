/*
 * $Id$
 *
 * ace - a collaborative editor
 * Copyright (C) 2005 Mark Bigler, Simon Raess, Lukas Zbinden
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package ch.iserver.ace.collaboration.jupiter.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.jupiter.PublisherConnection;
import ch.iserver.ace.collaboration.jupiter.server.serializer.JoinCommand;
import ch.iserver.ace.collaboration.jupiter.server.serializer.LeaveCommand;
import ch.iserver.ace.collaboration.jupiter.server.serializer.Serializer;
import ch.iserver.ace.collaboration.jupiter.server.serializer.SerializerCommand;
import ch.iserver.ace.collaboration.jupiter.server.serializer.ShutdownCommand;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.InterruptedRuntimeException;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.ThreadDomain;
import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
public class ServerLogicImpl implements ServerLogic, DocumentServerLogic, FailureHandler {
	
	private static final Logger LOG = Logger.getLogger(ServerLogicImpl.class);
	
	private int nextParticipantId;
	
	private final Forwarder forwarder;
	
	private final Serializer serializer;
	
	private final BlockingQueue serializerQueue;
	
	private final HashMap ports = new HashMap();
	
	private final HashMap proxies = new HashMap();
	
	private final TreeMap connections = new TreeMap();
	
	private DocumentServer server;
	
	private final ThreadDomain threadDomain;
	
	private final PublisherConnection publisherConnection;
	
	private final PublisherPort publisherPort;
	
	private final ServerDocument document;
	
	private boolean acceptingJoins;
	
	public ServerLogicImpl(Lock lock, 
	                       ThreadDomain domain, 
	                       PublisherConnection connection, 
	                       DocumentModel document) {
		ParameterValidator.notNull("lock", lock);
		ParameterValidator.notNull("domain", domain);
		ParameterValidator.notNull("connection", connection);
		ParameterValidator.notNull("document", document);
		
		this.nextParticipantId = 0;
		this.forwarder = new CompositeForwarder(this);
		
		this.threadDomain = domain;
		
		this.serializerQueue = new LinkedBlockingQueue();
		this.serializer = new Serializer(serializerQueue, lock, forwarder, this);
		
		this.publisherConnection = connection;
		
		ParticipantConnection wrapped = (ParticipantConnection) threadDomain.wrap(
				new ParticipantConnectionWrapper(connection, this), ParticipantConnection.class);
		this.publisherPort = createPublisherPort(wrapped);
		
		this.document = new ServerDocumentImpl();
		this.document.participantJoined(0, null);
		this.document.insertString(0, 0, document.getContent());
		this.document.updateCaret(0, document.getDot(), document.getMark());
		
		this.proxies.put(new Integer(-1), new DocumentUpdateProxy(this.document));
	}
	
	protected PublisherPort createPublisherPort(ParticipantConnection connection) {
		Algorithm algorithm = new Jupiter(false);
		int participantId = 0;
		connection.setParticipantId(participantId);
		PublisherPort port = new PublisherPortImpl(this, participantId, algorithm, getSerializerQueue());
		ParticipantProxy proxy = new ParticipantProxy(participantId, algorithm, connection);
		addParticipant(new SessionParticipant(port, proxy, connection, null));
		return port;
	}
	
	public boolean isAcceptingJoins() {
		return acceptingJoins;
	}
	
	public PortableDocument getDocument() {
		return document.toPortableDocument();
	}
	
	protected DocumentServer getDocumentServer() {
		return server;
	}
	
	public void setDocumentServer(DocumentServer server) {
		ParameterValidator.notNull("server", server);
		this.server = server;
	}
	
	public ThreadDomain getThreadDomain() {
		return threadDomain;
	}
		
	public void start() {
		acceptingJoins = true;
		serializer.start();
	}
	
	public void dispose() throws InterruptedException {
		serializer.kill();
	}
	
	private synchronized int nextParticipantId() {
		return ++nextParticipantId;
	}
	
	protected BlockingQueue getSerializerQueue() {
		return serializerQueue;
	}
	
	protected PublisherConnection getPublisherConnection() {
		return publisherConnection;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#addParticipant(ch.iserver.ace.collaboration.jupiter.server.SessionParticipant)
	 */
	public synchronized void addParticipant(SessionParticipant participant) {
		Integer key = new Integer(participant.getParticipantId());
		ports.put(key, participant.getParticipantPort());
		proxies.put(key, participant.getParticipantProxy());
		connections.put(key, participant.getParticipantConnection());
	}
		
	protected synchronized void removeParticipant(int participantId) {
		Integer key = new Integer(participantId);
		connections.remove(key);
		proxies.remove(key);
		ports.remove(key);
	}
		
	private synchronized ParticipantConnection getParticipantConnection(int id) {
		return (ParticipantConnection) connections.get(new Integer(id));
	}
				
	/**
	 * @see ch.iserver.ace.net.DocumentServerLogic#join(ch.iserver.ace.net.ParticipantConnection)
	 */
	public synchronized ParticipantPort join(ParticipantConnection target) {
		if (!isAcceptingJoins()) {
			// TODO: correct exception type
			throw new IllegalStateException("DocumentServerLogic is not accepting joins");
		}
		
		Algorithm algorithm = new Jupiter(false);
		int participantId = nextParticipantId();
		target.setParticipantId(participantId);
		
		ParticipantPort port = new ParticipantPortImpl(this, participantId, algorithm, getSerializerQueue());
		ParticipantProxy proxy = new ParticipantProxy(participantId, algorithm, target);
		ParticipantConnection connection = (ParticipantConnection) getThreadDomain().wrap(
				new ParticipantConnectionWrapper(target, this), ParticipantConnection.class);
		RemoteUserProxy user = target.getUser();
		SessionParticipant participant = new SessionParticipant(port, proxy, connection, user);
		SerializerCommand cmd = new JoinCommand(participant, this);
		getSerializerQueue().add(cmd);
		
		return port;
	}
	
	// --> session logic methods <--
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#getPublisherPort()
	 */
	public PublisherPort getPublisherPort() {
		return publisherPort;
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#setDocumentDetails(ch.iserver.ace.DocumentDetails)
	 */
	public void setDocumentDetails(DocumentDetails details) {
		getDocumentServer().setDocumentDetails(details);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#prepareShutdown()
	 */
	public synchronized void prepareShutdown() {
		this.acceptingJoins = false;
		getDocumentServer().prepareShutdown();
		getSerializerQueue().add(new ShutdownCommand(this));
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#shutdown()
	 */
	public void shutdown() {
		getDocumentServer().shutdown();
		try {
			dispose();
		} catch (InterruptedException e) {
			throw new InterruptedRuntimeException(e);
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#getParticipantProxies()
	 */
	public synchronized Iterator getParticipantProxies() {
		Map clone = (Map) proxies.clone();
		return clone.values().iterator();
	}

	/**
	 * @see ServerLogic#leave(int)
	 */
	public synchronized void leave(int participantId) {
		ParticipantConnection connection = getParticipantConnection(participantId);
		if (connection == null) {
			LOG.warn("participant with id " + participantId + " not (or no longer) in session");
			return;
		}
		connection.close();
		removeParticipant(participantId);
	}
		
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#kick(int)
	 */
	public void kick(int participantId) {
		if (participantId == PUBLISHER_ID) {
			throw new IllegalArgumentException("cannot kick publisher of session");
		}
		LOG.info("kicking participant " + participantId);
		synchronized (this) {
			ParticipantConnection connection = getParticipantConnection(participantId);
			if (connection == null) {
				LOG.warn("participant with id " + participantId + " not (or no longer) in session");
				return;
			}
			connection.sendKicked();
			connection.close();
			removeParticipant(participantId);
		}
	}
	
	// --> start FailureHandler methods <--
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.FailureHandler#handleFailure(int, int)
	 */
	public void handleFailure(int participantId, int reason) {
		LOG.info("handling failed connection to participant " + participantId);
		synchronized (this) {
			if (participantId == PUBLISHER_ID) {
				LOG.error("failure related to publisher: " + reason);
				getPublisherConnection().sessionFailed(reason, null);
				prepareShutdown();
			} else {
				removeParticipant(participantId);
				getSerializerQueue().add(new LeaveCommand(participantId, Participant.DISCONNECTED));
			}
		}
	}

}
