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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.InvitationPort;
import ch.iserver.ace.net.ParticipantConnection;
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
	private boolean isShutdown;
	private Map invitations;
	
	public PublishedDocument(String id, DocumentServerLogic docServer, DocumentDetails details, RequestFilter filter, NetworkServiceExt service) {
		ParameterValidator.notNull("id", id);
		ParameterValidator.notNull("documentServerLogic", docServer);
		this.docId = id;
		this.docServer = docServer;
		this.details = details;
		this.service = service;
		this.filter = (filter != null) ? filter : NullRequestFilter.getInstance();
		this.isShutdown = false;
		this.invitations  = Collections.synchronizedMap(new LinkedHashMap());
	}

	public DocumentDetails getDocumentDetails() {
		return details;
	}
	
	public void join(ParticipantConnection connection) {
		docServer.join(connection);
	}

	public String getId() {
		return docId;
	}
	
	public synchronized boolean isShutdown() {
		return isShutdown;
	}
	
	public String toString() {
		return "PublishedDocument("+docId+", '"+details.getTitle()+"')";
	}
	
	public boolean isUserInvited(String userId) {
		return invitations.containsKey(userId);
	}
	
	public void joinInvitedUser(String userId, ParticipantConnection connection) {
		LOG.debug("--> joinInvitedUser()");
		InvitationPort port = (InvitationPort) invitations.remove(userId);
		port.accept(connection);
		LOG.debug("<-- joinInvitedUser()");
	}
	
	public void rejectInvitedUser(String userId) {
		LOG.debug("--> rejectedInvitedUser()");
		InvitationPort port = (InvitationPort) invitations.remove(userId);
		port.reject();
		LOG.debug("<-- rejectedInvitedUser()");
	}

	/********************************************/
	/** methods from interface DocumentServer  **/
	/********************************************/
	
	public void invite(InvitationPort invitation) {
		LOG.debug("--> invite("+invitation.getUser()+")");
		String userId = invitation.getUser().getId();
		invitations.put(userId, invitation);
		Request request = new RequestImpl(ProtocolConstants.INVITE, userId, docId);
		filter.process(request);
		LOG.debug("<-- invite()");
	}
	
	public void setDocumentDetails(DocumentDetails details) {
		if (isShutdown()) { 
			throw new IllegalStateException("document has been shutdown");
		}
		this.details = details;
		Request request = new RequestImpl(ProtocolConstants.DOCUMENT_DETAILS_CHANGED, null, this);
		filter.process(request);
	}

	public synchronized void shutdown() {
		if (isShutdown()) throw new IllegalStateException("document has been shutdown already");
		Request request = new RequestImpl(ProtocolConstants.CONCEAL, null, this);
		filter.process(request);
		service.conceal(getId());
		//stop accepting joins
		isShutdown = true;
	}
}
