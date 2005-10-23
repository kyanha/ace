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

import ch.iserver.ace.net.PortableDocument;

/**
 * The CollaborationService is the entry point for all collaboration operations.
 * There are methods related to registering listeners for incoming events
 * such as discoveries and invitations, and there are methods for publishing
 * local documents.
 */
public interface CollaborationService {

	/**
	 * Adds a discovery listener to the list of registered listeners.
	 *
	 * @param l the listener to add
	 */
	void addDiscoveryListener(DiscoveryListener l);
	
	/**
	 * Removes a discovery listener from the list of registered listeners.
	 *
	 * @param l the listener to remove
	 */
	void removeDiscoveryListener(DiscoveryListener l);
	
	/**
	 * Sets the InvitationCallback for the service. The InvitationCallback
	 * is responsible to handle all invitations sent to this user.
	 * 
	 * @param processor the InvitationCallback used by the service
	 */
	void setInvitationCallback(InvitationCallback processor);
	
	/**
	 * Publishes a document so that other users can join editing it
	 * over the network.
	 *
	 * @param document the document to publish
	 * @return a session for the publisher itself
	 */
	PublishedSession publish(PortableDocument document);

}
