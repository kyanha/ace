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

package ch.iserver.ace.net.core;

import org.apache.log4j.Logger;

import ch.iserver.ace.collaboration.JoinRequest;
import ch.iserver.ace.net.InvitationProxy;
import ch.iserver.ace.net.JoinNetworkCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.protocol.ProtocolConstants;
import ch.iserver.ace.net.protocol.RemoteUserSession;
import ch.iserver.ace.net.protocol.Request;
import ch.iserver.ace.net.protocol.RequestImpl;
import ch.iserver.ace.net.protocol.filter.RequestFilter;
import ch.iserver.ace.net.protocol.filter.JoinRequestSenderFilter.JoinNetworkCallbackWrapper;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Default implementation of interface {@link ch.iserver.ace.net.InvitationProxy}.
 * 
 */
public class InvitationProxyImpl implements InvitationProxy {

	private static Logger LOG = Logger.getLogger(InvitationProxyImpl.class);
	
	/**
	 * The RemoteDocumentProxy this invitation is about.
	 */
	private RemoteDocumentProxyExt proxy;
	
	/**
	 * The RemoteUserSession this invitation is directed to.
	 */
	private RemoteUserSession session;
	
	/**
	 * The request filter chain to process outoing requests.
	 */
	private RequestFilter filter;
	
	
	/**
	 * Creates a new InvitationProxy.
	 * 
	 * @param proxy		the RemoteDocumentProxy object this invitation is about
	 * @param session	the RemoteUserSession object	this invitation is for
	 * @param filter		the RequestFilter object to process outgoing requests
	 */
	public InvitationProxyImpl(RemoteDocumentProxyExt proxy, RemoteUserSession session, RequestFilter filter) {
		ParameterValidator.notNull("proxy", proxy);
		ParameterValidator.notNull("session", session);
		ParameterValidator.notNull("filter", filter);
		this.proxy = proxy;
		this.session = session;
		this.filter = filter;
	}
	
	/**
	 * @see ch.iserver.ace.net.InvitationProxy#getDocument()
	 */
	public RemoteDocumentProxy getDocument() {
		return proxy;
	}

	/**
	 * @see ch.iserver.ace.net.InvitationProxy#accept(ch.iserver.ace.net.JoinNetworkCallback)
	 */
	public void accept(JoinNetworkCallback callback) {
		LOG.debug("--> accept("+callback+")");
		ParameterValidator.notNull("callback", callback);
		RemoteUserProxyExt user = session.getUser();
		if (user != null && user.hasDocumentShared(proxy.getId())) {
			proxy.invitationAccepted(callback);
			String docId = proxy.getId();
			JoinNetworkCallbackWrapper wrapper =  new JoinNetworkCallbackWrapper(docId, callback);
			Request request = new RequestImpl(ProtocolConstants.JOIN, proxy.getPublisher().getId(), wrapper);
			filter.process(request);
		} else { //user or documents has been discarded in the meanwhile
			LOG.warn("document to be joined no longer available [" +
					((user != null) ? user.getUserDetails().getUsername() : null) + ", "+proxy.getId() + "]");
			callback.rejected(JoinRequest.SHUTDOWN);
			proxy = null;
			session = null;
			filter = null;
		} 
		LOG.debug("<-- accept()");		
	}

	/**
	 * @see ch.iserver.ace.net.InvitationProxy#reject()
	 */
	public void reject() {
		LOG.debug("invitation for '"+getDocument().getDocumentDetails().getTitle()+"' rejected.");
		RemoteUserProxyExt user = session.getUser();
		if (user != null && user.hasDocumentShared(proxy.getId())) {
			Request request = new RequestImpl(ProtocolConstants.INVITE_REJECTED, getDocument().getId(), session);
			filter.process(request);
		} else {
			LOG.debug("document/user dissapeared in the meanwhile, don't send rejected message");
		}
		proxy = null;
		session = null;
		filter = null;
		LOG.debug("<-- reject()");
	}

}
