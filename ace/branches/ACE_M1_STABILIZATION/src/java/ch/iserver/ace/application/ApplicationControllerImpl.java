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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import ch.iserver.ace.application.dialog.DialogResult;
import ch.iserver.ace.application.dialog.SaveFilesDialog;
import ch.iserver.ace.collaboration.CollaborationService;

/**
 * 
 */
public class ApplicationControllerImpl implements ApplicationController {
	
	private CollaborationService collaborationService;
	
	private DocumentManager documentManager;

	private DialogController dialogController;

	public void setCollaborationService(CollaborationService collaborationService) {
		this.collaborationService = collaborationService;
	}
	
	public CollaborationService getCollaborationService() {
		return collaborationService;
	}
	
	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public DocumentManager getDocumentManager() {
		return documentManager;
	}
	
	public void setDialogController(DialogController dialogController) {
		this.dialogController = dialogController;
	}
	
	public DialogController getDialogController() {
		return dialogController;
	}

	public void showAbout() {
		getDialogController().showAbout();
	}

	public void showPreferences() {
		getDialogController().showPreferences();
	}

	public void quit() {
		Map failed = new TreeMap();
		List dirty = getDocumentManager().getDirtyDocuments();	
		
		if (dirty.size() == 0) {
			shutdown();
			
		} else {			
			DialogResult result = getDialogController().showSaveFilesDialog(dirty);
			
			if (result.getOption() == SaveFilesDialog.OK_OPTION) {
				Set saveSet = (Set) result.getResult();
			
				if (saveSet.size() == 0) {
					shutdown();
				}
			
				Iterator it = saveSet.iterator();
				while (it.hasNext()) {
					DocumentItem item = (DocumentItem) it.next();
					try {
						if (saveItem(item)) {
							getDocumentManager().closeDocument(item);
						}
					} catch (IOException e) {
						failed.put(item, e);
					}
				}
			
				// display files that cannot be saved
				if (failed.size() > 0) {
					getDialogController().showSaveFilesFailed(failed);
				
				// only if there are no more open documents... 
				} else if (getDocumentManager().getDocuments().size() == 0) {
					shutdown();
				}
			}
		}
	}

	public void closeDocument() {
		DocumentItem item = getDocumentManager().getSelectedDocument();
		if (item != null && item.isDirty()) {			
			int option = getDialogController().showConfirmCloseDirty();
			
			if (option == JOptionPane.YES_OPTION) {
				try {
					if (saveItem(item)) {
						getDocumentManager().closeDocument(item);
					}
				} catch (IOException e) {
					getDialogController().showSaveFailed(item, e);
				}
			} else if (option == JOptionPane.NO_OPTION) {
				getDocumentManager().closeDocument(item);
			}			
		} else {
			getDocumentManager().closeDocument(item);
		}
	}

	public void openDocument() {
		DialogResult result = getDialogController().showOpenDocuments();
		int option = result.getOption();
		if (JFileChooser.APPROVE_OPTION == option) {
			Map failed = new TreeMap();
			File[] files = (File[]) result.getResult();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				try {
					getDocumentManager().openDocument(file);
				} catch (IOException e) {
					failed.put(file.getAbsolutePath(), e);
				}
			}
			
			// display files that cannot be opened
			if (failed.size() > 0) {
				getDialogController().showOpenFilesFailed(failed);
			}
		}
	}

	public void openFile(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			getDialogController().showFileDoesNotExist(file);
		} else if (!file.isDirectory()) {
			getDialogController().showFileIsDirectory(file);
		} else {
			try {
				getDocumentManager().openDocument(file);
			} catch (IOException e) {
				getDialogController().showOpenFailed(file);
			}
		}
	}

	public void saveDocument() {
		DocumentItem item = getDocumentManager().getSelectedDocument();
		if (item.isDirty() || !item.hasBeenSaved()) {
			try {
				saveItem(item);
			} catch (IOException e) {
				getDialogController().showSaveFailed(item, e);
			}
		}
	}

	public void saveDocumentAs() {
		DocumentItem item = getDocumentManager().getSelectedDocument();
		if (item != null) {
			try {
				saveItemAs(item);
			} catch (IOException e) {
				getDialogController().showSaveFailed(item, e);
			}
		}
	}

	public void saveAllDocuments() {
		Map failed = new TreeMap();
		Iterator it = getDocumentManager().getDirtyDocuments().iterator();
		while (it.hasNext()) {
			DocumentItem item = (DocumentItem) it.next();
			try {
				saveItem(item);
			} catch (IOException e) {
				failed.put(item, e);
			}
		}
		
		// display files that cannot be saved
		if (failed.size() > 0) {
			getDialogController().showSaveFilesFailed(failed);
		}
	}

	public void discoverUser() {
		DialogResult result = getDialogController().showDiscoverUser();
		
		if (result.getOption() == JOptionPane.OK_OPTION) {
			/*ServerInfo info = (ServerInfo)*/ result.getResult();
			// TODO: implement discovery
		}
	}
	
	// --> internal methods <--
	
	/**
	 * Initiates the shutdown of the application. This method will call
	 * System.exit. There is no way to stop that!
	 */
	protected void shutdown() {
		getDocumentManager().closeAllDocuments();
		System.exit(0);
	}
	
	protected boolean saveItem(DocumentItem item) throws IOException {
		if (item.hasBeenSaved()) {
			getDocumentManager().saveDocument(item);
			return true;
		} else {
			return saveItemAs(item);
		}
	}
	
	protected boolean saveItemAs(DocumentItem item) throws IOException {
		DialogResult result = getDialogController().showSaveDocument(item.getTitle());
		if (JFileChooser.APPROVE_OPTION == result.getOption()) {
			File file = (File) result.getResult();
			if (file.exists()) {
				int option = getDialogController().showConfirmOverwrite(file);
				if (option == JOptionPane.YES_OPTION) {
					getDocumentManager().saveAsDocument(file, item);
					return true;
				} else {
					return false;
				}
			} else {
				getDocumentManager().saveAsDocument(file, item);
				return true;
			}
		} else {
			return false;
		}
	}

}
