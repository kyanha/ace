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

import ca.odell.glazedlists.EventList;

/**
 * 
 */
public interface DocumentManager {

	/**
	 * Gets the list of open documents.
	 * 
	 * @return the list of open documents
	 */
	EventList getDocuments();

	/**
	 * Gets the document for the given file. If there is no document open
	 * for the given file, this method returns null.
	 * 
	 * @param file the file for which to look for an open document
	 * @return the DocumentItem containing the file's content or null if
	 *         there is none
	 */
	DocumentItem getDocumentForFile(File file);
	
	/**
	 * Gets the currently selected document. This method returns null if
	 * there is no currently selected document.
	 * 
	 * @return the currently selected document or null if there is no document
	 *         selected
	 */
	DocumentItem getSelectedDocument();
	
	/**
	 * Sets the currently selected document.
	 */
	void setSelectedDocument(DocumentItem item);

	/**
	 * Gets a list containing all the dirty documents.
	 * 
	 * @return the list of dirty documents
	 */
	EventList getDirtyDocuments();

	/**
	 * Creates a new untitled document.
	 */
	void newDocument();

	/**
	 * Opens the given file in a new editor document.
	 * 
	 * @param file the file to open
	 * @throws IOException if the exception fails
	 */
	void openDocument(File file) throws IOException;

	/**
	 * Saves the given document to the associated file. It is an error
	 * to call this method with an item that has never been saved before.
	 * 
	 * @param item the document to save
	 * @throws IOException if there is a problem saving the document
	 * @throws IllegalArgumentException if the item is null or has not been
	 *         saved
	 * @see DocumentItem#hasBeenSaved()
	 */
	void saveDocument(DocumentItem item) throws IOException;

	/**
	 * Saves a document into a new file. The file will be overwritten if it
	 * exists. The file is associated with the item, further calls to
	 * {@link #saveDocument(DocumentItem)} save the document to the new
	 * file.
	 * 
	 * @param file where to save the document
	 * @param item the document item to save
	 * @param copy TODO
	 * @throws IOException in case of IO problems
	 */
	void saveDocumentAs(File file, DocumentItem item, boolean copy) throws IOException;

	/**
	 * Closes the given document item. Unsaved changes will be discarded,
	 * so be careful to ask the user for confirmation. If the document has
	 * been published, it is concealed.
	 * 
	 * @param item the document to close
	 */
	void closeDocument(DocumentItem item);

	/**
	 * Closes all documents. The documents are concealed first if they were
	 * published. 
	 */
	void closeAllDocuments();

	/**
	 * Publishes the currently selected document.
	 */
	void publishDocument();

	/**
	 * Conceals the currently selected document.
	 */
	void concealDocument();
	
	void joinSession(DocumentItem item);
	
	void leaveSession();
	
}