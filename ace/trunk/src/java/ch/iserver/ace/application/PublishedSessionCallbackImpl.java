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

package ch.iserver.ace.application;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.PublishedSessionCallback;



public class PublishedSessionCallbackImpl implements PublishedSessionCallback {

	private EventList participantSourceList;
	private ParticipationColorManager participationColorManager;

	public PublishedSessionCallbackImpl() {
		participantSourceList = new BasicEventList();
		participationColorManager = new ParticipationColorManager();
	}

	public void participantJoined(Participant participant) {
		participationColorManager.addParticipant(participant);
		participantSourceList.add(new ParticipantItem(participant, participationColorManager.getHighlightColor(participant)));
	}
	
	public void participantLeft(Participant participant, int code) {
		participantSourceList.remove(new ParticipantItem(participant, participationColorManager.getHighlightColor(participant)));
		participationColorManager.removeParticipant(participant);
	}	
	
	public void receiveCaretUpdate(Participant participant, CaretUpdate update) {
	}

	public void receiveOperation(Participant participant, Operation operation) {
	}
	
	public void sessionFailed(int reason, Exception e) {	
	}
	
}