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

package ch.iserver.ace.net.impl.discovery.dnssd;

import ch.iserver.ace.net.impl.discovery.AbstractQueryListener;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;

/**
 *
 */
public class QueryRecord extends DNSSDCall {

	private int ifIndex, rrtype;
	private String hostName;
	private AbstractQueryListener listener;
	
	/**
	 * Constructor. 
	 * 
	 * @param ifIndex
	 * @param hostName
	 * @param rrtype
	 * @param listener
	 */
	public QueryRecord(int ifIndex, String hostName, int rrtype, AbstractQueryListener listener) {
		this.ifIndex = ifIndex;
		this.hostName = hostName;
		this.rrtype = rrtype;
		this.listener = listener;
	}
	
	/**
	 * @see ch.iserver.ace.net.impl.discovery.dnssd.DNSSDCall#makeCall()
	 */
	protected Object makeCall() throws DNSSDCallException {
		try {
			return DNSSD.queryRecord(0, ifIndex, hostName, rrtype, 1 /* ns_c_in */, listener);
		} catch (DNSSDException de) {
			throw new DNSSDCallException(de);
		}
	}

}
