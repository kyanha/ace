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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import ch.iserver.ace.ServerInfo;
import ch.iserver.ace.application.dialog.AboutDialog;
import ch.iserver.ace.application.dialog.DialogResult;
import ch.iserver.ace.application.dialog.PreferencesDialog;
import ch.iserver.ace.application.dialog.SaveFilesDialog;
import ch.iserver.ace.application.preferences.PreferencesStore;

/**
 *
 */
public class DialogControllerImpl implements DialogController {
	
	private AboutDialog aboutDialog;
	
	private PreferencesDialog preferencesDialog;
	
	private SaveFilesDialog saveFilesDialog;
	
	private PreferencesStore preferences;
	
	private Frame mainFrame;
	
	private LocaleMessageSource messages;
	
	/**
	 * 
	 */
	private JFileChooser openFileChooser;
	
	/**
	 * 
	 */
	private JFileChooser saveFileChooser;
	
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
	
	public void setPreferences(PreferencesStore preferences) {
		this.preferences = preferences;
	}
	
	public PreferencesStore getPreferences() {
		return preferences;
	}	
	
	public void showSaveFailed(DocumentItem item, Exception e) {
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

	public void showSaveFilesFailed(Map failed) {
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

	public void showOpenFilesFailed(Map failed) {
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

	public DialogResult showSaveFilesDialog(List candidates) {
		if (saveFilesDialog == null) {
			saveFilesDialog = new SaveFilesDialog(
						getMainFrame(),
						getMessages()
			);
		}
		saveFilesDialog.setCandidates(candidates);
		saveFilesDialog.showDialog();
		return new DialogResult(saveFilesDialog.getOption(), saveFilesDialog.getSelectedFiles());
	}

	public void showFileDoesNotExist(File file) {
		JOptionPane.showMessageDialog(
						getMainFrame(),
						getMessages().getMessage("dOpenFileFailedTitle"),
						getMessages().getMessage("dOpenFileFailedExists", new Object[] { file.getName() }),
						JOptionPane.ERROR_MESSAGE
		);
	}

	public void showFileIsDirectory(File file) {
		JOptionPane.showMessageDialog(
						getMainFrame(),
						getMessages().getMessage("dOpenFileFailedTitle"),
						getMessages().getMessage("dOpenFileFailedDirectory", new Object[] { file.getName() }),
						JOptionPane.ERROR_MESSAGE
		);
	}

	public int showConfirmCloseDirty(String documentTitle) {
		String title = getMessages().getMessage("dConfirmCloseDirtyTitle");
		String message = getMessages().getMessage("dConfirmCloseDirtyMessage", new Object[] { documentTitle });
		return JOptionPane.showConfirmDialog(
						getMainFrame(), 
						message, 
						title, 
						JOptionPane.YES_NO_CANCEL_OPTION);
	}

	public DialogResult showOpenDocuments() {
		if (openFileChooser == null) {
			openFileChooser = new JFileChooser();
			openFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			openFileChooser.setMultiSelectionEnabled(true);
		}
		int option = openFileChooser.showOpenDialog(getMainFrame());
		File[] files = openFileChooser.getSelectedFiles();
		return new DialogResult(option, files);
	}
	
	public DialogResult showSaveDocument(String title) {
		if (saveFileChooser == null) {
			saveFileChooser = new JFileChooser();
			saveFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}
		saveFileChooser.setDialogTitle(title);
		int option = saveFileChooser.showSaveDialog(getMainFrame());
		return new DialogResult(option, saveFileChooser.getSelectedFile());
	}

	public void showOpenFailed(File file) {
		String title = getMessages().getMessage("dLoadFailedTitle");
		String message = getMessages().getMessage("dOpenSingleFailedMessage");
		JOptionPane.showMessageDialog(
						getMainFrame(),
						title,
						message,
						JOptionPane.ERROR_MESSAGE
		);
	}

	public int showConfirmOverwrite(File file) {
		String title = getMessages().getMessage("dSaveFileOverwriteTitle");
		String message = getMessages().getMessage("dSaveFileOverwriteMessage", new Object[] { file.getAbsoluteFile() });
		return JOptionPane.showConfirmDialog(
						getMainFrame(),
						message,
						title,
						JOptionPane.YES_NO_OPTION
		);
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
	
	public DialogResult showDiscoverUser() {
		String title = getMessages().getMessage("dDiscoverUserTitle");
		String message = getMessages().getMessage("dDiscoverUserMessage");
		String host = JOptionPane.showInputDialog(getMainFrame(), message, title);
		if (host != null) {
			try {
				InetAddress addr = InetAddress.getByName(host);
				return new DialogResult(JOptionPane.OK_OPTION, new ServerInfo(addr, 0));
			} catch (UnknownHostException e) {
				title = getMessages().getMessage("dDisocverUserFailedTitle");
				message = getMessages().getMessage("dDiscoverUserFailedMessage", new Object[] { e.getMessage() });
				JOptionPane.showMessageDialog(
								getMainFrame(),
								message,
								title,
								JOptionPane.WARNING_MESSAGE
				);
				return new DialogResult(JOptionPane.CANCEL_OPTION);
			}
		} else {
			return new DialogResult(JOptionPane.CANCEL_OPTION);
		}
	}
	
	/**
	 * @see ch.iserver.ace.application.DialogController#showDocumentWithSameNameExists(java.lang.String, java.io.File)
	 */
	public void showDocumentWithSameNameExists(String docTitle, File file) {
		String title = getMessages().getMessage("dDocumentWithSameNameTitle");
		String message = getMessages().getMessage("dDocumentWithSameNameMessage",
						new Object[] { docTitle, file.getAbsolutePath() });
		JOptionPane.showMessageDialog(
						getMainFrame(),
						message,
						title,
						JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * @see ch.iserver.ace.application.DialogController#showInvitationReceived(java.lang.String, java.lang.String)
	 */
	public int showInvitationReceived(String user, String docTitle) {
		String title = getMessages().getMessage("dInvitationReceivedTitle");
		String message = getMessages().getMessage("dInvitationReceivedMessage", new Object[] { user, docTitle });
		return JOptionPane.showConfirmDialog(
						getMainFrame(),
						message,
						title,
						JOptionPane.YES_NO_OPTION);
	}
	
	// --> dispose dialogs <--
	
	public void destroy() {
		if (aboutDialog != null) {
			aboutDialog.dispose();
		}
		if (preferencesDialog != null) {
			preferencesDialog.dispose();
		}
		if (saveFilesDialog != null) {
			saveFilesDialog.dispose();
		}
	}

}
