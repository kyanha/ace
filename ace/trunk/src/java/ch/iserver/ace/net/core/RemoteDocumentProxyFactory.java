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

package ch.iserver.ace.net.core;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.protocol.RequestFilter;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Singleton factory class to create instances of type <code>RemoteDocumentProxy</code>.
 * Create RemoteDocumentProxy objects only with this factory.
 * 
 * @see ch.iserver.ace.net.RemoteDocumentProxy
 * @see ch.iserver.ace.net.core.RemoteDocumentProxyExt
 */
public class RemoteDocumentProxyFactory {

	/**
	 * The request filter chain.
	 */
	private RequestFilter filter;
	
	/**
	 * The singleton factory instance.
	 */
	private static RemoteDocumentProxyFactory instance;
	
	/**
	 * Private constructor.
	 * 
	 * @param filter 	the request filter chain
	 */
	private RemoteDocumentProxyFactory(RequestFilter filter) {
		this.filter = filter;
	}
	
	/**
	 * Initializes this factory. 
	 * This method must be called prior to method {@link #getInstance()}.
	 * 
	 * @param filter		the request filter chain
	 */
	public static void init(RequestFilter filter) {
		ParameterValidator.notNull("filter", filter);
		instance = new RemoteDocumentProxyFactory(filter);
	}
	
	/**
	 * Gets the RemoteDocumentProxyFactory object.
	 * If the factory was not initialized properly, 
	 * an <code>IllegalStateException</code> is thrown.
	 *  
	 * @return the RemoteDocumentFactory instance
	 * @throws IllegalStateException 	if the factory was not initialized properly
	 */
	public static RemoteDocumentProxyFactory getInstance() {
		if (instance == null) {
			throw new IllegalStateException("instance has not been initialized");
		}
		return instance;
	}
	
	/**
	 * Creates a new <code>RemoteDocumentProxy</code> implementation object.
	 * 
	 * @param id			the document id
	 * @param details	the document details
	 * @param publisher	the RemoteUserProxy of the publisher
	 * @return a new <code>RemoteDocumentProxyExt</code> object
	 */
	public RemoteDocumentProxyExt createProxy(String id, DocumentDetails details, RemoteUserProxy publisher) {
		return new RemoteDocumentProxyImpl(id, details, publisher, filter);
	}
	
	
}
