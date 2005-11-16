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

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import ch.iserver.ace.application.dialog.AboutDialog;
import ch.iserver.ace.application.dialog.PreferencesDialog;
import ch.iserver.ace.application.dialog.SaveFilesDialog;
import ch.iserver.ace.application.dialog.TitledDialog;
import ch.iserver.ace.application.preferences.PreferencesStore;

/**
 * 
 */
public class ApplicationControllerImpl implements ApplicationController {

	private TitledDialog aboutDialog;

	private TitledDialog preferencesDialog;

	private Frame mainFrame;

	private LocaleMessageSource messages;

	private PreferencesStore preferences;

	private DocumentManager documentManager;

	public void setMainFrame(Frame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public Frame getMainFrame() {
		return mainFrame;
	}

	public void setMessages(LocaleMessageSource messages) {
		this.messages = messages;
	}

	public LocaleMessageSource getMessages() {
		return messages;
	}

	public PreferencesStore getPreferences() {
		return preferences;
	}

	public void setPreferences(PreferencesStore preferences) {
		this.preferences = preferences;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public DocumentManager getDocumentManager() {
		return documentManager;
	}

	public void showAbout() {
		if (aboutDialog == null) {
			aboutDialog = new AboutDialog(getMainFrame(), getMessages());
		}
		aboutDialog.showDialog();
	}

	public void showPreferences() {
		if (preferencesDialog == null) {
			preferencesDialog = new PreferencesDialog(getMainFrame(),
							getMessages(), getPreferences());
		}
		preferencesDialog.showDialog();
	}

	public void quit() {
		List dirty = getDocumentManager().getDirtyDocuments();
		
		if (dirty.size() > 0) {
			try {
				// TODO: call save all on each document
				if (saveAllItems()) {
					getDocumentManager().closeAllDocuments();
					System.exit(0);
				}
			} catch (IOException e) {
				// handle exceptions
			}
		} else {
			System.exit(0);
		}
	}

	public void closeDocument() {
		DocumentItem item = getDocumentManager().getSelectedDocument();
		if (item != null && item.isDirty()) {
			String title = getMessages().getMessage("dConfirmCloseDirtyTitle");
			String message = getMessages().getMessage("dConfirmCloseDirtyMessage");
			
			int option = JOptionPane.showConfirmDialog(
							getMainFrame(), 
							message, 
							title, 
							JOptionPane.YES_NO_CANCEL_OPTION);
			
			if (option == JOptionPane.YES_OPTION) {
				try {
					if (saveItem(item)) {
						getDocumentManager().closeDocument(item);
					}
				} catch (IOException e) {
					saveFailed(item, e);
				}
			} else if (option == JOptionPane.NO_OPTION) {
				getDocumentManager().closeDocument(item);
			}			
		}
	}

	public void openDocument() {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(getMainFrame())) {
			File[] files = chooser.getSelectedFiles();
			try {
				// TODO: call open document on each document
				getDocumentManager().openDocuments(files);
			} catch (IOException e) {
				//openFailed(files, e);
			}
		}
	}

	public void openFile(String filename) {
		File file = new File(filename);
		if (!file.exists() || file.isDirectory()) {
			// TODO: warning?
		} else {
			try {
				getDocumentManager().openDocument(new File(filename));
			} catch (IOException e) {
				openFailed(file, e);
			}
		}
	}

	public void saveDocument() {
		DocumentItem item = getDocumentManager().getSelectedDocument();
		if (item.isDirty()) {
			try {
				saveItem(item);
			} catch (IOException e) {
				saveFailed(item, e);
			}
		}
	}

	public void saveDocumentAs() {
		DocumentItem item = getDocumentManager().getSelectedDocument();
		if (item != null) {
			try {
				saveItemAs(item);
			} catch (IOException e) {
				saveFailed(item, e);
			}
		}
	}

	public void saveAllDocuments() {
	}

	public void discoverUser() {
		// TODO: unimplemented method: discoverUser()
	}
	
	// --> internal methods <--
	
	protected boolean saveItem(DocumentItem item) throws IOException {
		if (item.hasBeenSaved()) {
			getDocumentManager().saveDocument(item);
			return true;
		} else {
			return saveItemAs(item);
		}
	}
	
	protected boolean saveItemAs(DocumentItem item) throws IOException {
		JFileChooser chooser = new JFileChooser();
		if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(getMainFrame())) {
			File file = chooser.getSelectedFile();
			getDocumentManager().saveAsDocument(file, item);
			return true;
		} else {
			return false;
		}
	}
	
	protected boolean saveAllItems() throws IOException {
		List dirty = getDocumentManager().getDirtyDocuments();
		SaveFilesDialog saveFilesDialog = new SaveFilesDialog(
						getMainFrame(),
						getMessages(), 
						dirty);
		saveFilesDialog.showDialog();
		if (saveFilesDialog.getOption() == SaveFilesDialog.OK_OPTION) {
			Set saveSet = saveFilesDialog.getCheckedFiles();
			Iterator it = saveSet.iterator();
			while (it.hasNext()) {
				DocumentItem item = (DocumentItem) it.next();
				try {
					saveItem(item);
				} catch (IOException e) {
					saveFailed(item, e);
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	private void openFailed(File file, Exception e) {
		String title = getMessages().getMessage("dLoadFailedTitle");
		String message = "";
		JOptionPane.showMessageDialog(
						getMainFrame(),
						title,
						message,
						JOptionPane.ERROR);
	}
	
	private void saveFailed(DocumentItem item, Exception e) {
		String title = getMessages().getMessage("dSaveFailedTitle");
		String message = "";
		JOptionPane.showMessageDialog(
						getMainFrame(),
						title,
						message,
						JOptionPane.ERROR);
	}

}
