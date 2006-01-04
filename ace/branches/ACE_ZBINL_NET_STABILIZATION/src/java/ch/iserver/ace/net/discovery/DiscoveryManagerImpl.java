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
package ch.iserver.ace.net.discovery;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import ch.iserver.ace.net.core.DiscoveryCallback;
import ch.iserver.ace.net.core.MutableUserDetails;
import ch.iserver.ace.net.core.RemoteUserProxyExt;
import ch.iserver.ace.net.core.RemoteUserProxyFactory;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Default implementation of {@link ch.iserver.ace.net.discovery.DiscoveryManager} and
 * {@link ch.iserver.ace.net.discovery.DiscoveryCallbackAdapter}.
 * 
 * <p><code>DiscoveryManagerImpl</code> handles the accounting of remote users by being a mediator
 * between the DNSSD listener and the {@link ch.iserver.ace.net.core.DiscoveryCallback}. All discovery events
 * are passed to the <code>DiscoveryManagerImpl</code>, processed and forwarded to the <code>DiscoveryCallback</code>
 * which finally notifies the upper layer of the events. 
 * The network layer collaborates with the <code>DiscoveryManagerImpl</code> in the way that all user events received 
 * via the protocol and not the automatic discovery are handed to it. Also session management (the users who have a session
 * established with the local user) is done by the <code>DiscoveryManagerImpl</code>. Therefore the <code>DiscoveryManagerImpl</code> 
 * is the central component that handles the remote user accounting.
 * </p>   
 */
class DiscoveryManagerImpl implements DiscoveryCallbackAdapter, DiscoveryManager {
	
	private static Logger LOG = Logger.getLogger(DiscoveryManagerImpl.class);
	
	/**
	 * The DiscoveryCallback instance 
	 */
	private DiscoveryCallback forward;
	
	/**
	 * A map with all the remote user proxies.
	 */
	private Map remoteUserProxies; 	//id to proxy
	
	/**
	 * A map with all service names. The service names are received from DNSSD events
	 * and must be mapped to the user id's. This mapping is necessary because there
	 * are calls where only the service name is delivered as an information that allows for
	 * a correct assignment of the received data to the respective user.
	 * The service name is unique too.
	 */
	private Map services;				//service name to id
	
	/**
	 * A map that contains all remote user proxies with whom the local user has 
	 * a RemoteUserSession established.
	 */
	private Map peersWithEstablishedSession; //id to proxy
	
	/**
	 * Creates a new DiscoveryManagerImpl.
	 * Note that the argument may not be null.
	 * 
	 * @param forward	the discovery callback
	 * @throws IllegalArgumentException if the argument is null 
	 */
	public DiscoveryManagerImpl(DiscoveryCallback forward) {
		init();
		ParameterValidator.notNull("forward", forward);
		this.forward = forward;
	}
	
	/**
	 * Initializes the discovery manager.
	 */
	private void init() {
		remoteUserProxies = Collections.synchronizedMap(new LinkedHashMap());
		services = Collections.synchronizedMap(new LinkedHashMap());
		peersWithEstablishedSession = Collections.synchronizedMap(new LinkedHashMap());
	}
	
	/*********************************************/
	/** Methods from interface DiscoveryManager **/
	/*********************************************/
	
	/**
	 * {@inheritDoc}
	 */
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
	 * {@inheritDoc}
	 */
	public void setSessionEstablished(String userId) {
		Object proxy = remoteUserProxies.get(userId);
		if (proxy == null) {
			LOG.warn("proxy for userid="+userId+" not found.");
		}
		peersWithEstablishedSession.put(userId, proxy);		
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSessionTerminated(String userId) {
		if (userId != null) {
			peersWithEstablishedSession.remove(userId);
		} else {
			LOG.warn("userId null");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean hasSessionEstablished(String userid) {
		return peersWithEstablishedSession.containsKey(userid);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public RemoteUserProxyExt getUser(String userid) {
		RemoteUserProxyExt user =  (RemoteUserProxyExt) remoteUserProxies.get(userid);
		if (user == null) {
			LOG.warn("no user found for ["+userid+"]");
		}
		return user;
	}
	
	public Map getUsers() {
		return remoteUserProxies;
	}
	
	/**
	 * Adds a new user to the <code>DiscoveryManager</code> and notifies
	 * the <code>DiscoveryCallback</code> with a call to <code>userDiscovered(RemoteUserProxyExt)</code>.
	 * If the user was explicitly discovered, <code>userDiscoveryCompleted(RemoteUserProxyExt)</code> is 
	 * called as well.
	 * 
	 * @param user the user to be added
	 * @see DiscoveryCallback
	 */
	public void addUser(RemoteUserProxyExt user) {
		ParameterValidator.notNull("user", user);
		LOG.debug("--> addUser(" + user.getUserDetails().getUsername() + ")");
		remoteUserProxies.put(user.getId(), user);
		forward.userDiscovered(user);
		if (!user.isDNSSDdiscovered()) {
			forward.userDiscoveryCompleted(user);
		}
		LOG.debug("<-- addUser()");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void discardUser(String userid) {
		ParameterValidator.notNull("userid", userid);
		RemoteUserProxyExt user = (RemoteUserProxyExt)remoteUserProxies.remove(userid);
		if (user != null) {
			forward.userDiscarded(user);
		} else {
			LOG.warn("user to be discarded not found [" + userid + "]");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getSize() {
		return remoteUserProxies.size();
	}
	
	
	/*****************************************************/
	/** Methods from interface DiscoveryCallbackAdapter **/
	/*****************************************************/
	
	/**
	 * @see ch.iserver.ace.net.discovery.DiscoveryCallbackAdapter#setDiscoveryCallback(ch.iserver.ace.net.core.DiscoveryCallback)
	 */
	public void setDiscoveryCallback(DiscoveryCallback forward) {
		ParameterValidator.notNull("forward", forward);
		this.forward = forward;
	}
	
	/**
	 * @see ch.iserver.ace.net.discovery.DiscoveryCallbackAdapter#userDiscovered(java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public void userDiscovered(String serviceName, String username, String userId, int port) {
		ParameterValidator.notNull("serviceName", serviceName);
		ParameterValidator.notNull("username", username);
		ParameterValidator.notNull("userId", userId);
		
		MutableUserDetails details = new MutableUserDetails(username);
		details.setPort(port);
		
		services.put(serviceName, userId);
		RemoteUserProxyExt user = getUser(userId);
		if (user == null) { //add user
			user = RemoteUserProxyFactory.getInstance().createProxy(userId, details);
			user.setDNSSDdiscovered(true);
			addUser(user);
		} else { //update user info
			//user proxy has been created before but with no user details
			LOG.debug("user [" + username + "] has been created before, thus update only");
			user.getMutableUserDetails().setUsername(username);
			forward.userDetailsChanged(user);
		}
	}

	/**
	 * @see ch.iserver.ace.net.discovery.DiscoveryCallbackAdapter#userDiscarded(java.lang.String)
	 */
	public void userDiscarded(String serviceName) {
		if (serviceName != null) {
			String userId = (String)services.remove(serviceName);
			if (userId != null) {
				discardUser(userId);
			} else { 
				LOG.warn("userid for service ["+serviceName+"] not found");
			}
		} else {
			LOG.warn("serviceName null");
		}
	}

	/**
	 * @see ch.iserver.ace.net.discovery.DiscoveryCallbackAdapter#userNameChanged(java.lang.String, java.lang.String)
	 */
	public void userNameChanged(String serviceName, String userName) {
		ParameterValidator.notNull("serviceName", serviceName);
		ParameterValidator.notNull("userName", userName);
		
		String userId = (String)services.get(serviceName);
		if (userId != null) {
			RemoteUserProxyExt proxy = getUser(userId);
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
	 * @see ch.iserver.ace.net.discovery.DiscoveryCallbackAdapter#userAddressResolved(java.lang.String, java.net.InetAddress)
	 */
	public void userAddressResolved(String serviceName, InetAddress address) {
		ParameterValidator.notNull("serviceName", serviceName);
		ParameterValidator.notNull("address", address);
		
		String userId = (String)services.get(serviceName);
		if (userId != null) {
			RemoteUserProxyExt proxy = getUser(userId);
			MutableUserDetails details = (MutableUserDetails)proxy.getUserDetails();
			if (details.getAddress() == null) {
				details.setAddress(address);
				forward.userDiscoveryCompleted(proxy);
			} else {
				LOG.debug("address already set for [" + details.getUsername() + "], ignore [" + address + "]");
			}
		} else {
			LOG.warn("Host address received for unknown user ["+serviceName+"]");
		}
	}
	
	/**
	 * @see ch.iserver.ace.net.discovery.DiscoveryCallbackAdapter#isServiceKnown(java.lang.String)
	 */
	public boolean isServiceKnown(String serviceName) {
		return services.containsKey(serviceName);
	}

}
