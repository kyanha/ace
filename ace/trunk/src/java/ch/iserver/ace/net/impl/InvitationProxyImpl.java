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

import ch.iserver.ace.net.InvitationProxy;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.SessionConnection;
import ch.iserver.ace.net.SessionConnectionCallback;
import ch.iserver.ace.net.impl.protocol.ProtocolConstants;
import ch.iserver.ace.net.impl.protocol.RemoteUserSession;
import ch.iserver.ace.net.impl.protocol.Request;
import ch.iserver.ace.net.impl.protocol.RequestFilter;
import ch.iserver.ace.net.impl.protocol.RequestImpl;

/**
 *
 */
public class InvitationProxyImpl implements InvitationProxy {

	private static Logger LOG = Logger.getLogger(InvitationProxyImpl.class);
	
	private RemoteDocumentProxy proxy;
	private RemoteUserSession session;
	private RequestFilter filter;
	
	public InvitationProxyImpl(RemoteDocumentProxy proxy, RemoteUserSession session, RequestFilter filter) {
		this.proxy = proxy;
		this.session = session;
		this.filter = filter;
	}
	
	public RemoteDocumentProxy getDocument() {
		return proxy;
	}

	public SessionConnection accept(SessionConnectionCallback callback) {
		LOG.debug("--> accept("+callback+")");
		
		String docId = proxy.getId();

		SessionConnectionImpl connection = session.addSessionConnection(docId);
		connection.setSessionConnectionCallback(callback);
		Request request = new RequestImpl(ProtocolConstants.JOIN, proxy.getPublisher().getId(), docId);
		filter.process(request);
		
		LOG.debug("<-- accept()");
		return connection;
	}

	public void reject() {
		LOG.debug("invitation for '"+getDocument().getDocumentDetails().getTitle()+"' rejected.");
		proxy = null;
		session = null;
		filter = null;
		LOG.debug("--> reject(doc='" + getDocument().getDocumentDetails().getTitle() + "')");
		Request request = new RequestImpl(ProtocolConstants.INVITE_REJECTED, getDocument().getId(), session);
		filter.process(request);
		LOG.debug("<-- reject()");
	}

}
