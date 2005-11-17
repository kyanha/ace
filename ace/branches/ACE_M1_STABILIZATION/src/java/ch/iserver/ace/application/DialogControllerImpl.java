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

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

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
	
	private PreferencesStore preferences;
	
	private Frame mainFrame;
	
	private LocaleMessageSource messages;
	
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
		SaveFilesDialog saveFilesDialog = new SaveFilesDialog(
						getMainFrame(),
						getMessages(), 
						candidates);
		saveFilesDialog.setModal(true);
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

	public int showConfirmCloseDirty() {
		String title = getMessages().getMessage("dConfirmCloseDirtyTitle");
		String message = getMessages().getMessage("dConfirmCloseDirtyMessage");
		return JOptionPane.showConfirmDialog(
						getMainFrame(), 
						message, 
						title, 
						JOptionPane.YES_NO_CANCEL_OPTION);
	}

	public DialogResult showOpenDocuments() {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		int option = chooser.showOpenDialog(getMainFrame());
		File[] files = chooser.getSelectedFiles();
		return new DialogResult(option, files);
	}
	
	public DialogResult showSaveDocument(String title) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(title);
		int option = chooser.showSaveDialog(getMainFrame());
		return new DialogResult(option, chooser.getSelectedFile());
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

}
