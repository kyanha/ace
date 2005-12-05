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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.ServerInfo;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.algorithm.TimestampFactory;
import ch.iserver.ace.net.DiscoveryNetworkCallback;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.impl.discovery.DiscoveryLauncher;
import ch.iserver.ace.net.impl.discovery.ExplicitUserDiscovery;
import ch.iserver.ace.net.impl.protocol.BEEPSessionListener;
import ch.iserver.ace.net.impl.protocol.BEEPSessionListenerFactory;
import ch.iserver.ace.net.impl.protocol.ParticipantConnectionImplFactory;
import ch.iserver.ace.net.impl.protocol.ProtocolConstants;
import ch.iserver.ace.net.impl.protocol.Request;
import ch.iserver.ace.net.impl.protocol.RequestFilter;
import ch.iserver.ace.net.impl.protocol.RequestFilterFactory;
import ch.iserver.ace.net.impl.protocol.RequestImpl;
import ch.iserver.ace.net.impl.protocol.SessionManager;
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
	private BEEPSessionListener sessionListener;
	
	private Discovery discovery;
	private ServerInfo info;
	private UserDetails details;
	private String userId;
	
	private Map publishedDocs;
	
	private static NetworkServiceImpl instance;
	
	private NetworkServiceImpl() {
		publishedDocs = Collections.synchronizedMap(new LinkedHashMap());
		requestChain = RequestFilterFactory.createClientChain();
		RemoteDocumentProxyFactory.init(requestChain);
		RemoteUserProxyFactory.init(requestChain);
		InvitationProxyFactory.init(requestChain);
		ParticipantConnectionImplFactory.init(requestChain);
	}
	
	public static NetworkServiceImpl getInstance() {
		if (instance == null) {
			instance = new NetworkServiceImpl();
		}
		return instance;
	}
	
	public NetworkServiceCallback getCallback() {
		return networkCallback;
	}

	public String getUserId() {
		return userId;
	}
	
	public UserDetails getUserDetails() {
		return details;
	}
	
	
	public Map getPublishedDocuments() {
		return publishedDocs;
	}
	
	
	/*******************************************/
	/** Methods from interface NetworkService **/
	/*******************************************/
	public ServerInfo getServerInfo() {
		return info;
	} 
	
	public void setUserId(String id) {
		this.userId = id;
	}
	
	/**
	 * @inheritDoc
	 */
	public void start() { 
		SessionManager.getInstance().setTimestampFactory(getTimestampFactory());
		//start protocol server
		sessionListener = BEEPSessionListenerFactory.create();
		sessionListener.start();
		//start discovery process
		DiscoveryLauncher launcher = new DiscoveryLauncher(this, requestChain);
		launcher.start();
		//TODO: warn message if dnssd is not installed locally
	}
	
	public void stop() {
		LOG.debug("--> stop()");
		
		//TODO: workout shutdown
		
//		try {
//			//TODO: what if at the same time a new message is received and processed?
//			//abort discovery
//			discovery.abort();
//			//close all open sessions
//			SessionManager.getInstance().closeSessions();
//			//stop beep session listener
//			sessionListener.terminate();
//		
//			//TODO: conceal message for documents not necessary -> check with raess
//		} catch (Exception e) {
//			LOG.warn("could not stop network layer smoothly ["+e+", "+e.getMessage()+"]");
//		}
		LOG.debug("<-- stop()");
	}
	
	public void setUserDetails(UserDetails details) {
		this.details = details;
		if (discovery != null) {
			discovery.setUserDetails(details);
		}
	}

	public void setTimestampFactory(TimestampFactory factory) {
		this.timestampFactory = factory;
	}

	public void setCallback(NetworkServiceCallback callback) {
		ParameterValidator.notNull("callback", callback);
		this.networkCallback = callback;
	}

	public void discoverUser(DiscoveryNetworkCallback callback, InetAddress addr, int port) {
		LOG.debug("--> discoverUser(" + addr + ":" + port + ")");
		
		ExplicitUserDiscovery userDiscovery = new ExplicitUserDiscovery(callback, addr, port);
		userDiscovery.start();
		
		LOG.debug("<-- discoverUser()");
	}

	public synchronized DocumentServer publish(DocumentServerLogic logic, DocumentDetails details) {
		LOG.info("--> publish("+details+")");
		PublishedDocument doc = new PublishedDocument(UUID.nextUUID(), logic, details, requestChain, this);
		publishedDocs.put(doc.getId(), doc);
		Request request = new RequestImpl(ProtocolConstants.PUBLISH, null, doc);
		requestChain.process(request);
		LOG.info("<-- publish()");
		return doc;
	}
	
	/**********************************************/
	/** Methods from interface NetworkServiceExt **/
	/**********************************************/
	public TimestampFactory getTimestampFactory() {
		return timestampFactory;
	}
	
	public void conceal(String docId) {
		publishedDocs.remove(docId);
	}
	
	
	public void setDiscovery(Discovery discovery) {
		this.discovery = discovery;
	}
	
	public void setServerInfo(ServerInfo info) {
		this.info = info;
	}
	
	public boolean hasPublishedDocuments() {
		return !publishedDocs.isEmpty();
	}
}
