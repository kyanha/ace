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

import java.util.Properties;

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

	private DiscoveryCallback callback;
	private DNSSDService browser;
	
	public BonjourPeerDiscovery(DiscoveryCallback callback) {
		this.callback = callback;
	}

	public void browse(final Properties props) {
		try {
			browser = DNSSD.browse(0, 0, 
					(String)props.get(Bonjour.REGISTRATION_TYPE_KEY), 
					"", 
					this);
		} catch (Exception e) {
			//TODO:
		}
	}
	
	
	
	public void serviceFound(DNSSDService browser, int flags, int ifIndex,
			String serviceName, String regType, String domain) {
		try {
			DNSSD.resolve(flags, ifIndex, serviceName, regType, domain, this);
		} catch (Exception e) {
			//TODO:
		}
	}

	public void serviceLost(DNSSDService browser, int flags, int ifIndex,
			String serviceName, String regType, String domain) {
		// TODO Auto-generated method stub
		
		//callback.userDiscarded()
	}

	public void serviceResolved(DNSSDService resolver, int flags, int ifIndex,
			String fullName, String hostName, int port, TXTRecord txtRecord) {
		
		
		//callback.userDiscovered()
		resolver.stop();
	}
	
	public void operationFailed(DNSSDService service, int errorCode) {
		// TODO Auto-generated method stub
		
	}
	
	public void stop() {
		browser.stop();
	}

}
