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

import ch.iserver.ace.application.DocumentManager;
import ch.iserver.ace.application.ItemSelectionChangeEvent;
import ch.iserver.ace.application.LocaleMessageSource;
import ch.iserver.ace.application.ViewController;
import java.awt.event.ActionEvent;
import java.util.List;
import java.awt.Toolkit;
import javax.swing.KeyStroke;



public class FileCloseAction extends ItemSelectionChangeAction {

	DocumentManager documentManager;

	public FileCloseAction(LocaleMessageSource messageSource, List viewControllers, DocumentManager documentManager) {
		super(messageSource.getMessage("mFileClose"), messageSource.getIcon("iMenuFileClose"), viewControllers);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('W', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		putValue(SHORT_DESCRIPTION, messageSource.getMessage("mFileCloseTT"));
		this.documentManager = documentManager;
		setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		documentManager.closeDocument();
	}
	
	public void itemSelectionChanged(ItemSelectionChangeEvent e) {
		if(e.getItem() == null) {
			setEnabled(false);
		} else {
			setEnabled(true);
		}
	}

}

