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

/**
 * The Application Controller is the central class handling application level
 * events like for instance quit, showing different dialogs, saving, opening, 
 * and closing documents.
 * 
 * <p>The ApplicationController is further used by Customizer classes. For
 * instance on Mac OS X some methods in this interface are used to improve
 * the user experience on OS X.</p>
 */
public interface ApplicationController {
	
	void setApplicationTerminator(ApplicationTerminator terminator);
	
	/**
	 * Shows the applications about dialog.
	 */
	void showAbout();
	
	/**
	 * Shows the preferences dialog.
	 */
	void showPreferences();

	/**
	 * Opens the passed in file in a new editor document.
	 * 
	 * @param filename the filename of the file to open
	 */
	void openFile(String filename);
	
	/**
	 * Terminates the application. An implementation will typcially ask
	 * the user about unsaved changes, thus this method does not have to
	 * result in a shutdown because the user might cancel the shutdown
	 * process.
	 */
	void quit();
	
	/**
	 * Closes the currently selected document, if any. The user should
	 * be asked to save unsaved changes.
	 */
	void closeDocument();
	
	/**
	 * Closes all the documents. The user should be asked to save unsaved
	 * changes.
	 */
	void closeAllDocuments();

	/**
	 * Shows a file open dialog which lets the user choose one or more files
	 * to open in new editor documents.
	 */
	void openDocument();
	
	/**
	 * Saves the currently selected document. If the document has never been
	 * saved before, this method typically asks the user to set the filename
	 * in a file save dialog.
	 */
	void saveDocument();
	
	/**
	 * Saves the document under another name.
	 */
	void saveDocumentAs();

	/**
	 * Saves a copy of the currently selected document to a file.
	 */
	void saveDocumentCopy();
	
	/**
	 * Saves all the open and changed files.
	 */
	void saveAllDocuments();
	
	/**
	 * Shows a dialog which lets the user enter an IP address of another
	 * user. After this dialog closes, an explicit discovery request
	 * is started.
	 */
	void discoverUser();
	
}
