/*
 * $Id:ResolveListenerImpl.java 1205 2005-11-14 07:57:10Z zbinl $
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

import ch.iserver.ace.net.impl.discovery.dnssd.DNSSDUnavailable;
import ch.iserver.ace.net.impl.discovery.dnssd.QueryRecord;

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
		String userName = TXTRecordProxy.get(TXTRecordProxy.TXT_USER, txtRecord);
		LOG.debug("serviceResolved("+serviceName+", "+userName+", "+txtRecord+")");
		String userId = TXTRecordProxy.get(TXTRecordProxy.TXT_USERID, txtRecord);
		adapter.userDiscovered(serviceName, userName, userId, port);
		resolver.stop();
		
		resolveIP(flags, ifIndex, hostName, serviceName);
		monitorTXTRecord(flags, ifIndex, fullName);
	}
	
	/**
	 * 
	 * @param flags
	 * @param ifIndex
	 * @param hostName
	 */
	private void resolveIP(int flags, int ifIndex, String hostName, String serviceName) {
		ipQueryListener.addNextService(serviceName);
		QueryRecord call = new QueryRecord(ifIndex, hostName, Bonjour.T_HOST_ADDRESS, ipQueryListener);
		try {
			call.execute();
		} catch (DNSSDUnavailable du) {
			Bonjour.writeErrorLog(du);
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
		QueryRecord call = new QueryRecord(ifIndex, fullName, Bonjour.T_TXT, txtQueryListener);
		//TODO: do we have to keep the return value of the queryRecord call, so that on shutdown
		//we can stop that service too?
		try {
			call.execute();
		} catch (DNSSDUnavailable du) {
			Bonjour.writeErrorLog(du);
		}
	}

}
