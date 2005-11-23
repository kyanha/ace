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

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.collaboration.jupiter.server.Forwarder;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Command that receives a caret update and forwards it to the other 
 * participants.
 */
public class CaretUpdateSerializerCommand extends AbstractSerializerCommand {
		
	/**
	 * The CaretUpdateMessage to receive. 
	 */
	private final CaretUpdateMessage message;
	
	/**
	 * Creates a new CaretUpdateSerializerCommand using the given participant
	 * id, algorithm, and message.
	 * 
	 * @param participantId the participant id of the sender
	 * @param algorithm the server-side algorithm
	 * @param message the message to be received
	 */
	public CaretUpdateSerializerCommand(int participantId, Algorithm algorithm, CaretUpdateMessage message) {
		super(participantId, algorithm);
		ParameterValidator.notNull("message", message);
		this.message = message;
	}
	
	/**
	 * @return the message to be received
	 */
	public CaretUpdateMessage getMessage() {
		return message;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.serializer.SerializerCommand#execute(ch.iserver.ace.collaboration.jupiter.server.Forwarder)
	 */
	public void execute(Forwarder forwarder) throws SerializerException {
		try {
			CaretUpdate update = getMessage().getUpdate();
			Timestamp timestamp = getMessage().getTimestamp();
			int[] indices = getAlgorithm().transformIndices(timestamp, update.getIndices());
			forwarder.sendCaretUpdate(getParticipantId(), new CaretUpdate(indices[0], indices[1]));
		} catch (Exception e) {
			throw new SerializerException(getParticipantId(), e);
		}
	}

}
