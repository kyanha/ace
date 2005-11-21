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

package ch.iserver.ace.collaboration.jupiter.server;

import java.util.Iterator;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.collaboration.jupiter.server.serializer.SerializerCommand;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.PortableDocument;

/**
 * Extended interface for internal use of the server logic.
 */
public interface ServerLogic extends DocumentServerLogic {
	
	/**
	 * The participant id of the publisher.
	 */
	int PUBLISHER_ID = 0;
	
	/**
	 * Sets the document server that is associated with this server logic.
	 * 
	 * @param server the server
	 */
	void setDocumentServer(DocumentServer server);
		
	/**
	 * Adds a participant to the session logic.
	 * 
	 * @param participant the participant to add
	 */
	void addParticipant(SessionParticipant participant);
	
	/**
	 * Adds a command to the serializer queue.
	 * 
	 * @param command the command to add to the serializer queue
	 */
	void addCommand(SerializerCommand command);
	
	/**
	 * Gets an Iterator over all the participant proxy objects in the
	 * session.
	 * 
	 * @return an Iterator over all participant proxies
	 */
	Iterator getParticipantProxies();
	
	/**
	 * Retrieves the current document for sending it to a joining user.
	 * 
	 * @return the current document as present at the server
	 */
	PortableDocument getDocument();

	/**
	 * Retrieves the PublisherPort used by the publisher to send requests to
	 * the logical server.
	 * 
	 * @return the port for the publisher
	 */
	PublisherPort getPublisherPort();
	
	/**
	 * Sets the document details object for the session.
	 * 
	 * @param details the document details object
	 */
	void setDocumentDetails(DocumentDetails details);
	
	/**
	 * Notifies the server that the specified user left the editing session.
	 *
	 * @param participantId the participant id of the leaving user
	 */
	void leave(int participantId);

	/**
	 * Kicks the participant with the given id from the session.
	 * 
	 * @param participant the participant to be kicked
	 */
	void kick(int participant);
	
	/**
	 * @param connection
	 */
	void joinRejected(ParticipantConnection connection);
	
	/**
	 * @param connection
	 */
	void joinAccepted(ParticipantConnection connection);
	
	/**
	 * Prepares the shutdown of the server. After calling this method, the 
	 * server should no longer accept any join requests.
	 */
	void prepareShutdown();

	/**
	 * Shuts the server logic down. The server logic takes care that the
	 * associated document server is shut down too. 
	 */
	void shutdown();
			
}
