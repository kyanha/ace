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

import ch.iserver.ace.net.discovery.BonjourFactory;
import ch.iserver.ace.net.discovery.PeerDiscovery;
import ch.iserver.ace.net.discovery.UserRegistration;

/**
 * Factory class for creating discovery implementations. 
 *
 * @see ch.iserver.ace.net.core.Discovery
 */
public abstract class DiscoveryFactory {

	/**
	 * The single DiscoveryFactory instance
	 */
	private static DiscoveryFactory instance;
	
	/**
	 * Gets the DiscoveryFactory instance.
	 * 
	 * @return a DiscoveryFactory implementation
	 */
	public static DiscoveryFactory getInstance() {
		if (instance == null) {
			//TODO: load class via Spring framework
			instance = new BonjourFactory();
		}
		return instance;
	}
	
	/**
	 * Inits this DiscoveryFactory (optional). If <code>init</code> is not called,
	 * default implementations will be used.
	 * This method is actually intended for testability.
	 * 
	 * @param registration 	the UserRegistration implementation
	 * @param discovery		the PeerDiscovery implementation
	 */
	public abstract void init(UserRegistration registration, PeerDiscovery discovery);
	
	/**
	 * Creates a new discovery implementation.
	 * 
	 * @param callback the DiscoveryCallback for the discovery
	 * @return an instance of a Discovery implementation
	 */
	public abstract Discovery createDiscovery(DiscoveryCallback callback);

}
