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

package ch.iserver.ace.net.impl.discovery;

import org.apache.log4j.Logger;

import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.TXTRecord;

/**
 *
 */
public class TXTQueryListener extends AbstractQueryListener {

	private static Logger LOG = Logger.getLogger(TXTQueryListener.class);
	
	public TXTQueryListener(DiscoveryCallbackAdapter adapter) {
		super(adapter);
	}
	
	/**
	 * @inheritDoc
	 */
	protected void processQuery(DNSSDService query, int flags, int ifIndex,
			String fullName, int rrtype, int rrclass, byte[] rdata, int ttl) {
		if (ttl > 0) {
			String serviceName = Bonjour.getServiceName(fullName);
			if (adapter.isServiceKnown(serviceName)) {
				TXTRecord t = new TXTRecord(rdata);
				String userName = TXTRecordProxy.get(TXTRecordProxy.TXT_USER, t);
				adapter.userNameChanged(serviceName, userName);
			} else {
				LOG.warn("TXT record received for unknown user ["+serviceName+"]");
			}
		} else {
			LOG.warn("TXT record update ignored.");
		}

	}

}
