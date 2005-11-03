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

import org.apache.log4j.Logger;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.ResolveListener;

/**
 *
 */
class BrowseListenerImpl extends BaseListenerImpl implements BrowseListener {

	private static Logger LOG = Logger.getLogger(BrowseListenerImpl.class);
	
	private ResolveListener resolver;
	
	public BrowseListenerImpl(DiscoveryCallbackAdapter adapter, ResolveListener resolver) {
		super(adapter);
		this.resolver = resolver;
	}
	
	/**
	 * @inheritDoc
	 */
	public void serviceFound(DNSSDService browser, int flags, int ifIndex, String serviceName, String regType, String domain) {
		try {
			DNSSD.resolve(flags, ifIndex, serviceName, regType, domain, resolver);
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

}
