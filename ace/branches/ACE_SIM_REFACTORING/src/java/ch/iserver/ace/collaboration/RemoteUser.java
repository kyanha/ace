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

package ch.iserver.ace.collaboration;

import java.util.Collection;

import ch.iserver.ace.UserDetails;


/**
 * A RemoteUser is a local representation of a remote user. It is a proxy
 * providing all the user related operations in one convenient place.
 */
public interface RemoteUser {
	
	/**
	 * Gets the unique identifier of the user.
	 * 
	 * @return the unique identifier of the user
	 */
	String getId();
	
	/**
	 * Retrieves a collection of all remote documents published by this
	 * remote user.
	 *
	 * @return a collection of remote documents published by this user
	 */
	Collection getSharedDocuments();
	
	/**
	 * Gets the display information about the user.
	 * 
	 * @return the UserDetails information
	 */	
	UserDetails getUserDetails();
			
	/**
	 * Invites the user represented by this instance to a given published
	 * session.
	 *
	 * @param session the session for which the user is invited
	 */
	void invite(PublishedSession session);
	
}
