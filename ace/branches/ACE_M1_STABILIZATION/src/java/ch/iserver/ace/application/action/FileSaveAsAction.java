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

import ch.iserver.ace.application.ApplicationController;
import ch.iserver.ace.application.DocumentManager;
import ch.iserver.ace.application.ItemSelectionChangeEvent;
import ch.iserver.ace.application.LocaleMessageSource;
import ch.iserver.ace.application.DocumentViewController;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.Toolkit;
import javax.swing.KeyStroke;



public class FileSaveAsAction extends DocumentItemSelectionChangeAction {

	private ApplicationController appController;
	private DocumentManager documentManager;

	public FileSaveAsAction(LocaleMessageSource messageSource, DocumentManager documentManager,
			DocumentViewController viewController, ApplicationController appController) {
		super(messageSource.getMessage("mFileSaveAs"), messageSource.getIcon("iMenuFileSaveAs"), viewController);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('S', InputEvent.SHIFT_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		this.documentManager = documentManager;
		this.appController = appController;
		setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		appController.saveDocumentAs();
	}

	public void itemSelectionChanged(ItemSelectionChangeEvent e) {
		// TODO: maybe disable when document hasnt been saved yet?
		if(e.getItem() == null) {
			setEnabled(false);
		} else {
			setEnabled(true);
		}
	}

}

