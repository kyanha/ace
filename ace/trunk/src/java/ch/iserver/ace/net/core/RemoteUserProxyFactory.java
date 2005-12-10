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


/**
 * Singleton factory class to create instances of type <code>RemoteUserProxy</code>.
 * Create RemoteUserProxy objects only with this factory.
 * 
 * @see ch.iserver.ace.net.core.RemoteUserProxyExt
 */
public class RemoteUserProxyFactory {
	
	/**
	 * The singleton RemoteUserProxyFactory
	 */
	private static RemoteUserProxyFactory instance;
	
	/**
	 * Private default constructor.
	 */
	private RemoteUserProxyFactory() {
	}
	
	/**
	 * Gets the instance of RemoteUserProxyFactory.
	 * 
	 * @return the RemoteUserProxyFactory object
	 */
	public static RemoteUserProxyFactory getInstance() {
		if (instance == null) {
			instance = new RemoteUserProxyFactory();
		}
		return instance;
	}
	
	/**
	 * Creates a new RemoteUserProxy instance.
	 * 
	 * @param id			the user id
	 * @param details	the user details
	 * @return	the created RemoteUserProxyExt instance
	 * @see RemoteUserProxyExt
	 */
	public RemoteUserProxyExt createProxy(String id, MutableUserDetails details) {
		return new RemoteUserProxyImpl(id, details);
	}
	
}
