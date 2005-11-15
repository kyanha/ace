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

package ch.iserver.ace.application.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ch.iserver.ace.application.LocaleMessageSource;
import ch.iserver.ace.application.preferences.PreferencesStore;

public class PreferencesDialog extends TitledDialog {
	
	private final PreferencesStore preferences;
	
	private JTextField nameField;
	
	private JComboBox sizeCombo;

	public PreferencesDialog(LocaleMessageSource messages, PreferencesStore preferences) {
		super(messages, 
				messages.getMessage("dPreferencesTitle"),
				messages.getIcon("iPreferencesTitle"));
		if (preferences == null) {
			throw new IllegalArgumentException("preferences cannot be null");
		}
		this.preferences = preferences;
	}
	
	protected Object[] getSizeArray() {
		return new String[] {
			"8", "9", "10", "11", "12", "14", "16", "18", "20"	
		};
	}
	
	protected JComponent createContent() {
		GridBagConstraints gbc = new GridBagConstraints();
		JPanel pane = new JPanel(new GridBagLayout());
		
		JLabel name = new JLabel(getMessages().getMessage("dPreferencesNickname"));
		nameField = new JTextField(30);
		JLabel size = new JLabel(getMessages().getMessage("dPreferencesFontsize"));
		sizeCombo = new JComboBox(getSizeArray());
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(2, 2, 2, 5);
		pane.add(name, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 5, 2, 2);
		pane.add(nameField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(2, 2, 2, 5);
		pane.add(size, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 5, 2, 2);
		pane.add(sizeCombo, gbc);
		
		return pane;
	}
	
	protected void init() {
		String nickname = preferences.get(PreferencesStore.NICKNAME_KEY, System.getProperty("user.name"));
		nameField.setText(nickname);
		String size = preferences.get(PreferencesStore.FONTSIZE_KEY, "10");
		sizeCombo.setSelectedItem(size);
	}
	
	protected boolean onFinish() {
		String nickname = nameField.getText();
		if (nickname.trim().length() > 0) {
			preferences.put(PreferencesStore.NICKNAME_KEY, nickname);
		}
		String size = (String) sizeCombo.getSelectedItem();
		try {
			int s = Integer.parseInt(size);
			if (s > 7 && s < 30) {
				preferences.put(PreferencesStore.FONTSIZE_KEY, size);
			}
		} catch (NumberFormatException e) {
			// ignore, not an integer
		}
		return true;
	}

}