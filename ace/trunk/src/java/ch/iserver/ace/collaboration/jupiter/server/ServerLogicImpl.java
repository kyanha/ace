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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Fragment;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.collaboration.JoinRequest;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.collaboration.jupiter.AcknowledgeStrategy;
import ch.iserver.ace.collaboration.jupiter.AcknowledgeStrategyFactory;
import ch.iserver.ace.collaboration.jupiter.AlgorithmWrapperImpl;
import ch.iserver.ace.collaboration.jupiter.JoinRequestImpl;
import ch.iserver.ace.collaboration.jupiter.NullAcknowledgeStrategyFactory;
import ch.iserver.ace.collaboration.jupiter.PublisherConnection;
import ch.iserver.ace.collaboration.jupiter.UserRegistry;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.AopUtil;
import ch.iserver.ace.util.LoggingInterceptor;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.ThreadDomain;

/**
 * Default implementation of the ServerLogic interface.
 */
public class ServerLogicImpl implements ServerLogic, FailureHandler, AccessControlStrategy {
	
	/**
	 * The logger used by instances of this class.
	 */
	private static final Logger LOG = Logger.getLogger(ServerLogicImpl.class);
	
	/**
	 * The next participant id to be given to a new user.
	 */
	private int nextParticipantId;
	
	/**
	 * The CompositeForwarder used to forward events to other participants.
	 */
	private final CompositeForwarder forwarder;
	
	/**
	 * The FailureHandler used to handle failures in this class.
	 */
	private final FailureHandler failureHandler;
		
	/**
	 * The mapping from participant id to Forwarder objects.
	 */
	private final HashMap proxies = new HashMap();
	
	/**
	 * The mapping from participant id to ParticipantConnection objects.
	 */
	private final TreeMap connections = new TreeMap();
	
	/**
	 * The DocumentServer object from the network layer.
	 */
	private DocumentServer server;
	
	/**
	 * ThreadDomain used to wrap all incoming operations.
	 */
	private final ThreadDomain incomingDomain;
	
	/**
	 * ThreadDomain used to wrap all outgoing operations.
	 */
	private final ThreadDomain outgoingDomain;
	
	/**
	 * The AccessControlStrategy used by this object.
	 */
	private AccessControlStrategy accessControlStrategy;
	
	/**
	 * The factory used to create AcknowledgeStrategy objects.
	 */
	private AcknowledgeStrategyFactory acknowledgeStrategyFactory = new NullAcknowledgeStrategyFactory();

	/**
	 * The connection to the publisher of the document.
	 */
	private PublisherConnection publisherConnection;
	
	/**
	 * The port used by the publisher of the document.
	 */
	private PublisherPort publisherPort;
	
	/**
	 * The server copy of the current document. This document is sent to
	 * joining users.
	 */
	private final ServerDocument document;
	
	/**
	 * The user registry of the application.
	 */
	private final UserRegistry registry;
	
	/**
	 * Flag indicating whether this object is accepting joins.
	 */
	private boolean acceptingJoins;
	
	/**
	 * The blacklist of this session.
	 */
	private final Set blacklist = new HashSet();
	
	/**
	 * The set of currently joining users.
	 */
	private final Set joinSet = new HashSet();
	
	/**
	 * The set of users that are currently in the session.
	 */
	private final Set participants = new HashSet();
	
	/**
	 * A mapping from user id to participant id.
	 */
	private final Map userParticipantMapping = new HashMap();
			
	/**
	 * Creates a new ServerLogicImpl instance.
	 * 
	 * @param incomingDomain
	 * @param outgoingDomain
	 * @param document
	 * @param registry
	 */
	public ServerLogicImpl(ThreadDomain incomingDomain,
	                       ThreadDomain outgoingDomain, 
	                       DocumentModel document,
	                       UserRegistry registry) {
		ParameterValidator.notNull("document", document);
		ParameterValidator.notNull("registry", registry);
		
		this.nextParticipantId = 0;
		this.forwarder = createForwarder();
		
		this.incomingDomain = incomingDomain;
		this.outgoingDomain = outgoingDomain;
		this.registry = registry;
		this.accessControlStrategy = this;
		
		this.failureHandler = (FailureHandler) incomingDomain.wrap(this, FailureHandler.class);
		
		this.document = createServerDocument(document);
		
		Forwarder forwarderTarget = new DocumentUpdater(this.document);
		LoggingInterceptor interceptor = new LoggingInterceptor(DocumentUpdater.class, Level.DEBUG);
		Forwarder forwarder = (Forwarder) AopUtil.wrap(forwarderTarget, Forwarder.class, interceptor);
		this.forwarder.addForwarder(forwarder);
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
		doc.insertString(ParticipantConnection.PUBLISHER_ID, 0, document.getContent());
		doc.updateCaret(ParticipantConnection.PUBLISHER_ID, document.getDot(), document.getMark());
		return doc;
	}
	
	/**
	 * Creates a new forwarder responsible to forward the results of the
	 * command processor to all other participants.
	 * 
	 * @return the initialized forwarder
	 */
	protected CompositeForwarder createForwarder() {
		return new CompositeForwarderImpl();
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
		PublisherPort port = new PublisherPortImpl(
						this, 
						this, 
						PublisherConnection.PUBLISHER_ID, 
						new AlgorithmWrapperImpl(algorithm), 
						forwarder);
		Forwarder proxy = createForwarder(ParticipantConnection.PUBLISHER_ID, connection, algorithm);
		addParticipant(new SessionParticipant(port, proxy, connection, null));
		return (PublisherPort) incomingDomain.wrap(port, PublisherPort.class);
	}
	
	/**
	 * Creates a new forwarder for a participant.
	 * 
	 * @param participantId the participant id of the new participant
	 * @param connection the connection to the participant
	 * @param algorithm the algorithm to be used for that participant
	 * @return the forwarder for that particular participant
	 */
	protected Forwarder createForwarder(int participantId, ParticipantConnection connection, Algorithm algorithm) {
		AcknowledgeStrategy acknowledger = getAcknowledgeStrategyFactory().createStrategy();
		ParticipantForwarder proxy = new ParticipantForwarder(participantId, algorithm, connection);
		proxy.setAcknowledgeStrategy(acknowledger);
		return proxy;
	}

	/**
	 * Initializes the publisher connection and returns a corresponding publisher port.
	 * 
	 * @param publisherConnection the connection to the publisher
	 * @return the port for the publisher
	 */
	public PublisherPort initPublisherConnection(PublisherConnection publisherConnection) {
		this.publisherConnection = (PublisherConnection) outgoingDomain.wrap(
						new PublisherConnectionWrapper(
								publisherConnection, 
								getFailureHandler()),
						PublisherConnection.class);
		this.publisherPort = createPublisherPort(this.publisherConnection);
		return publisherPort;
	}
		
	/**
	 * @return whether this server logic is accepting joins
	 */
	protected boolean isAcceptingJoins() {
		return acceptingJoins;
	}
	
	/**
	 * @return the current copy of the server-side document
	 */
	protected PortableDocument getDocument() {
		return document.toPortableDocument();
	}
	
	/**
	 * @return the user registry of the application
	 */
	protected UserRegistry getUserRegistry() {
		return registry;
	}
	
	/**
	 * @return the document server of this session
	 */
	protected DocumentServer getDocumentServer() {
		return server;
	}
	
	/**
	 * Sets the document server of this session.
	 * 
	 * @param server the server of this session
	 */
	public void setDocumentServer(DocumentServer server) {
		ParameterValidator.notNull("server", server);
		this.server = server;
	}
			
	/**
	 * @return gets the forwarder used to forward events
	 */
	protected Forwarder getForwarder() {
		return forwarder;
	}
	
	/**
	 * @return gets the failure handler of this server logic
	 */
	protected FailureHandler getFailureHandler() {
		return failureHandler;
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
	}
	
	public void dispose() {
		acceptingJoins = false;
	}
	
	/**
	 * @return the connection object for the publisher of the session
	 */
	protected PublisherConnection getPublisherConnection() {
		return publisherConnection;
	}
	
//	/**
//	 * @return the port object for the publisher of the session
//	 */
//	protected PublisherPort getPublisherPort() {
//		return publisherPort;
//	}
	
	/**
	 * @return the current access control strategy of the session
	 */
	protected AccessControlStrategy getAccessControlStrategy() {
		return accessControlStrategy;
	}
	
	/**
	 * Sets the new access control strategy of the session.
	 * 
	 * @param strategy the new strategy
	 */
	public void setAccessControlStrategy(AccessControlStrategy strategy) {
		this.accessControlStrategy = strategy;
	}
	
	/**
	 * Sets the new AcknowledgeStrategyFactory object.
	 * 
	 * @param factory the factory for AcknowledgeStrategy objects
	 */
	public void setAcknowledgeStrategyFactory(AcknowledgeStrategyFactory factory) {
		this.acknowledgeStrategyFactory = factory;
	}
	
	/**
	 * @return the factory to create acknowledge strategy objects
	 */
	protected AcknowledgeStrategyFactory getAcknowledgeStrategyFactory() {
		return acknowledgeStrategyFactory;
	}
	
	/**
	 * @return the next available participant id
	 */
	protected synchronized int nextParticipantId() {
		return ++nextParticipantId;
	}
	
	/**
	 * Gets a participant id for a user.
	 * 
	 * @param userId the user id
	 * @return a participant id for the user
	 */
	protected int getParticipantId(String userId) {
		Integer id = (Integer) userParticipantMapping.get(userId);
		if (id == null) {
			id = new Integer(nextParticipantId());
			userParticipantMapping.put(userId, id);
		}
		return id.intValue();
	}
			
	/**
	 * Removes the participant specified with the given id from the
	 * session.
	 * 
	 * @param participantId the participant to be removed
	 */
	protected void removeParticipant(int participantId) {
		Integer key = new Integer(participantId);
		ParticipantConnection connection = (ParticipantConnection) connections.remove(key);
		if (connection != null) {
			participants.remove(connection.getUser().getId());
		}
		Forwarder removed = (Forwarder) proxies.remove(key);
		forwarder.removeForwarder(removed);
	}
		
	/**
	 * Gets the participant connection for a participant.
	 * 
	 * @param id the participant id
	 * @return the participant connection for the given participant or null
	 */
	protected ParticipantConnection getParticipantConnection(int id) {
		return (ParticipantConnection) connections.get(new Integer(id));
	}
	
	/**
	 * Adds a new participant to the session.
	 * 
	 * @param participant the participant to be added
	 */
	protected void addParticipant(SessionParticipant participant) {
		Integer key = new Integer(participant.getParticipantId());
		if (participant.getUserProxy() != null) {
			participants.add(participant.getUserProxy().getId());
		}
		proxies.put(key, participant.getForwarder());
		connections.put(key, participant.getParticipantConnection());
		forwarder.addForwarder(participant.getForwarder());
	}

	// --> server logic methods <--

	/**
	 * @see ch.iserver.ace.net.DocumentServerLogic#join(ch.iserver.ace.net.ParticipantConnection)
	 */
	public void join(ParticipantConnection target) {
		LOG.info("--> join");
		if (!isAcceptingJoins()) {
			target.joinRejected(JoinRequest.SHUTDOWN);
			return;
		}
		
		String id = target.getUser().getId();
		
		if (participants.contains(id)) {
			target.joinRejected(JoinRequest.JOINED);
			return;
		}
		
		if (blacklist.contains(id)) {
			target.joinRejected(JoinRequest.BLACKLISTED);
			return;
		}
		
		if (joinSet.contains(id)) {
			target.joinRejected(JoinRequest.IN_PROGRESS);
			return;
		}

		ParticipantConnection connection = (ParticipantConnection) outgoingDomain.wrap(
						new ParticipantConnectionWrapper(
								target, 
								getFailureHandler()), 
						ParticipantConnection.class);
		RemoteUser user = getUserRegistry().getUser(id);
		
		if (user == null) {
			target.joinRejected(JoinRequest.UNKNOWN_USER);
			return;
		}
		
		joinSet.add(id);
		ServerLogic wrapped = (ServerLogic) incomingDomain.wrap(this, ServerLogic.class);
		JoinRequest request = new JoinRequestImpl(wrapped, user, connection);
		getAccessControlStrategy().joinRequest(getPublisherConnection(), request);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#joinAccepted(ch.iserver.ace.net.ParticipantConnection)
	 */
	public void joinAccepted(ParticipantConnection connection) {
		LOG.info("--> join accepted");
		try {
			if (!isAcceptingJoins()) {
				LOG.info("join accepted by publisher but shutdown is in progress");
			} else {
				Algorithm algorithm = new Jupiter(false);
				int participantId = getParticipantId(connection.getUser().getId());
				connection.setParticipantId(participantId);
				
				ParticipantPort portTarget = new ParticipantPortImpl(this, this, participantId, new AlgorithmWrapperImpl(algorithm), forwarder);
				ParticipantPort port = (ParticipantPort) incomingDomain.wrap(portTarget, ParticipantPort.class);
				Forwarder proxy = createForwarder(participantId, connection, algorithm);
				RemoteUserProxy user = connection.getUser();
		
				SessionParticipant participant = new SessionParticipant(portTarget, proxy, connection, user);
				PortableDocument document = getDocument();
				
				System.out.println("server document ...");
				Iterator it = document.getFragments();
				while (it.hasNext()) {
					Fragment fragment = (Fragment) it.next();
					System.out.println(fragment.getParticipantId() + " | " + fragment.getText());
				}
				
				connection.joinAccepted(port);
				connection.sendDocument(document);
				addParticipant(participant);
				forwarder.sendParticipantJoined(participantId, user);
			}
		} finally {
			// remove from list of joining users
			joinSet.remove(connection.getUser().getId());
			LOG.info("<-- join accepted");
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#joinRejected(ch.iserver.ace.net.ParticipantConnection)
	 */
	public void joinRejected(ParticipantConnection connection) {
		LOG.info("--> joinRejected");
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
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#setDocumentDetails(ch.iserver.ace.DocumentDetails)
	 */
	public void setDocumentDetails(DocumentDetails details) {
		LOG.info("--> setDocumentDetails");
		getDocumentServer().setDocumentDetails(details);
	}
	
	/**
	 * @see ServerLogic#leave(int)
	 */
	public void leave(int participantId) {
		LOG.info("--> leave");
		ParticipantConnection connection = getParticipantConnection(participantId);
		if (connection == null) {
			LOG.warn("participant with id " + participantId + " not (or no longer) in session");
			return;
		}
		connection.close();
		removeParticipant(participantId);
		forwarder.sendParticipantLeft(participantId, Participant.LEFT);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#kick(int)
	 */
	public void kick(int participantId) {
		LOG.info("--> kick");
		if (participantId == PUBLISHER_ID) {
			throw new IllegalArgumentException("cannot kick publisher of session");
		}
		LOG.info("kicking participant " + participantId);
		ParticipantConnection connection = getParticipantConnection(participantId);
		if (connection == null) {
			LOG.info("participant with id " + participantId + " not (or no longer) in session");
		} else {
			RemoteUserProxy user = connection.getUser();
			if (user == null) {
				LOG.warn("connection did not return user, cannot add user to blacklist");
			} else {
				blacklist.add(connection.getUser().getId());
			}
			removeParticipant(participantId);
			forwarder.sendParticipantLeft(participantId, Participant.KICKED);
			connection.sendKicked();
			connection.close();
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#shutdown()
	 */
	public void shutdown() {
		LOG.info("--> shutdown");
		if (getDocumentServer() != null) {
			getDocumentServer().prepareShutdown();
		}
		forwarder.close();
		if (getDocumentServer() != null) {
			getDocumentServer().shutdown();
		}
	}
	
	// --> start FailureHandler methods <--
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.FailureHandler#handleFailure(int, int)
	 */
	public void handleFailure(int participantId, int reason) {
		LOG.info("handling failed connection to participant " + participantId);
		if (participantId == PUBLISHER_ID) {
			LOG.error("failure related to publisher: " + reason);
			getPublisherConnection().sessionFailed(reason, null);
			shutdown();
		} else {
			removeParticipant(participantId);
			getForwarder().sendParticipantLeft(participantId, Participant.DISCONNECTED);
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
