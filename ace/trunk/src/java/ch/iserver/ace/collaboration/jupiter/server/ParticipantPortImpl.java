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
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.util.ParameterValidator;
import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;

/**
 * 
 */
class ParticipantPortImpl implements ParticipantPort {
	
	private final ServerLogic logic;
	
	private final int participantId;
	
	private final Algorithm algorithm;
	
	private final BlockingQueue queue;
	
	public ParticipantPortImpl(ServerLogic logic, int participantId, Algorithm algorithm, BlockingQueue queue) {
		ParameterValidator.notNull("algorithm", algorithm);
		ParameterValidator.notNull("queue", queue);
		this.logic = logic;
		this.participantId = participantId;
		this.algorithm = algorithm;
		this.queue = queue;
	}
	
	protected ServerLogic getLogic() {
		return logic;
	}
	
	public int getParticipantId() {
		return participantId;
	}
	
	public Algorithm getAlgorithm() {
		return algorithm;
	}
	
	public void receiveCaretUpdate(CaretUpdateMessage message) {
		SerializerCommand cmd = new CaretUpdateSerializerCommand(
						getParticipantId(),
						getAlgorithm(),
						message);
		queue.add(cmd);
	}
	
	public void receiveRequest(Request request) {
		SerializerCommand cmd = new RequestSerializerCommand(
						getParticipantId(),
						getAlgorithm(),
						request);
		queue.add(cmd);
	}
	
	public void leave() {
		getLogic().leave(getParticipantId());
	}
	
}
