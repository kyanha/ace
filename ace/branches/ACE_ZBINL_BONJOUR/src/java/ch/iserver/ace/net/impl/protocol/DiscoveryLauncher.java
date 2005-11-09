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

package ch.iserver.ace.net.impl.protocol;

import org.apache.log4j.Logger;

import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.impl.Discovery;
import ch.iserver.ace.net.impl.DiscoveryCallback;
import ch.iserver.ace.net.impl.DiscoveryCallbackImpl;
import ch.iserver.ace.net.impl.DiscoveryFactory;
import ch.iserver.ace.net.impl.NetworkServiceExt;

/**
 *
 */
public class DiscoveryLauncher extends Thread {

	private static Logger LOG = Logger.getLogger(DiscoveryLauncher.class);
	
	private NetworkServiceCallback callback;
	private String userId;
	private NetworkServiceExt service;
	
	public DiscoveryLauncher(String userId, NetworkServiceCallback callback, NetworkServiceExt service) {
		this.userId = userId;
		this.callback = callback;
		this.service = service;
	}
	
	public void run() {
		LOG.info("--> run()");
		DiscoveryFactory factory = DiscoveryFactory.getInstance();
		DocumentDiscovery docDisc = DocumentDiscoveryFactory.create(callback);
		DiscoveryCallback discCallback = new DiscoveryCallbackImpl(callback, docDisc);
		Discovery discovery = factory.createDiscovery(discCallback);
		discovery.setUserId(userId);
		service.setDiscovery(discovery);
		discovery.execute();
		LOG.info("<-- run()");
	}
	
}
