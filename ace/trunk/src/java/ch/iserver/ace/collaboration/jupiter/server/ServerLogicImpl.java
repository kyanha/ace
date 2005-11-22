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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.collaboration.JoinRequest;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.collaboration.jupiter.AcknowledgeStrategy;
import ch.iserver.ace.collaboration.jupiter.AcknowledgeStrategyFactory;
import ch.iserver.ace.collaboration.jupiter.JoinRequestImpl;
import ch.iserver.ace.collaboration.jupiter.NullAcknowledgeStrategyFactory;
import ch.iserver.ace.collaboration.jupiter.PublisherConnection;
import ch.iserver.ace.collaboration.jupiter.UserRegistry;
import ch.iserver.ace.collaboration.jupiter.server.serializer.CommandProcessor;
import ch.iserver.ace.collaboration.jupiter.server.serializer.CommandProcessorImpl;
import ch.iserver.ace.collaboration.jupiter.server.serializer.JoinCommand;
import ch.iserver.ace.collaboration.jupiter.server.serializer.LeaveCommand;
import ch.iserver.ace.collaboration.jupiter.server.serializer.SerializerCommand;
import ch.iserver.ace.collaboration.jupiter.server.serializer.ShutdownCommand;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.InterruptedRuntimeException;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.SemaphoreLock;
import ch.iserver.ace.util.ThreadDomain;
import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

/**
 * Default implementation of the ServerLogic interface.
 */
public class ServerLogicImpl implements ServerLogic, FailureHandler, AccessControlStrategy {
	
	private static final Logger LOG = Logger.getLogger(ServerLogicImpl.class);
	
	private int nextParticipantId;
	
	private final Forwarder forwarder;

	private CommandProcessor commandProcessor;
		
	private final HashMap ports = new HashMap();
	
	private final HashMap proxies = new HashMap();
	
	private final TreeMap connections = new TreeMap();
	
	private DocumentServer server;
	
	private final ThreadDomain threadDomain;
	
	private AccessControlStrategy accessControlStrategy;
	
	private PublisherConnection publisherConnection;
	
	private PublisherPort publisherPort;
	
	private final ServerDocument document;
	
	private final UserRegistry registry;
	
	private boolean acceptingJoins;
	
	private final Set blacklist = new HashSet();
	
	private final Set joinSet = new HashSet();
	
	private AcknowledgeStrategyFactory acknowledgeStrategyFactory = new NullAcknowledgeStrategyFactory();
	
	public ServerLogicImpl(ThreadDomain domain,
					     DocumentModel document,
					     UserRegistry registry) {
		this(new SemaphoreLock("serializer"), domain, document, registry);
	}
	
	public ServerLogicImpl(Lock lock, 
	                       ThreadDomain domain, 
	                       DocumentModel document,
	                       UserRegistry registry) {
		ParameterValidator.notNull("lock", lock);
		ParameterValidator.notNull("domain", domain);
		ParameterValidator.notNull("document", document);
		ParameterValidator.notNull("registry", registry);
		
		this.nextParticipantId = 0;
		this.forwarder = new CompositeForwarder(this);
		
		this.threadDomain = domain;
		this.registry = registry;
		this.accessControlStrategy = this;
		
		this.commandProcessor = new CommandProcessorImpl(forwarder, this);
						
		this.document = new ServerDocumentImpl();
		this.document.participantJoined(0, null);
		this.document.insertString(0, 0, document.getContent());
		this.document.updateCaret(0, document.getDot(), document.getMark());
		
		this.proxies.put(new Integer(-1), new DocumentUpdater(this.document));
	}
	
	public void setPublisherConnection(PublisherConnection publisherConnection) {
		this.publisherConnection = publisherConnection;
		ParticipantConnection wrapped = (ParticipantConnection) threadDomain.wrap(
				new ParticipantConnectionWrapper(publisherConnection, this), ParticipantConnection.class);
		this.publisherPort = createPublisherPort(wrapped);
	}
		
	protected BlockingQueue createSerializerQueue() {
		return new LinkedBlockingQueue();
	}
	
	protected PublisherPort createPublisherPort(ParticipantConnection connection) {
		Algorithm algorithm = new Jupiter(false);
		PublisherPort port = new PublisherPortImpl(this, 0, algorithm);
		Forwarder proxy = createParticipantProxy(0, connection, algorithm);
		addParticipant(new SessionParticipant(port, proxy, connection, null));
		return port;
	}
	
	public boolean isAcceptingJoins() {
		return acceptingJoins;
	}
	
	public PortableDocument getDocument() {
		return document.toPortableDocument();
	}
	
	protected UserRegistry getUserRegistry() {
		return registry;
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
	
	public void setCommandProcessor(CommandProcessor commandProcessor) {
		this.commandProcessor = commandProcessor;
	}
	
	public CommandProcessor getCommandProcessor() {
		return commandProcessor;
	}
	
	public Forwarder getForwarder() {
		return forwarder;
	}
	
	protected Set getBlacklist() {
		return blacklist;
	}
	
	protected Set getJoinSet() {
		return joinSet;
	}
		
	public void start() {
		acceptingJoins = true;
		commandProcessor.startProcessor();
	}
	
	public void dispose() throws InterruptedException {
		commandProcessor.stopProcessor();
	}
	
	protected PublisherConnection getPublisherConnection() {
		return publisherConnection;
	}
	
	protected AccessControlStrategy getAccessControlStrategy() {
		return accessControlStrategy;
	}
	
	public void setAccessControlStrategy(AccessControlStrategy strategy) {
		this.accessControlStrategy = strategy;
	}
	
	public void setAcknowledgeStrategyFactory(AcknowledgeStrategyFactory factory) {
		this.acknowledgeStrategyFactory = factory;
	}
	
	public AcknowledgeStrategyFactory getAcknowledgeStrategyFactory() {
		return acknowledgeStrategyFactory;
	}
	
	protected synchronized int nextParticipantId() {
		return ++nextParticipantId;
	}
			
	protected synchronized void removeParticipant(int participantId) {
		Integer key = new Integer(participantId);
		connections.remove(key);
		proxies.remove(key);
		ports.remove(key);
	}
		
	protected synchronized ParticipantConnection getParticipantConnection(int id) {
		return (ParticipantConnection) connections.get(new Integer(id));
	}
	
	// --> server logic methods <--

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#addCommand(ch.iserver.ace.collaboration.jupiter.server.serializer.SerializerCommand)
	 */
	public void addCommand(SerializerCommand command) {
		commandProcessor.process(command);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#addParticipant(ch.iserver.ace.collaboration.jupiter.server.SessionParticipant)
	 */
	public synchronized void addParticipant(SessionParticipant participant) {
		Integer key = new Integer(participant.getParticipantId());
		ports.put(key, participant.getParticipantPort());
		proxies.put(key, participant.getForwarder());
		connections.put(key, participant.getParticipantConnection());
	}

	/**
	 * @see ch.iserver.ace.net.DocumentServerLogic#join(ch.iserver.ace.net.ParticipantConnection)
	 */
	public synchronized void join(ParticipantConnection target) {
		if (!isAcceptingJoins()) {
			target.joinRejected(JoinRequest.SHUTDOWN);
			return;
		}
		
		String id = target.getUser().getId();
		if (blacklist.contains(id)) {
			target.joinRejected(JoinRequest.BLACKLISTED);
			return;
		}
		
		if (joinSet.contains(id)) {
			target.joinRejected(JoinRequest.IN_PROGRESS);
			return;
		}

		ParticipantConnection connection = (ParticipantConnection) getThreadDomain().wrap(
						new ParticipantConnectionWrapper(target, this), ParticipantConnection.class);
		RemoteUser user = getUserRegistry().getUser(id);
		
		if (user == null) {
			target.joinRejected(JoinRequest.UNKNOWN_USER);
			return;
		}
		
		JoinRequest request = new JoinRequestImpl(this, user, connection);
		getAccessControlStrategy().joinRequest(getPublisherConnection(), request);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#joinAccepted(ch.iserver.ace.net.ParticipantConnection)
	 */
	public synchronized void joinAccepted(ParticipantConnection connection) {		
		try {
			if (!isAcceptingJoins()) {
				LOG.info("join accepted by publisher but shutdown is in progress");
			} else {
				Algorithm algorithm = new Jupiter(false);
				int participantId = nextParticipantId();
				connection.setParticipantId(participantId);
		
				ParticipantPort port = new ParticipantPortImpl(this, participantId, algorithm);
				Forwarder proxy = createParticipantProxy(participantId, connection, algorithm);
				RemoteUserProxy user = connection.getUser();
		
				SessionParticipant participant = new SessionParticipant(port, proxy, connection, user);
				SerializerCommand cmd = new JoinCommand(participant, this);
				addCommand(cmd);
			}
		} finally {
			// remove from list of joining users
			joinSet.remove(connection.getUser().getId());
		}
	}

	protected Forwarder createParticipantProxy(int participantId, ParticipantConnection connection, Algorithm algorithm) {
		AcknowledgeStrategy acknowledger = getAcknowledgeStrategyFactory().createStrategy();
		ParticipantProxy proxy = new ParticipantProxy(participantId, algorithm, connection);
		proxy.setAcknowledgeStrategy(acknowledger);
		return proxy;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#joinRejected(ch.iserver.ace.net.ParticipantConnection)
	 */
	public synchronized void joinRejected(ParticipantConnection connection) {
		try {
			if (!isAcceptingJoins()) {
				LOG.info("join rejected by publisher but shutdown is in progress");
			} else {
				connection.joinRejected(JoinRequest.REJECTED);
			}
		} finally {
			// remove from list of joining users
			joinSet.remove(connection.getUser().getId());
		}
	}
		
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
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#getForwarders()
	 */
	public synchronized Iterator getForwarders() {
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
				LOG.info("participant with id " + participantId + " not (or no longer) in session");
			} else {
				blacklist.add(connection.getUser().getId());
				removeParticipant(participantId);
				connection.sendKicked();
				connection.close();
			}
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#prepareShutdown()
	 */
	public synchronized void prepareShutdown() {
		this.acceptingJoins = false;
		if (getDocumentServer() != null) {
			getDocumentServer().prepareShutdown();
		}
		addCommand(new ShutdownCommand(this));
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
				addCommand(new LeaveCommand(participantId, Participant.DISCONNECTED));
			}
		}
	}
	
	// --> AccessControlStrategy implementation <--
	
	/**
	 * Default implementation of the AccessControlStrategy interface.
	 * 
	 * @param connection the publisher connection
	 * @param request the join request
	 */
	public void joinRequest(PublisherConnection connection, JoinRequest request) {
		connection.sendJoinRequest(request);
	}

}
