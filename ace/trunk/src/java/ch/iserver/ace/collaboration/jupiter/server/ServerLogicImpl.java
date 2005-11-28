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

import org.apache.log4j.Level;
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
import ch.iserver.ace.collaboration.jupiter.AlgorithmWrapperImpl;
import ch.iserver.ace.collaboration.jupiter.JoinRequestImpl;
import ch.iserver.ace.collaboration.jupiter.NullAcknowledgeStrategyFactory;
import ch.iserver.ace.collaboration.jupiter.PublisherConnection;
import ch.iserver.ace.collaboration.jupiter.RemoteUserImpl;
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
	 * The CompositeForwarder used to forward events to other participants.
	 */
	private final CompositeForwarder compositeForwarder;
	
	/**
	 * The FailureHandler used to handle failures in this class.
	 */
	private final FailureHandler failureHandler;
		
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
	 * The ParticipantManager of the session.
	 */
	private final ParticipantManager participants;
			
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
		
		this.incomingDomain = incomingDomain;
		this.outgoingDomain = outgoingDomain;
		this.registry = registry;
		this.accessControlStrategy = this;

		this.compositeForwarder = createForwarder();
		this.participants = createParticipantManager(compositeForwarder);
		this.document = createServerDocument(document);
				
		this.failureHandler = (FailureHandler) incomingDomain.wrap(this, FailureHandler.class);
				
		Forwarder forwarderTarget = new DocumentUpdater(this.document);
		LoggingInterceptor interceptor = new LoggingInterceptor(DocumentUpdater.class, Level.DEBUG);
		Forwarder forwarder = (Forwarder) AopUtil.wrap(forwarderTarget, Forwarder.class, interceptor);
		this.compositeForwarder.addForwarder(forwarder);
	}

	/**
	 * Creates a new ParticipantManager for this session.
	 * 
	 * @return the newly created participant manager
	 */
	protected ParticipantManagerImpl createParticipantManager(CompositeForwarder forwarder) {
		return new ParticipantManagerImpl(forwarder);
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
		int participantId = ParticipantConnection.PUBLISHER_ID;
		PublisherPort port = new PublisherPortImpl(
						this, 
						this, 
						participantId, 
						new AlgorithmWrapperImpl(algorithm), 
						compositeForwarder);
		Forwarder forwarder = createForwarder(participantId, connection, algorithm);
		participants.addParticipant(participantId, forwarder, connection);
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
		return createPublisherPort(this.publisherConnection);
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
	 * @return
	 */
	protected ParticipantManager getParticipantManager() {
		return participants;
	}
	
	/**
	 * @return gets the forwarder used to forward events
	 */
	protected Forwarder getCompositeForwarder() {
		return compositeForwarder;
	}
	
	/**
	 * @return gets the failure handler of this server logic
	 */
	protected FailureHandler getFailureHandler() {
		return failureHandler;
	}
		
	/**
	 * Starts the server logic. Unless this method is called, no joins are
	 * accepted.
	 */
	public void start() {
		acceptingJoins = true;
	}
	
	/**
	 * @return the connection object for the publisher of the session
	 */
	protected PublisherConnection getPublisherConnection() {
		return publisherConnection;
	}
	
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
	 * Removes the participant specified with the given id from the
	 * session.
	 * 
	 * @param participantId the participant to be removed
	 */
	protected void removeParticipant(int participantId) {
		participants.removeParticipant(participantId);
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
		
		if (participants.isInvited(id)) {
			acceptJoin(target);
		} else if (participants.isParticipant(id)) {
			target.joinRejected(JoinRequest.JOINED);
		} else if (participants.isBlackListed(id)) {
			target.joinRejected(JoinRequest.BLACKLISTED);
		} else if (participants.isJoining(id)) {
			target.joinRejected(JoinRequest.IN_PROGRESS);
		} else {
			ParticipantConnection connection = (ParticipantConnection) outgoingDomain.wrap(
						new ParticipantConnectionWrapper(
								target, 
								getFailureHandler()), 
						ParticipantConnection.class);
			RemoteUser user = getUserRegistry().getUser(id);
		
			if (user == null) {
				target.joinRejected(JoinRequest.UNKNOWN_USER);
			} else {
				ServerLogic wrapped = (ServerLogic) incomingDomain.wrap(this, ServerLogic.class);
				JoinRequest request = new JoinRequestImpl(wrapped, user, connection);
				getAccessControlStrategy().joinRequest(getPublisherConnection(), request);
			}
		}
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
				acceptJoin(connection);
			}
		} finally {
			participants.joinRequestAccepted(connection.getUser().getId());
			LOG.info("<-- join accepted");
		}
	}
	
	protected void acceptJoin(ParticipantConnection connection) {
		Algorithm algorithm = new Jupiter(false);
		int participantId = participants.getParticipantId(connection.getUser().getId());
		connection.setParticipantId(participantId);
		
		ParticipantPort portTarget = new ParticipantPortImpl(this, this, participantId, new AlgorithmWrapperImpl(algorithm), compositeForwarder);
		ParticipantPort port = (ParticipantPort) incomingDomain.wrap(portTarget, ParticipantPort.class);
		Forwarder forwarder = createForwarder(participantId, connection, algorithm);
		RemoteUserProxy user = connection.getUser();

		PortableDocument document = getDocument();
					
		connection.joinAccepted(port);
		connection.sendDocument(document);
		participants.addParticipant(participantId, forwarder, connection);
		compositeForwarder.sendParticipantJoined(participantId, user);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#joinRejected(ch.iserver.ace.net.ParticipantConnection)
	 */
	public void joinRejected(ParticipantConnection connection) {
		LOG.info("--> joinRejected");
		try {
			connection.joinRejected(JoinRequest.REJECTED);
		} finally {
			participants.joinRequestRejected(connection.getUser().getId());
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
		ParticipantConnection connection = participants.getConnection(participantId);
		if (connection == null) {
			LOG.warn("participant with id " + participantId + " not (or no longer) in session");
			return;
		}
		connection.close();
		removeParticipant(participantId);
		compositeForwarder.sendParticipantLeft(participantId, Participant.LEFT);
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
		ParticipantConnection connection = participants.getConnection(participantId);
		if (connection == null) {
			LOG.info("participant with id " + participantId + " not (or no longer) in session");
		} else {
			RemoteUserProxy user = connection.getUser();
			if (user == null) {
				LOG.warn("connection did not return user, cannot add user to blacklist");
			} else {
				participants.participantKicked(participantId);
			}
			removeParticipant(participantId);
			compositeForwarder.sendParticipantLeft(participantId, Participant.KICKED);
			connection.sendKicked();
			connection.close();
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#invite(ch.iserver.ace.collaboration.RemoteUser)
	 */
	public void invite(RemoteUser user) {
		ParameterValidator.notNull("user", user);
		participants.userInvited(user.getId());
		getDocumentServer().invite(((RemoteUserImpl) user).getProxy());
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#shutdown()
	 */
	public void shutdown() {
		LOG.info("--> shutdown");
		acceptingJoins = false;
		if (getDocumentServer() != null) {
			getDocumentServer().prepareShutdown();
		}
		compositeForwarder.close();
		if (getDocumentServer() != null) {
			getDocumentServer().shutdown();
		}
		LOG.info("<-- shutdown");
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
			getCompositeForwarder().sendParticipantLeft(participantId, Participant.DISCONNECTED);
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
