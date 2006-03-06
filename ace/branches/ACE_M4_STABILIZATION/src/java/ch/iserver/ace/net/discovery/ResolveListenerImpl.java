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

import ch.iserver.ace.net.discovery.dnssd.DNSSDUnavailable;
import ch.iserver.ace.net.discovery.dnssd.QueryRecord;

import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.ResolveListener;
import com.apple.dnssd.TXTRecord;

/**
 * Default implementation of {@link com.apple.dnssd.ResolveListener}.
 * This is a DNSSD listener that receives resolve events. A resolve event
 * delivers further information about a discovered service and user, respectively.
 * This includes the TXT record, which holds the users name and id. 
 * When a resolve event has been received, two more DNSSD queries are started.
 * One is an IP address query request for the discovered user and the other is 
 * a TXT record monitor request, that is, to be kept updated by DNSSD about changes
 * in the TXT record of the respective user. This currently limits to a user name change since
 * this information is stored in the TXT record.
 */
class ResolveListenerImpl extends BaseListenerImpl implements ResolveListener {

	private static Logger LOG = Logger.getLogger(ResolveListenerImpl.class);
	
	/**
	 * The IP query listener
	 */
	private AbstractQueryListener ipQueryListener;
	
	/**
	 * The TXT record listener
	 */
	private AbstractQueryListener txtQueryListener;
	
	/**
	 * Creates a new ResolveListenerImpl instance.
	 * 
	 * @param adapter			the discovery callback adapter
	 * @param ipQueryListener		the IP query listener
	 * @param txtQueryListener	the TXT query listener
	 */
	public ResolveListenerImpl(DiscoveryCallbackAdapter adapter, 
			AbstractQueryListener ipQueryListener, AbstractQueryListener txtQueryListener) {
		super(adapter);
		this.ipQueryListener = ipQueryListener;
		this.txtQueryListener = txtQueryListener;
	}
	
	
	/**
	 * @see com.apple.dnssd.ResolveListener#serviceResolved(com.apple.dnssd.DNSSDService, 
	 * 				int, int, java.lang.String, java.lang.String, int, com.apple.dnssd.TXTRecord)
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
	 * Resolve the IP address for the given <code>serviceName</code>. The <code>ipQueryListener</code>
	 * is passed to the DNSSD call to be notified when the IP is actually resolved. This service name is
	 * added to the IP query listener in order to enable it to correctly match received IP address results with 
	 * the correspoding service name and user, respectively.
	 * 
	 * @param flags			Possible values are: DNSSD.MORE_COMING.
	 * @param ifIndex		If non-zero, specifies the interface on which to issue the query  
	 * 						(the index for a given interface is determined via the if_nametoindex() family of calls.) 
	 * 						Passing 0 causes the name to be queried for on all interfaces.
	 * @param hostName		The host name to get the IP address from.
	 * @param serviceName		The full domain name of the resource record to be queried for.
	 */
	private void resolveIP(int flags, int ifIndex, String hostName, String serviceName) {
		QueryRecord call = new QueryRecord(ifIndex, hostName, Bonjour.T_HOST_ADDRESS, ipQueryListener);
		try {
			//TODO: wie kann ich garantieren, dass folgende 2 zeilen atomar ausgeführt werden?
			DNSSDService service = (DNSSDService) call.execute();
			ipQueryListener.addNextService(service.toString(), serviceName);
		} catch (DNSSDUnavailable du) {
			Bonjour.writeErrorLog(du);
		}
	}
	
	/**
	 * Monitors the TXT record of remote user.
	 * 	
	 * @param flags			The full domain name of the resource record to be queried for.	
	 * @param ifIndex		If non-zero, specifies the interface on which to issue the query  
	 * 						(the index for a given interface is determined via the if_nametoindex() family of calls.) 
	 * 						Passing 0 causes the name to be queried for on all interfaces.
	 * @param fullName		The full service domain name.
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
