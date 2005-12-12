/*
 * $Id:DiscoveryLauncher.java 1205 2005-11-14 07:57:10Z zbinl $
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

import org.apache.log4j.Logger;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.core.Discovery;
import ch.iserver.ace.net.core.DiscoveryCallback;
import ch.iserver.ace.net.core.DiscoveryCallbackImpl;
import ch.iserver.ace.net.core.DiscoveryFactory;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.protocol.filter.RequestFilter;

/**
 * The DiscoveryLauncher initializes and starts the automatic and dynamic discovery process
 * in a separate thread. It terminates as soon as the discovery is up and
 * running.
 * 
 * @see ch.iserver.ace.net.core.Discovery
 * @see ch.iserver.ace.net.discovery.DiscoveryCallbackAdapter
 */
public class DiscoveryLauncher extends Thread {

	private static Logger LOG = Logger.getLogger(DiscoveryLauncher.class);
	
	/**
	 * The network service object. Used to properly initialize the
	 * discovery components.
	 */
	private NetworkServiceImpl service;
	
	/**
	 * The request filter chain.
	 */
	private RequestFilter filter;
	
	/**
	 * Creates a new DiscoveryLauncher.
	 * 
	 * @param service	the network service
	 * @param filter		the request filter chain
	 */
	public DiscoveryLauncher(NetworkServiceImpl service, RequestFilter filter) {
		this.service = service;
		this.filter = filter;
	}
	
	/**
	 * @inheritDoc
	 */
	public void run() {
		LOG.info("--> run()");
		DiscoveryFactory factory = DiscoveryFactory.getInstance();
		DiscoveryCallback discCallback = new DiscoveryCallbackImpl(service.getCallback(), service, filter);
		Discovery discovery = factory.createDiscovery(discCallback);
		discovery.setUserId(service.getUserId());
		UserDetails details = service.getUserDetails();
		if (details != null)
			discovery.setUserDetails(details);
		service.setDiscovery(discovery);
		discovery.execute();
		LOG.info("<-- run()");
	}
	
}
