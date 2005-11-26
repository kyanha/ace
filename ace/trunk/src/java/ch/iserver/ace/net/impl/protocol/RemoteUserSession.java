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
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.ProfileRegistry;
import org.beepcore.beep.core.RequestHandler;
import org.beepcore.beep.core.StartChannelProfile;
import org.beepcore.beep.lib.NullReplyListener;
import org.beepcore.beep.transport.tcp.TCPSession;
import org.beepcore.beep.transport.tcp.TCPSessionCreator;

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.net.impl.NetworkProperties;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.SessionConnectionImpl;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class RemoteUserSession {
	
	public static final String CHANNEL_MAIN = "main";
	public static final String CHANNEL_COLLABORATION = "coll";
	//TODO: could open a channel to a host which acts as a proxy to another host inside that subnet
	public static final String CHANNEL_PROXY = "proxy";

	private static Logger LOG = Logger.getLogger(RemoteUserSession.class);
	
	private InetAddress host;
	private int port;
	private TCPSession session;
	private MainConnection mainConnection;
	private Map participantConnections, sessionConnections;
	private RemoteUserProxyExt user;
	private boolean isInitiated;
	private boolean isAlive;
	
	public RemoteUserSession(InetAddress address, int port, RemoteUserProxyExt user) {
		ParameterValidator.notNull("address", address);
		ParameterValidator.notNegative("port", port);
		this.host = address;
		this.port = port;
		this.session = null;
		this.user = user;
		isInitiated = false;
		isAlive = true;
		participantConnections = Collections.synchronizedMap(new LinkedHashMap());
		sessionConnections = Collections.synchronizedMap(new LinkedHashMap());
	}
	
	public RemoteUserSession(TCPSession session, MainConnection connection, RemoteUserProxyExt user) {
		ParameterValidator.notNull("session", session);
		ParameterValidator.notNull("connection", connection);
		ParameterValidator.notNull("user", user);
		this.session = session;
		this.mainConnection = connection;
		this.user = user;
		isInitiated = true;
		isAlive = true;
		participantConnections = Collections.synchronizedMap(new LinkedHashMap());
		sessionConnections = Collections.synchronizedMap(new LinkedHashMap());
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
			initiate();
		if (mainConnection == null) {
			Channel channel = startNewChannel(CHANNEL_MAIN);
			LOG.debug("main channel to ["+user.getUserDetails().getUsername()+"] started");
			mainConnection = new MainConnection(channel);
		} else {
			LOG.debug("main channel to ["+user.getUserDetails().getUsername()+"] available");
		}
		return mainConnection;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 * @throws ConnectionException
	 */
	public Channel startChannel(String type) throws ConnectionException {
		return startNewChannel(type);
	}
	

	/**
	 * 
	 * @param docId
	 * @param collaborationChannel
	 * @return
	 */
	public SessionConnectionImpl addSessionConnection(String docId, Channel collaborationChannel) {
		LOG.debug("--> addSessionConnection() for doc ["+docId+"]");
		CollaborationSerializer serializer = new CollaborationSerializer();
		SessionConnectionImpl conn = new SessionConnectionImpl(docId, this, 
				collaborationChannel, NullReplyListener.getListener(), serializer);
		sessionConnections.put(docId, conn);
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
		connection.cleanup();
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
	
	/**
	 * 
	 * @param docId
	 * @return
	 */
	public ParticipantConnectionImpl createParticipantConnection(String docId) {
		//TODO: do i have to check for isAlive as well?
		LOG.debug("--> createParticipantConnection() for doc ["+docId+"]");
		ParticipantConnectionImpl connection = new ParticipantConnectionImpl(docId, this,
				NullReplyListener.getListener(), SerializerImpl.getInstance());
		participantConnections.put(docId, connection);
		LOG.debug("<-- createCollaborationConnection()");
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
		return conn;
	}

	public boolean hasParticipantConnection(String docId) {
		return participantConnections.containsKey(docId);
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 * @throws ConnectionException
	 */
	private Channel startNewChannel(String type) throws ConnectionException {
		try {
			String uri = NetworkProperties.get(NetworkProperties.KEY_PROFILE_URI);
			LOG.debug("startChannel("+uri+", type="+type+")");
			
			StartChannelProfile profile = new StartChannelProfile(uri, false, type);
			RequestHandler handler = null;
			if (type == CHANNEL_MAIN) {
				handler = ProfileRegistryFactory.getMainRequestHandler();
			} else if (type == CHANNEL_COLLABORATION) {
//				handler = SessionRequestHandlerFactory.getInstance().createHandler();
				CollaborationDeserializer deserializer = new CollaborationDeserializer();
				CollaborationParserHandler parserHandler = new CollaborationParserHandler();
				handler = new ParticipantRequestHandler(deserializer, parserHandler);
			} else {
				//TODO: proxy channel?
				throw new IllegalStateException("unknown channel type ["+type+"]");
			}
			return session.startChannel(profile, handler);
//			return session.startChannel(uri, false, type);
		} catch (BEEPException be) {
			//TODO: retry strategy?
			LOG.error("could not start channel ["+be+"]");
			throw new ConnectionException("could not start channel");
		}
	}
	
	/**
	 * Helper method to initiate the TCPSession for this 
	 * RemoteUserSession.
	 *
	 * @see TCPSession
	 */
	private void initiate() throws ConnectionException {
		try {
			ProfileRegistry registry = ProfileRegistryFactory.getProfileRegistry();
			session =  TCPSessionCreator.initiate(host, port, registry);
			LOG.info("initiated session to "+host+":"+port);
			isInitiated = true;
		} catch (BEEPException be) {
			//TODO: retry strategy?
			LOG.error("could not initiate session ["+be+"]");
			if (be.getCause() instanceof ConnectException) {
				String msg = "connection refused to host [" + host + ":" + port + "]";
				NetworkServiceImpl.getInstance().getCallback().serviceFailure(FailureCodes.CONNECTION_REFUSED, msg, be);
			}
			throw new ConnectionException("session init failed ["+be.getMessage()+"]");
		}
	}
	
	/**
	 * Cleans up the session. No methods may be called
	 * after a call to <code>cleanup()</code>. This method
	 * is called when the BEEP session has terminated already.
	 */
	public synchronized void cleanup() {
		mainConnection = null;
		session = null;
		user = null;
		isAlive = false;
	}
	
	/**
	 * Closes the active session. This method must
	 * be called when the TCPSession is still active.
	 */
	public synchronized void close() {
		if (session != null) {
			try {
				mainConnection.close();
				session.close();
			} catch (BEEPException be) {
				LOG.warn("could not close active session ["+be.getMessage()+"]");
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
}
