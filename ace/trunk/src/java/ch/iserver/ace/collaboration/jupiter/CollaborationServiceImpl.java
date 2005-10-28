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

import ch.iserver.ace.UserDetails;
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
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.SemaphoreLock;

/**
 *
 */
public class CollaborationServiceImpl implements CollaborationService, NetworkServiceCallback {
	
	private final NetworkService service;
	
	private final EventListenerList listeners = new EventListenerList();
	
	private InvitationCallback callback = NullInvitationCallback.getInstance();
	
	public CollaborationServiceImpl(NetworkService service) {
		ParameterValidator.notNull("service", service);
		this.service = service;
		this.service.setCallback(this);
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
	 * @see ch.iserver.ace.collaboration.CollaborationService#publish(ch.iserver.ace.net.PortableDocument, ch.iserver.ace.collaboration.SessionCallback)
	 */
	public PublishedSession publish(PortableDocument document,
					PublishedSessionCallback callback) {
		PublishedSessionImpl session = new PublishedSessionImpl(callback);
		ServerLogicImpl logic = new ServerLogicImpl(new SemaphoreLock(), session);
		session.setServerLogic(logic);
		DocumentServer server = getNetworkService().publish(logic);
		logic.setDocumentServer(server);
		return session;
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#discoverUser(ch.iserver.ace.collaboration.DiscoveryCallback, java.net.InetAddress, int)
	 */
	public void discoverUser(DiscoveryCallback callback, InetAddress addr,
					int port) {
		getNetworkService().discoverUser(
						new DiscoveryNetworkCallbackImpl(callback), 
						addr, 
						port);
	}
	
	// --> network service callback methods <--

	public void documentDiscovered(RemoteDocumentProxy proxy) {
		RemoteDocument doc = new RemoteDocumentImpl(proxy);
		DocumentListener[] list = (DocumentListener[]) listeners.getListeners(DocumentListener.class);
		for (int i = 0; i < list.length; i++) {
			DocumentListener listener = list[i];
			listener.documentDiscovered(doc);
		}		
	}
	
	public void documentDetailsChanged(RemoteDocumentProxy proxy) {
		RemoteDocument doc = new RemoteDocumentImpl(proxy);
		DocumentListener[] list = (DocumentListener[]) listeners.getListeners(DocumentListener.class);
		for (int i = 0; i < list.length; i++) {
			DocumentListener listener = list[i];
			listener.documentDetailsChanged(doc);
		}		
	}
	
	public void documentDiscarded(RemoteDocumentProxy proxy) {
		RemoteDocument doc = new RemoteDocumentImpl(proxy);
		DocumentListener[] list = (DocumentListener[]) listeners.getListeners(DocumentListener.class);
		for (int i = 0; i < list.length; i++) {
			DocumentListener listener = list[i];
			listener.documentDiscarded(doc);
		}
	}
	
	public void userDiscovered(RemoteUserProxy proxy) {
		RemoteUser user = new RemoteUserImpl(proxy);
		UserListener[] list = (UserListener[]) listeners.getListeners(UserListener.class);
		for (int i = 0; i < list.length; i++) {
			UserListener listener = list[i];
			listener.userDiscovered(user);
		}
	}

	public void userDetailsChanged(RemoteUserProxy proxy) {
		RemoteUser user = new RemoteUserImpl(proxy);
		UserListener[] list = (UserListener[]) listeners.getListeners(UserListener.class);
		for (int i = 0; i < list.length; i++) {
			UserListener listener = list[i];
			listener.userDetailsChanged(user);
		}
	}

	public void userDiscarded(RemoteUserProxy proxy) {
		RemoteUser user = new RemoteUserImpl(proxy);
		UserListener[] list = (UserListener[]) listeners.getListeners(UserListener.class);
		for (int i = 0; i < list.length; i++) {
			UserListener listener = list[i];
			listener.userDiscarded(user);
		}
	}

	public void invitationReceived(InvitationProxy invitation) {
		getInvitationCallback().invitationReceived(new InvitationImpl(invitation));
	}
			
}
