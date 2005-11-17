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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

import org.apache.commons.io.FileUtils;

import ca.odell.glazedlists.EventList;
import ch.iserver.ace.collaboration.CollaborationService;
import ch.iserver.ace.util.ParameterValidator;



public class DocumentManager implements ItemSelectionChangeListener {
	
	private static int counter = 1;
	
	private DocumentViewController documentController;
	private CollaborationService collaborationService;
	private DocumentItem currentDocumentItem;

	public DocumentManager(DocumentViewController documentController) {
		documentController.addItemSelectionChangeListener(this);
		this.documentController = documentController;
	}
	
	public EventList getDocuments() {
		return documentController.getDocumentSourceList();
	}

	public DocumentItem getSelectedDocument() {
		return currentDocumentItem;
	}

	public boolean isSelectedDocumentDirty() {
		return currentDocumentItem.isDirty();
	}
	
	public List getDirtyDocuments() {
		List result = new LinkedList();
		
		EventList source = getDocuments();
		source.getReadWriteLock().readLock().lock();
		try {
			Iterator it = source.iterator();
			while (it.hasNext()) {
				DocumentItem item = (DocumentItem) it.next();
				if (item.isDirty()) {
					result.add(item);
				}
			}
		} finally {
			source.getReadWriteLock().readLock().unlock();
		}
		
		return result;
	}

	public void newDocument() {
		DocumentItem newItem = new DocumentItem("Untitled Document " + counter++);
		documentController.addDocument(newItem);
		documentController.setSelectedIndex(documentController.indexOf(newItem));
	}

	public void openDocument(File file) throws IOException {
		DocumentItem item = findDocumentForFile(file);
		if (item != null) {
			documentController.setSelectedItem(item);
		} else {
			item = new DocumentItem(file);
			// TODO: encoding
			String content = FileUtils.readFileToString(file, null);
			try {
				item.getEditorDocument().insertString(0, content, null);
			} catch (BadLocationException e) {
				throw new RuntimeException("unexpected code path exception");
			}
			documentController.addDocument(item);
			documentController.setSelectedIndex(documentController.indexOf(item));
			item.setClean();
		}
	}

	protected DocumentItem findDocumentForFile(File file) {
		getDocuments().getReadWriteLock().readLock().lock();
		try {
			Iterator it = getDocuments().iterator();
			while (it.hasNext()) {
				DocumentItem item = (DocumentItem) it.next();
				if (item.hasBeenSaved() && file.equals(item.getFile())) {
					return item;
				}
			}
		} finally {
			getDocuments().getReadWriteLock().readLock().unlock();
		}
		return null;
	}
	
	public void saveDocument(DocumentItem item) throws IOException {
		ParameterValidator.notNull("item", item);
		if (!item.hasBeenSaved()) {
			throw new IllegalArgumentException("item has no fail name, save impossible");
		}
		saveAsDocument(item.getFile(), item);
	}
	
	public void saveAsDocument(File file, DocumentItem item) throws IOException {
		ParameterValidator.notNull("file", file);
		ParameterValidator.notNull("item", item);
		
		AbstractDocument doc = (AbstractDocument) item.getEditorDocument();
		String content;
		doc.readLock();
		try {
			content = doc.getText(0, doc.getLength());
		} catch (BadLocationException e) {
			throw new RuntimeException("unexpected code path exception");
		} finally {
			doc.readUnlock();
		}
		// TODO: encoding
		FileUtils.writeStringToFile(file, content, null);
		item.setFile(file);
		item.setClean();
	}
	
	public void closeDocument(DocumentItem item) {
		// closes the current document
		if (item.getType() == DocumentItem.PUBLISHED) {
			// conceal a published document before closing
			concealDocument();
		}
		documentController.removeDocument(documentController.getSelectedDocumentItem());
		documentController.setSelectedIndex(documentController.getDocumentSourceList().size()-1);
	}

	public void closeAllDocuments() {
		EventList documents = getDocuments();
		documents.getReadWriteLock().readLock().lock();
		try {
			Iterator it = documents.iterator();
			while (it.hasNext()) {
				DocumentItem item = (DocumentItem) it.next();
				documentController.removeDocument(item);
			}
		} finally {
			documents.getReadWriteLock().readLock().unlock();
		}
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