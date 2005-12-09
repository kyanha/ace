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

import org.apache.log4j.Logger;

import ch.iserver.ace.collaboration.JoinRequest;
import ch.iserver.ace.net.InvitationProxy;
import ch.iserver.ace.net.JoinNetworkCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.impl.protocol.JoinRequestSenderFilter;
import ch.iserver.ace.net.impl.protocol.ProtocolConstants;
import ch.iserver.ace.net.impl.protocol.RemoteUserSession;
import ch.iserver.ace.net.impl.protocol.Request;
import ch.iserver.ace.net.impl.protocol.RequestFilter;
import ch.iserver.ace.net.impl.protocol.RequestImpl;
import ch.iserver.ace.net.impl.protocol.JoinRequestSenderFilter.JoinNetworkCallbackWrapper;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class InvitationProxyImpl implements InvitationProxy {

	private static Logger LOG = Logger.getLogger(InvitationProxyImpl.class);
	
	private RemoteDocumentProxyExt proxy;
	private RemoteUserSession session;
	private RequestFilter filter;
	
	public InvitationProxyImpl(RemoteDocumentProxyExt proxy, RemoteUserSession session, RequestFilter filter) {
		ParameterValidator.notNull("proxy", proxy);
		ParameterValidator.notNull("session", session);
		ParameterValidator.notNull("filter", filter);
		this.proxy = proxy;
		this.session = session;
		this.filter = filter;
	}
	
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
