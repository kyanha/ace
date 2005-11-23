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
import ch.iserver.ace.util.ParameterValidator;

/**
 * Command that notifies the session's participants about a leaving user.
 */
public class LeaveCommand implements SerializerCommand {
	
	/**
	 * The participant id of the leaving user.
	 */
	private final int participantId;
	
	/**
	 * The reason why the participant left.
	 */
	private final int reason;
	
	/**
	 * Creates a new LeaveCommand that notifies the other participants that
	 * the user with the given participantId left the session.
	 * 
	 * @param participantId the participant that left
	 * @param reason the reason why he left
	 */
	public LeaveCommand(int participantId, int reason) {
		ParameterValidator.notNegative("participantId", participantId);
		this.participantId = participantId;
		this.reason = reason;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.serializer.SerializerCommand#execute(ch.iserver.ace.collaboration.jupiter.server.Forwarder)
	 */
	public void execute(Forwarder forwarder) {
		forwarder.sendParticipantLeft(participantId, reason);
	}
	
}
