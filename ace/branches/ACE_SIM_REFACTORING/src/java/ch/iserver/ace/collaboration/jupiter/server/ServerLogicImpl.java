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
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.ThreadDomain;

/**
 * Default implementation of the ServerLogic interface.
 */
public class ServerLogicImpl implements ServerLogic, FailureHandler, AccessControlStrategy {
	
	private static final Logger LOG = Logger.getLogger(ServerLogicImpl.class);
	
	private int nextParticipantId;
	
	private final CompositeForwarder forwarder;
		
	private final HashMap proxies = new HashMap();
	
	private final TreeMap connections = new TreeMap();
	
	private DocumentServer server;
	
	private final ThreadDomain incomingDomain;
	
	private final ThreadDomain outgoingDomain;
	
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
		
		this.document = createServerDocument(document);
		this.proxies.put(new Integer(-1), new DocumentUpdater(this.document));
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
		PublisherPort port = new PublisherPortImpl(this, 0, new AlgorithmWrapperImpl(algorithm), forwarder);
		Forwarder proxy = createParticipantProxy(0, connection, algorithm);
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
	protected Forwarder createParticipantProxy(int participantId, ParticipantConnection connection, Algorithm algorithm) {
		AcknowledgeStrategy acknowledger = getAcknowledgeStrategyFactory().createStrategy();
		ParticipantProxy proxy = new ParticipantProxy(participantId, algorithm, connection);
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
						new PublisherConnectionWrapper(publisherConnection, this), PublisherConnection.class);;
		this.publisherPort = createPublisherPort(this.publisherConnection);
		return publisherPort;
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
	}
	
	public void dispose() {
		// ignore
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
		Forwarder removed = (Forwarder) proxies.remove(key);
		forwarder.removeForwarder(removed);
	}
		
	protected synchronized ParticipantConnection getParticipantConnection(int id) {
		return (ParticipantConnection) connections.get(new Integer(id));
	}
	
	
	public synchronized void addParticipant(SessionParticipant participant) {
		Integer key = new Integer(participant.getParticipantId());
		proxies.put(key, participant.getForwarder());
		connections.put(key, participant.getParticipantConnection());
		forwarder.addForwarder(participant.getForwarder());
	}

	// --> server logic methods <--

	/**
	 * @see ch.iserver.ace.net.DocumentServerLogic#join(ch.iserver.ace.net.ParticipantConnection)
	 */
	public synchronized void join(ParticipantConnection target) {
		LOG.info("--> join");
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

		ParticipantConnection connection = (ParticipantConnection) outgoingDomain.wrap(
						new ParticipantConnectionWrapper(target, this), ParticipantConnection.class);
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
	public synchronized void joinAccepted(ParticipantConnection connection) {
		LOG.info("--> join accepted");
		try {
			if (!isAcceptingJoins()) {
				LOG.info("join accepted by publisher but shutdown is in progress");
			} else {
				Algorithm algorithm = new Jupiter(false);
				int participantId = getParticipantId(connection.getUser().getId());
				connection.setParticipantId(participantId);
				
				ParticipantPort portTarget = new ParticipantPortImpl(this, participantId, new AlgorithmWrapperImpl(algorithm), forwarder);
				ParticipantPort port = (ParticipantPort) incomingDomain.wrap(portTarget, ParticipantPort.class);
				Forwarder proxy = createParticipantProxy(participantId, connection, algorithm);
				RemoteUserProxy user = connection.getUser();
		
				SessionParticipant participant = new SessionParticipant(portTarget, proxy, connection, user);
				PortableDocument document = getDocument();
				connection.joinAccepted(port);
				connection.sendDocument(document);
				addParticipant(participant);
				forwarder.sendParticipantJoined(participantId, user);
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
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#getPublisherPort()
	 */
	public PublisherPort getPublisherPort() {
		LOG.info("--> getPublisherPort");
		return publisherPort;
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#setDocumentDetails(ch.iserver.ace.DocumentDetails)
	 */
	public void setDocumentDetails(DocumentDetails details) {
		LOG.info("--> setDocumentDetails");
		getDocumentServer().setDocumentDetails(details);
	}
		
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ServerLogic#getForwarders()
	 */
	public synchronized Iterator getForwarders() {
		LOG.info("--> getForwarders");
		Map clone = (Map) proxies.clone();
		return clone.values().iterator();
	}

	/**
	 * @see ServerLogic#leave(int)
	 */
	public synchronized void leave(int participantId) {
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
	public synchronized void shutdown() {
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
		synchronized (this) {
			if (participantId == PUBLISHER_ID) {
				LOG.error("failure related to publisher: " + reason);
				getPublisherConnection().sessionFailed(reason, null);
				shutdown();
			} else {
				removeParticipant(participantId);
				forwarder.sendParticipantLeft(participantId, Participant.DISCONNECTED);
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
