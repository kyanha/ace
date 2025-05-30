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

import java.awt.Color;
import java.util.HashMap;

import javax.swing.SwingUtilities;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.algorithm.Operation;
import ch.iserver.ace.algorithm.text.DeleteOperation;
import ch.iserver.ace.algorithm.text.InsertOperation;
import ch.iserver.ace.algorithm.text.NoOperation;
import ch.iserver.ace.algorithm.text.SplitOperation;
import ch.iserver.ace.application.editor.CollaborativeDocument;
import ch.iserver.ace.application.editor.DocumentLock;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.SessionCallback;
import ch.iserver.ace.util.Lock;





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

	
	private final Lock lock;

	
	private int participantCount = 0;

	private Color[] defaultParticipantTextColors = {
		new Color(230, 154, 154),
		new Color(230, 191, 115), new Color(191, 230, 115),
		new Color(115, 230, 115), new Color(115, 230, 191),
		new Color(154, 154, 230), new Color(191, 115, 230)
	};

	private Color[] defaultParticipantCursorColors = {
		new Color(230, 30, 30),
		new Color(230, 163, 30), new Color(163, 230, 30),
		new Color(30, 230, 30), new Color(30, 230, 163),
		new Color(97, 97, 230), new Color(163, 30, 230)
	};

	private int defaultColorAmount = 7;
	
	


	public SessionCallbackImpl(DocumentItem documentItem, DialogController dialogController) {
		this.dialogController = dialogController;
		participantSourceList = new BasicEventList();
		participantItemMap = new HashMap();		
		participantCaretMap = new PropertyChangeHashMapImpl();
		participantTextColorMap = new HashMap();
		participantCursorColorMap = new HashMap();
		this.documentItem = documentItem;
		cDocument = documentItem.getEditorDocument();
		this.lock = new DocumentLock(cDocument);
	}
	
	public Lock getLock() {
		return lock;
	}
	
	public void setParticipantId(int participantId) {
		//System.out.println("setting the local participant id: " + participantId);		
		this.participantId = participantId;
		mpId = "" + participantId;
	}
	
	public void participantJoined(Participant participant) {
		String pId = "" + participant.getParticipantId();

		// UPDATE MAP
		Color pColor = participantColorJoined(pId);
		ParticipantItem mapParticipantItem = new ParticipantItem(participant, pColor);
		participantItemMap.put(pId, mapParticipantItem);
		
		participantSourceList.getReadWriteLock().writeLock().lock();
		try {
			participantSourceList.add(mapParticipantItem);
		} finally {
			participantSourceList.getReadWriteLock().writeLock().unlock();
		}
		//System.out.println("participantJoined: " + participant);
		
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
		
		participantSourceList.getReadWriteLock().writeLock().lock();
		try {
			participantSourceList.remove(mapParticipantItem);
		} finally {
			participantSourceList.getReadWriteLock().writeLock().unlock();
		}
		//System.out.println("participantLeft: " + participant + "   code: " + code);
		
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
			//System.out.println("split... " + op);
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
			//System.out.println("no operation..." + op);
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
			Color pCursorColor = defaultParticipantCursorColors[participantCount++%defaultColorAmount];
			participantCursorColorMap.put(pId, pCursorColor);
		}
		return pTextColor;
	}
	
	protected void participantColorLeft(String pId) {
		// do nothing on color map (save color)
	}

}