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

package ch.iserver.ace.collaboration;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import ch.iserver.ace.DocumentDetails;
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
public class NetworkServiceStub implements NetworkService {

	private NetworkServiceCallback callback;
	
	private UserDetails details;
	
	private TimestampFactory factory;
	
	private List documentServers = new ArrayList();
	
	/**
	 * @return
	 */
	public List getDocumentServers() {
		return documentServers;
	}
	
	/**
	 * @see ch.iserver.ace.net.NetworkService#setUserDetails(ch.iserver.ace.UserDetails)
	 */
	public void setUserDetails(UserDetails details) {
		this.details = details;
	}
	
	/**
	 * @return
	 */
	public UserDetails getUserDetails() {
		return details;
	}

	/**
	 * @see ch.iserver.ace.net.NetworkService#setTimestampFactory(ch.iserver.ace.algorithm.TimestampFactory)
	 */
	public void setTimestampFactory(TimestampFactory factory) {
		this.factory = factory;
	}
	
	public TimestampFactory getTimestampFactory() {
		return factory;
	}

	/**
	 * @see ch.iserver.ace.net.NetworkService#setCallback(ch.iserver.ace.net.NetworkServiceCallback)
	 */
	public void setCallback(NetworkServiceCallback callback) {
		this.callback = callback;
	}
	
	/**
	 * @return
	 */
	public NetworkServiceCallback getCallback() {
		return callback;
	}

	/**
	 * @see ch.iserver.ace.net.NetworkService#publish(ch.iserver.ace.net.DocumentServerLogic, ch.iserver.ace.DocumentDetails)
	 */
	public DocumentServer publish(DocumentServerLogic logic, DocumentDetails details) {
		DocumentServer server = new DocumentServerStub(logic);
		documentServers.add(server);
		return server;
	}

	/**
	 * @see ch.iserver.ace.net.NetworkService#discoverUser(ch.iserver.ace.net.DiscoveryNetworkCallback, java.net.InetAddress, int)
	 */
	public void discoverUser(DiscoveryNetworkCallback callback,
					InetAddress addr, int port) {

	}

}
