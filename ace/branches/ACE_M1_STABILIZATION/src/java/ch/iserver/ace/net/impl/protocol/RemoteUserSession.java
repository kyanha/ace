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

import org.apache.log4j.Logger;
import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.ProfileRegistry;
import org.beepcore.beep.transport.tcp.TCPSession;
import org.beepcore.beep.transport.tcp.TCPSessionCreator;

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class RemoteUserSession {

	private static Logger LOG = Logger.getLogger(RemoteUserSession.class);
	
	private InetAddress host;
	private int port;
	private TCPSession session;
	private ParticipantConnectionExt connection;
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
	}
	
	public RemoteUserSession(TCPSession session, RemoteUserProxyExt user) {
		ParameterValidator.notNull("session", session);
		ParameterValidator.notNull("user", user);
		this.session = session;
		this.user = user;
		isInitiated = true;
		isAlive = true;
	}
	
	/**
	 * 
	 * If the session has been cleaned up, a <code>ConnectionExeption</code>
	 * is thrown.
	 * 
	 * @return
	 */
	public synchronized ParticipantConnectionExt getConnection() throws ConnectionException {
		if (!isAlive)
			throw new ConnectionException("session has been ended");
		
		if (!isInitiated())
			initiate();
		if (connection == null) {
			try {
			Channel channel = session.startChannel(ProtocolConstants.PROFILE_URI);
			connection = new ParticipantConnectionImpl(channel);
			} catch (BEEPException be) {
				//TODO: retry strategy?
				LOG.error("could not start channel ["+be+"]");
			}
		}
		return connection;
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
	 * after a call to <code>cleanup()</code>.
	 */
	public synchronized void cleanup() {
		connection = null;
		session = null;
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
