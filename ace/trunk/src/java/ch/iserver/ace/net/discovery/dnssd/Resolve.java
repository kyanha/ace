/*
 * $Id:Resolve.java 2412 2005-12-09 13:15:29Z zbinl $
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

package ch.iserver.ace.net.discovery.dnssd;

import org.apache.log4j.Logger;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.ResolveListener;

/**
 * <code>DNSSDCall</code> implementation for a DNSSD resolve call. This call resolves
 * a service name discovered to a target host name, port number, and txt record. 
 * This is necessary in order to connect to a discovered user.
 */
public class Resolve extends DNSSDCall {

	private Logger LOG = Logger.getLogger(Resolve.class);
	
	private int flags, ifIndex;
	private String serviceName, regType, domain;
	private ResolveListener resolver;
	
	/**
	 * Constructor.
	 * 
	 * @param flags
	 * @param ifIndex
	 * @param serviceName
	 * @param regType
	 * @param domain
	 * @param resolver
	 */
	public Resolve(int flags, int ifIndex, String serviceName, String regType, String domain, ResolveListener resolver) {
		this.flags = flags;
		this.ifIndex = ifIndex;
		this.serviceName = serviceName;
		this.regType = regType;
		this.domain = domain;
		this.resolver = resolver;
	}
	
	/**
	 * @see ch.iserver.ace.net.discovery.dnssd.DNSSDCall#makeCall()
	 */
	protected Object makeCall() throws DNSSDCallException {
		try {
			return DNSSD.resolve(flags, ifIndex, serviceName, regType, domain, resolver);
		} catch (DNSSDException de) {
			throw new DNSSDCallException(de);
		}
	}
	
	protected Logger getLogger() {
		return LOG;
	}

}
