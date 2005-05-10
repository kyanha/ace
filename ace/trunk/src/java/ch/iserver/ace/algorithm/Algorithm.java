/*
 * $$Id$$
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

package ch.iserver.ace.algorithm;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;

/**
 * This interface is the basic interface every OT algorithm implementation
 * must implement. It contains methods for generating requests for
 * an operation, receiving requests and for site management.
 */
public interface Algorithm {

	/**
	 * Undo the last local operation.
	 * 
	 * @return the request to be sent to other sites
	 */
	public Request undo();
	
	/**
	 * Redo the last undone local operation.
	 * 
	 * @return the request to be sent to other sites
	 */
	public Request redo();
	
	/**
	 * Generates a request for the given operation. The operation is
	 * a locally generated operation. The returned request must be
	 * sent to the other sites.
	 * 
	 * @param op the operation for which a request should be generated
	 */
	public Request generateRequest(Operation op);
	
	/**
	 * Receives a request from a remote site. The request must be
	 * transformed and the resulting operation must be applied to
	 * the document model.
	 * 
	 * @param req the request to transform and apply
	 * @see DocumentModel
	 */
	public void receiveRequest(Request req);

	/**
	 * Notifies the algorithm that a new site with id <var>siteId</var> has
	 * been added. Depending on the algorithm, this method can be used to
	 * adjust internal data structures to the change.
	 * 
	 * @param siteId the id of the added site
	 */
	public void siteAdded(int siteId);
	
	/**
	 * Notifies the algorithm that a site with id <var>siteId</var> has been
	 * removed. Depending on the algorithm, this method can be used to
	 * adjust internal data structures to the change.
	 * 
	 * @param siteId the id of the removed site
	 */
	public void siteRemoved(int siteId);
	
	/**
	 * Initialize the algorithm with the given document and initial timestamp.
	 * 
	 * @param doc the document model to which operations are applied
	 * @param timestamp the initial timestamp associated with the document
	 */
	public void init(DocumentModel doc, Timestamp timestamp);
	
	/**
	 * Gets the document model used by the algorithm at the current time.
	 * 
	 * @return the document model
	 */
	public DocumentModel getDocument();
	
}
