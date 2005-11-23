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

import ch.iserver.ace.collaboration.Invitation;
import ch.iserver.ace.collaboration.InvitationCallback;



public class InvitationCallbackImpl implements InvitationCallback {

	public InvitationCallbackImpl() {
	}
	
	public void invitationReceived(Invitation invitation) {
		System.out.println("auto accepted invitation");
		// 1. create document item with remote document
/*		doc = new DocumentItem(invitation.getDocument());
		// 2. create participant callback
		ParticipantCallback callback = doc.createParticipantCallback();
		// 3. accept
		Session session = invitation.accept(callback);
		// 4. set session
		doc.setSession(session);*/
	}
	
}