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

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import ch.iserver.ace.application.DocumentItem;
import ch.iserver.ace.application.DocumentManager;
import ch.iserver.ace.application.DocumentViewController;
import ch.iserver.ace.application.ItemSelectionChangeEvent;
import ch.iserver.ace.application.LocaleMessageSource;
import java.awt.Toolkit;
import javax.swing.KeyStroke;



public class NetConcealDocumentAction extends DocumentItemSelectionChangeAction {

	private DocumentManager documentManager;

	public NetConcealDocumentAction(LocaleMessageSource messageSource, DocumentManager documentManager,
			DocumentViewController viewController) {
		super(messageSource.getMessage("mNetConceal"), messageSource.getIcon("iMenuNetConceal"), viewController);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('C', InputEvent.SHIFT_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		putValue(SHORT_DESCRIPTION, messageSource.getMessage("mNetConcealTT"));
		this.documentManager = documentManager;
		setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		documentManager.concealDocument();
	}

	public void itemSelectionChanged(ItemSelectionChangeEvent e) {
		if(e.getItem() == null) {
			setEnabled(false);
		} else {
			DocumentItem item = (DocumentItem)e.getItem();
			if(item.getType() == DocumentItem.PUBLISHED) {
				setEnabled(true);
			} else {
				setEnabled(false);
			}
		}
	}

}

