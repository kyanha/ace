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

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.ResolveListener;
import com.apple.dnssd.TXTRecord;

/**
 *
 */
class ResolveListenerImpl extends BaseListenerImpl implements ResolveListener {

	private static Logger LOG = Logger.getLogger(ResolveListenerImpl.class);
	
	
	private AbstractQueryListener ipQueryListener;
	private AbstractQueryListener txtQueryListener;
	
	public ResolveListenerImpl(DiscoveryCallbackAdapter adapter, 
			AbstractQueryListener ipQueryListener, AbstractQueryListener txtQueryListener) {
		super(adapter);
		this.ipQueryListener = ipQueryListener;
		this.txtQueryListener = txtQueryListener;
	}
	
	
	/**
	 * @see com.apple.dnssd.ResolveListener#serviceResolved(com.apple.dnssd.DNSSDService, int, int, java.lang.String, java.lang.String, int, com.apple.dnssd.TXTRecord)
	 */
	public void serviceResolved(DNSSDService resolver, int flags, int ifIndex,
			String fullName, String hostName, int port, TXTRecord txtRecord) {
		String serviceName = Bonjour.getServiceName(fullName);
		String userName = TXTRecordProxy.get(Bonjour.KEY_USER, txtRecord);
		String userId = TXTRecordProxy.get(Bonjour.KEY_USERID, txtRecord);
		adapter.userDiscovered(serviceName, userName, userId, port);
		resolver.stop();
		
		resolveIP(flags, ifIndex, hostName);
		monitorTXTRecord(flags, ifIndex, fullName);
	}
	
	/**
	 * 
	 * @param flags
	 * @param ifIndex
	 * @param hostName
	 */
	private void resolveIP(int flags, int ifIndex, String hostName) {
		try {
			// Start a record query to obtain IP address from hostname
			DNSSD.queryRecord(0, ifIndex, hostName, Bonjour.T_HOST_ADDRESS, 1 /* ns_c_in */, ipQueryListener);
		} catch (Exception e) {
			//TODO: retry strategy
			LOG.error("Query record failed ["+e.getMessage()+"]");
		}
		
	}
	
	/**
	 * Monitors TXT record of remote user.
	 * 
	 * @param flags
	 * @param ifIndex
	 * @param fullName
	 */
	private void monitorTXTRecord(int flags, int ifIndex, String fullName) {
		try {
			//16=txt record, 1 = ns_c_in; cf. nameser.h
			//TODO: do we have to keep the return value of the queryRecord call, so that on shutdown
			//we can stop that service too?
			DNSSD.queryRecord(flags, ifIndex, fullName, Bonjour.T_TXT, 1, txtQueryListener);
		} catch (Exception e) {
			//TODO: retry strategy
			LOG.error("Query record failed ["+e.getMessage()+"]");
		}
	}

}
