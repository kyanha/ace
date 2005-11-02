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
import java.util.Properties;

import org.apache.log4j.Logger;

import ch.iserver.ace.util.ParameterValidator;

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

	private DiscoveryCallbackAdapter adapter = NullDiscoveryCallbackAdapter.getInstance();
	
	private DNSSDService browser;
	
	/**
	 * 
	 *
	 */
	public PeerDiscoveryImpl() {}
	
	/**
	 * 
	 * @param adapter
	 */
	public PeerDiscoveryImpl(DiscoveryCallbackAdapter adapter) {
		ParameterValidator.notNull("adapter", adapter);
		this.adapter = adapter;
	}

	/**
	 * @inheritDoc
	 */
	public void browse(final Properties props) {
		ParameterValidator.notNull("properties", props);
		try {
			browser = DNSSD.browse(0, 0, 
					(String)props.get(Bonjour.KEY_REGISTRATION_TYPE), 
					"", 
					this);
		} catch (Exception e) {
			//TODO: retry strategy
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
			//TODO: retry strategy
			LOG.error("Resolve failed ["+e.getMessage()+"]");
		}
	}

	/**
	 * @inheritDoc
	 */
	public void serviceLost(DNSSDService browser, int flags, int ifIndex,
			String serviceName, String regType, String domain) {
		adapter.userDiscarded(serviceName);
	}

	/**
	 * @inheritDoc
	 */
	public void serviceResolved(DNSSDService resolver, int flags, int ifIndex,
			String fullName, String hostName, int port, TXTRecord txtRecord) {
		
		//TODO: get address by call to InetAddress or by a queryRecord call?
		queryRecordForIP(flags, ifIndex, hostName);
		
		String serviceName = getServiceName(fullName);
		String userName = TXTRecordProxy.get(Bonjour.KEY_USER, txtRecord);
		String userId = TXTRecordProxy.get(Bonjour.KEY_USERID, txtRecord);
		adapter.userDiscovered(serviceName, userName, userId, port);
		
		resolver.stop();
		
		//monitor TXT record of remote user
		monitorTXTRecord(flags, ifIndex, fullName);
	}
	
	private String getServiceName(String fullName) {
		return fullName.substring(0, fullName.indexOf(SERVICE_NAME_SEPARATOR));
	}

	private void queryRecordForIP(int flags, int ifIndex, String hostName) {
		try {
			// Start a record query to obtain IP address from hostname
			DNSSD.queryRecord(0, ifIndex, hostName, Bonjour.T_HOST_ADDRESS, 1 /* ns_c_in */, this);
		} catch (Exception e) {
			//TODO: retry strategy
			LOG.error("Query record failed ["+e.getMessage()+"]");
		}
		
	}

	/**
	 * @param flags
	 * @param ifIndex
	 * @param fullName
	 */
	private void monitorTXTRecord(int flags, int ifIndex, String fullName) {
		try {
			//16=txt record, 1 = ns_c_in; cf. nameser.h
			DNSSD.queryRecord(flags, ifIndex, fullName, Bonjour.T_TXT, 1, this);
		} catch (Exception e) {
			//TODO: retry strategy
			LOG.error("Query record failed ["+e.getMessage()+"]");
		}
	}


	/**
	 * @inheritDoc
	 */
	public void queryAnswered(DNSSDService query, int flags, int ifIndex,
			String fullName, int rrtype, int rrclass, byte[] rdata, int ttl) {
		if (rrtype == Bonjour.T_TXT) {
			handleTXTQuery(fullName, rdata, ttl);
		} else if (rrtype == Bonjour.T_HOST_ADDRESS) {
			handleHostAddressQuery(query, fullName, rdata);
		}
	}
	
	/**
	 * @param fullName
	 * @param rdata
	 * @param ttl
	 */
	private void handleTXTQuery(String fullName, byte[] rdata, int ttl) {
		if (ttl > 0) {
			String serviceName = getServiceName(fullName);
			if (adapter.isServiceKnown(serviceName)) {
				TXTRecord t = new TXTRecord(rdata);
				String userName = TXTRecordProxy.get(Bonjour.KEY_USER, t);
				adapter.userNameChanged(serviceName, userName);
			} else {
				LOG.warn("TXT record received for unknown user ["+serviceName+"]");
			}
		} else {
			LOG.warn("TXT record update ignored.");
		}
	}

	/**
	 * 
	 * @param query
	 * @param rdata
	 */
	private void handleHostAddressQuery(DNSSDService query, String fullName, byte[] rdata) {
		InetAddress address = null;
		try {
			address = InetAddress.getByAddress(rdata);
		} catch (Exception e) {
			LOG.error("Could not resolve address ["+e.getMessage()+"]");
		}
		String serviceName = getServiceName(fullName);
		adapter.userAddressResolved(serviceName, address);
		query.stop();
	}
	
	/**
	 * @inheritDoc
	 */
	public void operationFailed(DNSSDService service, int errorCode) {
		// TODO: retry strategy
		LOG.error("operationFailed ["+errorCode+"]");
	}
	
	/**
	 * @inheritDoc
	 */
	public void stop() {
		browser.stop();
	}
	
	/**
	 * @inheritDoc
	 */
	public void setDiscoveryCallbackAdapter(DiscoveryCallbackAdapter callback) {
		this.adapter = (callback == null) ? NullDiscoveryCallbackAdapter.getInstance() : callback;
	}
}
