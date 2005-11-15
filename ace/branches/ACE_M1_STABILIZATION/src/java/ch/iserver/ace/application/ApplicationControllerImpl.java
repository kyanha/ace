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

import ch.iserver.ace.application.dialog.AboutDialog;
import ch.iserver.ace.application.dialog.PreferencesDialog;
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
			preferencesDialog = new PreferencesDialog(getMainFrame(), getMessages(), getPreferences());
		}
		preferencesDialog.showDialog();
	}
	
	public void openFile(String filename) {
		// TODO Auto-generated method stub
		
	}
	
	public void quit() {
		getDocumentManager().exitApplication();
		System.exit(0);
	}

}
