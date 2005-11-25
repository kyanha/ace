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

package ch.iserver.ace.net.impl;

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.JoinNetworkCallback;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.SessionConnection;
import ch.iserver.ace.net.SessionConnectionCallback;
import ch.iserver.ace.net.impl.protocol.ProtocolConstants;
import ch.iserver.ace.net.impl.protocol.Request;
import ch.iserver.ace.net.impl.protocol.RequestFilter;
import ch.iserver.ace.net.impl.protocol.RequestImpl;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class RemoteDocumentProxyImpl implements RemoteDocumentProxyExt {

	private static Logger LOG = Logger.getLogger(RemoteDocumentProxyImpl.class);
	
	private String id;
	private DocumentDetails details;
	private RemoteUserProxy publisher;
	private JoinNetworkCallback callback;
	private RequestFilter filterChain;
	private SessionConnection sessionConnection;
	private SessionConnectionCallback sessionConnectionCallback;
	private boolean isJoined;
	
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
	}
	
	
	/***************************************************/
	/** methods from interface RemoteDocumentProxyExt **/
	/***************************************************/
	
	/**
	 * @see ch.iserver.ace.net.impl.RemoteDocumentProxyExt#setDocumentDetails(DocumentDetails)
	 */
	public void setDocumentDetails(DocumentDetails details) {
		this.details = details;
	}
	
	public void joinAccepted(PortableDocumentExt document, SessionConnection connection) {
		LOG.debug("--> joinAccepted()");
		isJoined = true;
		sessionConnection = connection;
		sessionConnectionCallback = callback.accepted(sessionConnection);
		sessionConnectionCallback.setDocument(document);
		LOG.debug("<-- joinAccepted()");
	}
	
	public void joinRejected(int code) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isJoined() {
		return isJoined;
	}
	
	public void cleanupAfterLeave() {
		isJoined = false;
		sessionConnectionCallback = null;
		callback = null;
		sessionConnection = null;
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
		ParameterValidator.notNull("callback", callback);
		this.callback = callback;
		Request request = new RequestImpl(ProtocolConstants.JOIN, publisher.getId(), id);
		filterChain.process(request);
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
