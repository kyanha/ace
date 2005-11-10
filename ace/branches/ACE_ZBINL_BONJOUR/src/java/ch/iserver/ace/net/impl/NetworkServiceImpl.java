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
package ch.iserver.ace.net.impl;

import java.net.InetAddress;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.algorithm.TimestampFactory;
import ch.iserver.ace.net.DiscoveryNetworkCallback;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.NetworkService;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.impl.protocol.DiscoveryLauncher;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.UUID;

/**
 *
 */
public class NetworkServiceImpl implements NetworkServiceExt {
	
	private TimestampFactory timestampFactory;
	private NetworkServiceCallback networkCallback;
	
	private Discovery discovery;
	private UserDetails details;
	private String userId;
	
	public NetworkServiceImpl() {
		userId = UUID.nextUUID();
	}

	public void setUserDetails(UserDetails details) {
		this.details = details;
		//update user details in sessions
		if (discovery != null) {
			//TODO: how is threading here?
			discovery.setUserDetails(details);
		}
	}

	public void setTimestampFactory(TimestampFactory factory) {
		this.timestampFactory = factory;
	}

	public void setCallback(NetworkServiceCallback callback) {
		ParameterValidator.notNull("callback", callback);
		this.networkCallback = callback;
		//since the callback to the upper layer is available now, 
		//start discovery process
		launchDiscovery();
	}

	/**
	 * Launches the discovery process.
	 */
	private void launchDiscovery() {
		DiscoveryLauncher launcher = new DiscoveryLauncher(getUserId(), getCallback(), this);
		launcher.start();
	}
	
	public void setDiscovery(Discovery discovery) {
		this.discovery = discovery;
	}
	
	public NetworkServiceCallback getCallback() {
		return networkCallback;
	}

	public String getUserId() {
		return userId;
	}

	public void discoverUser(DiscoveryNetworkCallback callback, InetAddress addr, int port) {
		
		
	}

	public DocumentServer publish(DocumentServerLogic logic, DocumentDetails details) {
		return null;
	}

}
