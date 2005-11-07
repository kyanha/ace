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

import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class SessionParticipant {
	
	private final ParticipantPort participantPort;
	
	private final ParticipantProxy participantProxy;
	
	private final ParticipantConnection participantConnection;
	
	private final RemoteUserProxy userProxy;
	
	/**
	 * @param port
	 * @param proxy
	 * @param connection
	 * @param userProxy
	 */
	SessionParticipant(ParticipantPort port, ParticipantProxy proxy, ParticipantConnection connection, RemoteUserProxy userProxy) {
		ParameterValidator.notNull("port", port);
		ParameterValidator.notNull("proxy", proxy);
		ParameterValidator.notNull("connection", connection);
		this.participantConnection = connection;
		this.participantPort = port;
		this.participantProxy = proxy;
		this.userProxy = userProxy;
	}
	
	public int getParticipantId() {
		return participantPort.getParticipantId();
	}
	
	public ParticipantConnection getParticipantConnection() {
		return participantConnection;
	}

	public ParticipantPort getParticipantPort() {
		return participantPort;
	}

	public ParticipantProxy getParticipantProxy() {
		return participantProxy;
	}
	
	public RemoteUserProxy getUserProxy() {
		return userProxy;
	}
	
}
