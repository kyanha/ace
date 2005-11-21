/*
 * $Id:PublishedDocument.java 1205 2005-11-14 07:57:10Z zbinl $
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

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.JoinException;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.impl.protocol.NullRequestFilter;
import ch.iserver.ace.net.impl.protocol.ParticipantConnectionExt;
import ch.iserver.ace.net.impl.protocol.ProtocolConstants;
import ch.iserver.ace.net.impl.protocol.Request;
import ch.iserver.ace.net.impl.protocol.RequestFilter;
import ch.iserver.ace.net.impl.protocol.RequestImpl;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class PublishedDocument implements DocumentServer {

	private static Logger LOG = Logger.getLogger(PublishedDocument.class);
	
	private String docId;
	private DocumentServerLogic logic;
	private DocumentDetails details;
	private RequestFilter filter;
	private NetworkServiceExt service;
	private boolean isConcealed, isShutdown;
	
	public PublishedDocument(String id, DocumentServerLogic logic, DocumentDetails details, RequestFilter filter, NetworkServiceExt service) {
		ParameterValidator.notNull("id", id);
		LOG.debug("new PublishedDocument("+id+", "+details+")");
		this.docId = id;
		this.logic = logic;
		this.details = details;
		this.service = service;
		this.filter = (filter != null) ? filter : NullRequestFilter.getInstance();
		this.isConcealed = false;
		this.isShutdown = false;
	}

	public DocumentDetails getDocumentDetails() {
		return details;
	}
	
	public synchronized ParticipantPort join(ParticipantConnectionExt connection) throws JoinException {
		/* REMOVED TO FIX COMPILER ERROR
		if (!isConcealed()) {
			return logic.join(connection);
		} else {
			throw new JoinException();
		}*/
		return null;
	}

	public String getId() {
		return docId;
	}
	
	public boolean isConcealed() {
		return isConcealed;
	}
	
	public boolean isShutdown() {
		return isShutdown;
	}
	
	public String toString() {
		return "PublishedDocument("+docId+", '"+details.getTitle()+"')";
	}

	/********************************************/
	/** methods from interface DocumentServer  **/
	/********************************************/
	public void setDocumentDetails(DocumentDetails details) {
		if (isShutdown()) { 
			throw new IllegalStateException("document has been shutdown");
		} else if (!isConcealed()) {
			this.details = details;
			Request request = new RequestImpl(ProtocolConstants.DOCUMENT_DETAILS_CHANGED, null, this);
			filter.process(request);
		}
	}

	public void shutdown() {
		if (isShutdown()) throw new IllegalStateException("document has been shutdown already");
		//TODO: consider doing this task in ParticipantConnection.close()
		
		Request request = new RequestImpl(ProtocolConstants.CONCEAL, null, this);
		filter.process(request);
		service.conceal(getId());
		isShutdown = true;
	}

	public synchronized void prepareShutdown() {
		if (isShutdown()) throw new IllegalStateException("document has been shutdown already");
		//stop accepting joins
		isConcealed = true;
	}
}
