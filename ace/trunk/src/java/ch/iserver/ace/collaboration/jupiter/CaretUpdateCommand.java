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

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.PublishedSessionCallback;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.ParameterValidator;

/**
 * The CaretUpdateCommand is used by the the CallbackWorker to transform
 * a CaretUpdateMessage and receive the resulting CaretUpdate in the
 * PublishedSessionCallback.
 */
class CaretUpdateCommand extends LockingCommand {
	
	/**
	 * The message to be received.
	 */
	private final CaretUpdateMessage message;
	
	/**
	 * The Participant that generated the message.
	 */
	private final Participant participant;
	
	/**
	 * Creates a new CaretUpdateCommand.
	 * 
	 * @param lock the Lock used to lock the algorithm
	 * @param algorithm the AlgorithmWrapper to transform the message
	 * @param participant the Participant that generated the message
	 * @param message the CaretUpdateMessage to be received
	 */
	CaretUpdateCommand(Lock lock, 
					AlgorithmWrapper algorithm, 
					Participant participant, 
					CaretUpdateMessage message) {
		super(lock, algorithm);
		ParameterValidator.notNull("participant", participant);
		ParameterValidator.notNull("message", message);
		this.participant = participant;
		this.message = message;
	}
		
	/**
	 * @return the CaretUpdateMessage to be received
	 */
	private CaretUpdateMessage getMessage() {
		return message;
	}
	
	/**
	 * @return the Participant that generated the message 
	 */
	private Participant getParticipant() {
		return participant;
	}
		
	protected void doWork(PublishedSessionCallback callback) {
		CaretUpdate update = getAlgorithm().receiveCaretUpdateMessage(getMessage());
		callback.receiveCaretUpdate(getParticipant(), update);
	}
	
}
