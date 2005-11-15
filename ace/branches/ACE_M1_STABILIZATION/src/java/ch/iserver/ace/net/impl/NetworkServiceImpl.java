/*
 * $Id:NetworkServiceImpl.java 1205 2005-11-14 07:57:10Z zbinl $
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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.algorithm.TimestampFactory;
import ch.iserver.ace.net.DiscoveryNetworkCallback;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.impl.protocol.BEEPServer;
import ch.iserver.ace.net.impl.protocol.BEEPServerFactory;
import ch.iserver.ace.net.impl.protocol.DiscoveryLauncher;
import ch.iserver.ace.net.impl.protocol.ProtocolConstants;
import ch.iserver.ace.net.impl.protocol.Request;
import ch.iserver.ace.net.impl.protocol.RequestFilter;
import ch.iserver.ace.net.impl.protocol.RequestFilterFactory;
import ch.iserver.ace.net.impl.protocol.RequestImpl;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.UUID;

/**
 *
 */
public class NetworkServiceImpl implements NetworkServiceExt {
	
	private static Logger LOG = Logger.getLogger(NetworkServiceImpl.class);
	
	private TimestampFactory timestampFactory;
	private NetworkServiceCallback networkCallback;
	private RequestFilter requestChain;
	private BEEPServer server;
	
	private Discovery discovery;
	private UserDetails details;
	private String userId;
	
	private List publishedDocs;
	
	private static NetworkServiceImpl instance;
	
	private NetworkServiceImpl() {
		userId = UUID.nextUUID();
		publishedDocs = new ArrayList();
		requestChain = RequestFilterFactory.createClientChain();
		server = BEEPServerFactory.create();
		//TODO: when is it appropriate to start the server?
		server.start();
	}
	
	public static NetworkServiceImpl getInstance() {
		if (instance == null) {
			instance = new NetworkServiceImpl();
		}
		return instance;
	}
	
	
	public void setUserDetails(UserDetails details) {
		//TOOD: verify that: return immediately
		this.details = details;
		//update user details in sessions
		if (discovery != null) {
			//TODO: how is threading here?
			discovery.setUserDetails(details);
		}
	}

	public void setTimestampFactory(TimestampFactory factory) {
		this.timestampFactory = factory;
	}

	//TODO: how is threading here?
	public void setCallback(NetworkServiceCallback callback) {
		ParameterValidator.notNull("callback", callback);
		this.networkCallback = callback;
		//since the callback to the upper layer is available now, 
		//start discovery process
		launchDiscovery();
	}

	/**
	 * Launches the discovery process.
	 */
	private void launchDiscovery() {
		DiscoveryLauncher launcher = new DiscoveryLauncher(getUserId(), getCallback(), this);
		launcher.start();
	}
	
	public void setDiscovery(Discovery discovery) {
		this.discovery = discovery;
	}
	
	public NetworkServiceCallback getCallback() {
		return networkCallback;
	}

	public String getUserId() {
		return userId;
	}

	//immediate return
	public void discoverUser(DiscoveryNetworkCallback callback, InetAddress addr, int port) {
		throw new UnsupportedOperationException();
	}

	public synchronized DocumentServer publish(DocumentServerLogic logic, DocumentDetails details) {
		LOG.info("--> publish("+details+")");
		PublishedDocument doc = new PublishedDocument(UUID.nextUUID(), logic, details, requestChain);
		
		//TODO: maybe I need a map id-doc, get published docs at GetPDFilter
		publishedDocs.add(doc);
		
		//TODO: threading?
		Request request = new RequestImpl(ProtocolConstants.PUBLISH, doc);
		requestChain.process(request);
		LOG.info("<-- publish()");
		return doc;
	}
	
	public synchronized List getPublishedDocuments() {
		return publishedDocs;
	}
	
	public void discoverDocuments(RemoteUserProxyExt proxy) {
		//TODO: possibly include SingleThreadDomain between DiscoveryCallbackImpl and DocumentDiscovery?
		LOG.info("--> discoverDocuments() for ["+proxy.getUserDetails().getUsername()+"]");
		
		Request request = new RequestImpl(ProtocolConstants.PUBLISHED_DOCUMENTS, proxy);
		requestChain.process(request);
		
		LOG.info("<-- discoverDocuments()");
	}
}
