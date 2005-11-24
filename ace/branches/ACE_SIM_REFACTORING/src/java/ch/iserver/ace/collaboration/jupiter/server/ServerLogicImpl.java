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
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.ThreadDomain;

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
	
	private final Map userParticipantMapping = new HashMap();
	
	private AcknowledgeStrategyFactory acknowledgeStrategyFactory = new NullAcknowledgeStrategyFactory();
		
	public ServerLogicImpl(ThreadDomain domain, 
	                       DocumentModel document,
	                       UserRegistry registry) {
		ParameterValidator.notNull("domain", domain);
		ParameterValidator.notNull("document", document);
		ParameterValidator.notNull("registry", registry);
		
		this.nextParticipantId = 0;
		this.forwarder = createForwarder(this);
		
		this.threadDomain = domain;
		this.registry = registry;
		this.accessControlStrategy = this;
		
		this.commandProcessor = createCommandProcessor(forwarder, this);			
		this.document = createServerDocument(document);
		this.proxies.put(new Integer(-1), new DocumentUpdater(this.document));
	}

	/**
	 * Creates the command processor used to process incoming serializer
	 * commands. 
	 * 
	 * @param forwarder the forwarder receiving the results of the command
	 * @param handler the failure handler used to handle failures
	 * @return the newly created command processor
	 */
	protected CommandProcessor createCommandProcessor(Forwarder forwarder, FailureHandler handler) {
		return new CommandProcessorImpl(forwarder, handler);
	}
	
	/**
	 * Sets the command processor used by the server logic. This is used only
	 * for testing purposes. Do not use it especially not after calling
	 * start on this object.
	 * 
	 * @param commandProcessor the command processor used to process commands
	 */
	public void setCommandProcessor(CommandProcessor commandProcessor) {
		this.commandProcessor = commandProcessor;
	}

	/**
	 * Creates a new server document used to keep track of the document's
	 * content on the server side.
	 * 
	 * @param document the initial document content
	 * @return the server document ready to be used
	 */
	protected ServerDocument createServerDocument(DocumentModel document) {
		ServerDocument doc = new ServerDocumentImpl();
		doc.insertString(0, 0, document.getContent());
		doc.updateCaret(0, document.getDot(), document.getMark());
		return doc;
	}
	
	/**
	 * Creates a new forwarder responsible to forward the results of the
	 * command processor to all other participants.
	 * 
	 * @param logic the server logic used to retrieve the forwarders for
	 *              each participant
	 * @return the initialized forwarder
	 */
	protected Forwarder createForwarder(ServerLogic logic) {
		return new CompositeForwarder(logic);
	}
	
	/**
	 * Creates the publisher port for the publisher of the session.
	 * 
	 * @param connection the connection to the publisher
	 * @return the publisher port used by the publisher to communicate with
	 *         the session
	 */
	protected PublisherPort createPublisherPort(ParticipantConnection connection) {
		Algorithm algorithm = new Jupiter(false);
		PublisherPort port = new PublisherPortImpl(this, 0, algorithm);
		Forwarder proxy = createParticipantProxy(0, connection, algorithm);
		addParticipant(new SessionParticipant(port, proxy, connection, null));
		return port;
	}
	
	/**
	 * Creates a new forwarder for a participant.
	 * 
	 * @param participantId the participant id of the new participant
	 * @param connection the connection to the participant
	 * @param algorithm the algorithm to be used for that participant
	 * @return the forwarder for that particular participant
	 */
	protected Forwarder createParticipantProxy(int participantId, ParticipantConnection connection, Algorithm algorithm) {
		AcknowledgeStrategy acknowledger = getAcknowledgeStrategyFactory().createStrategy();
		ParticipantProxy proxy = new ParticipantProxy(participantId, algorithm, connection);
		proxy.setAcknowledgeStrategy(acknowledger);
		return proxy;
	}

	public void setPublisherConnection(PublisherConnection publisherConnection) {
		this.publisherConnection = publisherConnection;
		ParticipantConnection wrapped = (ParticipantConnection) threadDomain.wrap(
				new ParticipantConnectionWrapper(publisherConnection, this), ParticipantConnection.class);
		this.publisherPort = createPublisherPort(wrapped);
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
		
	public Forwarder getForwarder() {
		return forwarder;
	}
	
	/**
	 * Gets the blacklist of the session. The black list is a collection of
	 * user ids (element type is String), which are no longer allowed to join
	 * the session.
	 * 
	 * @return the black list of the session
	 */
	protected Set getBlacklist() {
		return blacklist;
	}
	
	/**
	 * Gets the set of user ids who have tried to join the session but have
	 * not been accepted or rejected. This list is used to reject join
	 * requests for users that have already issued an unanswered join request.
	 * 
	 * @return the join set of the session
	 */
	protected Set getJoinSet() {
		return joinSet;
	}
		
	public void start() {
		acceptingJoins = true;
		commandProcessor.startProcessor();
	}
	
	public void dispose() {
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
	
	protected synchronized int getParticipantId(String userId) {
		Integer id = (Integer) userParticipantMapping.get(userId);
		if (id == null) {
			id = new Integer(nextParticipantId());
			userParticipantMapping.put(userId, id);
		}
		return id.intValue();
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
		
		joinSet.add(id);
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
				int participantId = getParticipantId(connection.getUser().getId());
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
		dispose();
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
