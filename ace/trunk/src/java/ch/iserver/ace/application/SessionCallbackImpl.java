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
import ch.iserver.ace.util.CaretHandler;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;





public class SessionCallbackImpl implements SessionCallback {

	public final static String ADDED_PROPERTY	= "caretAdded";
	public final static String REMOVED_PROPERTY	= "caretRemoved";
	public final static String UPDATED_PROPERTY	= "caretUpdates";

	protected DocumentViewController viewController;

	protected int participantId;
	protected String mpId;
	protected EventList participantSourceList;
	protected CollaborativeDocument cDocument;
	protected HashMap participantItemMap;
	protected PropertyChangeHashMapImpl participantCaretMap;
	protected HashMap participantTextColorMap;
	protected HashMap participantCursorColorMap;
	protected DocumentItem documentItem;
	protected DialogController dialogController;

	private int participantCount = 0;
//	private Color[] defaultParticipantColors = {
//		new Color(0xFF, 0x60, 0x60), new Color(0xFF, 0xDD, 0x60),
//		new Color(0xFF, 0xFF, 0x60), new Color(0x60, 0xDD, 0x60),
//		new Color(0x60, 0xFF, 0xFF), new Color(0x60, 0x60, 0xFF),
//		new Color(0xDD, 0x60, 0xFF), new Color(0xFF, 0x60, 0xDD),
//		new Color(0xFF, 0x80, 0x80), new Color(0xFF, 0xDD, 0x80),
//		new Color(0xFF, 0xFF, 0x80), new Color(0x80, 0xDD, 0x80),
//		new Color(0x80, 0xFF, 0xFF), new Color(0x80, 0x80, 0xFF),
//		new Color(0xDD, 0x80, 0xFF), new Color(0xFF, 0x80, 0xDD),
//	};

	private Color[] defaultParticipantTextColors = {
		new Color(0xFF, 0x80, 0x80), new Color(0xFF, 0xDD, 0x80),
		new Color(0xFF, 0xFF, 0x80), new Color(0x80, 0xDD, 0x80),
		new Color(0x80, 0xFF, 0xFF), new Color(0x80, 0x80, 0xFF),
		new Color(0xDD, 0x80, 0xFF), new Color(0xFF, 0x80, 0xDD),
	};

	private Color[] defaultParticipantCursorColors = {
		new Color(0xFF, 0x00, 0x00), new Color(0xFF, 0xDD, 0x80),
		new Color(0xFF, 0xFF, 0x80), new Color(0x80, 0xDD, 0x80),
		new Color(0x80, 0xFF, 0xFF), new Color(0x80, 0x80, 0xFF),
		new Color(0xDD, 0x80, 0xFF), new Color(0xFF, 0x80, 0xDD),
	};



	public SessionCallbackImpl(DocumentItem documentItem, DialogController dialogController) {
		this.dialogController = dialogController;
		participantSourceList = new BasicEventList();
		participantItemMap = new HashMap();		
		participantCaretMap = new PropertyChangeHashMapImpl();
		participantTextColorMap = new HashMap();
		participantCursorColorMap = new HashMap();
		this.documentItem = documentItem;
		cDocument = documentItem.getEditorDocument();
	}
	
	public void setParticipantId(int participantId) {
		// TODO: implement this
		System.out.println("setting the local participant id: " + participantId);		
		this.participantId = participantId;
		mpId = "" + participantId;
	}
	
	public void participantJoined(Participant participant) {
		String pId = "" + participant.getParticipantId();

		// UPDATE MAP
		Color pColor = participantColorJoined(pId);
		ParticipantItem mapParticipantItem = new ParticipantItem(participant, pColor);
		participantItemMap.put(pId, mapParticipantItem);
		participantSourceList.add(mapParticipantItem);		
		System.out.println("participantJoined: " + participant);
		
		// add caret handler
		PropertyChangeCaretHandlerImpl pCaretHandler = new PropertyChangeCaretHandlerImpl(0, 0);
		cDocument.addDocumentListener(pCaretHandler);
		participantCaretMap.put(pId, pCaretHandler);
		CaretUpdate newCaretUpdate = new CaretUpdate(pCaretHandler.getDot(), pCaretHandler.getMark());
		participantCaretMap.firePropertyChange(ADDED_PROPERTY, null, newCaretUpdate);

		// create style for new participant or get his old style
		Style pStyle = cDocument.getStyle(pId);
		if(pStyle != null) {
			// style exists -> recolor document
			StyleConstants.setBackground(pStyle, pColor);
		} else {
			// style doesnt exists -> add style
			pStyle = cDocument.addStyle(pId, null);
			StyleConstants.setBackground(pStyle, pColor);
		}

	}
	
	public void participantLeft(Participant participant, int code) {
		String pId = "" + participant.getParticipantId();
		// UPDATE MAP
		participantColorLeft(pId);
		ParticipantItem mapParticipantItem = (ParticipantItem)participantItemMap.remove(pId);
		participantSourceList.remove(mapParticipantItem);
		System.out.println("participantLeft: " + participant + "   code: " + code);
		
		// remove caret handler
		PropertyChangeCaretHandlerImpl pCaretHandler = (PropertyChangeCaretHandlerImpl)participantCaretMap.remove(pId);
		cDocument.removeDocumentListener(pCaretHandler);
		CaretUpdate oldCaretUpdate = new CaretUpdate(pCaretHandler.getDot(), pCaretHandler.getMark());
		participantCaretMap.firePropertyChange(REMOVED_PROPERTY, oldCaretUpdate, null);

		// clean up listeners
		mapParticipantItem.cleanUp();

		// set color from left participant to grey
		Style pStyle = cDocument.getStyle(pId);
		StyleConstants.setBackground(pStyle, new Color(0xDD, 0xDD, 0xDD));
	}	
	
	public void receiveCaretUpdate(Participant participant, CaretUpdate update) {
		String pId = "" + participant.getParticipantId();
		PropertyChangeCaretHandlerImpl pCaretHandler = (PropertyChangeCaretHandlerImpl)participantCaretMap.get(pId);
		pCaretHandler.setCaret(update.getDot(), update.getMark());
	}

	public void receiveOperation(Participant participant, Operation operation) {
		String pId = "" + participant.getParticipantId();
		
		PropertyChangeCaretHandlerImpl pCaretHandler = (PropertyChangeCaretHandlerImpl)participantCaretMap.get(pId);

		// apply operation
		Style pStyle = cDocument.getStyle(pId);
		applyOperation(operation, pStyle);
	}

	public PropertyChangeHashMap getCaretHandlerMap() {
		return participantCaretMap;
	}
	
	public HashMap getParticipationTextColorMap() {
		return participantTextColorMap;
	}
	
	public HashMap getParticipationCursorColorMap() {
		return participantCursorColorMap;
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
	
	public void sessionFailed(final int reason, Exception e) {
		e.printStackTrace();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// create local copy of the document
				DocumentItem newItem = new DocumentItem(documentItem.getTitle() + "(failed)", dialogController);
		
				newItem.setEditorDocument(documentItem.createEditorDocumentCopy());
		
				// add item to document view
				viewController.addDocument(newItem);
		
				// set type
				documentItem.setType(DocumentItem.REMOTE);

				String user = "\"" + documentItem.getPublisher() + "\"";
				String title = "\"" + documentItem.getTitle() + "\"";
				dialogController.showSessionFailed(user, title, reason);
			}
		});
	}
	
	public EventList getParticipantSourceList() {
		return participantSourceList;
	}
	
	protected Color participantColorJoined(String pId) {
		Color pTextColor;
		if(participantTextColorMap.containsKey(pId)) {
			// return old participant color
			pTextColor = (Color)participantTextColorMap.get(pId);
		} else {
			// next participant color
			pTextColor = defaultParticipantTextColors[participantCount];
			participantTextColorMap.put(pId, pTextColor);
			
			// save cursor color too
			Color pCursorColor = defaultParticipantCursorColors[participantCount++%8];
			participantCursorColorMap.put(pId, pCursorColor);
		}
		return pTextColor;
	}
	
	protected void participantColorLeft(String pId) {
		// do nothing on color map (save color)
	}

}