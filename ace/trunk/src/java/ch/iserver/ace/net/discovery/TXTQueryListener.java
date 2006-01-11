/*
 * $Id:TXTQueryListener.java 1205 2005-11-14 07:57:10Z zbinl $
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

import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.TXTRecord;

/**
 * Extension listener of {@link ch.iserver.ace.net.discovery.AbstractQueryListener} 
 * for TXT query results. There are two cases when TXT events are received and must be 
 * processed:
 * <pre>
 * 	1. User discovery: 	the TXT record contains the user's name and id.
 * 	2. User name change: 	the TXT record contains the new user name.
 * </pre>
 * Other TXT query results (such as TXT record discarded) are ignored.
 * 
 * <p> All TXT query results are forwarded to the 
 * {@link ch.iserver.ace.net.discovery.DiscoveryCallbackAdapter}.
 * </p>
 */
public class TXTQueryListener extends AbstractQueryListener {

	private static Logger LOG = Logger.getLogger(TXTQueryListener.class);
	
	/**
	 * Creates a new TXTQueryListener instance.
	 * 
	 * @param adapter	the discovery callback adapter
	 */
	public TXTQueryListener(DiscoveryCallbackAdapter adapter) {
		super(adapter);
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void processQueryResult(DNSSDService query, int flags, int ifIndex,
			String fullName, int rrtype, int rrclass, byte[] rdata, int ttl) {
		if (ttl > 0) {
			String serviceName = Bonjour.getServiceName(fullName);
			TXTRecord t = new TXTRecord(rdata);
			String userName = TXTRecordProxy.get(TXTRecordProxy.TXT_USER, t);
			adapter.userNameChanged(serviceName, userName);
		} else {
			LOG.warn("TXT record update ignored.");
		}

	}

}
