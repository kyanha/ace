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

package ch.iserver.ace.application.editor;

import ch.iserver.ace.application.*;
import ch.iserver.ace.application.preferences.PreferenceChangeEvent;
import ch.iserver.ace.application.preferences.PreferenceChangeListener;
import ch.iserver.ace.application.preferences.PreferencesStore;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import javax.swing.text.*;

import org.apache.log4j.Logger;



public class DummyEditorController implements ItemSelectionChangeListener, PreferenceChangeListener {

	private static final Logger LOG = Logger.getLogger(DummyEditorController.class);
	
	private DummyEditor editor;

	public DummyEditorController(DummyEditor editor, 
			                    DocumentViewController documentViewController,
			                    PreferencesStore preferences) {
		this.editor = editor;
		this.editor.setFontSize(getFontSize(preferences, 12));
		documentViewController.addItemSelectionChangeListener(this);
		this.editor.setEnabled(false);
		preferences.addPreferenceChangeListener(this);
	}
	
	private int getFontSize(PreferencesStore preferences, int def) {
		try {
			return Integer.parseInt(preferences.get(PreferencesStore.FONTSIZE_KEY, "" + def));
		} catch (NumberFormatException e) {
			LOG.debug("failed to set font size from preferences: " + e.getMessage());
			return def;
		}
	}
	
	public void itemSelectionChanged(ItemSelectionChangeEvent e) {
		if(e.getItem() != null) {
			/*System.out.print("Item:   ");
			if(e.getItem() instanceof DocumentItem) {
				System.out.println("DocumentItem");
			}*/
			// enable editor
			StyledDocument doc = ((DocumentItem)e.getItem()).getEditorDocument();
			editor.setDocument(doc);
			editor.setTitle(((DocumentItem)e.getItem()).getTitle());
			editor.setEnabled(true);
		} else {
			// disable editor
			editor.setDocument(new DefaultStyledDocument());
			editor.setTitle(" ");
			editor.setEnabled(false);
		}
	}
	
	public void preferenceChanged(PreferenceChangeEvent event) {
		if (PreferencesStore.FONTSIZE_KEY.equals(event.getKey())) {
			try {
				editor.setFontSize(Integer.parseInt(event.getValue()));
			} catch (NumberFormatException e) {
				LOG.debug("failed to set font size from preferences: " + e.getMessage());
			}
		}
	}
	
}
