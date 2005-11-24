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

import ch.iserver.ace.application.DocumentItem;
import ch.iserver.ace.application.UserItem;
import ch.iserver.ace.application.DocumentManager;
import ch.iserver.ace.application.ItemSelectionChangeEvent;
import ch.iserver.ace.application.LocaleMessageSource;
import ch.iserver.ace.application.DocumentViewController;
import ch.iserver.ace.application.UserViewController;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.awt.event.*;



public class NetInviteUserAction extends DocumentItemSelectionChangeAction {

	private DocumentManager documentManager;
	private DocumentItem currentDocumentItem;
	private UserItem currentUserItem;
	private DocumentViewController viewController;
	private UserViewController userController;

	public NetInviteUserAction(LocaleMessageSource messageSource, DocumentManager documentManager,
			DocumentViewController viewController, UserViewController userController) {
		super(messageSource.getMessage("mNetInvite"), messageSource.getIcon("iMenuNetInvite"), viewController);
		putValue(SHORT_DESCRIPTION, messageSource.getMessage("mNetInviteTT"));
		userController.addItemSelectionChangeListener(this);
		/*userController.getViewList().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					inviteUser();
				}
			}
		});*/
		this.documentManager = documentManager;
		this.viewController = viewController;
		this.userController = userController;
		setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		inviteUser();
	}

	private void inviteUser() {
		if(currentDocumentItem != null && currentUserItem != null) {
			System.out.println("NetInviteUserAction");
		}
	}

	public void itemSelectionChanged(ItemSelectionChangeEvent e) {
		// HANDLE USER & DOCUMENT ITEM SELECTION CHANGES
		/*if(e.getSource() == viewController) {
			if(e.getItem() == null) {
				currentDocumentItem = null;
			} else {
				currentDocumentItem = (DocumentItem)e.getItem();
			}
		} else if(e.getSource() == userController){
			if(e.getItem() == null) {
				currentUserItem = null;
			} else {
				currentUserItem = (UserItem)e.getItem();
			}
		}
		
		if(currentDocumentItem != null && currentUserItem != null) {
			setEnabled(true);
		} else {
			setEnabled(false);
		}*/
		
	}

}
