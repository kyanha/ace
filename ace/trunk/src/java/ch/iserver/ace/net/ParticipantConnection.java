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

package ch.iserver.ace.net;

import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;

/**
 * A ParticipantConnection provides a logical connection to a participant in
 * an editing session. It allows to send various type of information to
 * the participant this connection is connected to.
 */
public interface ParticipantConnection {
	
	/**
	 * The participant id of the publisher.
	 */
	int PUBLISHER_ID = 0;

	/**
	 * @param participantId the participant id of the participant
	 */
	void setParticipantId(int participantId);
	
	/**
	 * Notifies the connection that the join request was accepted. The passed
	 * in port can be used to communicate with the session.
	 * 
	 * @param port the port for the participant
	 */
	void joinAccepted(ParticipantPort port);
	
	/**
	 * Notifies the connection that the join was rejected. The passed in code
	 * specifies why the join request was rejected.
	 * 
	 * @param code the error code
	 */
	void joinRejected(int code);
	
	/**
	 * @return the remote user proxy of the participant represented by the
	 *         connection
	 */
	RemoteUserProxy getUser();
		
	/**
	 * Sends the given <var>document</var> to the participant. This method
	 * is typically called after a user joins a session to send the
	 * initial document.
	 * 
	 * @param document the document content represented as PortableDocument
	 */
	void sendDocument(PortableDocument document);
	
	/**
	 * Sends a request to the participant this connection represents.
	 * 
	 * @param participantId the participant that created the operation
	 * @param request the request to be sent to the participant
	 */
	void sendRequest(int participantId, Request request);
	
	/**
	 * Sends a caret update message to the participant this connection
	 * represents.
	 * 
	 * @param participantId the participant that created the caret update
	 * @param message the CaretUpdateMessage to send
	 */
	void sendCaretUpdateMessage(int participantId, CaretUpdateMessage message);
	
	/**
	 * Sends a participant joined message to the participant represented by
	 * this ParticipantConnection.
	 * 
	 * @param participantId the id of the joined participant
	 * @param proxy the user proxy of the joined participant
	 */
	void sendParticipantJoined(int participantId, RemoteUserProxy proxy);
	
	/**
	 * Sends a participant left message to the participant represented by
	 * this ParticipantConnection.
	 * 
	 * @param participantId the participant id of the participant that left 
	 *                      the session
	 * @param reason the reason code why the participant left
	 */
	void sendParticipantLeft(int participantId, int reason);
	
	/**
	 * Sends a kicked message to the participant represented by this
	 * ParticipatantConnection.
	 */
	void sendKicked();
	
	/**
	 * Closes the connection to the participant.
	 */
	void close();
	
}
