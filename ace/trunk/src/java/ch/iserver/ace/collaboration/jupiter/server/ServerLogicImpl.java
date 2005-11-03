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

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.ParameterValidator;
import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
public class ServerLogicImpl implements ServerLogic, DocumentServerLogic {
	
	private int nextParticipantId;
	
	private final Forwarder forwarder;
	
	private final Serializer serializer;
	
	private final BlockingQueue serializerQueue;
	
	private final HashMap ports = new HashMap();
	
	private final HashMap proxies = new HashMap();
	
	private final TreeMap connections = new TreeMap();
	
	private final BlockingQueue dispatcherQueue;
	
	private final Dispatcher dispatcher;
	
	private DocumentServer server;
	
	private final Lock lock;
	
	private final ParticipantPort publisherPort;
	
	private final ServerDocument document;
	
	public ServerLogicImpl(Lock lock, ParticipantConnection connection, DocumentModel document) {
		ParameterValidator.notNull("lock", lock);
		ParameterValidator.notNull("connection", connection);
		ParameterValidator.notNull("document", document);
		
		this.nextParticipantId = 0;
		this.forwarder = new ForwarderImpl(this);
		
		this.lock = lock;
		
		this.serializerQueue = new LinkedBlockingQueue();
		this.serializer = new Serializer(serializerQueue, lock, forwarder);
		
		this.dispatcherQueue = new LinkedBlockingQueue();
		this.dispatcher = new Dispatcher(dispatcherQueue);
		
		this.publisherPort = createPublisherPort(connection);
		
		this.document = new ServerDocumentImpl();
		this.document.participantJoined(0, null);
		this.document.insertString(0, 0, document.getContent());
		this.document.updateCaret(0, document.getDot(), document.getMark());
		
		this.proxies.put(new Integer(-1), new DocumentUpdateProxy(this.document));
	}
	
	protected ParticipantPort createPublisherPort(ParticipantConnection connection) {
		Algorithm algorithm = new Jupiter(false);
		int participantId = 0;
		connection.setParticipantId(participantId);
		ParticipantPort port = new ParticipantPortImpl(this, participantId, algorithm, getSerializerQueue());
		ParticipantProxy proxy = new ParticipantProxyImpl(participantId, dispatcherQueue, algorithm, connection);
		addParticipant(port, proxy, connection);
		return port;
	}
	
	protected ServerDocument getDocument() {
		return document;
	}
	
	protected DocumentServer getDocumentServer() {
		return server;
	}
	
	public void setDocumentServer(DocumentServer server) {
		ParameterValidator.notNull("server", server);
		this.server = server;
	}
	
	public void start() {
		serializer.start();
		dispatcher.start();
	}
	
	public void dispose() throws InterruptedException {
		serializer.kill();
		dispatcher.kill();
	}
	
	private synchronized int nextParticipantId() {
		return ++nextParticipantId;
	}
	
	protected BlockingQueue getSerializerQueue() {
		return serializerQueue;
	}
	
	protected synchronized void addParticipant(ParticipantPort port, ParticipantProxy proxy, ParticipantConnection connection) {
		Integer key = new Integer(port.getParticipantId());
		ports.put(key, port);
		proxies.put(key, proxy);
		connections.put(key, connection);
	}
	
	protected synchronized void removeParticipant(int participantId) {
		Integer key = new Integer(participantId);
		connections.remove(key);
		proxies.remove(key);
		ports.remove(key);
	}
		
	protected synchronized Map getParticipantConnections() {
		return (Map) connections.clone();
	}
	
	private ParticipantConnection getParticipantConnection(int id) {
		return (ParticipantConnection) connections.get(new Integer(id));
	}
	
	public ParticipantPort getPublisherPort() {
		return publisherPort;
	}
		
	protected PortableDocument retrieveDocument() {
		lock.lock();
		try {
			return getDocument().toPortableDocument();
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * @see ch.iserver.ace.net.DocumentServerLogic#join(ch.iserver.ace.net.ParticipantConnection)
	 */
	public ParticipantPort join(ParticipantConnection connection) {
		Algorithm algorithm = new Jupiter(false);
		int participantId = nextParticipantId();
		connection.setParticipantId(participantId);
		connection.sendDocument(retrieveDocument());
		ParticipantPort port = new ParticipantPortImpl(this, participantId, algorithm, getSerializerQueue());
		ParticipantProxy proxy = new ParticipantProxyImpl(participantId, dispatcherQueue, algorithm, connection);
		notifyOthersAboutJoin(participantId, connection.getUser());
		addParticipant(port, proxy, connection);
		return port;
	}
	
	protected void notifyOthersAboutJoin(int participantId, RemoteUserProxy proxy) {
		getDocument().participantJoined(participantId, proxy);
		Map map = getParticipantConnections();
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			Integer key = (Integer) it.next();
			ParticipantConnection connection = (ParticipantConnection) map.get(key);
			if (key.intValue() != participantId) {
				connection.sendParticipantJoined(participantId, proxy);
			}
		}
	}

	protected void notifyOthersAboutLeave(int participantId, int reason) {
		getDocument().participantLeft(participantId);
		Map map = getParticipantConnections();
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			Integer key = (Integer) it.next();
			ParticipantConnection connection = (ParticipantConnection) map.get(key);
			if (key.intValue() != participantId) {
				connection.sendParticipantLeft(participantId, reason);
			}
		}
	}

	/**
	 * @see ServerLogic#leave(int)
	 */
	public void leave(int participantId) {
		ParticipantConnection connection = getParticipantConnection(participantId);
		connection.close();
		removeParticipant(participantId);
		notifyOthersAboutLeave(participantId, Participant.LEFT);
	}
		
	// --> session logic methods <--
	
	public void setDocumentDetails(DocumentDetails details) {
		getDocumentServer().setDocumentDetails(details);
	}
	
	public void shutdown() {
		getDocumentServer().conceal();
	}
	
	public synchronized Iterator getParticipantProxies() {
		Map clone = (Map) proxies.clone();
		return clone.values().iterator();
	}
	
	public void kick(Participant participant) {
		int participantId = participant.getParticipantId();
		ParticipantConnection connection = getParticipantConnection(participantId);
		connection.sendKicked();
		connection.close();
		removeParticipant(participantId);
		notifyOthersAboutLeave(participantId, Participant.KICKED);
	}

}
