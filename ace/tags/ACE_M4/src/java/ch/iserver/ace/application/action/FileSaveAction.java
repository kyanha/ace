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

import javax.swing.KeyStroke;

import ch.iserver.ace.application.ApplicationController;
import ch.iserver.ace.application.DocumentItem;
import ch.iserver.ace.application.DocumentViewController;
import ch.iserver.ace.application.ItemSelectionChangeEvent;
import ch.iserver.ace.application.LocaleMessageSource;



public class FileSaveAction extends DocumentItemSelectionChangeAction {

	private ApplicationController appController;

	public FileSaveAction(LocaleMessageSource messageSource, DocumentViewController viewController,
			ApplicationController appController) {
		super(messageSource.getMessage("mFileSave"), messageSource.getIcon("iMenuFileSave"), viewController);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		putValue(SHORT_DESCRIPTION, messageSource.getMessage("mFileSaveTT"));
		this.appController = appController;
		setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		appController.saveDocument();
	}
	
	public void itemSelectionChanged(ItemSelectionChangeEvent e) {
		if(e.getItem() == null) {
			setEnabled(false);
		} else {
			DocumentItem item = (DocumentItem)e.getItem();
			if((item.getType() == DocumentItem.LOCAL || item.getType() == DocumentItem.PUBLISHED) &&
					(!item.hasBeenSaved() || item.isDirty())) {
				// enable for all local document that havent been saved yet or that are dirty
				setEnabled(true);
			} else {
				setEnabled(false);
			}
		}
	}

}

