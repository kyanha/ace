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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import ch.iserver.ace.collaboration.JoinCallback;
import ch.iserver.ace.collaboration.RemoteDocument;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.net.JoinNetworkCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Default implementation of the RemoteDocument interface.
 */
class RemoteDocumentImpl implements MutableRemoteDocument {
		
	/**
	 * 
	 */
	private final PropertyChangeSupport support = new PropertyChangeSupport(this);
	
	/**
	 * The wrapped RemoteDocumentProxy instance.
	 */
	private final RemoteDocumentProxy proxy;
	
	/**
	 * The publisher of the document.
	 */
	private final RemoteUser publisher;
	
	/**
	 * 
	 */
	private final UserRegistry registry;
	
	/**
	 * 
	 */
	private final SessionConnectionDecorator connectionDecorator;
	
	/**
	 * 
	 */
	private String title;
	
	/**
	 * Creates a new RemoteDocumentImpl passing most requests directly to
	 * the passed in RemoteDocumentProxy.
	 * 
	 * @param proxy the wrapped RemoteDocumentProxy
	 * @param publisher the publisher of the document
	 */
	RemoteDocumentImpl(SessionConnectionDecorator decorator, RemoteDocumentProxy proxy, UserRegistry registry) {
		ParameterValidator.notNull("decorator", decorator);
		ParameterValidator.notNull("proxy", proxy);
		ParameterValidator.notNull("registry", registry);
		this.connectionDecorator = decorator;
		this.proxy = proxy;
		this.registry = registry;
		this.publisher = registry.getUser(proxy.getPublisher().getId());
		this.title = proxy.getDocumentDetails().getTitle();
		this.title = this.title == null ? "" : title;
	}
	
	private UserRegistry getUserRegistry() {
		return registry;
	}
	
	private SessionConnectionDecorator getConnectionDecorator() {
		return connectionDecorator;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.RemoteDocument#getId()
	 */
	public String getId() {
		return proxy.getId();
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		String old = this.title;
		if (!old.equals(title)) {
			this.title = title;
			support.firePropertyChange(TITLE_PROPERTY, old, title);
		}
	}

	/**
	 * @see ch.iserver.ace.collaboration.RemoteDocument#getPublisher()
	 */
	public RemoteUser getPublisher() {
		return publisher;
	}

	/**
	 * @see ch.iserver.ace.collaboration.RemoteDocument#join(ch.iserver.ace.collaboration.JoinCallback)
	 */
	public void join(final JoinCallback callback) {
		JoinNetworkCallback networkCallback = new JoinNetworkCallbackImpl(callback, getConnectionDecorator(), getUserRegistry());
		proxy.join(networkCallback);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.RemoteDocument#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.RemoteDocument#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof RemoteDocument) {
			RemoteDocument document = (RemoteDocument) obj;
			return getId().equals(document.getId());
		} else {
			return false;
		}
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getId().hashCode();
	}

}
