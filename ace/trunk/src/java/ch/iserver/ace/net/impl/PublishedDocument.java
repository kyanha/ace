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
import ch.iserver.ace.net.InvitationPort;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.impl.protocol.NullRequestFilter;
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
	private DocumentServerLogic docServer;
	private DocumentDetails details;
	private RequestFilter filter;
	private NetworkServiceExt service;
	private boolean isConcealed, isShutdown;
	
	public PublishedDocument(String id, DocumentServerLogic docServer, DocumentDetails details, RequestFilter filter, NetworkServiceExt service) {
		ParameterValidator.notNull("id", id);
		LOG.debug("new PublishedDocument("+id+", "+details+")");
		this.docId = id;
		this.docServer = docServer;
		this.details = details;
		this.service = service;
		this.filter = (filter != null) ? filter : NullRequestFilter.getInstance();
		this.isConcealed = false;
		this.isShutdown = false;
	}

	public DocumentDetails getDocumentDetails() {
		return details;
	}
	
	public DocumentServerLogic getDocumentServerLogic() {
		return docServer;
	}
	
	public void join(ParticipantConnection connection) {
		docServer.join(connection);
	}

	public String getId() {
		return docId;
	}
	
	public synchronized boolean isConcealed() {
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
	
	public void invite(InvitationPort invitation) {
		// TODO: implement handling of invitations
		LOG.debug("--> invite("+invitation.getUser()+")");
		Request request = new RequestImpl(ProtocolConstants.INVITE, invitation.getUser().getId(), docId);
		filter.process(request);
		LOG.debug("<-- invite()");
	}
	
	
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
