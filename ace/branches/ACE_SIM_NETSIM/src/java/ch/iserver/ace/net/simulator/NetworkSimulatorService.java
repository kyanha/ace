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

package ch.iserver.ace.net.simulator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.ServerInfo;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.algorithm.TimestampFactory;
import ch.iserver.ace.net.DiscoveryNetworkCallback;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.NetworkService;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;

/**
 *
 */
public class NetworkSimulatorService implements NetworkService {
	
	private String userId;
	
	private UserDetails details;
	
	private MessageBus messageBus;
	
	private MessagePort port;
	
	private Map users = new HashMap();

	private Map documents = new HashMap();

	private NetworkServiceCallback callback;
	
	public NetworkServiceCallback getCallback() {
		return callback;
	}
	
	public void setMessageBus(MessageBus messageBus) {
		this.messageBus = messageBus;
	}
	
	/**
	 * @see ch.iserver.ace.net.NetworkService#getServerInfo()
	 */
	public ServerInfo getServerInfo() {
		try {
			return new ServerInfo(InetAddress.getLocalHost(), 4123);
		} catch (UnknownHostException e) {
			return null;
		}
	}

	/**
	 * @see ch.iserver.ace.net.NetworkService#start()
	 */
	public void start() {
		port = messageBus.register(userId, details, new MessageListenerImpl());
	}

	/**
	 * @see ch.iserver.ace.net.NetworkService#stop()
	 */
	public void stop() {
		messageBus.unregister(userId);
		port = null;
	}

	/**
	 * @see ch.iserver.ace.net.NetworkService#setUserId(java.lang.String)
	 */
	public void setUserId(String id) {
		this.userId = id;
	}

	/**
	 * @see ch.iserver.ace.net.NetworkService#setUserDetails(ch.iserver.ace.UserDetails)
	 */
	public void setUserDetails(UserDetails details) {
		if (port != null) {
			port.setUserDetails(details);
		}
		this.details = details;
	}

	/**
	 * @see ch.iserver.ace.net.NetworkService#setTimestampFactory(ch.iserver.ace.algorithm.TimestampFactory)
	 */
	public void setTimestampFactory(TimestampFactory factory) {
		// ignore: is not needed
	}

	/**
	 * @see ch.iserver.ace.net.NetworkService#setCallback(ch.iserver.ace.net.NetworkServiceCallback)
	 */
	public void setCallback(NetworkServiceCallback callback) {
		this.callback = callback;
	}

	/**
	 * @see ch.iserver.ace.net.NetworkService#publish(ch.iserver.ace.net.DocumentServerLogic, ch.iserver.ace.DocumentDetails)
	 */
	public DocumentServer publish(DocumentServerLogic logic,
			DocumentDetails details) {
		DocumentServer server = new PublishedDocument(port, logic, details);
		return server;
	}

	/**
	 * @see ch.iserver.ace.net.NetworkService#discoverUser(ch.iserver.ace.net.DiscoveryNetworkCallback, java.net.InetAddress, int)
	 */
	public void discoverUser(DiscoveryNetworkCallback callback,
			InetAddress addr, int port) {
		callback.userDiscoveryFailed(FailureCodes.CONNECTION_REFUSED, "explicit discovery not supported");
	}
	
	
	// --> user related methods <--
	
	private void addUser(String userId, User user) {
		users.put(userId, user);
	}
	
	private void removeUser(String userId) {
		users.remove(userId);
	}
	
	private User getUser(String userId) {
		if (!users.containsKey(userId)) {
			throw new IllegalArgumentException("unknown user " + userId);
		}
		return (User) users.get(userId);
	}
	
	// --> document related methods <--
	
	private void addDocument(String docId, Document doc) {
		documents.put(docId, doc);
	}
	
	private void removeDocument(String docId) {
		documents.remove(docId);
	}
	
	private Document getDocument(String docId) {
		if (!documents.containsKey(docId)) {
			throw new IllegalArgumentException("unknown document " + docId);
		}
		return (Document) documents.get(docId);
	}

	private class MessageListenerImpl implements MessageListener {	
		public void userRegistered(String userId, UserDetails details) {
			User user = new User(userId, details);
			addUser(userId, user);
			callback.userDiscovered(user);
		}
		
		public void userChanged(String userId, UserDetails details) {
			User user = getUser(userId);
			user.setUserDetails(details);
			callback.userDetailsChanged(user);
		}
		
		public void userUnregistered(String userId) {
			User user = getUser(userId);
			removeUser(userId);
			callback.userDiscarded(user);
		}
		
		public void documentPublished(String userId, String docId, DocumentDetails details) {
			Document doc = new Document(getUser(userId), docId, details);
			addDocument(docId, doc);
			callback.documentDiscovered(new RemoteDocumentProxy[] { doc });
		}
		
		public void documentChanged(String userId, String docId, DocumentDetails details) {
			Document doc = getDocument(docId);
			doc.setDocumentDetails(details);
			callback.documentDetailsChanged(doc);
		}
		
		public void documentConcealed(String userId, String docId) {
			Document doc = getDocument(docId);
			removeDocument(docId);
			callback.documentDiscarded(new RemoteDocumentProxy[] { doc });
		}
	}
	
}
