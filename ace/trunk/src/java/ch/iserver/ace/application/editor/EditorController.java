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
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.BasicEventList;



public class EditorController implements ItemSelectionChangeListener, PreferenceChangeListener {

	private static final Logger LOG = Logger.getLogger(EditorController.class);
	private Editor editor;
	private ParticipantViewController participantViewController;
	
	public EditorController(Editor editor, DocumentViewController documentViewController,
			                    ParticipantViewController participantViewController,
			                    PreferencesStore preferences) {
		this.editor = editor;
		this.participantViewController = participantViewController;
		this.editor.setFontSize(getFontSize(preferences, 12));
		documentViewController.addItemSelectionChangeListener(this);
		this.editor.setEnabled(false);
		preferences.addPreferenceChangeListener(this);
	}
	
	public void itemSelectionChanged(ItemSelectionChangeEvent e) {
		if(e.getItem() == null) {
			setEditorEnabled(false);
		} else {
			DocumentItem item = (DocumentItem)e.getItem();
			if(item.getType() == DocumentItem.LOCAL || item.getType() == DocumentItem.PUBLISHED ||
			   		item.getType() == DocumentItem.JOINED) {
				// enable editor
				CollaborativeDocument doc = item.getEditorDocument();			
				editor.setDocument(doc);
				editor.setTitle(item.getExtendedTitle());
				editor.setEnabled(true);
				// set participantlist
				if(item.getType() == DocumentItem.LOCAL) {
					// there are no participants for local editing
					participantViewController.setParticipantList(new BasicEventList());
				} else {
					participantViewController.setParticipantList(item.getParticipantSourceList());
				}
			} else {
				setEditorEnabled(false);
			}
		}
	}
	
	private void setEditorEnabled(boolean enabled) {
		if(enabled) {
			/*// enable editor
			editor.setDocument(item.getEditorDocument());
			editor.setTitle(item.getExtendedTitle());
			editor.setEnabled(true);
			// set participantlist
			participantViewController.setParticipantList(item.getParticipantSourceList());*/
		} else {
			// disable editor
			editor.setDocument(new CollaborativeDocument());
			editor.setTitle(" ");
			editor.setEnabled(false);
			// set participantlist
			participantViewController.setParticipantList(new BasicEventList());
		}
	}
	
	private int getFontSize(PreferencesStore preferences, int def) {
		try {
			return Integer.parseInt(preferences.get(PreferencesStore.FONTSIZE_KEY, "" + def));
		} catch (NumberFormatException e) {
			LOG.debug("failed to set font size from preferences: " + e.getMessage());
			return def;
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
