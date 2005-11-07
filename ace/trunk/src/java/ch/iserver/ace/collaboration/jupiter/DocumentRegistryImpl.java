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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.util.ParameterValidator;

/**
 * The default implementation of DocumentRegistry.
 */
class DocumentRegistryImpl implements DocumentRegistry {
	
	/**
	 * The user registry used to create/get RemoteUser objects
	 */
	private final UserRegistry registry;
	
	/**
	 * 
	 */
	private final SessionConnectionDecorator connectionDecorator;
	
	/**
	 * The mapping from document id to document.
	 */
	private final Map documents;
	
	/**
	 * Creates a new DocumentRegistryImpl using the given user registry
	 * to get/create remote users.
	 * 
	 * @param registry the UserRegistry used to get/create remote users
	 */
	public DocumentRegistryImpl(SessionConnectionDecorator decorator, UserRegistry registry) {
		ParameterValidator.notNull("decorator", decorator);
		ParameterValidator.notNull("registry", registry);
		this.documents = Collections.synchronizedMap(new HashMap());
		this.connectionDecorator = decorator;
		this.registry = registry;
	}
	
	/**
	 * @return
	 */
	protected SessionConnectionDecorator getConnectionDecorator() {
		return connectionDecorator;
	}
	
	/**
	 * @return the UserRegistry used by this DocumentRegistry
	 */
	protected UserRegistry getUserRegistry() {
		return registry;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.DocumentRegistry#addDocument(ch.iserver.ace.net.RemoteDocumentProxy)
	 */
	public MutableRemoteDocument addDocument(RemoteDocumentProxy proxy) {
		MutableRemoteDocument doc = new RemoteDocumentImpl(getConnectionDecorator(), proxy, getUserRegistry());
		documents.put(proxy.getId(), doc);
		return doc;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.DocumentRegistry#getDocument(java.lang.String)
	 */
	public MutableRemoteDocument getDocument(String id) {
		return (MutableRemoteDocument) documents.get(id);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.DocumentRegistry#removeDocument(ch.iserver.ace.net.RemoteDocumentProxy)
	 */
	public MutableRemoteDocument removeDocument(RemoteDocumentProxy proxy) {
		return (MutableRemoteDocument) documents.remove(proxy.getId());
	}
	
}
