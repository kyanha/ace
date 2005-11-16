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

import ch.iserver.ace.application.*;
import ch.iserver.ace.collaboration.*;

import java.io.File;
import java.util.List;



public class DocumentManager implements ItemSelectionChangeListener {

	private DocumentViewController documentController;
	private CollaborationService collaborationService;
	private DocumentItem currentDocumentItem;

	public DocumentManager(DocumentViewController documentController) {
		documentController.addItemSelectionChangeListener(this);
		this.documentController = documentController;
	}





	public boolean isSelectedDocumentDirty() {
		// return if the selected document is dirty
		return false;
	}
	
	public List getDirtyDocuments() {
		// returns a list of local & published documents (DocumentItem) that have changes
		return null;
	}





	public void newDocument() {
		// create new local document
		DocumentItem newItem = new DocumentItem("New Document");
		documentController.addDocument(newItem);
		documentController.setSelectedIndex(documentController.indexOf(newItem));
	}

	public void openDocument(File filename) {
		// open a file
	}

	public void openDocuments(File[] filenames) {
		// open a list of files
	}

	public void saveDocument() {
		// save the current document
	}

	public void saveDocument(DocumentItem item) {
		// save the given document
	}
	
	public void saveAsDocument(File file) {
		// saves the current document with the new filename
	}
	
	public void closeDocument() {
		// closes the current document
		if(currentDocumentItem.getType() == DocumentItem.PUBLISHED) {
			// conceal a published document before closing
			concealDocument();
		}
		documentController.removeDocument(documentController.getSelectedDocumentItem());
		documentController.setSelectedIndex(documentController.getDocumentSourceList().size()-1);
	}

	public void exitApplication() {
		// all documents are allready saved -> conceal all published documents
	}


	
	
	public void publishDocument() {
		// publish the selected document
		currentDocumentItem.publish(collaborationService);
	}
	
	public void concealDocument() {
		// conceal the selected document
		currentDocumentItem.conceal();
	}
	
	public void sessionJoined(DocumentItem item) {
		// called from JoinCallbackImpl when join request is accepted
		
	}
	
	public void leaveSession() {
		// leave the current session

		// 1. document
		//currentDocumentItem.leave();
	}
	
	public void inviteUser() {
		// invite the selected user
		
		// 1. user allready in session?
	}
	
	public void kickParticipant() {
		// kick the selected participant
	}
	
	
	
	
		
	public void itemSelectionChanged(ItemSelectionChangeEvent e) {
		// set the actual document
		currentDocumentItem = (DocumentItem)e.getItem();
	}

	public void setCollaborationService(CollaborationService collaborationService) {
		this.collaborationService = collaborationService;
	}

}