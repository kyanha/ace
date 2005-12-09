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
package ch.iserver.ace.net.core;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
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
import ch.iserver.ace.net.discovery.DiscoveryLauncher;
import ch.iserver.ace.net.discovery.DiscoveryManagerFactory;
import ch.iserver.ace.net.discovery.ExplicitUserDiscovery;
import ch.iserver.ace.net.protocol.BEEPSessionListener;
import ch.iserver.ace.net.protocol.BEEPSessionListenerFactory;
import ch.iserver.ace.net.protocol.ParticipantConnectionImplFactory;
import ch.iserver.ace.net.protocol.ProtocolConstants;
import ch.iserver.ace.net.protocol.RemoteUserSession;
import ch.iserver.ace.net.protocol.Request;
import ch.iserver.ace.net.protocol.RequestFilter;
import ch.iserver.ace.net.protocol.RequestFilterFactory;
import ch.iserver.ace.net.protocol.RequestImpl;
import ch.iserver.ace.net.protocol.SessionManager;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.UUID;

/**
 * Default implementation for interface {@link ch.iserver.ace.net.NetworkService}.
 * This class is the main entry point to the network layer for the upper layer.
 */
public class NetworkServiceImpl implements NetworkServiceExt {
	
	private static Logger LOG = Logger.getLogger(NetworkServiceImpl.class);
	
	/**
	 * The TimestampFactory object used for the creation of timestamps
	 * when incoming requests are parsed.
	 */
	private TimestampFactory timestampFactory;
	
	/**
	 * The callback for the upper layer.
	 */
	private NetworkServiceCallback networkCallback;
	
	/**
	 * The request filter chain to process outoing requests.
	 */
	private RequestFilter requestChain;
	
	/**
	 * The BEEP session listener object.
	 */
	private BEEPSessionListener sessionListener;
	
	/**
	 * The discovery object to communicate with the discovery .
	 */
	private Discovery discovery;
	
	/**
	 * Ther server info for the local user.
	 */
	private ServerInfo info;
	
	/**
	 * The user details of the local user.
	 */
	private UserDetails details;
	
	/**
	 * The user id for the local user.
	 */
	private String userId;
	
	/**
	 * Flag to indicate that the network layer has been stopped.
	 */
	private boolean isStopped = false;
	
	/**
	 * The published documents of the local user.
	 */
	private Map publishedDocs;
	
	/**
	 * The singleton instance.
	 */
	private static NetworkServiceImpl instance;
	
	/**
	 * Private constructor. 
	 * 
	 * Initializes several factories and the RequestFilter chain.
	 */
	private NetworkServiceImpl() {
		publishedDocs = Collections.synchronizedMap(new LinkedHashMap());
		requestChain = RequestFilterFactory.createClientChain();
		RemoteDocumentProxyFactory.init(requestChain);
		RemoteUserProxyFactory.init(requestChain);
		InvitationProxyFactory.init(requestChain);
		ParticipantConnectionImplFactory.init(requestChain);
	}
	
	/**
	 * Gets the NetworkServiceImpl instance.
	 * 
	 * @return the NetworkServiceImpl object
	 */
	public static NetworkServiceImpl getInstance() {
		if (instance == null) {
			instance = new NetworkServiceImpl();
		}
		return instance;
	}
	
	/**
	 * Gets the NetworkServiceCallback object.
	 * 
	 * @return the callback
	 */
	public NetworkServiceCallback getCallback() {
		return networkCallback;
	}

	/**
	 * Gets the id of the local user.
	 * 
	 * @return the id
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * Gets the details of the local user.
	 * 
	 * @return the UserDetails
	 */
	public UserDetails getUserDetails() {
		return details;
	}
	
	/**
	 * Gets the published documents as a document id to
	 * <code>PublishedDocument</code> map.
	 * 
	 * @return the published documents in a Map
	 */
	public Map getPublishedDocuments() {
		return publishedDocs;
	}
	
	
	/*******************************************/
	/** Methods from interface NetworkService **/
	/*******************************************/
	
	/**
	 * @inheritDoc
	 */
	public ServerInfo getServerInfo() {
		return info;
	} 
	
	/**
	 * @inheritDoc
	 */
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
	}
	
	/**
	 * @inheritDoc
	 */
	public void stop() {
		LOG.debug("--> stop()");
		isStopped = true;
		try {
			//stop session listener
			sessionListener.terminate();
			//end discovery
			discovery.abort();
			/** server site **/
			//close() on all participantconnections of all documents
			Map docs = getPublishedDocuments();
			LOG.debug("close() on all participant connections of all documents (" + docs.size() + ")");
			synchronized(docs) {
				Iterator iter = docs.values().iterator();
				while (iter.hasNext()) {
					PublishedDocument doc = (PublishedDocument) iter.next();
					doc.shutdown();
				}
			}
			
			/** client site **/
			//send leave to all joined document publishers
			LOG.debug("send leave to all joined document publishers");
			Map users = DiscoveryManagerFactory.getDiscoveryManager().getUsers();
			synchronized (users) {
				Iterator iter = users.values().iterator();
				while (iter.hasNext()) { //for each user
					RemoteUserProxyExt user = (RemoteUserProxyExt) iter.next();
					LOG.debug("process stop() for "+user.getUserDetails().getUsername());
					RemoteUserSession session = SessionManager.getInstance().removeSession(user.getId()); //session may be null
					Map documents = user.getDocuments();
					synchronized(documents) {
						Iterator iter2 = documents.values().iterator();
						while (iter2.hasNext()) { //for each doc of the current user
							RemoteDocumentProxyExt doc = (RemoteDocumentProxyExt) iter2.next();
							if (doc.isJoined() && session != null) {
								session.getSessionConnection(doc.getId()).leave();
							}
						}
					}
					if (!user.isDNSSDdiscovered()) { //send sign-off message to explicitly discovered user
						LOG.debug("send sign-off message to explicitly discovered user");
						String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
								"<ace><notification><userDiscarded id=\"" + getUserId() + "\"/></notification></ace>";
						byte[] data = message.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
						LOG.debug("send userDiscarded to " + user.getUserDetails().getUsername());
						session.getMainConnection().send(data, null, null);
					}
					//close main channel and TCPSession
					LOG.debug("close main channel and TCPSession");
					if (session != null) {
						session.close();
					}
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			LOG.warn("could not stop network layer smoothly ["+e+", "+e.getMessage()+"]");
		}
		LOG.debug("<-- stop()");
	}
	
	/**
	 * @inheritDoc
	 */
	public void setUserDetails(UserDetails details) {
		this.details = details;
		if (discovery != null) {
			discovery.setUserDetails(details);
		}
	}

	/**
	 * @inheritDoc
	 */
	public void setTimestampFactory(TimestampFactory factory) {
		this.timestampFactory = factory;
	}

	public void setCallback(NetworkServiceCallback callback) {
		ParameterValidator.notNull("callback", callback);
		this.networkCallback = callback;
	}

	/**
	 * @inheritDoc
	 */
	public void discoverUser(DiscoveryNetworkCallback callback, InetAddress addr, int port) {
		LOG.debug("--> discoverUser(" + addr + ")");
		String defaultPort = NetworkProperties.get(NetworkProperties.KEY_PROTOCOL_PORT);
		ExplicitUserDiscovery userDiscovery = new ExplicitUserDiscovery(callback, addr, Integer.parseInt(defaultPort));
		userDiscovery.start();
		LOG.debug("<-- discoverUser()");
	}

	/**
	 * @inheritDoc
	 */
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
	
	/**
	 * @inheritDoc
	 */
	public boolean isStopped() {
		return isStopped;
	}
	
	/**
	 * @inheritDoc
	 */
	public TimestampFactory getTimestampFactory() {
		return timestampFactory;
	}
	
	/**
	 * @inheritDoc
	 */
	public void conceal(String docId) {
		LOG.debug("conceal(" + docId + ")");
		publishedDocs.remove(docId);
	}
	
	/**
	 * @inheritDoc
	 */
	public void setDiscovery(Discovery discovery) {
		this.discovery = discovery;
	}
	
	/**
	 * @inheritDoc
	 */
	public void setServerInfo(ServerInfo info) {
		this.info = info;
	}
	
	/**
	 * @inheritDoc
	 */
	public boolean hasPublishedDocuments() {
		return !publishedDocs.isEmpty();
	}
}
