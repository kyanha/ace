/*
 * $Id:BrowseListenerImpl.java 1205 2005-11-14 07:57:10Z zbinl $
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

import org.apache.log4j.Logger;

import ch.iserver.ace.net.discovery.dnssd.DNSSDUnavailable;
import ch.iserver.ace.net.discovery.dnssd.Resolve;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.ResolveListener;

/**
 * Default implementation of {@link com.apple.dnssd.BrowseListener}.
 * A DNSSD listener that receives browse events, e.g. when a new service 
 * was found. All events are interpreted either that a new user was discovered or discarded. 
 * Therefore, each event is processed to a 
 * {@link ch.iserver.ace.net.discovery.dnssd.Resolve#makeCall()} to DNSSD, which in reply 
 * will deliver further information about the discovered service and user, respectively.
 */
class BrowseListenerImpl extends BaseListenerImpl implements BrowseListener {

	private static Logger LOG = Logger.getLogger(BrowseListenerImpl.class);
	
	/**
	 * The resolve listener to be passed on each resolve call to DNSSD.
	 */
	private ResolveListener resolver;
	
	/**
	 * Creates a new BrowseListenerImpl.
	 * 
	 * @param adapter		the discovery callback adapter
	 * @param resolver		the resolve listener
	 */
	public BrowseListenerImpl(DiscoveryCallbackAdapter adapter, ResolveListener resolver) {
		super(adapter);
		this.resolver = resolver;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void serviceFound(DNSSDService browser, int flags, int ifIndex, String serviceName, String regType, String domain) {
		LOG.debug("DNSSD.serviceFound("+serviceName+", "+regType+", "+domain+")");
		if (!Bonjour.getLocalServiceName().equals(serviceName)) {
			LOG.debug("serviceFound("+serviceName+")");
			try {
				Resolve call = new Resolve(flags, ifIndex, serviceName, regType, domain, resolver);
				call.execute();
			} catch (DNSSDUnavailable du) {
				Bonjour.writeErrorLog(du);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void serviceLost(DNSSDService browser, int flags, int ifIndex,
			String serviceName, String regType, String domain) {
		LOG.debug("DNSSD.serviceLost("+serviceName+", "+regType+", "+domain+")");
		adapter.userDiscarded(serviceName);
	}

}
