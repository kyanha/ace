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

import java.util.Collection;


/**
 * A RemoteUserProxy is a network layer representation of a remote user. It
 * provides a set of actions available on the user level.
 */
public interface RemoteUserProxy {
	
	/**
	 * Gets the display name of this remote user.
	 *
	 * @return the display name of this user
	 */
	String getDisplayName();
	
	/**
	 * Retrieves a collection of documents shared by the represented remote
	 * user. The elements in the collection are of type
	 * <code>RemoteDocumentProxy</code>.
	 * 
	 * @return the shared documents of the represented remote user
	 */
	Collection getSharedDocuments();
	
	/**
	 * Invites the represented user to the given locally shared document.
	 * The DocumentServerLogic is the logic layer server object that allows
	 * the network layer to join a new user (should the remote user accept
	 * the invitation).
	 * 
	 * @param logic the logic layer server object of the shared document
	 */
	void invite(DocumentServerLogic logic);
	
}
