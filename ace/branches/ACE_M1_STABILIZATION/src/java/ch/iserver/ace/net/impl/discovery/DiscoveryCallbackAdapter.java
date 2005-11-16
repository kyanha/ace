/*
 * $Id:DiscoveryCallbackAdapter.java 1205 2005-11-14 07:57:10Z zbinl $
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
import ch.iserver.ace.util.ParameterValidator;

/**
 * 
 */
class DiscoveryCallbackAdapter {
	
	private static Logger LOG = Logger.getLogger(DiscoveryCallbackAdapter.class);

	private DiscoveryCallback forward;
	private Map remoteUserProxies; 	//id to proxy
	private Map services;				//service name to id
	//TODO: service to proxy??
	
	/**
	 * 
	 */
	public DiscoveryCallbackAdapter(DiscoveryCallback forward) {
		this();
		ParameterValidator.notNull("forward", forward);
		this.forward = forward;
	}
	
	/**
	 * 
	 *
	 */
	public DiscoveryCallbackAdapter() {
		remoteUserProxies = new HashMap();
		services = new HashMap();
	}
	
	/**
	 * 
	 * @param forward
	 */
	public void setDiscoveryCallback(DiscoveryCallback forward) {
		ParameterValidator.notNull("forward", forward);
		this.forward = forward;
	}
	
	/**
	 * 
	 * @param serviceName
	 * @param username
	 * @param userId
	 * @param port
	 */
	public void userDiscovered(String serviceName, String username, String userId, int port) {
		ParameterValidator.notNull("serviceName", serviceName);
		ParameterValidator.notNull("username", username);
		ParameterValidator.notNull("userId", userId);
		
		MutableUserDetails details = new MutableUserDetails(username);
		details.setPort(port);
		
		RemoteUserProxyImpl proxy = new RemoteUserProxyImpl(userId, details);
		remoteUserProxies.put(userId, proxy);
		services.put(serviceName, userId);
		
		forward.userDiscovered(proxy);
	}

	/**
	 * 
	 * @param serviceName
	 */
	public void userDiscarded(String serviceName) {
		if (serviceName != null) {
			String userId = (String)services.remove(serviceName);
			if (userId != null) {
				RemoteUserProxyExt user = (RemoteUserProxyExt)remoteUserProxies.remove(userId);
				forward.userDiscarded(user);	
			} else { 
				LOG.warn("userid for service ["+serviceName+"] not found");
			}
		} else {
			LOG.warn("serviceName null");
		}
	}

	/**
	 * 
	 * @param serviceName
	 * @param userName
	 */
	public void userNameChanged(String serviceName, String userName) {
		ParameterValidator.notNull("serviceName", serviceName);
		ParameterValidator.notNull("userName", userName);
		
		String userId = (String)services.get(serviceName);
		if (userId != null) {
			RemoteUserProxyExt proxy = (RemoteUserProxyExt)remoteUserProxies.get(userId);
			String oldName = proxy.getUserDetails().getUsername();
			if (!oldName.equals(userName)) {
				proxy.getUserDetails().setUsername(userName);
				forward.userDetailsChanged(proxy);
			}
		} else {
			LOG.warn("username change received for unknown user ["+serviceName+"]");
		}
	}
	
	/**
	 * 
	 * @param serviceName
	 * @param address
	 */
	public void userAddressResolved(String serviceName, InetAddress address) {
		ParameterValidator.notNull("serviceName", serviceName);
		ParameterValidator.notNull("address", address);
		
		String userId = (String)services.get(serviceName);
		if (userId != null) {
			RemoteUserProxyExt proxy = (RemoteUserProxyExt)remoteUserProxies.get(userId);
			MutableUserDetails details = (MutableUserDetails)proxy.getUserDetails();
			details.setAddress(address);
			forward.userDiscoveryCompleted(proxy);
		} else {
			LOG.warn("Host address received for unknown user ["+serviceName+"]");
		}
	}
	
	/**
	 * 
	 * @param serviceName
	 * @return boolean true iff 
	 */
	public boolean isServiceKnown(String serviceName) {
		return services.containsKey(serviceName);
	}

}
