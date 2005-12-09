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
package ch.iserver.ace.net.impl.discovery;

import java.net.InetAddress;

import ch.iserver.ace.net.core.DiscoveryCallback;

/**
 * Interface between Bonjour related class and the network layer.
 *
 */
interface DiscoveryCallbackAdapter {

	/**
	 * 
	 * @param forward
	 */
	public void setDiscoveryCallback(DiscoveryCallback forward);

	/**
	 * 
	 * @param serviceName
	 * @param username
	 * @param userId
	 * @param port
	 */
	public void userDiscovered(String serviceName, String username,
			String userId, int port);

	/**
	 * 
	 * @param serviceName
	 */
	public void userDiscarded(String serviceName);

	/**
	 * 
	 * @param serviceName
	 * @param userName
	 */
	public void userNameChanged(String serviceName, String userName);

	/**
	 * 
	 * @param serviceName
	 * @param address
	 */
	public void userAddressResolved(String serviceName, InetAddress address);

	/**
	 * 
	 * @param serviceName
	 * @return boolean true iff 
	 */
	public boolean isServiceKnown(String serviceName);

}