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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.impl.DiscoveryCallback;
import ch.iserver.ace.net.impl.MutableUserDetails;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.RemoteUserProxyFactory;
import ch.iserver.ace.net.impl.RemoteUserProxyImpl;
import ch.iserver.ace.util.ParameterValidator;

/**
 * 
 */
class DiscoveryManagerImpl implements DiscoveryCallbackAdapter, DiscoveryManager {
	
	private static Logger LOG = Logger.getLogger(DiscoveryManagerImpl.class);

	private DiscoveryCallback forward;
	private Map remoteUserProxies; 	//id to proxy
	private Map services;				//service name to id
	private Map peersWithEstablishedSession; //id to proxy
	
	/**
	 * 
	 */
	public DiscoveryManagerImpl(DiscoveryCallback forward) {
		this();
		ParameterValidator.notNull("forward", forward);
		this.forward = forward;
	}
	
	/**
	 * 
	 *
	 */
	public DiscoveryManagerImpl() {
		remoteUserProxies = Collections.synchronizedMap(new LinkedHashMap());
		services = Collections.synchronizedMap(new LinkedHashMap());
		peersWithEstablishedSession = Collections.synchronizedMap(new LinkedHashMap());
	}
	
	/*********************************************/
	/** Methods from interface DiscoveryManager **/
	/*********************************************/
	public RemoteUserProxyExt[] getPeersWithNoSession() {
		Set ids = remoteUserProxies.keySet();
		Set allUsers = new LinkedHashSet();
		allUsers.addAll(ids);
		Set sessionIds = peersWithEstablishedSession.keySet();
		Set sessionUsers = new LinkedHashSet();
		sessionUsers.addAll(sessionIds);
		allUsers.removeAll(sessionUsers);
		
		RemoteUserProxyExt[] proxies = new RemoteUserProxyExt[allUsers.size()];
		Iterator iter = allUsers.iterator();
		int i = 0;
		while (iter.hasNext()) {
			String id = (String) iter.next();
			proxies[i++] = (RemoteUserProxyExt) remoteUserProxies.get(id);
		}
		return proxies;
	}

	/**
	 * Called when the TCPSession for a user has been established.
	 */
	public void setSessionEstablished(String userId) {
		peersWithEstablishedSession.put(userId, remoteUserProxies.get(userId));
		
	}

	public void setSessionTerminated(String userId) {
		peersWithEstablishedSession.remove(userId);
		
	}
	
	public boolean hasSessionEstablished(String userid) {
		return peersWithEstablishedSession.containsKey(userid);
	}
	
	public RemoteUserProxyExt getUser(String userid) {
		return (RemoteUserProxyExt) remoteUserProxies.get(userid);
	}
	
	public int getSize() {
		return remoteUserProxies.size();
	}
	
	
	/*****************************************************/
	/** Methods from interface DiscoveryCallbackAdapter **/
	/*****************************************************/
	
	/**
	 * @see ch.iserver.ace.net.impl.discovery.DiscoveryCallbackAdapter#setDiscoveryCallback(ch.iserver.ace.net.impl.DiscoveryCallback)
	 */
	public void setDiscoveryCallback(DiscoveryCallback forward) {
		ParameterValidator.notNull("forward", forward);
		this.forward = forward;
	}
	
	/**
	 * @see ch.iserver.ace.net.impl.discovery.DiscoveryCallbackAdapter#userDiscovered(java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public void userDiscovered(String serviceName, String username, String userId, int port) {
		ParameterValidator.notNull("serviceName", serviceName);
		ParameterValidator.notNull("username", username);
		ParameterValidator.notNull("userId", userId);
		
		MutableUserDetails details = new MutableUserDetails(username);
		details.setPort(port);
		
		RemoteUserProxyExt proxy = RemoteUserProxyFactory.getInstance().createProxy(userId, details);
		remoteUserProxies.put(userId, proxy);
		services.put(serviceName, userId);
		
		forward.userDiscovered(proxy);
	}

	/**
	 * @see ch.iserver.ace.net.impl.discovery.DiscoveryCallbackAdapter#userDiscarded(java.lang.String)
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
	 * @see ch.iserver.ace.net.impl.discovery.DiscoveryCallbackAdapter#userNameChanged(java.lang.String, java.lang.String)
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
	 * @see ch.iserver.ace.net.impl.discovery.DiscoveryCallbackAdapter#userAddressResolved(java.lang.String, java.net.InetAddress)
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
	 * @see ch.iserver.ace.net.impl.discovery.DiscoveryCallbackAdapter#isServiceKnown(java.lang.String)
	 */
	public boolean isServiceKnown(String serviceName) {
		return services.containsKey(serviceName);
	}

}
