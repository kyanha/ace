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

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.net.PortableDocument;

/**
 * This interface describes the interface between the logic layer and
 * the application layer.
 */
public interface SessionCallback {
	
	/**
	 * Sets the document content to the given <var>doc</var>.
	 * 
	 * @param doc the new document content
	 */
	void setDocument(PortableDocument doc);
	
	/**
	 * Retrieves the document content. This method is only called on
	 * published sessions.
	 * 
	 * @return the document content
	 */	
	PortableDocument getDocument();
	
	/**
	 * Receives an operation from the given participant.
	 * 
	 * @param participant the participant that sent the operation
	 * @param operation the operation to be applied to the document
	 */
	void receiveOperation(Participant participant, Operation operation);
	
	/**
	 * Receives a caret update from the given participant.
	 * 
	 * @param participant the participant that sent the CaretUpdate
	 * @param update the caret update specification
	 */
	void receiveCaretUpdate(Participant participant, CaretUpdate update);
	
	/**
	 * Called to notify the document controller that the session was
	 * terminated, that is the publisher closed the document.
	 */
	void sessionTerminated();
	
	/**
	 * Called to notify the document controller that the local user has been
	 * kicked out of the session.
	 */
	void kicked();
	
}
