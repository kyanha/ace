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

package ch.iserver.ace.collaboration;


/**
 * A Participant is a contributor to an editing session. He has a session
 * wide unique id identifying him.
 * 
 * @todo add session property
 */
public interface Participant {
	
	/**
	 * Constant used in notifications when a participant leaves a Session.
	 * Signifies a participant that left the Session voluntarily.
	 */
	int LEFT = 1;
	
	/**
	 * Constant used in notifications when a participant leaves a Session.
	 * Signifies a participant that was kicked from the Session.
	 */
	int KICKED = 2;
	
	/**
	 * Gets the remote user corresponding to this participant.
	 *
	 * @return the remote user corresponding to this participant
	 */
	RemoteUser getUser();
	
	/**
	 * Retrieves the participant id of this participant in a session. Note,
	 * a user has (most likely) a different participant id in each session
	 * he/she joins. The participant id is a session-wide unique id.
	 *
	 * @return the participant id of this participant
	 */
	int getParticipantId();
	
}
