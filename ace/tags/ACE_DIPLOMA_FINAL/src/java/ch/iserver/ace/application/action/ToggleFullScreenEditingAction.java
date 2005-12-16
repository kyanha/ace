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

package ch.iserver.ace.application.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import ch.iserver.ace.application.LocaleMessageSource;
import ch.iserver.ace.application.PersistentContentPane;
import ch.iserver.ace.application.editor.Editor;



public class ToggleFullScreenEditingAction extends AbstractAction {

	private PersistentContentPane persistentContentPane;
	private boolean fullScreenEditingMode = false;
	private LocaleMessageSource messageSource;
	
	public ToggleFullScreenEditingAction(LocaleMessageSource messageSource, PersistentContentPane persistentContentPane,
			Editor editor) {
		super(messageSource.getMessage("mWindowToggleFullScreen"), messageSource.getIcon("iWindowToggleFullScreen"));
		putValue(SHORT_DESCRIPTION, messageSource.getMessage("mWindowToggleFullScreenTT"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('F', InputEvent.SHIFT_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		this.messageSource = messageSource;
		this.persistentContentPane = persistentContentPane;

		editor.getEditorComponent().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					toggleEditingMode();
				}
			}
		});
	
	}
	
	public void actionPerformed(ActionEvent e) {
		toggleEditingMode();
	}
	
	public void toggleEditingMode() {
		if(fullScreenEditingMode) {
			// switch to normal mode
			putValue(SMALL_ICON, messageSource.getIcon("iWindowToggleFullScreen"));
			putValue(SHORT_DESCRIPTION, messageSource.getMessage("mWindowToggleFullScreenTT"));
			persistentContentPane.switchFullScreenEditing();
			fullScreenEditingMode = false;
		} else {
			// switch to fullscreen mode
			putValue(SMALL_ICON, messageSource.getIcon("iWindowToggleNormalScreen"));
			putValue(SHORT_DESCRIPTION, messageSource.getMessage("mWindowToggleNormalScreenTT"));
			persistentContentPane.switchFullScreenEditing();
			fullScreenEditingMode = true;
		}
	}
	
}