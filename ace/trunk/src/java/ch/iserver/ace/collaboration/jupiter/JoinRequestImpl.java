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

package ch.iserver.ace.collaboration.jupiter;

import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.collaboration.JoinRequest;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.collaboration.jupiter.server.ParticipantPortImpl;
import ch.iserver.ace.collaboration.jupiter.server.ParticipantProxy;
import ch.iserver.ace.collaboration.jupiter.server.ServerLogic;
import ch.iserver.ace.collaboration.jupiter.server.SessionParticipant;
import ch.iserver.ace.collaboration.jupiter.server.serializer.JoinCommand;
import ch.iserver.ace.collaboration.jupiter.server.serializer.SerializerCommand;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class JoinRequestImpl implements JoinRequest {

	private RemoteUser user;
	
	private ParticipantConnection connection;
	
	private ServerLogic logic;
	
	public JoinRequestImpl(ServerLogic logic, RemoteUser user, ParticipantConnection connection) {
		ParameterValidator.notNull("logic", logic);
		ParameterValidator.notNull("user", user);
		ParameterValidator.notNull("connection", connection);
		this.logic = logic;
		this.user = user;
		this.connection = connection;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.JoinRequest#getUser()
	 */
	public RemoteUser getUser() {
		return user;
	}

	/**
	 * @see ch.iserver.ace.collaboration.JoinRequest#accept()
	 */
	public void accept() {
		Algorithm algorithm = new Jupiter(false);
		
		int participantId = logic.nextParticipantId();;
		connection.setParticipantId(participantId);
		
		ParticipantPort port = new ParticipantPortImpl(logic, participantId, algorithm);
		ParticipantProxy proxy = new ParticipantProxy(participantId, algorithm, connection);
		RemoteUserProxy user = connection.getUser();
		
		SessionParticipant participant = new SessionParticipant(port, proxy, connection, user);
		SerializerCommand cmd = new JoinCommand(participant, logic);
		logic.addCommand(cmd);

		connection.joinAccepted(port);
	}

	/**
	 * @see ch.iserver.ace.collaboration.JoinRequest#reject()
	 */
	public void reject() {
		connection.joinRejected(JoinRequest.REJECTED);
	}

}
