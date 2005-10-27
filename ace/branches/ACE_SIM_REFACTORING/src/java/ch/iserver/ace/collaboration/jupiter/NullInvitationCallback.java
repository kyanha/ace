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

package ch.iserver.ace.collaboration.jupiter;

import ch.iserver.ace.collaboration.Invitation;
import ch.iserver.ace.collaboration.InvitationCallback;

/**
 * Null object pattern implementation of an InvitationCallback.
 */
final class NullInvitationCallback implements InvitationCallback {
	
	private static InvitationCallback instance;
	
	private NullInvitationCallback() {
		// hidden constructor
	}
	
	public static synchronized InvitationCallback getInstance() {
		if (instance == null) {
			instance = new NullInvitationCallback();
		}
		return instance;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.InvitationCallback#invitationReceived(ch.iserver.ace.collaboration.Invitation)
	 */
	public void invitationReceived(Invitation event) {
		// ignore invitations
	}

}
