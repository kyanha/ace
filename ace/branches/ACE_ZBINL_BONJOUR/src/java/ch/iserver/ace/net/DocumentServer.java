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

import ch.iserver.ace.DocumentDetails;

/**
 * This interface represents the network layer server object for a document.
 * DocumentServer instances can be obtained from the NetworkService by 
 * publishing a document.
 *
 * @see ch.iserver.ace.net.NetworkService#publish(DocumentServerLogic)
 */
public interface DocumentServer {
	
	/**
	 * Sets the DocumentDetails for the published document.
	 * 
	 * @param details the DocumentDetails object
	 */
	void setDocumentDetails(DocumentDetails details);
	
	/**
	 * Shuts down the DocumentServer. The DocumentServer should take care that
	 * the document is no longer published on the network. Further, any
	 * resources associated with this particular document can be released.
	 * 
	 * <p><b>Note:</b> calling further methods on this object is an error
	 * and results in an IllegalStateException beeing thrown.</p>
	 */
	void shutdown();
	
	/**
	 * Notifies the document server that the collaboration layer is no longer
	 * accepting joins on the DocumentServerLogic. Further attempts to invoke
	 * join on the DocumentServerLogic will fail with an exception.
	 * 
	 * <p>The document should not be unpublished on the network. Wait until
	 * the shutdown method is called to unpublish the document.</p>
	 * 
	 * <p>TODO: JavaDoc Exception</p>
	 */
	void prepareShutdown();
	
	
}
