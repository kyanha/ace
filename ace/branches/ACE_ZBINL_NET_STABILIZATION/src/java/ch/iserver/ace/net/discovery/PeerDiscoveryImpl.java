/*
 * $Id:PeerDiscoveryImpl.java 1205 2005-11-14 07:57:10Z zbinl $
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

import ch.iserver.ace.net.core.NetworkProperties;
import ch.iserver.ace.net.discovery.dnssd.Browse;
import ch.iserver.ace.net.discovery.dnssd.DNSSDUnavailable;
import ch.iserver.ace.util.ParameterValidator;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSDService;

/**
 * Default implementation of {@link ch.iserver.ace.net.discovery.PeerDiscovery}. 
 * The peer discovery process is implemented by means of the Bonjour technology.
 * 
 * <p>For Bonjour related classes refer to 
 *  {@link http://developer.apple.com/documentation/Java/Reference/DNSServiceDiscovery_JavaRef/index.html}	
 * </p>
 */
class PeerDiscoveryImpl implements PeerDiscovery {
	
	/**
	 * The DNSSDService object for the browsing activity.
	 */
	private DNSSDService browser;
	
	/**
	 * The BrowseListener object.
	 */
	private BrowseListener listener;
	
	/**
	 * Creates a new PeerDiscoveryImpl.
	 * 
	 * @param listener	the browse listener to be used
	 */
	public PeerDiscoveryImpl(BrowseListener listener) {
		ParameterValidator.notNull("listener", listener);
		this.listener = listener;
	}
	
	/******************************************/
	/** methods from interface PeerDiscovery **/
	/******************************************/

	/**
	 * Browses the local network for other services of the same type, i.e.
	 * other users.
	 * 
	 * @see Browse
	 */
	public void browse() {
		String regType = NetworkProperties.get(NetworkProperties.KEY_REGISTRATION_TYPE);
		Browse call = new Browse(regType, listener);
		try {
			browser = (DNSSDService)call.execute();
		} catch (DNSSDUnavailable du) {
			Bonjour.writeErrorLog(du);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() {
		if (browser != null) {
			browser.stop();
		}
	}
}
