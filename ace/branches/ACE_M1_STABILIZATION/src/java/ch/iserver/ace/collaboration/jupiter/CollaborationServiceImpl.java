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

package ch.iserver.ace.collaboration.jupiter;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.algorithm.jupiter.JupiterTimestampFactory;
import ch.iserver.ace.collaboration.CollaborationService;
import ch.iserver.ace.collaboration.DiscoveryCallback;
import ch.iserver.ace.collaboration.DocumentListener;
import ch.iserver.ace.collaboration.InvitationCallback;
import ch.iserver.ace.collaboration.PublishedSession;
import ch.iserver.ace.collaboration.PublishedSessionCallback;
import ch.iserver.ace.collaboration.RemoteDocument;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.collaboration.UserListener;
import ch.iserver.ace.collaboration.jupiter.server.ServerLogicImpl;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.InvitationProxy;
import ch.iserver.ace.net.NetworkService;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.SemaphoreLock;
import ch.iserver.ace.util.ThreadDomain;

/**
 *
 */
public class CollaborationServiceImpl implements CollaborationService, NetworkServiceCallback {
	
	private static final Logger LOG = Logger.getLogger(CollaborationServiceImpl.class);
	
	private final NetworkService service;
	
	private final EventListenerList listeners = new EventListenerList();
	
	private InvitationCallback callback = NullInvitationCallback.getInstance();
	
	private UserRegistry userRegistry;
	
	private DocumentRegistry documentRegistry;;
	
	private SessionFactory sessionFactory;
	
	private ThreadDomain publisherThreadDomain;
	
	public CollaborationServiceImpl(NetworkService service) {
		ParameterValidator.notNull("service", service);
		this.service = service;
		this.service.setCallback(this);
		this.service.setTimestampFactory(new JupiterTimestampFactory());
	}
	
	public void start() {
		service.start();
	}
	
	public UserRegistry getUserRegistry() {
		return userRegistry;
	}
	
	public void setUserRegistry(UserRegistry registry) {
		ParameterValidator.notNull("registry", registry);
		this.userRegistry = registry;
	}
	
	public DocumentRegistry getDocumentRegistry() {
		return documentRegistry;
	}
	
	public void setDocumentRegistry(DocumentRegistry registry) {
		ParameterValidator.notNull("registry", registry);
		this.documentRegistry = registry;
	}
		
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public ThreadDomain getPublisherThreadDomain() {
		return publisherThreadDomain;
	}
	
	public void setPublisherThreadDomain(ThreadDomain domain) {
		ParameterValidator.notNull("domain", domain);
		this.publisherThreadDomain = domain;
	}
	
	protected NetworkService getNetworkService() {
		return service;
	}
	
	protected InvitationCallback getInvitationCallback() {
		return callback;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#setUserDetails(ch.iserver.ace.UserDetails)
	 */
	public void setUserDetails(UserDetails details) {
		getNetworkService().setUserDetails(details);
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#addUserListener(ch.iserver.ace.collaboration.UserListener)
	 */
	public void addUserListener(UserListener listener) {
		ParameterValidator.notNull("listener", listener);
		listeners.add(UserListener.class, listener);
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#removeUserListener(ch.iserver.ace.collaboration.UserListener)
	 */
	public void removeUserListener(UserListener listener) {
		listeners.remove(UserListener.class, listener);
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#addDocumentListener(ch.iserver.ace.collaboration.DocumentListener)
	 */
	public void addDocumentListener(DocumentListener listener) {
		ParameterValidator.notNull("listener", listener);
		listeners.add(DocumentListener.class, listener);
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#removeDocumentListener(ch.iserver.ace.collaboration.DocumentListener)
	 */
	public void removeDocumentListener(DocumentListener listener) {
		listeners.remove(DocumentListener.class, listener);
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#setInvitationCallback(ch.iserver.ace.collaboration.InvitationCallback)
	 */
	public void setInvitationCallback(InvitationCallback callback) {
		this.callback = callback == null ? NullInvitationCallback.getInstance() : callback;
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#publish(ch.iserver.ace.collaboration.PublishedSessionCallback, ch.iserver.ace.DocumentModel)
	 */
	public PublishedSession publish(PublishedSessionCallback callback, DocumentModel document) {
		PublishedSessionImpl session = new PublishedSessionImpl(callback);
		session.setUserRegistry(getUserRegistry());
		Lock semaphoreLock = new SemaphoreLock("server-lock");
		ServerLogicImpl logic = new ServerLogicImpl(semaphoreLock, getPublisherThreadDomain(), session, document);
		session.setServerLogic(logic);
		DocumentServer server = getNetworkService().publish(logic, document.getDetails());
		logic.setDocumentServer(server);
		logic.start();
		return session;
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#discoverUser(ch.iserver.ace.collaboration.DiscoveryCallback, java.net.InetAddress, int)
	 */
	public void discoverUser(DiscoveryCallback callback, InetAddress addr,
					int port) {
		getNetworkService().discoverUser(
						new DiscoveryNetworkCallbackImpl(callback, getUserRegistry()), 
						addr, 
						port);
	}
	
	// --> network service callback methods <--

	/**
	 * @see ch.iserver.ace.net.NetworkServiceCallback#documentDiscovered(ch.iserver.ace.net.RemoteDocumentProxy[])
	 */
	public void documentDiscovered(RemoteDocumentProxy[] proxies) {
		RemoteDocument[] documents = new RemoteDocument[proxies.length];
		for (int i = 0; i < proxies.length; i++) {
			documents[i] = getDocumentRegistry().addDocument(proxies[i]);
		}
		DocumentListener[] list = (DocumentListener[]) listeners.getListeners(DocumentListener.class);
		for (int i = 0; i < list.length; i++) {
			DocumentListener listener = list[i];
			listener.documentsDiscovered(documents);
		}		
	}
	
	/**
	 * @see ch.iserver.ace.net.NetworkServiceCallback#documentDetailsChanged(ch.iserver.ace.net.RemoteDocumentProxy)
	 */
	public void documentDetailsChanged(RemoteDocumentProxy proxy) {
		MutableRemoteDocument doc = getDocumentRegistry().getDocument(proxy.getId());
		if (doc == null) {
			LOG.error("documentDetailsChanged called with an unknown document id (i.e. documentDiscovered not called)");
		} else {
			doc.setTitle(proxy.getDocumentDetails().getTitle());
		}
	}
	
	/**
	 * @see ch.iserver.ace.net.NetworkServiceCallback#documentDiscarded(ch.iserver.ace.net.RemoteDocumentProxy[])
	 */
	public void documentDiscarded(RemoteDocumentProxy[] proxies) {
		List tmp = new ArrayList();
		for (int i = 0; i < proxies.length; i++) {
			RemoteDocument doc = getDocumentRegistry().removeDocument(proxies[i]);
			if (doc == null) {
				LOG.error("documentDiscarded called without previos documentDiscovered call");
			}
			tmp.add(doc);
		}
		RemoteDocument[] documents = (RemoteDocument[]) tmp.toArray(new RemoteDocument[tmp.size()]);
		DocumentListener[] list = (DocumentListener[]) listeners.getListeners(DocumentListener.class);
		for (int i = 0; i < list.length; i++) {
			DocumentListener listener = list[i];
			listener.documentsDiscovered(documents);
		}
	}
	
	/**
	 * @see ch.iserver.ace.net.NetworkServiceCallback#userDiscovered(ch.iserver.ace.net.RemoteUserProxy)
	 */
	public void userDiscovered(RemoteUserProxy proxy) {
		RemoteUser user = getUserRegistry().addUser(proxy);
		UserListener[] listeners = (UserListener[]) this.listeners.getListeners(UserListener.class);
		for (int i = 0; i < listeners.length; i++) {
			UserListener listener = listeners[i];
			listener.userDiscovered(user);
		}
	}

	/**
	 * @see ch.iserver.ace.net.NetworkServiceCallback#userDetailsChanged(ch.iserver.ace.net.RemoteUserProxy)
	 */
	public void userDetailsChanged(RemoteUserProxy proxy) {
		MutableRemoteUser user = getUserRegistry().getUser(proxy.getId());
		if (user == null) {
			// TODO: throw exception
			LOG.error("userDetailsChanged called with an unkown user id (i.e. userDiscovered not called)");
		} else {
			user.setName(proxy.getUserDetails().getUsername());
		}
	}

	/**
	 * @see ch.iserver.ace.net.NetworkServiceCallback#userDiscarded(ch.iserver.ace.net.RemoteUserProxy)
	 */
	public void userDiscarded(RemoteUserProxy proxy) {
		RemoteUser user = getUserRegistry().removeUser(proxy);
		if (user == null) {
			// TODO: throw exception
			LOG.error("userDiscarded called without previous userDiscovered call");
		} else {
			UserListener[] listeners = (UserListener[]) this.listeners.getListeners(UserListener.class);
			for (int i = 0; i < listeners.length; i++) {
				UserListener listener = listeners[i];
				listener.userDiscarded(user);
			}
		}
	}

	/**
	 * @see ch.iserver.ace.net.NetworkServiceCallback#invitationReceived(ch.iserver.ace.net.InvitationProxy)
	 */
	public void invitationReceived(InvitationProxy proxy) {
		RemoteDocument document = getDocumentRegistry().getDocument(proxy.getDocument().getId());
		InvitationImpl invitation = new InvitationImpl(
						proxy,
						document,
						getSessionFactory());
		getInvitationCallback().invitationReceived(invitation);
	}
			
}
