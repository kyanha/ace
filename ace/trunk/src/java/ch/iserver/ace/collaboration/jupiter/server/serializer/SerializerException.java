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

/**
 * Exception used to report errors in command processing
 */
public class SerializerException extends Exception {
	
	/**
	 * The participant id of the failing command.
	 */
	private final int participantId;
	
	/**
	 * @param participantId the participant id of the failing command
	 */
	public SerializerException(int participantId) {
		super();
		this.participantId = participantId; 
	}

	/**
	 * @param participantId the participant id of the failing command
	 * @param message
	 * @param cause
	 */
	public SerializerException(int participantId, String message, Throwable cause) {
		super(message, cause);
		this.participantId = participantId;
	}

	/**
	 * @param participantId the participant id of the failing command
	 * @param message
	 */
	public SerializerException(int participantId, String message) {
		super(message);
		this.participantId = participantId;
	}

	/**
	 * @param participantId the participant id of the failing command
	 * @param cause
	 */
	public SerializerException(int participantId, Throwable cause) {
		super(cause);
		this.participantId = participantId;
	}
	
	/**
	 * @return the participant id of the failing command
	 */
	public int getParticipantId() {
		return participantId;
	}
		
}
