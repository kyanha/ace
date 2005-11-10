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

import java.net.InetAddress;

import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.transport.tcp.TCPSession;
import org.beepcore.beep.transport.tcp.TCPSessionCreator;

import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class RemoteUserSession {

	private InetAddress host;
	private int port;
	private TCPSession session;
	private Connection connection;
	private RemoteUserProxyExt user;
	private boolean isInitiated;
	
	public RemoteUserSession(InetAddress address, int port, RemoteUserProxyExt user) {
		ParameterValidator.notNull("address", address);
		ParameterValidator.notNegative("port", port);
		this.host = address;
		this.port = port;
		this.session = null;
		this.user = user;
		isInitiated = false;
	}
	
	public void initiate() {
		if ( session != null ) {
			try {
				session =  TCPSessionCreator.initiate(host, port);
				isInitiated = true;
			} catch (BEEPException be) {
				//TODO: 
			}
		}
	}
	
	public Connection getConnection() {
		if (!isInitiated()) {
			initiate();
		}
		if (connection == null) {
			try {
			Channel channel = session.startChannel(AbstractProfile.PROFILE_URI);
			connection = new Connection(channel);
			} catch (BEEPException be) {
				//TODO:
			}
		}
		return connection;
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
