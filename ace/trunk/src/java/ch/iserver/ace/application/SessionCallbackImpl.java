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

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.SessionCallback;
import ch.iserver.ace.application.editor.CollaborativeDocument;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

import javax.swing.text.*;
import java.awt.*;



public class SessionCallbackImpl implements SessionCallback {

	private EventList participantSourceList;
	protected ParticipationColorManager participationColorManager;
	private CollaborativeDocument doc;

	public SessionCallbackImpl() {
		participantSourceList = new BasicEventList();
		participationColorManager = new ParticipationColorManager();
	}

	/*public SessionCallbackImpl(StyledDocument doc) {
		this.doc = doc;
		participantSourceList = new BasicEventList();
		participationColorManager = new ParticipationColorManager();
	}*/
	
	public void setDoc(CollaborativeDocument doc) {
		this.doc = doc;
	}
	
	public void participantJoined(Participant participant) {
		Color pColor = participationColorManager.addParticipant(participant);
		participantSourceList.add(new ParticipantItem(participant, pColor));
		
		System.out.println("participantJoined");
		Style pStyle = doc.getStyle("" + participant.getParticipantId());
		if(pStyle != null) {
			// style exists -> recolor document
			StyleConstants.setBackground(pStyle, pColor);
		} else {
			// style doesnt exists -> add style
			pStyle = doc.addStyle("" + participant.getParticipantId(), null);
			StyleConstants.setBackground(pStyle, pColor);
		}
		
	}
	
	public void participantLeft(Participant participant, int code) {
		Color pColor = participationColorManager.getHighlightColor(participant);
		participantSourceList.remove(new ParticipantItem(participant, pColor));
		participationColorManager.removeParticipant(participant);

		System.out.println("participantLeft");
		// set color from left participant to grey
		Style pStyle = doc.getStyle("" + participant.getParticipantId());
		StyleConstants.setBackground(pStyle, new Color(0xDD, 0xDD, 0xDD));
	}	
	
	public void receiveCaretUpdate(Participant participant, CaretUpdate update) {
		System.out.println("receiveCaretUpdate");
	}

	public void receiveOperation(Participant participant, Operation operation) {
		System.out.println("receiveOperation");
		Style pStyle = doc.getStyle("" + participant.getParticipantId());
		try {
			doc.insertString(0, "hallo", pStyle);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sessionFailed(int reason, Exception e) {
		System.out.println("sessionFailed");
		e.printStackTrace();
	}
	
	public EventList getParticipantSourceList() {
		return participantSourceList;
	}

}