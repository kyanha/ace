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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
		Map failed = new TreeMap();
		List dirty = getDocumentManager().getDirtyDocuments();	
		
		if (dirty.size() == 0) {
			shutdown();
		}
		
		SaveFilesDialog saveFilesDialog = new SaveFilesDialog(
						getMainFrame(),
						getMessages(), 
						dirty);
		saveFilesDialog.showDialog();
		if (saveFilesDialog.getOption() == SaveFilesDialog.OK_OPTION) {
			Set saveSet = saveFilesDialog.getCheckedFiles();
			
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
				showSaveFilesFailed(failed);
				
			// only if there are no more open documents... 
			} else if (getDocumentManager().getDocuments().size() == 0) {
				shutdown();
			}
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
		} else {
			getDocumentManager().closeDocument(item);
		}
	}

	public void openDocument() {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(getMainFrame())) {
			Map failed = new TreeMap();
			File[] files = chooser.getSelectedFiles();
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
				String title = getMessages().getMessage("dOpenFileFailedTitle");
				String header = getMessages().getMessage("dOpenFileFailedHeader");
				StringBuffer buf = new StringBuffer();
				buf.append("<html>");
				buf.append(header);
				buf.append("<ul>");
				Iterator it = failed.keySet().iterator();
				while (it.hasNext()) {
					String name = (String) it.next();
					IOException e = (IOException) failed.get(name);
					buf.append("<li>");
					buf.append(getMessages().getMessage("dOpenFileFailedMessage", new Object[] { name, e.getMessage() }));
					buf.append("</li>");
				}
				buf.append("</ul></html>");
				JOptionPane.showMessageDialog(
								getMainFrame(),
								buf.toString(),
								title,
								JOptionPane.ERROR_MESSAGE
				);
			}
		}
	}

	public void openFile(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			JOptionPane.showMessageDialog(
							getMainFrame(),
							getMessages().getMessage("dOpenFileFailedTitle"),
							getMessages().getMessage("dOpenFileFailedExists", new Object[] { file.getName() }),
							JOptionPane.ERROR_MESSAGE
			);
		} else if (!file.isDirectory()) {
			JOptionPane.showMessageDialog(
							getMainFrame(),
							getMessages().getMessage("dOpenFileFailedTitle"),
							getMessages().getMessage("dOpenFileFailedDirectory", new Object[] { file.getName() }),
							JOptionPane.ERROR_MESSAGE
			);
		} else {
			try {
				getDocumentManager().openDocument(new File(filename));
			} catch (IOException e) {
				String title = getMessages().getMessage("dLoadFailedTitle");
				String message = getMessages().getMessage("dOpenSingleFailedMessage");
				JOptionPane.showMessageDialog(
								getMainFrame(),
								title,
								message,
								JOptionPane.ERROR_MESSAGE);
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
			showSaveFilesFailed(failed);
		}
	}

	public void discoverUser() {
		// TODO: unimplemented method: discoverUser()
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
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(item.getTitle());
		if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(getMainFrame())) {
			File file = chooser.getSelectedFile();
			if (file.exists()) {
				String title = getMessages().getMessage("dSaveFileOverwriteTitle");
				String message = getMessages().getMessage("dSaveFileOverwriteMessage", new Object[] { file.getAbsoluteFile() });
				int option = JOptionPane.showConfirmDialog(
								getMainFrame(),
								message,
								title,
								JOptionPane.YES_NO_OPTION
				);
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
		
	private void saveFailed(DocumentItem item, Exception e) {
		String title = getMessages().getMessage("dSaveFileFailedTitle");
		String message = getMessages().getMessage(
						"dSaveFileFailedMessage", 
						new Object[] { item.getFile().getAbsolutePath(), e.getMessage() });
		JOptionPane.showMessageDialog(
						getMainFrame(),
						title,
						message,
						JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Show a message dialog displaying a list of documents that could not be
	 * saved.
	 * 
	 * @param failed a Map containing DocumentItem keys and IOException values
	 */
	protected void showSaveFilesFailed(Map failed) {
		String title = getMessages().getMessage("dSaveFilesFailedTitle");
		String header = getMessages().getMessage("dSaveFilesFailedHeader");
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		buf.append(header);
		buf.append("<ul>");
		Iterator it = failed.keySet().iterator();
		while (it.hasNext()) {
			DocumentItem item = (DocumentItem) it.next();
			String name = item.getFile().getAbsolutePath();
			IOException e = (IOException) failed.get(name);
			buf.append("<li>");
			buf.append(getMessages().getMessage("dSaveFilesFailedMessage", new Object[] { name, e.getMessage() }));
			buf.append("</li>");
		}
		buf.append("</ul></html>");
		JOptionPane.showMessageDialog(
						getMainFrame(),
						buf.toString(),
						title,
						JOptionPane.ERROR_MESSAGE
		);
	}

}
