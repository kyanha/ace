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
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Forwarder implementation that is responsible to forward events
 * to one particular participant. The participant is represented
 * by a ParticipantConnection.
 */
public class ParticipantProxy implements Forwarder {
	
	/**
	 * The participant id of the participant represented by this proxy.
	 */
	private final int participantId;
		
	/**
	 * The algorithm used to transform requests.
	 */
	private final AlgorithmWrapper algorithm;
	
	/**
	 * The connection to the participant.
	 */
	private final ParticipantConnection connection;
	
	/**
	 * Creates a new ParticipantProxy instance.
	 * 
	 * @param participantId the participant id of this proxy
	 * @param algorithm the algorithm used to transform requests
	 * @param connection the connection to the participant
	 */
	public ParticipantProxy(int participantId, 
					Algorithm algorithm, 
					ParticipantConnection connection) {
		this(participantId, new AlgorithmWrapperImpl(algorithm), connection);
	}
	
	/**
	 * Creates a new ParticipantProxy instance.
	 * 
	 * @param participantId the participant id of this proxy
	 * @param algorithm the algorithm wrapper used by this proxy
	 * @param connection the connection to the participant
	 */
	ParticipantProxy(int participantId,
					AlgorithmWrapper algorithm,
					ParticipantConnection connection) {
		ParameterValidator.notNull("connection", connection);
		ParameterValidator.notNull("algorithm", algorithm);
		this.participantId = participantId;
		this.algorithm = algorithm;
		this.connection = connection;
	}
	
	/**
	 * @return the algorithm wrapper of this proxy
	 */
	protected AlgorithmWrapper getAlgorithm() {
		return algorithm;
	}
	
	/**
	 * @return the connection to the participant
	 */
	protected ParticipantConnection getConnection() {
		return connection;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantProxy#sendCaretUpdate(int, ch.iserver.ace.CaretUpdate)
	 */
	public void sendCaretUpdate(int participantId, CaretUpdate update) {
		if (this.participantId != participantId) {
			AlgorithmWrapper algorithm = getAlgorithm();
			CaretUpdateMessage message = algorithm.generateCaretUpdateMessage(update);
			getConnection().sendCaretUpdateMessage(participantId, message);
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantProxy#sendOperation(int, ch.iserver.ace.Operation)
	 */
	public void sendOperation(int participantId, Operation operation) {
		if (this.participantId != participantId) {
			AlgorithmWrapper algorithm = getAlgorithm();
			Request request = algorithm.generateRequest(operation);
			getConnection().sendRequest(participantId, request);
		}
	}
		
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantProxy#sendParticipantLeft(int, int)
	 */
	public void sendParticipantLeft(int participantId, int reason) {
		if (this.participantId != participantId) {
			getConnection().sendParticipantLeft(participantId, reason);
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantProxy#sendParticipantJoined(int, ch.iserver.ace.net.RemoteUserProxy)
	 */
	public void sendParticipantJoined(int participantId, RemoteUserProxy proxy) {
		if (this.participantId != participantId) {
			getConnection().sendParticipantJoined(participantId, proxy);
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.Forwarder#close()
	 */
	public void close() {
		getConnection().close();
	}
	
}
