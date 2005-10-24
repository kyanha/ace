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
 * A RemoteDocumentProxy is a network layer object that represents a document
 * shared by another user. The {@link #join(SessionConnectionCallback)} method
 * tries to join the
 * shared document editing session. The returned SessionConnection is used
 * by the logic layer for communication with the session.
 */
public interface RemoteDocumentProxy {
	
	/**
	 * Gets the unique identifier of the document.
	 * 
	 * @return the unique identifier
	 */
	String getId();
	
	/**
	 * Gets the title of the remote document.
	 * 
	 * @return the title of the remote document
	 */
	String getTitle();
	
	/**
	 * Gets the RemoteUserProxy of the publisher of the document.
	 * 
	 * @return the RemoteUserProxy of the publisher
	 */
	RemoteUserProxy getPublisher();
	
	/**
	 * Tries to join the editing session of this remote document. The returned
	 * SessionConnectionIngo allows to communicate with the session.
	 * 
	 * @param callback the SessionConnectionCallback for callbacks to the logic
	 *                 layer
	 * @return a SessionConnectionInfo representing a connection to the session
	 */
	SessionConnection join(SessionConnectionCallback callback);
	
}
