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
package ch.iserver.ace.net.discovery;

import java.net.InetAddress;

import ch.iserver.ace.net.core.DiscoveryCallback;

/**
 * Interface between Bonjour related classes and the network layer.
 * All callback events received from the DNSSD are passed via this
 * interface. Therefore the events can be channelized and collected
 * in a central class.
 */
interface DiscoveryCallbackAdapter {

	/**
	 * Sets the discovery callback for this adapter. All events
	 * are forwarded to the discovery callback.
	 * 
	 * @param forward 	the discovery callback to set
	 */
	public void setDiscoveryCallback(DiscoveryCallback forward);

	/**
	 * Notifies the adapter that a new user was discovered by DNSSD.
	 * 
	 * @param serviceName		the name of the discovered service
	 * @param username		the name of the discovered user
	 * @param userId			the user id
	 * @param port			the port of the discovered service
	 */
	public void userDiscovered(String serviceName, String username,
			String userId, int port);

	/**
	 * Notifies the adapter that a user was discarded.
	 * 
	 * @param serviceName 	the service name of the discovered user
	 */
	public void userDiscarded(String serviceName);

	/**
	 * Notifies the adapter that a user name changed.
	 * 
	 * @param serviceName		the service name of the user
	 * @param userName		the new name of the user
	 */
	public void userNameChanged(String serviceName, String userName);

	/**
	 * Notifies the callback that the address of a user was resolved.
	 * 
	 * @param serviceName		the service name of the user
	 * @param address		the address of the user
	 */
	public void userAddressResolved(String serviceName, InetAddress address);

	/**
	 * Checks whether a service name is known.
	 * 
	 * @param serviceName		the service name to check
	 * @return boolean true iff the service name is known by the local user
	 */
	public boolean isServiceKnown(String serviceName);

}