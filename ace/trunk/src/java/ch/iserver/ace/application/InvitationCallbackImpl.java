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

import ch.iserver.ace.collaboration.Invitation;
import ch.iserver.ace.collaboration.InvitationCallback;
import ch.iserver.ace.collaboration.ParticipantSessionCallback;



public class InvitationCallbackImpl implements InvitationCallback {

	private DialogController dialogController;
	
	public InvitationCallbackImpl(DialogController dialogController) {
		this.dialogController = dialogController;
	}
	
	public void invitationReceived(Invitation invitation) {
		String inviterName = invitation.getInviter().getName();
		String docTitle = invitation.getDocument().getTitle();
		int result = dialogController.showInvitationReceived(inviterName, docTitle);
		if (result == JOptionPane.OK_OPTION) {
			// accepted
			/*System.out.println("invitation auto accepted");
			// create new document item
			DocumentItem documentItem = new DocumentItem(invitation.getDocument());
			
			// create and set session callback
			ParticipantSessionCallback callback = new ParticipantSessionCallbackImpl(documentItem);
			documentItem.setSessionCallback(callback);
	
			// set session
			documentItem.setSession(invitation.accept(callback));
	
			// TODO: add item to document view
			documentItem.setType(DocumentItem.JOINED);*/

		} else {
			// rejected
			invitation.reject();
		}
		
	}
	
}