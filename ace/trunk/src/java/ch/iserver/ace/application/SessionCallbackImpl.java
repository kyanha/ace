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

import ch.iserver.ace.text.*;
import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.SessionCallback;
import ch.iserver.ace.application.editor.CollaborativeDocument;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

import javax.swing.text.*;
import java.awt.*;
import java.util.HashMap;



public class SessionCallbackImpl implements SessionCallback {

	protected EventList participantSourceList;
	protected ParticipationColorManager participationColorManager;
	protected CollaborativeDocument cDocument;
	protected HashMap participantItemMap;
	protected DocumentItem documentItem;

	public SessionCallbackImpl(DocumentItem documentItem) {
		participantSourceList = new BasicEventList();
		participationColorManager = new ParticipationColorManager();
		participantItemMap = new HashMap();
		this.documentItem = documentItem;
		cDocument = documentItem.getEditorDocument();
	}
	
	public void participantJoined(Participant participant) {
		// UPDATE MAP
		Color pColor = participationColorManager.participantJoined(participant);
		ParticipantItem mapParticipantItem = new ParticipantItem(participant, pColor);
		participantItemMap.put("" + participant.getParticipantId(), mapParticipantItem);
		participantSourceList.add(mapParticipantItem);		
		System.out.println("participantJoined");

		// create style for new participant or get his old style
		Style pStyle = cDocument.getStyle("" + participant.getParticipantId());
		if(pStyle != null) {
			// style exists -> recolor document
			StyleConstants.setBackground(pStyle, pColor);
		} else {
			// style doesnt exists -> add style
			pStyle = cDocument.addStyle("" + participant.getParticipantId(), null);
			StyleConstants.setBackground(pStyle, pColor);
		}

	}
	
	public void participantLeft(Participant participant, int code) {
		// UPDATE MAP
		participationColorManager.participantLeft(participant);
		ParticipantItem mapParticipantItem = (ParticipantItem)participantItemMap.remove("" + participant.getParticipantId());
		participantSourceList.remove(mapParticipantItem);
		System.out.println("participantLeft");

		// clean up listeners
		mapParticipantItem.cleanUp();

		// set color from left participant to grey
		Style pStyle = cDocument.getStyle("" + participant.getParticipantId());
		StyleConstants.setBackground(pStyle, new Color(0xDD, 0xDD, 0xDD));
	}	
	
	public void receiveCaretUpdate(Participant participant, CaretUpdate update) {
		System.out.println("receiveCaretUpdate");
	}

	public void receiveOperation(Participant participant, Operation operation) {
		System.out.println("receiveOperation");
		Style pStyle = cDocument.getStyle("" + participant.getParticipantId());
		applyOperation(operation, pStyle);
	}


	
	private void applyOperation(Operation operation, Style style) {
		if(operation instanceof SplitOperation) {
			// split operation
			SplitOperation op = (SplitOperation)operation;
			System.out.println("split... " + op);
			applyOperation(op.getSecond(), style);
			applyOperation(op.getFirst(), style);
		} else if(operation instanceof InsertOperation) {
			// insert operation
			InsertOperation op = (InsertOperation)operation;
			try {
				cDocument.insertString(op.getPosition(), op.getText(), style);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else if(operation instanceof DeleteOperation) {
			// delete operation
			DeleteOperation op = (DeleteOperation)operation;
			try {
				cDocument.remove(op.getPosition(), op.getTextLength());
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else if (operation instanceof NoOperation) {
			NoOperation op = (NoOperation)operation;
			System.out.println("no operation..." + op);
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