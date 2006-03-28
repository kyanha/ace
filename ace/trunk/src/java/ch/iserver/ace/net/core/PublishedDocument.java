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

package ch.iserver.ace.net.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.InvitationPort;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.protocol.ProtocolConstants;
import ch.iserver.ace.net.protocol.Request;
import ch.iserver.ace.net.protocol.RequestImpl;
import ch.iserver.ace.net.protocol.filter.LogFilter;
import ch.iserver.ace.net.protocol.filter.RequestFilter;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Default implementation for interface {@link ch.iserver.ace.net.DocumentServer}.
 * PublishedDocument represents a published document of the local user and is responsible
 * for processing invitations as well as accepted join requests from other users. 
 * 
 * <p>This class represents the server view of a published, shared document by the local user.</p>
 * 
 * @see ch.iserver.ace.net.DocumentServer
 */
public class PublishedDocument implements DocumentServer {

	private static Logger LOG = Logger.getLogger(PublishedDocument.class);
	
	/**
	 * The id of this document
	 */
	private String docId;
	
	/**
	 * The document server logic. Used to pass ParticipantConnection's
	 * to the upper layer for users who want to join this document.
	 */
	private DocumentServerLogic docServer;
	
	/**
	 * The document details of this document.
	 */
	private DocumentDetails details;
	
	/**
	 * The request filter chain to process outoing requests.
	 */
	private RequestFilter filter;
	
	/**
	 * The network service object used for published document
	 * management.
	 */
	private NetworkServiceExt service;
	
	/**
	 * Flag to indicate if this document is shutdown.
	 */
	private boolean isShutdown;
	
	/**
	 * A map containing InvitationPorts for invited users.
	 */
	private Map invitations;
	
	/**
	 * Creates a new PublishedDocument with the given data.
	 * 
	 * @param id			the document id
	 * @param docServer	the document server logic 
	 * @param details	the document details 
	 * @param filter		the request filter chain for request processing
	 * @param service	the network service 
	 */
	public PublishedDocument(String id, DocumentServerLogic docServer, 
			DocumentDetails details, RequestFilter filter, NetworkServiceExt service) {
		ParameterValidator.notNull("id", id);
		ParameterValidator.notNull("documentServerLogic", docServer);
		this.docId = id;
		this.docServer = docServer;
		this.details = details;
		this.service = service;
		this.filter = (filter != null) ? filter : new LogFilter(null, false);
		this.isShutdown = false;
		this.invitations  = Collections.synchronizedMap(new LinkedHashMap());
	}

	/**
	 * Gets the document details.
	 * 
	 * @return the DocumentDetails
	 */
	public DocumentDetails getDocumentDetails() {
		return details;
	}
	
	/**
	 * Join request by a user represented by the given
	 * <cdoe>ParticipantConnection</code>.
	 * 
	 * @param connection	the connection to the joining user
	 * @see ParticipantConnection
	 */
	public void join(ParticipantConnection connection) {
		docServer.join(connection);
	}

	/**
	 * Gets the document id.
	 * 
	 * @return the document id
	 */
	public String getId() {
		return docId;
	}
	
	/**
	 * Returns true if this document is shutdown.
	 * 
	 * @return true iff this document is shutdown
	 */
	public synchronized boolean isShutdown() {
		return isShutdown;
	}
	
	/**
	 * Checks wheter the given user has been invited.
	 * 
	 * @param userId		the user id
	 * @return	true iff the user has been invited, false otherwise
	 */
	public boolean isUserInvited(String userId) {
		return invitations.containsKey(userId);
	}
	
	/**
	 * Joins an invited user to this document. This method is to be called
	 * when the invited user accepted the invitation and wants to join.
	 * 
	 * @param userId			the user id
	 * @param connection		the ParticipantConnection for the user
	 * @see ParticipantConnection
	 */
	public void joinInvitedUser(String userId, ParticipantConnection connection) {
		LOG.debug("--> joinInvitedUser()");
		InvitationPort port = (InvitationPort) invitations.remove(userId);
		port.accept(connection);
		LOG.debug("<-- joinInvitedUser()");
	}
	
	/**
	 * Notifies the upper layer that the invited user rejected the
	 * invitation.
	 * 
	 * @param userId 	the id of the user who rejected the invitation
	 */
	public void rejectInvitedUser(String userId) {
		LOG.debug("--> rejectedInvitedUser()");
		InvitationPort port = (InvitationPort) invitations.remove(userId);
		if (port != null) {
			port.reject();
		} else {
			LOG.warn("InvitationPort for [" + userId + "] not found.");
		}
		LOG.debug("<-- rejectedInvitedUser()");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "PublishedDocument("+docId+", '"+details.getTitle()+"')";
	}

	/********************************************/
	/** methods from interface DocumentServer  **/
	/********************************************/
	
	/**
	 * {@inheritDoc}
	 */
	public void invite(InvitationPort invitation) {
		LOG.debug("--> invite("+invitation.getUser()+")");
		String userId = invitation.getUser().getId();
		invitations.put(userId, invitation);
		Request request = new RequestImpl(ProtocolConstants.INVITE, userId, this);
		filter.process(request);
		LOG.debug("<-- invite()");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setDocumentDetails(DocumentDetails details) {
		if (isShutdown()) { 
			throw new IllegalStateException("document has been shutdown");
		}
		this.details = details;
		Request request = new RequestImpl(ProtocolConstants.DOCUMENT_DETAILS_CHANGED, null, this);
		filter.process(request);
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void shutdown() {
		LOG.info("--> shutdown()");
		if (isShutdown()) throw new IllegalStateException("document has been shutdown already");
		Request request = new RequestImpl(ProtocolConstants.CONCEAL, null, this);
		filter.process(request);
		if (!service.isStopped()) {
			service.conceal(getId());
		}
		//stop accepting joins
		isShutdown = true;
		LOG.info("<-- shutdown()");
	}
}
