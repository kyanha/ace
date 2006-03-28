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

import ch.iserver.ace.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;

/**
 *
 */
public class ParticipantPortStub implements ParticipantPort {
	
	private final int participantId;
	
	public ParticipantPortStub(int participantId) {
		this.participantId = participantId;
	}
	
	/**
	 * @see ch.iserver.ace.net.ParticipantPort#getParticipantId()
	 */
	public int getParticipantId() {
		return participantId;
	}

	/**
	 * @see ch.iserver.ace.net.ParticipantPort#receiveRequest(ch.iserver.ace.algorithm.Request)
	 */
	public void receiveRequest(Request request) {

	}

	/**
	 * @see ch.iserver.ace.net.ParticipantPort#receiveCaretUpdate(ch.iserver.ace.algorithm.CaretUpdateMessage)
	 */
	public void receiveCaretUpdate(CaretUpdateMessage message) {

	}
	
	/**
	 * @see ch.iserver.ace.net.ParticipantPort#receiveAcknowledge(int, ch.iserver.ace.algorithm.Timestamp)
	 */
	public void receiveAcknowledge(int siteId, Timestamp timestamp) {
		
	}

	/**
	 * @see ch.iserver.ace.net.ParticipantPort#leave()
	 */
	public void leave() {

	}

}
