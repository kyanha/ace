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

import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.jupiter.server.serializer.CaretUpdateSerializerCommand;
import ch.iserver.ace.collaboration.jupiter.server.serializer.LeaveCommand;
import ch.iserver.ace.collaboration.jupiter.server.serializer.RequestSerializerCommand;
import ch.iserver.ace.collaboration.jupiter.server.serializer.SerializerCommand;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.util.ParameterValidator;

/**
 * 
 */
public class ParticipantPortImpl implements ParticipantPort {
	
	private final ServerLogic logic;
	
	private final int participantId;
	
	private final Algorithm algorithm;
		
	/**
	 * @param logic
	 * @param participantId
	 * @param algorithm
	 * @param queue
	 */
	public ParticipantPortImpl(ServerLogic logic, int participantId, Algorithm algorithm) {
		ParameterValidator.notNull("algorithm", algorithm);
		this.logic = logic;
		this.participantId = participantId;
		this.algorithm = algorithm;
	}

	/**
	 * @return
	 */
	protected ServerLogic getLogic() {
		return logic;
	}
		
	/**
	 * @return
	 */
	public Algorithm getAlgorithm() {
		return algorithm;
	}
	
	/** 
	 * @see ch.iserver.ace.net.ParticipantPort#getParticipantId()
	 */
	public int getParticipantId() {
		return participantId;
	}

	/**
	 * @see ch.iserver.ace.net.ParticipantPort#receiveCaretUpdate(ch.iserver.ace.algorithm.CaretUpdateMessage)
	 */
	public void receiveCaretUpdate(CaretUpdateMessage message) {
		SerializerCommand cmd = new CaretUpdateSerializerCommand(
						getParticipantId(),
						getAlgorithm(),
						message);
		getLogic().addCommand(cmd);
	}
	
	/**
	 * @see ch.iserver.ace.net.ParticipantPort#receiveRequest(ch.iserver.ace.algorithm.Request)
	 */
	public void receiveRequest(Request request) {
		SerializerCommand cmd = new RequestSerializerCommand(
						getParticipantId(),
						getAlgorithm(),
						request);
		getLogic().addCommand(cmd);
	}
	
	/**
	 * @see ch.iserver.ace.net.ParticipantPort#leave()
	 */
	public void leave() {
		getLogic().leave(getParticipantId());
		getLogic().addCommand(new LeaveCommand(getParticipantId(), Participant.LEFT));
	}
	
}
