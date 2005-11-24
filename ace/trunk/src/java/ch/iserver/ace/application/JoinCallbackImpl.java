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

import ch.iserver.ace.collaboration.JoinCallback;
import ch.iserver.ace.collaboration.Session;
import ch.iserver.ace.collaboration.ParticipantSessionCallback;



public class JoinCallbackImpl implements JoinCallback {

	private DocumentItem documentItem;
	
	public JoinCallbackImpl(DocumentItem documentItem) {
		this.documentItem = documentItem;
	}
	
	public ParticipantSessionCallback accepted(Session session) {
		System.out.println("join accepted");
		// set session
		documentItem.setSession(session);

		// create and set session callback
		ParticipantSessionCallback callback = new ParticipantSessionCallbackImpl();
		((SessionCallbackImpl)callback).setDoc(documentItem.getEditorDocument());
		documentItem.setSessionCallback(callback);
		
		documentItem.setType(DocumentItem.JOINED);
		return callback;
	}
	
	public void rejected(int code) {
		System.out.println("rejected");
	}

}