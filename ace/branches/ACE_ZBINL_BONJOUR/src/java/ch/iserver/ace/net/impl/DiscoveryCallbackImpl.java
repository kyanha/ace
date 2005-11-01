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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.RemoteUserProxy;

public class DiscoveryCallbackImpl implements DiscoveryCallback {

	private static Logger LOG = Logger.getLogger(DiscoveryCallbackImpl.class);
	
	private NetworkServiceCallback callback;
	private Map remoteUserProxies;
	
	public DiscoveryCallbackImpl(NetworkServiceCallback callback) {
		this.callback = callback;
		remoteUserProxies = new HashMap();
	}
	
	public void userDiscovered(RemoteUserProxyNet user) {
		remoteUserProxies.put(user.getId(), user);
		//notify upper layer of discovery
		callback.userDiscovered(user);
		
		//TODO: initiate process of getting published documents for this remote user
		
	}

	public void userDiscarded(String id) {
		RemoteUserProxy user = (RemoteUserProxy)remoteUserProxies.remove(id);
		callback.userDiscarded(user);
	}

	public void userDetailsChanged(String id, UserDetails details) {
		RemoteUserProxyNet proxy = (RemoteUserProxyNet)remoteUserProxies.get(id);
		proxy.setUserDetails(details);
		callback.userDetailsChanged(proxy);
	}


}
