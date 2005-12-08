/*
 * $Id:RemoteUserSession.java 1095 2005-11-09 13:56:51Z zbinl $
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

package ch.iserver.ace.net.impl.protocol;

import java.net.ConnectException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.OutputDataStream;
import org.beepcore.beep.core.ProfileRegistry;
import org.beepcore.beep.core.RequestHandler;
import org.beepcore.beep.lib.Reply;
import org.beepcore.beep.transport.tcp.TCPSession;
import org.beepcore.beep.transport.tcp.TCPSessionCreator;

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.algorithm.TimestampFactory;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.SessionConnectionCallback;
import ch.iserver.ace.net.impl.NetworkProperties;
import ch.iserver.ace.net.impl.NetworkServiceExt;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.RemoteDocumentProxyExt;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.discovery.DiscoveryManagerFactory;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.SingleThreadDomain;

/**
 *
 */
public class RemoteUserSession {
	
	public static final String CHANNEL_MAIN = "main";
	public static final String CHANNEL_SESSION = "session";
	//TODO: could open a channel to a host which acts as a proxy to another host inside that subnet
	public static final String CHANNEL_PROXY = "proxy";

	private static Logger LOG = Logger.getLogger(RemoteUserSession.class);
	
	private static SingleThreadDomain domain = new SingleThreadDomain();
	
	private InetAddress host;
	private int port;
	private TCPSession session;
	private MainConnection mainConnection;
	private Map participantConnections, sessionConnections;
	private RemoteUserProxyExt user;
	private boolean isInitiated;
	private boolean isAlive;
	private TimestampFactory factory;
	
	
	/**
	 * 
	 * @param address
	 * @param port
	 * @param user
	 * @param factory used for the CollaborationParserHandler
	 */
	public RemoteUserSession(InetAddress address, int port, RemoteUserProxyExt user) {
		ParameterValidator.notNull("address", address);
		ParameterValidator.notNegative("port", port);
		this.host = address;
		this.port = port;
		this.user = user;
		setTCPSession(null);
		isInitiated = false;
		isAlive = true;
		participantConnections = Collections.synchronizedMap(new LinkedHashMap());
		sessionConnections = Collections.synchronizedMap(new LinkedHashMap());
	}
	
	/**
	 * 
	 * @param session
	 * @param connection
	 * @param user
	 * @param factory used for the CollaborationParserHandler
	 */
	public RemoteUserSession(TCPSession session, MainConnection connection, RemoteUserProxyExt user) {
		ParameterValidator.notNull("session", session);
		ParameterValidator.notNull("connection", connection);
		ParameterValidator.notNull("user", user);
		this.mainConnection = connection;
		this.user = user;
		setTCPSession(session);
		isInitiated = true;
		isAlive = true;
		participantConnections = Collections.synchronizedMap(new LinkedHashMap());
		sessionConnections = Collections.synchronizedMap(new LinkedHashMap());
	}
	
	public void setTimestampFactory(TimestampFactory factory) {
		this.factory = factory;
	}
	
	public TimestampFactory getTimestampFactory() {
		return factory;
	}
	
	/**
	 * 
	 * If the session has been cleaned up, a <code>ConnectionExeption</code>
	 * is thrown.
	 * 
	 * @return
	 */
	public synchronized MainConnection getMainConnection() throws ConnectionException {
		if (!isAlive)
			throw new ConnectionException("session has been ended");
		
		if (!isInitiated())
			initiateTCPSession();
		if (mainConnection == null) {
			Channel channel = startChannel(CHANNEL_MAIN, null);
			LOG.debug("main channel to ["+user.getUserDetails().getUsername()+"] started");
			mainConnection = new MainConnection(channel);
		} else {
			LOG.debug("main channel to ["+user.getUserDetails().getUsername()+"] available");
		}
		return mainConnection;
	}
	
	/**
	 * 
	 * @param type the type of the channel
	 * @param port the ParticipantPort for the ParticipantRequestHandler (optional)
	 * @return
	 * @throws ConnectionException
	 */
	public Channel startChannel(String type, ParticipantPort port) throws ConnectionException {
		return startChannelImpl(type, port);
	}
	
	/********************************************/
	/** methods relating to SessionConnection  **/
	/********************************************/

	/**
	 * 
	 * @param docId
	 * @param collaborationChannel
	 * @return
	 */
	public SessionConnectionImpl addSessionConnection(String docId, Channel collaborationChannel) {
		//TODO: must not be snychronized right?
		LOG.debug("--> addSessionConnection() for doc ["+docId+"]");
		String username = getUser().getUserDetails().getUsername();
		SessionConnectionImpl conn = new SessionConnectionImpl(docId, collaborationChannel, 
				ResponseListener.getInstance(), new CollaborationSerializer(), username, getUser().getId());
		sessionConnections.put(docId, conn);
		LOG.debug(sessionConnections.size() + " SessionConnection(s) with " + username);
		LOG.debug("<-- addSessionConnection()");
		return conn;
	}
	
	/**
	 * 
	 * @param docId
	 * @return
	 */
	public SessionConnectionImpl removeSessionConnection(String docId) {
		SessionConnectionImpl connection = (SessionConnectionImpl) sessionConnections.remove(docId);
		if (connection == null) {
			LOG.warn("SessionConnection to remove for [" + docId + "] is already removed");
		} else {
			connection.cleanup();
		}
		LOG.debug(sessionConnections.size() + " SessionConnection(s) with " + getUser().getUserDetails().getUsername()+" remain.");
		return connection;
	}

	/**
	 * 
	 * @param docId
	 * @return
	 */
	public boolean hasSessionConnection(String docId) {
		return sessionConnections.containsKey(docId);
	}
	
	
	public SessionConnectionImpl getSessionConnection(String docId) {
		return (SessionConnectionImpl) sessionConnections.get(docId);
	}
	
	
	/***********************************************/
	/** methods relating to ParticipantConnection **/
	/***********************************************/
	
	/**
	 * 
	 * @param docId
	 * @return
	 */
	public ParticipantConnectionImpl addParticipantConnection(String docId) {
		//TODO: do i have to check for isAlive as well?
		LOG.debug("--> createParticipantConnection() for doc ["+docId+"]");
		assert !participantConnections.containsKey(docId);
		ParticipantConnectionImpl connection = ParticipantConnectionImplFactory.getInstance().
						createConnection(docId, this,	ResponseListener.getInstance(), new CollaborationSerializer());
		participantConnections.put(docId, connection);
		LOG.debug(participantConnections.size() + " ParticipantConnection(s) for " + getUser().getUserDetails().getUsername());
		LOG.debug("<-- createParticipantConnection()");
		return connection;
	}
	
	/**
	 * 
	 * @param docId
	 * @return
	 */
	public ParticipantConnectionImpl removeParticipantConnection(String docId) {
		ParticipantConnectionImpl conn = (ParticipantConnectionImpl) participantConnections.remove(docId);
		conn.cleanup();
		LOG.debug(participantConnections.size() + " ParticipantConnection(s) for " + getUser().getUserDetails().getUsername()+" remain.");
		return conn;
	}

	public boolean hasParticipantConnection(String docId) {
		return participantConnections.containsKey(docId);
	}
	
	public ParticipantConnectionImpl getParticipantConnection(String docId) {
		ParticipantConnectionImpl conn = (ParticipantConnectionImpl) participantConnections.get(docId);
		if (conn == null) {
			LOG.debug("ParticipantConnection for docId [" + docId + "] not found.");
		}
		return conn;
	}
	
	/**
	 * Cleans up the session. No methods may be called
	 * after a call to <code>cleanup()</code>. This method
	 * is called when the BEEP session has terminated already.
	 */
	public synchronized void cleanup() {
		LOG.debug("--> cleanup()");
		
		Iterator iter = participantConnections.values().iterator();
		while (iter.hasNext()) {
			ParticipantConnectionImpl conn = (ParticipantConnectionImpl) iter.next();
			ParticipantPort port = conn.getParticipantPort();
			removeParticipantConnection(conn.getDocumentId());
			port.leave();
		}
		iter = sessionConnections.values().iterator();
		while (iter.hasNext()) {
			SessionConnectionImpl conn = (SessionConnectionImpl) iter.next();
			RemoteDocumentProxyExt doc = getUser().getSharedDocument(conn.getDocumentId());
			SessionConnectionCallback callback = doc.getSessionConnectionCallback();
			removeSessionConnection(conn.getDocumentId());
			if (getUser().getId().equals(doc.getPublisher().getId())) { //terminated user was publisher
				callback.sessionTerminated();
			} else { //terminated user was participant
				callback.participantLeft(conn.getParticipantId(), Participant.DISCONNECTED);
			}
		}
		
		participantConnections = null;
		sessionConnections = null;
		mainConnection = null;
		setTCPSession(null);
		user = null;
		factory = null;
		isAlive = false;
		LOG.debug("<-- cleanup()");
	}
	
	/**
	 * Closes the active <code>TCPSession</code> and the <code>MainConnection</code>. This method must
	 * be called when the TCPSession is still active.
	 */
	public synchronized void close() {
		if (session != null) {
			try {
				mainConnection.close();
				session.close();
			} catch (Exception be) {
				LOG.warn("could not close active session [" + be + ", " + be.getMessage() + "]");
			}
		}
		user = null;
		isAlive = false;
	}
	
	public synchronized boolean isAlive() {
		return isAlive;
	}
	
	public boolean isInitiated() {
		return isInitiated;
	}
	
	public RemoteUserProxyExt getUser() {
		return user;
	}
	
	public InetAddress getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	private void setTCPSession(TCPSession session) {
		this.session = session;
		if (getUser() != null)
			getUser().setSessionEstablished( (session != null) );
	}
	
	/**
	 * Helper method to initiate the TCPSession for this 
	 * RemoteUserSession.
	 *
	 * @see TCPSession
	 */
	private void initiateTCPSession() throws ConnectionException {
		LOG.debug("--> initiateTCPSession()");
		try {
			//TODO: how to handle when a connection to a user is to be established who resides
			//behind a router? timeout?
			ProfileRegistry registry = ProfileRegistryFactory.getProfileRegistry();
			TCPSession newSession = TCPSessionCreator.initiate(host, port, registry); 
			setTCPSession( newSession );
			LOG.info("initiated session to "+host+":"+port);
			isInitiated = true;
			DiscoveryManagerFactory.getDiscoveryManager(null).setSessionEstablished(user.getId());
		} catch (BEEPException be) {
			//TODO: retry strategy?
			LOG.error("could not initiate session ["+be+"]");
			if (be.getCause() instanceof ConnectException) {
				String msg = getUser().getUserDetails().getUsername() + "[" + host + ":" + port + "]";
				NetworkServiceImpl.getInstance().getCallback().serviceFailure(FailureCodes.CONNECTION_REFUSED, msg, be);
			}
			throw new ConnectionException("session init failed ["+be.getMessage()+"]");
		}
		LOG.debug("<-- initiateTCPSession()");
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 * @throws ConnectionException
	 */
	private Channel startChannelImpl(String type, ParticipantPort port) throws ConnectionException {
		try {
			String uri = NetworkProperties.get(NetworkProperties.KEY_PROFILE_URI);
			LOG.debug("startChannel(type="+type+")");
			
			RequestHandler handler = null;
			String channelType;
			if (type == CHANNEL_MAIN) {
				handler = ProfileRegistryFactory.getMainRequestHandler();
				channelType = getChannelTypeXML(CHANNEL_MAIN);
			} else if (type == CHANNEL_SESSION) {
				CollaborationDeserializer deserializer = new CollaborationDeserializer();
				CollaborationParserHandler parserHandler = new CollaborationParserHandler();
				parserHandler.setTimestampFactory(getTimestampFactory());
				NetworkServiceExt service = NetworkServiceImpl.getInstance();
				ParticipantRequestHandler pHandler = new ParticipantRequestHandler(deserializer, getTimestampFactory(), service);
				pHandler.setParticipantPort(port);
				handler = (RequestHandler) domain.wrap(pHandler, RequestHandler.class);
				channelType = getChannelTypeXML(CHANNEL_SESSION);
			} else {
				//TODO: proxy channel?
				throw new IllegalStateException("unknown channel type ["+type+"]");
			}
			Channel channel = session.startChannel(uri, handler);
			byte[] data = channelType.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
			OutputDataStream output = DataStreamHelper.prepare(data);
			LOG.debug("--> sendMSG() for channel type");
//			channel.sendMSG(output, ResponseListener.getInstance());
			Reply reply = new Reply();
			channel.sendMSG(output, reply);
			reply.getNextReply(); //wait for synchronous response
			LOG.debug("<-- sendMSG()");
			return channel;
		} catch (Exception be) {
			//TODO: retry strategy?
			LOG.error("could not start channel ["+be+"]");
			throw new ConnectionException("could not start channel");
		}
	}
	
	private String getChannelTypeXML(String type) {
		 return "<ace><channel type=\"" + type + "\"/></ace>";
	}
}
