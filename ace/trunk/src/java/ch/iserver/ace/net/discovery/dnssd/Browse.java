/*
 * $Id:Browse.java 2412 2005-12-09 13:15:29Z zbinl $
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

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;

/**
 * <code>DNSSDCall</code> implementation for a DNSSD browse call. This call
 * registers the local user with DNSSD to receive browse events from the local area 
 * network, e.g. discovery of a new service or the loss of a service.
 * 
 * @see ch.iserver.ace.net.discovery.dnssd.DNSSDCall
 */
public class Browse extends DNSSDCall {

	private Logger LOG = Logger.getLogger(Browse.class);
	
	/**
	 * The registration type for DNSSD
	 */
	private String registrationType;
	
	/**
	 * The browse listener
	 */
	private BrowseListener listener;
	
	/**
	 * Constructor.
	 * 
	 * @param registrationType 	the registration type
	 * @param listener			the browse listener
	 */
	public Browse(String registrationType, BrowseListener listener) {
		this.registrationType = registrationType;
		this.listener = listener;
	}
	
	/**
	 * @see ch.iserver.ace.net.discovery.dnssd.DNSSDCall#makeCall()
	 */
	protected Object makeCall() throws DNSSDCallException {
		try {
			return DNSSD.browse(0, 0, registrationType, "", listener);
		} catch (DNSSDException de) {
			throw new DNSSDCallException(de);
		}
	}

	/**
	 * @see DNSSDCall#getLogger()
	 */
	protected Logger getLogger() {
		return LOG;
	}
}
