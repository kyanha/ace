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

import ch.iserver.ace.DocumentDetails;


/**
 * A PublishedSession extends a Session with methods that are only
 * available to the owner (publisher) of the session itself. These
 * methods are {@link #kick(Participant)} and {@link #shutdown()}.
 *
 * <p>PublishedSessions are obtained by calling publish on the
 * {@link CollaborationService} instance.</p>
 */
public interface PublishedSession extends Session {
	
	/**
	 * Sets the document detail information.
	 * 
	 * @param details the new DocumentDetails
	 */
	void setDocumentDetails(DocumentDetails details);
	
	/**
	 * Kicks the given participant from the session.
	 *
	 * @param participant the participant to kick from the session
	 */
	void kick(Participant participant);
			
}
