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

import org.apache.log4j.Logger;

import com.apple.dnssd.DNSSDService;

/**
 *
 */
class IPQueryListener extends AbstractQueryListener {

	private static Logger LOG = Logger.getLogger(IPQueryListener.class);
	
	/**
	 * 
	 * @param adapter
	 */
	public IPQueryListener(DiscoveryCallbackAdapter adapter) {
		super(adapter);
	}
	
	/**
	 * @inherit
	 */
	protected void processQuery(DNSSDService query, int flags, int ifIndex,
			String fullName, int rrtype, int rrclass, byte[] rdata, int ttl) {
		InetAddress address = null;
		try {
			address = InetAddress.getByAddress(rdata);
		} catch (Exception e) {
			LOG.error("Could not resolve address ["+e.getMessage()+"]");
		}
		String serviceName = Bonjour.getServiceName(fullName);
		adapter.userAddressResolved(serviceName, address);
		query.stop();
	}
}
