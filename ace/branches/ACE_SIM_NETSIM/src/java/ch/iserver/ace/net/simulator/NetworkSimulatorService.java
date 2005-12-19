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

package ch.iserver.ace.net.simulator;

import java.net.InetAddress;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.ServerInfo;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.algorithm.TimestampFactory;
import ch.iserver.ace.net.DiscoveryNetworkCallback;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.NetworkService;
import ch.iserver.ace.net.NetworkServiceCallback;

/**
 *
 */
public class NetworkSimulatorService implements NetworkService {

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.NetworkService#getServerInfo()
	 */
	public ServerInfo getServerInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.NetworkService#start()
	 */
	public void start() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.NetworkService#stop()
	 */
	public void stop() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.NetworkService#setUserId(java.lang.String)
	 */
	public void setUserId(String id) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.NetworkService#setUserDetails(ch.iserver.ace.UserDetails)
	 */
	public void setUserDetails(UserDetails details) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.NetworkService#setTimestampFactory(ch.iserver.ace.algorithm.TimestampFactory)
	 */
	public void setTimestampFactory(TimestampFactory factory) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.NetworkService#setCallback(ch.iserver.ace.net.NetworkServiceCallback)
	 */
	public void setCallback(NetworkServiceCallback callback) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.NetworkService#publish(ch.iserver.ace.net.DocumentServerLogic, ch.iserver.ace.DocumentDetails)
	 */
	public DocumentServer publish(DocumentServerLogic logic,
			DocumentDetails details) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.NetworkService#discoverUser(ch.iserver.ace.net.DiscoveryNetworkCallback, java.net.InetAddress, int)
	 */
	public void discoverUser(DiscoveryNetworkCallback callback,
			InetAddress addr, int port) {
		// TODO Auto-generated method stub

	}

}
