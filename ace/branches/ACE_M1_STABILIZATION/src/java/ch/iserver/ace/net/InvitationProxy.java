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

package ch.iserver.ace.net;


/**
 * The InvitationProxy interface represents an invitation as represented by the
 * network layer. 
 */
public interface InvitationProxy {
	
	/**
	 * Retrieves the remote document for which the local user is 
	 * invited.
	 * 
	 * @return the remote document to which the local user is invited
	 */
	RemoteDocumentProxy getDocument();

	/**
	 * Accepts the invitation. The returned SessionConnectionInfo is ready to
	 * be used for any communication with the joined session.
	 * 
	 * @param callback the callback interface for callbacks to the logic layer
	 * @return the SessionConnection to the shared document
	 */
	SessionConnection accept(SessionConnectionCallback callback);
	
	/**
	 * Rejects the invitation. Sends a notification to the inviter that the
	 * invitation was rejected.
	 */
	void reject();
	
}
