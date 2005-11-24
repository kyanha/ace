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

package ch.iserver.ace.collaboration.jupiter.server.serializer;

import ch.iserver.ace.collaboration.jupiter.server.Forwarder;
import ch.iserver.ace.collaboration.jupiter.server.ServerLogic;
import ch.iserver.ace.collaboration.jupiter.server.SessionParticipant;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Command that joins a participant to the session.
 */
public class JoinCommand implements SerializerCommand {

	/**
	 * The participant that joins the session.
	 */
	private final SessionParticipant participant;
	
	/**
	 * The server logic of the session.
	 */
	private final ServerLogic logic;
	
	/**
	 * Creates a new JoinCommand that joins the participant to the session
	 * represented by the logic.
	 * 
	 * @param participant the participant to join
	 * @param logic the server logic of the session
	 */
	public JoinCommand(SessionParticipant participant, ServerLogic logic) {
		ParameterValidator.notNull("participant", participant);
		ParameterValidator.notNull("logic", logic);
		this.participant = participant;
		this.logic = logic;
	}
	
	/**
	 * @return the user proxy of the participant
	 */
	protected RemoteUserProxy getUserProxy() {
		return participant.getUserProxy();
	}
	
	/**
	 * @return the participant id of the participant
	 */
	protected int getParticipantId() {
		return participant.getParticipantId();
	}
	
	/**
	 * @return the connection of the participant
	 */
	protected ParticipantConnection getConnection() {
		return participant.getParticipantConnection();
	}
	
	/**
	 * @return the port of the participant
	 */
	protected ParticipantPort getPort() {
		return participant.getParticipantPort();
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.serializer.SerializerCommand#execute(ch.iserver.ace.collaboration.jupiter.server.Forwarder)
	 */
	public void execute(Forwarder forwarder) {
		ParticipantConnection connection = getConnection();
		PortableDocument document = logic.getDocument();
		connection.joinAccepted(getPort());
		connection.sendDocument(document);
		logic.addParticipant(participant);
		forwarder.sendParticipantJoined(getParticipantId(), getUserProxy());		
	}

}
