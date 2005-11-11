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

package ch.iserver.ace.net.impl;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class PublishedDocument implements DocumentServer {

	private String docId;
	private DocumentServerLogic logic;
	private DocumentDetails details;
	
	public PublishedDocument(String id, DocumentServerLogic logic, DocumentDetails details) {
		ParameterValidator.notNull("id", id);
		this.docId = id;
		this.logic = logic;
		this.details = details;
	}

	public DocumentDetails getDocumentDetails() {
		return details;
	}
	
	public DocumentServerLogic getDocumentServerLogic() {
		return logic;
	}

	public String getId() {
		return docId;
	}

	/********************************************/
	/** methods from interface DocumentServer  **/
	/********************************************/
	public void setDocumentDetails(DocumentDetails details) {
		this.details = details;
		//TODO: notify participants
	}

	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	public void prepareShutdown() {
		// TODO Auto-generated method stub
		
	}
	
	public String toString() {
		return "PublishedDocument("+docId+", "+details.getTitle()+")";
	}
}
