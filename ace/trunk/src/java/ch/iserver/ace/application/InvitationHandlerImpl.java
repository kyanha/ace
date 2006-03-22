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

package ch.iserver.ace.application;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ch.iserver.ace.application.editor.CollaborativeDocument;
import ch.iserver.ace.collaboration.Invitation;
import ch.iserver.ace.collaboration.InvitationHandler;
import ch.iserver.ace.collaboration.JoinCallback;



public class InvitationHandlerImpl implements InvitationHandler {

	private DialogController dialogController;
	private DocumentViewController viewController;
	private BrowseViewController browseViewController;
	
	public InvitationHandlerImpl(DialogController dialogController, DocumentViewController viewController,
			BrowseViewController browseViewController) {
		this.dialogController = dialogController;
		this.viewController = viewController;
		this.browseViewController = browseViewController;
	}
	
	public void handleInvitation(final Invitation invitation) {
	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String inviterName = invitation.getInviter().getName();
				String docTitle = "\"" + invitation.getDocument().getTitle() + "\"";
				int result = dialogController.showInvitationReceived(inviterName, docTitle);
				//int result = dialogController.showInvitationReceived(inviterName, docTitle);
				
				DocumentItem inviteItem = browseViewController.findItem(invitation.getDocument());

				if (result == JOptionPane.OK_OPTION) {
					// get document item from browse view
					if (inviteItem ==  null) {
						return;
					}
					inviteItem.setEditorDocument(new CollaborativeDocument());
								
					// join
					JoinCallback joinCallback = new JoinCallbackImpl(inviteItem, viewController, dialogController);
					invitation.accept(joinCallback);
							
				} else {
					// rejected
					if(inviteItem ==  null) {
						return;
					}
					invitation.reject();
				}
			}
		});
		
	}
	
}