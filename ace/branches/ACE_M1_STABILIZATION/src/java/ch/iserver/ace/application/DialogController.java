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
import java.util.List;
import java.util.Map;

import ch.iserver.ace.application.dialog.DialogResult;

/**
 * Interface to be implemented by that class that is responsible to
 * display the dialogs of the application. This is factored out into
 * an interface to facilitate automatic unit testing of the application
 * logic (handle close, save, save all, open, failures, ...).
 */
public interface DialogController {
	
	/**
	 * Shows a file chooser for saving the document with the given 
	 * title. Returns a dialog result whose option specifies whether
	 * the user pressed cancel or save. The {@link DialogResult#getResult()}
	 * method returns a File object if the user decided the 
	 * document.
	 * 
	 * @param title the title of the document to save
	 * @return a dialog result containing the relevant information
	 */
	DialogResult showSaveDocument(String title);
	
	/**
	 * Shows a message dialog that notifies the user about a failed 
	 * save attempt.
	 * 
	 * @param item the document for which the save failed
	 * @param e the exception thrown while saving
	 */
	void showSaveFailed(DocumentItem item, Exception e);
	
	/**
	 * Shows a dialog with a list of documents that could not be
	 * saved. The passed in Map has keys of type DocumentItem and values
	 * of type Exception.
	 * 
	 * @param failed a map containing document items and corresponding
	 *               exceptions
	 */
	void showSaveFilesFailed(Map failed);
	
	/**
	 * Shows a dialog with a list of files that could not be
	 * opened. The passed in Map has String keys (filenames) and
	 * values of type Exception.
	 * 
	 * @param failed the list of documents that could not be opened
	 */
	void showOpenFilesFailed(Map failed);
	
	/**
	 * Shows a dialog that allows to select from a list of candidate
	 * documents to save. The method returns a dialog result whose
	 * option conveys the desired user action (cancel or proceed).
	 * The {@link DialogResult#getResult()} object is an array of
	 * File instances, representing the list of selected documents.
	 * 
	 * @param candidates the list of candidates to select from
	 * @return a dialog result with the results of the dialog
	 */
	DialogResult showSaveFilesDialog(List candidates);
	
	/**
	 * Shows a message dialog that tells the user that the given file does
	 * not exist.
	 * 
	 * @param file the file that does not exist
	 */
	void showFileDoesNotExist(File file);
	
	/**
	 * Shows a message dialog that tells the user that the given file is
	 * a directory and not a file (as expected by the application).
	 * 
	 * @param file the file that is a directory
	 */
	void showFileIsDirectory(File file);
	
	/**
	 * Shows a confirmation dialog that asks the user for confirmation
	 * about the closing of a dirty document. The returned value conveys
	 * the user's desired action, i.e. either closing the document anyway or
	 * not closing the document.
	 * 
	 * @param title the title of the dirty document about to be closed
	 * @return JOptionPane.OK_OPTION or JOptionPane.CANCEL_OPTION
	 */
	int showConfirmCloseDirty(String title);
	
	/**
	 * Shows a file chooser that allows the user to open one or more documents.
	 * The returned result has as option the users action (cancel or open)
	 * and if the action is open then it the result of the DialogResult is
	 * an array of File objects.
	 * 
	 * @return the dialogs result
	 */
	DialogResult showOpenDocuments();
	
	/**
	 * Shows a message dialog that tells the user that the given file could
	 * not be opened.
	 * 
	 * @param file the file that could not be opened
	 */
	void showOpenFailed(File file);

	/**
	 * Shows a confirmation dialog that asks the user if it is OK to overwrite
	 * the given file, even though it already exists.
	 * 
	 * @param file the file about to be overwritten
	 * @return the users decision (either OK or CANCEL)
	 */
	int showConfirmOverwrite(File file);

	/**
	 * Shows an about dialog.
	 */
	void showAbout();

	/**
	 * Shows the preferences dialog.
	 */
	void showPreferences();

	/**
	 * Shows a dialog that allows the user to enter an IP address. This IP
	 * address is returned as part of a ServerInfo object as result of the
	 * DialogResult object.
	 * 
	 * @return the result of the dialog
	 */
	DialogResult showDiscoverUser();
	
}
