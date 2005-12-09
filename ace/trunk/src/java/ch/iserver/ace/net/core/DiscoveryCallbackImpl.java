/*
 * $Id:DiscoveryCallbackImpl.java 1205 2005-11-14 07:57:10Z zbinl $
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

import java.util.Map;

import org.apache.log4j.Logger;

import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.discovery.DiscoveryManager;
import ch.iserver.ace.net.discovery.DiscoveryManagerFactory;
import ch.iserver.ace.net.protocol.ProtocolConstants;
import ch.iserver.ace.net.protocol.RemoteUserSession;
import ch.iserver.ace.net.protocol.Request;
import ch.iserver.ace.net.protocol.RequestFilter;
import ch.iserver.ace.net.protocol.RequestImpl;
import ch.iserver.ace.net.protocol.SessionManager;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Default implementation of interface {@link ch.iserver.ace.net.core.DiscoveryCallback}.
 *
 */
public class DiscoveryCallbackImpl implements DiscoveryCallback {

	private static Logger LOG = Logger.getLogger(DiscoveryCallbackImpl.class);
	
	/**
	 *  The network service callback to notify the upper layer about events.
	 */
	private NetworkServiceCallback callback;
	
	/**
	 * The network service reference used for document management.
	 */
	private NetworkServiceExt service;
	
	/**
	 * The request filter chain to process outoing requests.
	 */
	private RequestFilter filter;
	
	/**
	 * Creates a new DiscoveryCallbackImpl.
	 * 
	 * @param callback	the network service callback object from the upper layer
	 * @param service	the network service object for document management
	 * @param filter		the request filter object for request processing
	 */
	public DiscoveryCallbackImpl(NetworkServiceCallback callback, NetworkServiceExt service, RequestFilter filter) {
		ParameterValidator.notNull("callback", callback);
		ParameterValidator.notNull("filter", filter);
		this.callback = callback;
		this.service = service;
		this.filter = filter;
	}
	
	
	/**********************************************/
	/** methods from interface DiscoveryCallback **/
	/**********************************************/
	
	/**
	 * @inheritDoc
	 */
	public void userDiscovered(RemoteUserProxyExt proxy) {
		LOG.debug("--> userDiscovered("+proxy+")");
		callback.userDiscovered(proxy);
		LOG.debug("<-- userDiscovered()");
	}
	
	/**
	 * @inheritDoc
	 */
	public void userDiscoveryCompleted(RemoteUserProxyExt user) {
		LOG.debug("--> userDiscoveryCompleted("+user+")");

		if (service.hasPublishedDocuments()) {
			sendDocuments(user);
		} else {
			LOG.debug("no published documents, not sending anything to ["+user.getUserDetails().getUsername()+"]");
		}
		LOG.debug("<-- userDiscoveryCompleted()");
	}
	
	/**
	 * @inheritDoc
	 */
	private void sendDocuments(RemoteUserProxyExt user) {
		LOG.info("--> sendDocuments() to ["+user.getUserDetails().getUsername()+"]");
		Request request = new RequestImpl(ProtocolConstants.SEND_DOCUMENTS, null, user);
		filter.process(request);
		LOG.info("<-- sendDocuments()");
	}

	/**
	 * @inheritDoc
	 */
	public void userDetailsChanged(RemoteUserProxyExt proxy) {
		LOG.debug("--> userDetailsChanged("+proxy+")");
		callback.userDetailsChanged(proxy);
		LOG.debug("<-- userDetailsChanged()");
	}
	
	/**
	 * @inheritDoc
	 */
	public void userDiscarded(RemoteUserProxyExt proxy) {
		LOG.debug("--> userDiscarded("+proxy+")");
		
		Map documents = proxy.getDocuments();
		RemoteDocumentProxy[] docs = null;
		synchronized(documents) {
			docs = (RemoteDocumentProxy[]) documents.values().toArray(new RemoteDocumentProxy[0]);
		}
		
		if (docs != null && docs.length > 0) {
			callback.documentDiscarded(docs);
		}
		callback.userDiscarded(proxy);
		
		DiscoveryManager manager = DiscoveryManagerFactory.getDiscoveryManager();
		String userId = proxy.getId();
		if (manager.hasSessionEstablished(userId)) {
			RemoteUserSession session = 
				SessionManager.getInstance().removeSession(userId);
			session.cleanup();
			DiscoveryManagerFactory.getDiscoveryManager().setSessionTerminated(userId);
		}
		LOG.debug("<-- userDiscarded()");
	}
}
