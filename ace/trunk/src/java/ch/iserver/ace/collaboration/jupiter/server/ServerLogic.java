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
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.PortableDocument;

/**
 *
 */
public interface ServerLogic extends DocumentServerLogic {
	
	void addParticipant(SessionParticipant participant);
	
	PublisherPort getPublisherPort();
	
	void setDocumentDetails(DocumentDetails details);
	
	void kick(int participant);
	
	void prepareShutdown();

	void shutdown();
	
	Iterator getParticipantProxies();
	
	PortableDocument getDocument();
	
	/**
	 * Notifies the server that the specified user leaves the editing session.
	 *
	 * @param participantId the participant id of the leaving user
	 */
	void leave(int participantId);
	
}
