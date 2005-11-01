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
import ch.iserver.ace.net.impl.DiscoveryCallback;
import ch.iserver.ace.net.impl.NullDiscoveryCallback;
import ch.iserver.ace.net.impl.RemoteUserProxyImpl;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.TXTRecord;

/**
 * 
 *
 */
class PeerDiscoveryImpl implements PeerDiscovery {
	
	private static Logger LOG = Logger.getLogger(PeerDiscoveryImpl.class);
	
	private static final String SERVICE_NAME_SEPARATOR = "._";

	private DiscoveryCallback callback = NullDiscoveryCallback.getInstance();
	
	private DNSSDService browser;
	
	private Map services;
	
	/**
	 * 
	 *
	 */
	public PeerDiscoveryImpl() {
		services = new HashMap();
	}
	
	/**
	 * 
	 * @param callback
	 */
	public PeerDiscoveryImpl(DiscoveryCallback callback) {
		this.callback = callback;
	}

	/**
	 * Browses the local network for other services and users, respectively.
	 * 
	 * @param props the properties for the DNSSD call.
	 */
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
	
	/**
	 * @inheritDoc
	 */
	public void serviceFound(DNSSDService browser, int flags, int ifIndex, String serviceName, String regType, String domain) {
		try {
			DNSSD.resolve(flags, ifIndex, serviceName, regType, domain, this);
		} catch (Exception e) {
			//TODO:
			LOG.error("Resolve failed ["+e.getMessage()+"]");
		}
	}

	/**
	 * @inheritDoc
	 */
	public void serviceLost(DNSSDService browser, int flags, int ifIndex,
			String serviceName, String regType, String domain) {
		String userId = (String)services.remove(serviceName);
		if (userId != null)
			callback.userDiscarded(userId);
		else 
			LOG.warn("userid for service ["+serviceName+"] not found");
	}

	/**
	 * @inheritDoc
	 */
	public void serviceResolved(DNSSDService resolver, int flags, int ifIndex,
			String fullName, String hostName, int port, TXTRecord txtRecord) {
		String userId = TXTRecordProxy.get(Bonjour.KEY_USERID, txtRecord);
		RemoteUserProxyImpl user = new RemoteUserProxyImpl(
						userId,
						new UserDetails(TXTRecordProxy.get(Bonjour.KEY_USER, txtRecord)),
						hostName, 
						port);
		String serviceName = getServiceName(fullName);
		services.put(serviceName, userId);
		callback.userDiscovered(user);
		resolver.stop();
		
		//monitor TXT record of remote user
		monitorTXTRecord(flags, ifIndex, fullName);
	}

	/**
	 * @param flags
	 * @param ifIndex
	 * @param fullName
	 */
	private void monitorTXTRecord(int flags, int ifIndex, String fullName) {
		try {
			//16=txt record, 1 = ns_c_in, cf. nameser.h
			DNSSD.queryRecord(flags, ifIndex, fullName, 16, 1, this);
		} catch (Exception e) {
			//TODO:
			LOG.error("Query record failed ["+e.getMessage()+"]");
		}
	}
	
	private String getServiceName(String fullName) {
		return fullName.substring(0, fullName.indexOf(SERVICE_NAME_SEPARATOR));
	}

	/**
	 * @inheritDoc
	 */
	public void queryAnswered(DNSSDService query, int flags, int ifIndex,
			String fullName, int rrtype, int rrclass, byte[] rdata, int ttl) {
		//TXT record updates have a TTL value > 0, ignore others 
		if (ttl > 0) {
			String serviceName = getServiceName(fullName);
			String userId = (String)services.get(serviceName);
			if (userId != null) {
				TXTRecord t = new TXTRecord(rdata);
				String userName = TXTRecordProxy.get(Bonjour.KEY_USER, t);
				UserDetails detail = new UserDetails(userName);
				callback.userDetailsChanged(userId, detail);
			} else {
				LOG.warn("TXT record received for unknown user ["+serviceName+"]");
			}
		} else {
			LOG.warn("TXT record update ignored.");
		}
	}
	
	/**
	 * @inheritDoc
	 */
	public void operationFailed(DNSSDService service, int errorCode) {
		// TODO:
		LOG.error("operationFailed ["+errorCode+"]");
	}
	
	/**
	 * Stops the Bonjour peer discovery process.
	 */
	public void stop() {
		browser.stop();
	}
	
	/**
	 * Sets the discovery callback.
	 * @param callback
	 * @see DiscoveryCallback
	 */
	public void setDiscoveryCallback(DiscoveryCallback callback) {
		this.callback = (callback == null) ? NullDiscoveryCallback.getInstance() : callback;
	}
	
	/**
	 * Gets the discovery callback.
	 * @return DiscoveryCallback
	 */
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
