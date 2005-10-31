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
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.impl.DiscoveryCallback;
import ch.iserver.ace.net.impl.RemoteUserProxyImpl;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.ResolveListener;
import com.apple.dnssd.TXTRecord;

/**
 * 
 *
 */
class BonjourPeerDiscovery implements BrowseListener, ResolveListener {
	
	private static Logger LOG = Logger.getLogger(BonjourPeerDiscovery.class);
	
	private static final String SERVICE_NAME_SEPARATOR = "._";

	private DiscoveryCallback callback = NullDiscoveryCallback.getInstance();
	private DNSSDService browser;
	
	private Map services;
	
	public BonjourPeerDiscovery() {
		services = new HashMap();
	}
	
	public BonjourPeerDiscovery(DiscoveryCallback callback) {
		this.callback = callback;
	}

	public void browse(final Properties props) {
		try {
			browser = DNSSD.browse(0, 0, 
					(String)props.get(Bonjour.KEY_REGISTRATION_TYPE), 
					"", 
					this);
		} catch (Exception e) {
			//TODO:
			LOG.error("Browsing failed ["+e.getMessage()+"]");
		}
	}
	
	
	
	public void serviceFound(DNSSDService browser, int flags, int ifIndex,
			String serviceName, String regType, String domain) {
		try {
			DNSSD.resolve(flags, ifIndex, serviceName, regType, domain, this);
		} catch (Exception e) {
			//TODO:
			LOG.error("Resolve failed ["+e.getMessage()+"]");
		}
	}

	public void serviceLost(DNSSDService browser, int flags, int ifIndex,
			String serviceName, String regType, String domain) {
		String userId = (String)services.remove(serviceName);
		callback.userDiscarded(userId);
	}

	public void serviceResolved(DNSSDService resolver, int flags, int ifIndex,
			String fullName, String hostName, int port, TXTRecord txtRecord) {
		String userId = TXTRecordProxy.get(Bonjour.KEY_USERID, txtRecord);
		RemoteUserProxy user = new RemoteUserProxyImpl(
						userId,
						new UserDetails(TXTRecordProxy.get(Bonjour.KEY_USER, txtRecord)),
						hostName, 
						port);
		String serviceName = fullName.substring(0, fullName.indexOf(SERVICE_NAME_SEPARATOR));
		services.put(serviceName, userId);
		callback.userDiscovered(user);
		resolver.stop();
	}
	
	public void operationFailed(DNSSDService service, int errorCode) {
		// TODO:
		LOG.error("operationFailed ["+errorCode+"]");
	}
	
	public void stop() {
		browser.stop();
	}
	
	public void setDiscoveryCallback(DiscoveryCallback callback) {
		this.callback = (callback == null) ? NullDiscoveryCallback.getInstance() : callback;
	}
	
	public DiscoveryCallback getDiscoveryCallback() {
		return callback;
	}
	
	private InetAddress getInetAddress(String hostName) {
		InetAddress address = null;
		try {
			address = InetAddress.getByName(hostName);
		} catch (UnknownHostException uhe) {
			LOG.error(uhe);
		}
		return address;
	}

}
