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
import ch.iserver.ace.collaboration.jupiter.server.ServerLogic;
import ch.iserver.ace.collaboration.jupiter.server.ServerLogicImpl;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.InvitationProxy;
import ch.iserver.ace.net.NetworkService;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.SemaphoreLock;

/**
 *
 */
public class CollaborationServiceImpl implements CollaborationService, NetworkServiceCallback {
	
	private static final Logger LOG = Logger.getLogger(CollaborationServiceImpl.class);
	
	private final NetworkService service;
	
	private final EventListenerList listeners = new EventListenerList();
	
	private InvitationCallback callback = NullInvitationCallback.getInstance();
	
	private UserRegistry userRegistry = new UserRegistryImpl();
	
	private DocumentRegistry documentRegistry = new DocumentRegistryImpl(userRegistry);
	
	public CollaborationServiceImpl(NetworkService service) {
		ParameterValidator.notNull("service", service);
		this.service = service;
		this.service.setCallback(this);
		this.service.setTimestampFactory(new JupiterTimestampFactory());
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
		ServerLogic logic = new ServerLogicImpl(new SemaphoreLock("server-lock"), session, document);
		session.setServerLogic(logic);
		DocumentServer server = getNetworkService().publish(logic);
		logic.setDocumentServer(server);
		// TODO: replace with improved start sequence
		((ServerLogicImpl) logic).start();
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

	public void documentDiscovered(RemoteDocumentProxy proxy) {
		RemoteDocument doc = getDocumentRegistry().addDocument(proxy);
		DocumentListener[] list = (DocumentListener[]) listeners.getListeners(DocumentListener.class);
		for (int i = 0; i < list.length; i++) {
			DocumentListener listener = list[i];
			listener.documentDiscovered(doc);
		}		
	}
	
	public void documentDetailsChanged(RemoteDocumentProxy proxy) {
		MutableRemoteDocument doc = getDocumentRegistry().getDocument(proxy.getId());
		if (doc == null) {
			// TODO: throw exception
			LOG.error("documentDetailsChanged called with an unknown document id (i.e. documentDiscovered not called)");
		} else {
			doc.setTitle(proxy.getDocumentDetails().getTitle());
		}
	}
	
	public void documentDiscarded(RemoteDocumentProxy proxy) {
		RemoteDocument doc = getDocumentRegistry().removeDocument(proxy);
		if (doc == null) {
			// TODO: throw exception
			LOG.error("documentDiscarded called without previos documentDiscovered call");
		} else {
			DocumentListener[] list = (DocumentListener[]) listeners.getListeners(DocumentListener.class);
			for (int i = 0; i < list.length; i++) {
				DocumentListener listener = list[i];
				listener.documentDiscovered(doc);
			}
		}
	}
	
	public void userDiscovered(RemoteUserProxy proxy) {
		RemoteUser user = getUserRegistry().addUser(proxy);
		UserListener[] listeners = (UserListener[]) this.listeners.getListeners(UserListener.class);
		for (int i = 0; i < listeners.length; i++) {
			UserListener listener = listeners[i];
			listener.userDiscovered(user);
		}
	}

	public void userDetailsChanged(RemoteUserProxy proxy) {
		MutableRemoteUser user = getUserRegistry().getUser(proxy.getId());
		if (user == null) {
			// TODO: throw exception
			LOG.error("userDetailsChanged called with an unkown user id (i.e. userDiscovered not called)");
		} else {
			user.setName(proxy.getUserDetails().getUsername());
		}
	}

	public void userDiscarded(RemoteUserProxy proxy) {
		RemoteUser user = getUserRegistry().removeUser(proxy);
		if (user == null) {
			// TODO: throw exception
			LOG.error("userDiscarded called without previous userDiscovered call");
		} else {
			UserListener[] listeners = (UserListener[]) this.listeners.getListeners(UserListener.class);
			for (int i = 0; i < listeners.length; i++) {
				UserListener listener = listeners[i];
				listener.userDiscovered(user);
			}
		}
	}

	public void invitationReceived(InvitationProxy invitation) {
		RemoteDocument document = getDocumentRegistry().getDocument(invitation.getDocument().getId());
		getInvitationCallback().invitationReceived(new InvitationImpl(invitation, getUserRegistry(), document));
	}
			
}
