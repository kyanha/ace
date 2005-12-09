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

import org.apache.log4j.Logger;

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.net.DiscoveryNetworkCallback;
import ch.iserver.ace.net.core.MutableUserDetails;
import ch.iserver.ace.net.core.RemoteUserProxyExt;
import ch.iserver.ace.net.core.RemoteUserProxyFactory;
import ch.iserver.ace.net.impl.protocol.DiscoveryException;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.UUID;

/**
 *
 */
public class ExplicitUserDiscovery extends Thread {

	private Logger LOG = Logger.getLogger(ExplicitUserDiscovery.class);
	
	private InetAddress address;
	private int port;
	private DiscoveryNetworkCallback callback;
	
	public ExplicitUserDiscovery(DiscoveryNetworkCallback callback, InetAddress address, int port) {
		ParameterValidator.notNull("callback", callback);
		ParameterValidator.notNull("address", address);
		this.callback = callback;
		this.address = address;
		this.port = port;
	}
	
	public void run() {
		LOG.debug("--> run()");
		MutableUserDetails details = new MutableUserDetails("discovering...", address, port);
		String temporaryID = UUID.nextUUID();
		RemoteUserProxyExt proxy = RemoteUserProxyFactory.getInstance().createProxy(temporaryID, details);
		proxy.setDNSSDdiscovered(false);
		
		try {
			proxy.discover();
			DiscoveryManagerFactory.getDiscoveryManager().addUser(proxy);
			callback.userDiscoverySucceeded();
		} catch (DiscoveryException ce) {
			LOG.debug("could not connect to [" + address + ":" + port + "], explicit user discovery failed");
			callback.userDiscoveryFailed(FailureCodes.DISCOVERY_FAILED,  address + ":" + port);
		}
		
		LOG.debug("<-- run()");
	}
	
	
}
