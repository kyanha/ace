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

import java.net.InetAddress;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.ServerInfo;
import ch.iserver.ace.UserDetails;

/**
 * The CollaborationService is the entry point for all collaboration operations.
 * There are methods related to registering listeners for incoming events
 * such as discoveries and invitations, and there are methods for publishing
 * local documents.
 */
public interface CollaborationService {
	
	/**
	 * @return
	 */
	ServerInfo getServerInfo();
	
	/**
	 * Starts the CollaborationService. No events are fired before this
	 * method is called.
	 */
	void start();
	
	/**
	 * Stops the CollaborationService. This method destroys the collaboration
	 * service. Do not call any methods after this method call.
	 */
	void stop();
	
	/**
	 * Sets the user id of the local user. This method has to be called before
	 * starting the service with the {@link #start()} method. Note, it is
	 * an error to set the user id twice!
	 * 
	 * @param id the id of the local user
	 */
	void setUserId(String id);
	
	/**
	 * Sets the UserDetails for the local user.
	 * 
	 * @param details the new UserDetails
	 */
	void setUserDetails(UserDetails details);
	
	/**
	 * Adds a user discovery listener to the list of registered listeners.
	 *
	 * @param l the listener to add
	 */
	void addUserListener(UserListener l);
	
	/**
	 * Removes a user discovery listener from the list of registered listeners.
	 *
	 * @param l the listener to remove
	 */
	void removeUserListener(UserListener l);
	
	/**
	 * Adds a document listener to the list of registered listeners.
	 * 
	 * @param l the listener to add
	 */
	void addDocumentListener(DocumentListener l);
	
	/**
	 * Removes a document discovery listener from the list of registered 
	 * listeners.
	 * 
	 * @param l the listener to remove
	 */
	void removeDocumentListener(DocumentListener l);
	
	/**
	 * Sets the InvitationCallback for the service. The InvitationCallback
	 * is responsible to handle all invitations sent to this user.
	 * 
	 * @param processor the InvitationCallback used by the service
	 */
	void setInvitationCallback(InvitationCallback processor);
	
	/**
	 * Sets the ServiceFailureHandler for the service. This handler gets
	 * notified about failures in the collaboration and network layers.
	 * 
	 * @param handler the new failure handler
	 */
	void setFailureHandler(ServiceFailureHandler handler);
	
	/**
	 * Publishes a document so that other users can join editing it
	 * over the network.
	 *
	 * @param callback the SessionCallback used for callbacks
	 * @param document the document to publish
	 * @return a session for the publisher itself
	 */
	PublishedSession publish(PublishedSessionCallback callback, DocumentModel document);
	
	/**
	 * Initiates an explicit discovery of a user. The network layer tries to 
	 * contact the given host on the given port. The result is communicated 
	 * to the DiscoveryCallback objects.
	 * 
	 * @param callback the DiscoveryCallback to be notified
	 * @param addr the target address
	 * @param port the target port
	 */	
	void discoverUser(DiscoveryCallback callback, InetAddress addr, int port);
	
}
