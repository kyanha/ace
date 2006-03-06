/*
 * $Id: NetPublishConcealDocumentToggleAction.java 1620 2005-11-23 09:39:43Z pyron $
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

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import ch.iserver.ace.application.DocumentItem;
import ch.iserver.ace.application.DocumentViewController;
import ch.iserver.ace.application.ItemSelectionChangeEvent;
import ch.iserver.ace.application.LocaleMessageSource;
import ch.iserver.ace.application.editor.CollaborativeEditor;



public class ToggleWordWrapAction extends AbstractAction { //DocumentItemSelectionChangeAction {

	private CollaborativeEditor cEditor;
	private ImageIcon wordWrapEnable, wordWrapDisable;
	private String toolTipEnable, toolTipDisable;
//	DocumentItem currentDocumentItem;
	private boolean wordWrapping = false;
	
	public ToggleWordWrapAction(LocaleMessageSource messageSource, CollaborativeEditor cEditor,
			DocumentViewController viewController) {
//		super(messageSource.getMessage("mWordWrapEnableTT"), messageSource.getIcon("iWordWrapEnable"), viewController);
		super(messageSource.getMessage("mViewWordWrapEnable"), messageSource.getIcon("iViewWordWrapEnable"));
		putValue(SHORT_DESCRIPTION, messageSource.getMessage("mViewWordWrapEnableTT"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('W', InputEvent.SHIFT_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		this.cEditor = cEditor;
		toolTipEnable = messageSource.getMessage("mViewWordWrapEnableTT");
		wordWrapEnable = messageSource.getIcon("iViewWordWrapEnable");
		toolTipDisable = messageSource.getMessage("mViewWordWrapDisableTT");
		wordWrapDisable = messageSource.getIcon("iViewWordWrapDisable");
//		setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
//		if(currentDocumentItem.isWordWrapping()) {
//			putValue(SMALL_ICON, wordWrapEnable);
//			putValue(SHORT_DESCRIPTION, toolTipEnable);
//			putValue(NAME, toolTipEnable);
//			currentDocumentItem.setWordWrapping(false);
//			cEditor.setWordWrapping(false);
//		} else {
//			putValue(SMALL_ICON, wordWrapDisable);
//			putValue(SHORT_DESCRIPTION, toolTipDisable);
//			putValue(NAME, toolTipDisable);
//			currentDocumentItem.setWordWrapping(true);
//			cEditor.setWordWrapping(true);
//		}
		switchWordWrapping();
	}

//	public void itemSelectionChanged(ItemSelectionChangeEvent e) {
//		if(e.getItem() == null) {
//			currentDocumentItem = null;
//			setEnabled(false);
//		} else {
//			currentDocumentItem = (DocumentItem)e.getItem();
//			if(currentDocumentItem.isWordWrapping()) {
//				putValue(SMALL_ICON, wordWrapDisable);
//				putValue(SHORT_DESCRIPTION, toolTipDisable);
//				putValue(NAME, toolTipDisable);
//			} else {
//				putValue(SMALL_ICON, wordWrapEnable);
//				putValue(SHORT_DESCRIPTION, toolTipEnable);
//				putValue(NAME, toolTipEnable);
//			}
//			setEnabled(true);
//		}
//	}
	
	public void switchWordWrapping() {
		if(wordWrapping) {
			putValue(NAME, toolTipEnable);
			putValue(SHORT_DESCRIPTION, toolTipEnable);
			putValue(SMALL_ICON, wordWrapEnable);
			cEditor.setWordWrapping(false);
			wordWrapping = false;
		} else {
			putValue(NAME, toolTipDisable);
			putValue(SHORT_DESCRIPTION, toolTipDisable);
			putValue(SMALL_ICON, wordWrapDisable);
			cEditor.setWordWrapping(true);
			wordWrapping = true;
		}
	}

}