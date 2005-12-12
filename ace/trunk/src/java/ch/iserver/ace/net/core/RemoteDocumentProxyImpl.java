/*
 * $Id:RemoteDocumentProxyImpl.java 1205 2005-11-14 07:57:10Z zbinl $
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

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.JoinNetworkCallback;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.SessionConnection;
import ch.iserver.ace.net.SessionConnectionCallback;
import ch.iserver.ace.net.protocol.ProtocolConstants;
import ch.iserver.ace.net.protocol.Request;
import ch.iserver.ace.net.protocol.RequestImpl;
import ch.iserver.ace.net.protocol.filter.RequestFilter;
import ch.iserver.ace.net.protocol.filter.JoinRequestSenderFilter.JoinNetworkCallbackWrapper;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Default implementation of type {@link ch.iserver.ace.net.RemoteDocumentProxy}. 
 * 
 * <p>This class represents the client view of a published, shared document from another user.</p>
 * 
 * @see ch.iserver.ace.net.core.RemoteDocumentProxyExt
 */
public class RemoteDocumentProxyImpl implements RemoteDocumentProxyExt {

	private static Logger LOG = Logger.getLogger(RemoteDocumentProxyImpl.class);
	
	/**
	 * The document id
	 */
	private String id;
	
	/**
	 * The document details, basically the document name
	 */
	private DocumentDetails details;
	
	/**
	 * The publisher object
	 */
	private RemoteUserProxy publisher;
	
	/**
	 * The callback for join related actions.
	 */
	private JoinNetworkCallback callback;
	
	/**
	 * The request filter chain for outgoing request processing.
	 */
	private RequestFilter filterChain;
	
	/**
	 * Flag to indicate whether this document is joined.
	 */
	private boolean isJoined;
	
	/**
	 * Flag to indicate whether an invitation for this document was 
	 * accepted by the local user.
	 */
	private boolean hasInvitationAccepted;
	
	/**
	 * The session connection callback.
	 */
	private SessionConnectionCallback sessionCallback;
	
	/**
	 * Creates a new RemoteDocumentProxyImpl. Note that none of the arguments may be null.
	 * 
	 * @param id			the document id
	 * @param details	the document details containing the document title
	 * @param publisher	the RemoteUserProxy of the publisher	
	 * @param filter		the request filter chain
	 */
	public RemoteDocumentProxyImpl(String id, DocumentDetails details, RemoteUserProxy publisher, RequestFilter filter) {
		ParameterValidator.notNull("id", id);
		ParameterValidator.notNull("details", details);
		ParameterValidator.notNull("publisher", publisher);
		ParameterValidator.notNull("filter", filter);
		this.id = id;
		this.details = details;
		this.publisher = publisher;
		this.filterChain = filter;
		isJoined = false;
		hasInvitationAccepted = false;
	}
	
	
	/***************************************************/
	/** methods from interface RemoteDocumentProxyExt **/
	/***************************************************/
	
	/**
	 * @inheritDoc
	 */
	public SessionConnectionCallback getSessionConnectionCallback() {
		return sessionCallback;
	}
	
	/**
	 * @see ch.iserver.ace.net.core.RemoteDocumentProxyExt#setDocumentDetails(DocumentDetails)
	 */
	public void setDocumentDetails(DocumentDetails details) {
		this.details = details;
	}
	
	/**
	 * @inheritDoc
	 */
	public SessionConnectionCallback joinAccepted(SessionConnection connection) {
		LOG.debug("--> joinAccepted()");
		isJoined = true;
		SessionConnectionCallback sessionCb = callback.accepted(connection);
		ParameterValidator.notNull("sesionCallback", sessionCb);
		sessionCallback = sessionCb;
		LOG.debug("<-- joinAccepted()");
		return sessionCallback;
	}
	
	/**
	 * @inheritDoc
	 */
	public void joinRejected(int code) {
		LOG.debug("--> joinRejected("+code+")");
		callback.rejected(code);
		LOG.debug("<-- joinRejected()");
	}
	
	/**
	 * @inheritDoc
	 */
	public boolean isJoined() {
		return isJoined;
	}
	
	/**
	 * @inheritDoc
	 */
	public void cleanupAfterLeave() {
		isJoined = false;
		hasInvitationAccepted = false;
		callback = null;
	}
	
	/**
	 * Called when the local user accepted an invitation for this
	 * document.
	 * 
	 * @param callback 	the JoinNetworkCallback object
	 */
	public void invitationAccepted(JoinNetworkCallback callback) {
		this.callback = callback;
		hasInvitationAccepted = true;
	}
	
	/************************************************/
	/** methods from interface RemoteDocumentProxy **/
	/************************************************/
	
	/**
	 * @see ch.iserver.ace.net.RemoteDocumentProxy#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * @see ch.iserver.ace.net.RemoteDocumentProxy#getDocumentDetails()
	 */
	public DocumentDetails getDocumentDetails() {
		return details;
	}

	/**
	 * @see ch.iserver.ace.net.RemoteDocumentProxy#getPublisher()
	 */
	public RemoteUserProxy getPublisher() {
		return publisher;
	}

	/**
	 * @see ch.iserver.ace.net.RemoteDocumentProxy#join(ch.iserver.ace.net.JoinNetworkCallback)
	 */
	public void join(JoinNetworkCallback callback) {
		LOG.debug("--> join("+callback+")");
		if (hasInvitationAccepted) {
			LOG.warn("do not accept join request, user was already invited to join document");
		} else {
			ParameterValidator.notNull("callback", callback);
			this.callback = callback;
			JoinNetworkCallbackWrapper wrapper =  new JoinNetworkCallbackWrapper(id, callback);
			Request request = new RequestImpl(ProtocolConstants.JOIN, publisher.getId(), wrapper);
			filterChain.process(request);
		}
		LOG.debug("<-- join()");
	}
	
	/**
	 * @inheritDoc
	 */
	public String toString() {
		return "RemoteDocumentProxyImpl("+getId()+", "+getDocumentDetails()+", "+publisher.getId()+")";
	}
	
	
	/**
	 * @inheritDoc
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof RemoteDocumentProxyImpl) {
			RemoteDocumentProxyImpl proxy = (RemoteDocumentProxyImpl) obj;
			return this.getId().equals(proxy.getId()) &&
				this.getDocumentDetails().equals(proxy.getDocumentDetails()) &&
				this.getPublisher().getId().equals(proxy.getPublisher().getId());
		}
		return false;
	}
	
	/**
	 * @inheritDoc
	 */
	public int hashCode() {
		int hash = 13;
		hash += id.hashCode();
		hash += details.hashCode();
		hash += publisher.hashCode();		
		return hash;
		
	}

}
