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
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.iserver.ace.net.impl.DiscoveryCallback;
import ch.iserver.ace.net.impl.MutableUserDetails;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.RemoteUserProxyImpl;

class DiscoveryCallbackAdapter {
	
	private static Logger LOG = Logger.getLogger(DiscoveryCallbackAdapter.class);

	private DiscoveryCallback forward;
	private Map remoteUserProxies; 	//id to proxy
	private Map services;				//service name to id
	//TODO: service to proxy??
	
	public DiscoveryCallbackAdapter(DiscoveryCallback forward) {
		this();
		this.forward = forward;
	}
	
	public DiscoveryCallbackAdapter() {
		remoteUserProxies = new HashMap();
		services = new HashMap();
	}
	
	public void setDiscoveryCallback(DiscoveryCallback callback) {
		this.forward = callback;
	}
	
	public void userDiscovered(RemoteUserProxyExt proxy) {
		remoteUserProxies.put(proxy.getId(), proxy);
		
		forward.userDiscovered(proxy);
	}
	
	public void userDiscovered(String serviceName, String username, String userId, int port) {
		MutableUserDetails details = new MutableUserDetails(username);
		details.setPort(port);
		
		RemoteUserProxyImpl proxy = new RemoteUserProxyImpl(userId, details);
		remoteUserProxies.put(userId, proxy);
		services.put(serviceName, userId);
		
		forward.userDiscovered(proxy);
	}

	public void userDiscarded(String serviceName) {
		String userId = (String)services.remove(serviceName);
		if (userId != null) {
			RemoteUserProxyExt user = (RemoteUserProxyExt)remoteUserProxies.remove(userId);
			forward.userDiscarded(user);	
		} else { 
			LOG.warn("userid for service ["+serviceName+"] not found");
		}
	}

	public void userNameChanged(String serviceName, String userName) {
		String userId = (String)services.get(serviceName);
		if (userId != null) {
			RemoteUserProxyExt proxy = (RemoteUserProxyExt)remoteUserProxies.get(userId);
			MutableUserDetails details = (MutableUserDetails)proxy.getUserDetails();
			details.setUsername(userName);
			proxy.setUserDetails(details);
			forward.userDetailsChanged(proxy);
		} else {
			LOG.warn("Username change received for unknown user ["+serviceName+"]");
		}
	}
	
	public void userAddressResolved(String serviceName, InetAddress address) {
		String userId = (String)services.get(serviceName);
		if (userId != null) {
			RemoteUserProxyExt proxy = (RemoteUserProxyExt)remoteUserProxies.get(userId);
			MutableUserDetails details = (MutableUserDetails)proxy.getUserDetails();
			details.setAddress(address);
			forward.userDetailsChanged(proxy);
		} else {
			LOG.warn("Host address received for unknown user ["+serviceName+"]");
		}
	}
	
	public boolean isServiceKnown(String serviceName) {
		return services.containsKey(serviceName);
	}

}
