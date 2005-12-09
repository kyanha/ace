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

import ch.iserver.ace.net.InvitationProxy;
import ch.iserver.ace.net.protocol.RemoteUserSession;
import ch.iserver.ace.net.protocol.RequestFilter;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Factory class for the creation of InvitationProxy objects.
 * In order to use this factory, method {@link #init(RequestFilter)} must
 * be called first to initialize the factory.
 * 
 * @see ch.iserver.ace.net.InvitationProxy
 */
public class InvitationProxyFactory {

	/**
	 * The request filter chain to process outoing requests.
	 */
	private RequestFilter filter;
	
	/**
	 * The singleton factory instance
	 */
	private static InvitationProxyFactory instance;
	
	/**
	 * Private constructor.
	 * 
	 * @param filter 	the request filter chain
	 */
	private InvitationProxyFactory(RequestFilter filter) {
		this.filter = filter;
	}
	
	/**
	 * Initializes this InvitationProxyFactory. This method must be called
	 * prior to method {@link #getInstance()}.
	 * 
	 * @param filter 	the request filter chain
	 */
	public static void init(RequestFilter filter) {
		ParameterValidator.notNull("filter", filter);
		instance = new InvitationProxyFactory(filter);
	}
	
	/**
	 * Gets the InvitationProxyFactory object.
	 * If the factory was not initialized properly, 
	 * an <code>IllegalStateException</code> is thrown.
	 *  
	 * @return the InvitationProxyFactory instance
	 * @throws IllegalStateException 	if the factory was not initialized properly
	 */
	public static InvitationProxyFactory getInstance() {
		if (instance == null) {
			throw new IllegalStateException("instance has not been initialized");
		}
		return instance;
	}
	
	/**
	 * Creates a new InvitationProxy object.
	 * 
	 * @param proxy		the RemoteDocumentProxy the invitation proxy is about
	 * @param session	the RemoteUserSession that receives the invitation
	 * @return	the created InvitationProxy object
	 */
	public InvitationProxy createProxy(RemoteDocumentProxyExt proxy, RemoteUserSession session) {
		return new InvitationProxyImpl(proxy, session, filter);
	}
	
	
}
