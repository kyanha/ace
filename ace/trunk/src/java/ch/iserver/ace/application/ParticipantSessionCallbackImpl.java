/*
 * $Id:PublishedSessionCallbackImpl.java 1091 2005-11-09 13:29:05Z zbinl $
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

import ch.iserver.ace.Fragment;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.PortableDocument;
import ch.iserver.ace.collaboration.ParticipantSessionCallback;
import ch.iserver.ace.application.editor.*;
import ch.iserver.ace.CaretUpdate;

import java.awt.Color;
import java.util.Iterator;
import javax.swing.event.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ch.iserver.ace.util.CaretHandler;




public class ParticipantSessionCallbackImpl extends SessionCallbackImpl implements ParticipantSessionCallback {

	//private DocumentViewController viewController;

	public ParticipantSessionCallbackImpl(DocumentItem documentItem, DocumentViewController viewController,
			DialogController dialogController) {
		super(documentItem, dialogController);
		this.viewController = viewController;
	}
	
	public synchronized void setDocument(PortableDocument doc) {
		// IMPORTANT: this method is called first from the collaboration layer. set participants here.
		// add all participants

		//add participants
		Iterator pIter = doc.getParticipants().iterator();
		while(pIter.hasNext()) {
			Participant participant = (Participant)pIter.next();
			String pId = "" + participant.getParticipantId();
			//System.out.println("found participant: " + participant);
			if(!pId.equals(mpId)) {
				
				Color pColor = participantColorJoined(pId);
				ParticipantItem mapParticipantItem = new ParticipantItem(participant, pColor);
				participantItemMap.put(pId, mapParticipantItem);
				participantSourceList.add(mapParticipantItem);
				
				Style pStyle = cDocument.addStyle(pId, null);
				StyleConstants.setBackground(pStyle, pColor);
			}
		}

		// get document fragments
		Iterator fIter = doc.getFragments();
		int insertPos = 0;
		while(fIter.hasNext()) {
			Fragment fragment = (Fragment)fIter.next();
			// get participant style
			Style pStyle = cDocument.getStyle("" + fragment.getParticipantId());
			try {
				cDocument.insertString(insertPos, fragment.getText(), pStyle);
			} catch(Exception e) {
				e.printStackTrace();
			}
			insertPos += fragment.getText().length();
		}

		// add carets
		pIter = doc.getParticipants().iterator();
		while(pIter.hasNext()) {
			Participant participant = (Participant)pIter.next();
			String pId = "" + participant.getParticipantId();
			//System.out.println("found participant: " + participant);
			if(!pId.equals(mpId)) {
			
				// caret handler
				CaretUpdate pCaretUpdate = doc.getSelection(participant.getParticipantId());
				PropertyChangeCaretHandlerImpl pCaretHandler =
					new PropertyChangeCaretHandlerImpl(pCaretUpdate.getDot(), pCaretUpdate.getMark());
				cDocument.addDocumentListener(pCaretHandler);
				participantCaretMap.put(pId, pCaretHandler);
			}
		}
		
		// own caret
		PropertyChangeCaretHandlerImpl pCaretHandler = new PropertyChangeCaretHandlerImpl(0, 0);
		cDocument.addDocumentListener(pCaretHandler);
		participantCaretMap.put(mpId, pCaretHandler);
	}
	
	public void sessionTerminated() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// create local copy of the document
				DocumentItem newItem = new DocumentItem(documentItem.getTitle() + "(terminated)", dialogController);
		
				newItem.setEditorDocument(documentItem.createEditorDocumentCopy());
		
				// add item to document view
				viewController.addDocument(newItem);
		
				// set type
				documentItem.setType(DocumentItem.REMOTE);

				// show info dialog
				String user = "\"" + documentItem.getPublisher() + "\"";
				String title = "\"" + documentItem.getTitle() + "\"";
				dialogController.showSessionTerminated(user, title);
			}
		});
	}
	
	public void kicked() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// create local copy of the document
				DocumentItem newItem = new DocumentItem(documentItem.getTitle() + "(kicked)", dialogController);

				newItem.setEditorDocument(documentItem.createEditorDocumentCopy());

				// add item to document view
				viewController.addDocument(newItem);

				// set type
				documentItem.setType(DocumentItem.REMOTE);

				// show info dialog
				String user = "\"" + documentItem.getPublisher() + "\"";
				String title = "\"" + documentItem.getTitle() + "\"";
				dialogController.showKicked(user, title);
			}
		});
	}
	
}