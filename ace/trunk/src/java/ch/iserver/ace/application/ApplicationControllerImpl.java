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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import ca.odell.glazedlists.EventList;
import ch.iserver.ace.application.dialog.DialogResult;
import ch.iserver.ace.application.dialog.SaveFilesDialog;
import ch.iserver.ace.collaboration.CollaborationService;
import ch.iserver.ace.util.CompareUtil;

/**
 * 
 */
public class ApplicationControllerImpl implements ApplicationController, ApplicationContextAware {
	
	private ConfigurableApplicationContext context;
	
	private CollaborationService collaborationService;
	
	private DocumentManager documentManager;

	private DialogController dialogController;

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = (ConfigurableApplicationContext) context;
	}
	
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

	/**
	 * @see ch.iserver.ace.application.ApplicationController#showAbout()
	 */
	public void showAbout() {
		getDialogController().showAbout();
	}

	/**
	 * @see ch.iserver.ace.application.ApplicationController#showPreferences()
	 */
	public void showPreferences() {
		getDialogController().showPreferences();
	}

	/**
	 * @see ch.iserver.ace.application.ApplicationController#quit()
	 */
	public void quit() {
		Map failed = new TreeMap();
		EventList dirty = getDocumentManager().getDirtyDocuments();	
		
		dirty.getReadWriteLock().writeLock().lock();
		try {
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
								it.remove();
							}
						} catch (IOException e) {
							failed.put(item, e);
						}
					}
			
					// display files that cannot be saved
					if (failed.size() > 0) {
						getDialogController().showSaveFilesFailed(failed);
				
					// only if there are no more open documents... 
					} else if (saveSet.size() == 0){
						shutdown();
					}
				}
			}
		} finally {
			dirty.getReadWriteLock().writeLock().unlock();
		}
	}

	/**
	 * @see ch.iserver.ace.application.ApplicationController#closeDocument()
	 */
	public void closeDocument() {
		DocumentItem item = getDocumentManager().getSelectedDocument();
		try {
			if (item != null && item.isDirty()) {
				int option = getDialogController().showConfirmCloseDirty(item.getTitle());
				
				if (option == JOptionPane.YES_OPTION) {
					if (saveItem(item)) {
						getDocumentManager().closeDocument(item);
					}
				} else if (option == JOptionPane.NO_OPTION) {
					getDocumentManager().closeDocument(item);
				}
			} else {
				getDocumentManager().closeDocument(item);
			}
		} catch (IOException e) {
			getDialogController().showSaveFailed(item, e);
		}
	}
	
	/**
	 * @see ch.iserver.ace.application.ApplicationController#closeAllDocuments()
	 */
	public void closeAllDocuments() {
		List closeList = new ArrayList();
		getDocumentManager().getDocuments().getReadWriteLock().readLock().lock();
		
		try {
			Iterator it = getDocumentManager().getDocuments().iterator();
			while (it.hasNext()) {
				DocumentItem item = (DocumentItem) it.next();
				if (item.isDirty()) {
					try {
						getDocumentManager().setSelectedDocument(item);
						int option = getDialogController().showConfirmCloseDirty(item.getTitle());
						
						if (option == JOptionPane.YES_OPTION) {
							if (saveItem(item)) {
								closeList.add(item);
							}
						} else if (option == JOptionPane.NO_OPTION) {
							closeList.add(item);
						}
					} catch (IOException e) {
						getDialogController().showSaveFailed(item, e);
					}
				} else {
					closeList.add(item);
				}
			}
		} finally {
			getDocumentManager().getDocuments().getReadWriteLock().readLock().unlock();
		}
		
		Iterator it = closeList.iterator();
		while (it.hasNext()) {
			DocumentItem item = (DocumentItem) it.next();
			getDocumentManager().closeDocument(item);
		}
	}

	/**
	 * @see ch.iserver.ace.application.ApplicationController#openDocument()
	 */
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

	/**
	 * @see ch.iserver.ace.application.ApplicationController#openFile(java.lang.String)
	 */
	public void openFile(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			getDialogController().showFileDoesNotExist(file);
		} else if (file.isDirectory()) {
			getDialogController().showFileIsDirectory(file);
		} else {
			try {
				getDocumentManager().openDocument(file);
			} catch (IOException e) {
				getDialogController().showOpenFailed(file);
			}
		}
	}

	/**
	 * @see ch.iserver.ace.application.ApplicationController#saveDocument()
	 */
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

	/**
	 * @see ch.iserver.ace.application.ApplicationController#saveDocumentAs()
	 */
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

	/**
	 * @see ch.iserver.ace.application.ApplicationController#saveDocumentCopy()
	 */
	public void saveDocumentCopy() {
		DocumentItem item = getDocumentManager().getSelectedDocument();
		if (item != null) {
			try {
				saveItemAs(item, true);
			} catch (IOException e) {
				getDialogController().showSaveFailed(item, e);
			}
		}
	}

	/**
	 * @see ch.iserver.ace.application.ApplicationController#saveAllDocuments()
	 */
	public void saveAllDocuments() {
		Map failed = new TreeMap();
		EventList dirty = getDocumentManager().getDirtyDocuments();
		dirty.getReadWriteLock().readLock().lock();
		try {
			Iterator it = dirty.iterator();
			while (it.hasNext()) {
				DocumentItem item = (DocumentItem) it.next();
				try {
					saveItem(item);
				} catch (IOException e) {
					failed.put(item, e);
				}
			}
		} finally {
			dirty.getReadWriteLock().readLock().unlock();
		}
		
		// display files that cannot be saved
		if (failed.size() > 0) {
			getDialogController().showSaveFilesFailed(failed);
		}
	}

	/**
	 * @see ch.iserver.ace.application.ApplicationController#discoverUser()
	 */
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
		context.close();
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
		return saveItemAs(item, false);
	}
	
	protected boolean saveItemAs(DocumentItem item, boolean copy) throws IOException {
		getDocumentManager().setSelectedDocument(item);
		DialogResult result = getDialogController().showSaveDocument(item.getTitle());
		if (JFileChooser.APPROVE_OPTION == result.getOption()) {
			File file = (File) result.getResult();
			
			boolean renamed = !CompareUtil.nullSafeEquals(file, item.getFile());
			if (renamed && getDocumentManager().getDocumentForFile(file) != null) {
				getDialogController().showDocumentWithSameNameExists(item.getTitle(), file);
				return false;
			}
			
			if (renamed && file.exists()) {
				int option = getDialogController().showConfirmOverwrite(file);
				if (option == JOptionPane.YES_OPTION) {
					getDocumentManager().saveDocumentAs(file, item, copy);
					return true;
				} else {
					return false;
				}
			} else {
				getDocumentManager().saveDocumentAs(file, item, copy);
				return true;
			}
		} else {
			return false;
		}
	}
		
}
