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

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.collaboration.jupiter.AlgorithmWrapper;
import ch.iserver.ace.collaboration.jupiter.AlgorithmWrapperImpl;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.util.ParameterValidator;
import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;

class ParticipantProxyImpl implements ParticipantProxy {
	
	private final int participantId;
	
	private final BlockingQueue queue;
	
	private final AlgorithmWrapper algorithm;
	
	private final ParticipantConnection connection;
	
	public ParticipantProxyImpl(int participantId, 
					BlockingQueue queue, 
					Algorithm algorithm, 
					ParticipantConnection connection) {
		ParameterValidator.notNull("queue", queue);
		ParameterValidator.notNull("connection", connection);
		this.participantId = participantId;
		this.queue = queue;
		this.algorithm = new AlgorithmWrapperImpl(algorithm);
		this.connection = connection;
	}
	
	protected AlgorithmWrapper getAlgorithm() {
		return algorithm;
	}
	
	protected ParticipantConnection getConnection() {
		return connection;
	}
	
	public void sendCaretUpdate(int participantId, CaretUpdate update) {
		if (this.participantId != participantId) {
			AlgorithmWrapper algorithm = getAlgorithm();
			CaretUpdateMessage message = algorithm.generateCaretUpdateMessage(update);
			ParticipantConnection connection = getConnection();
			DispatcherCommand command = new CaretUpdateDispatcherCommand(connection, participantId, message);
			queue.add(command);
		}
	}
	
	public void sendOperation(int participantId, Operation operation) {
		if (this.participantId != participantId) {
			AlgorithmWrapper algorithm = getAlgorithm();
			Request request = algorithm.generateRequest(operation);
			ParticipantConnection connection = getConnection();
			DispatcherCommand command = new RequestDispatcherCommand(connection, participantId, request);
			queue.add(command);
		}
	}
}