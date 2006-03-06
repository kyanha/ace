/*
 * $Id:QueryRecord.java 1205 2005-11-14 07:57:10Z zbinl $
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
import com.apple.dnssd.QueryListener;

/**
 * <code>DNSSDCall</code> implementation for a DNSSD query record call. This call
 * queries for an arbitrary DNS record, e.g. IP address or TXT record.
 * 
 * @see ch.iserver.ace.net.discovery.dnssd.DNSSDCall
 */
public class QueryRecord extends DNSSDCall {

	private Logger LOG = Logger.getLogger(QueryRecord.class);
	
	/**
	 * The interface type
	 */
	private int ifIndex;
	
	/**
	 * The resource record type
	 */
	private int rrtype;
	
	/**
	 * The host name
	 */
	private String hostName;
	
	/**
	 * The query listener
	 */
	private QueryListener listener;
	
	/**
	 * Creates a new QueryRecord. 
	 * 
	 * @param ifIndex	the interface index 
	 * @param hostName	the host name
	 * @param rrtype		The numerical type of the resource record to be queried for (e.g. PTR, SRV, etc)  as defined in nameser.h.
	 * @param listener	This object will get called when the query completes.
	 */
	public QueryRecord(int ifIndex, String hostName, int rrtype, QueryListener listener) {
		this.ifIndex = ifIndex;
		this.hostName = hostName;
		this.rrtype = rrtype;
		this.listener = listener;
	}
	
	/**
	 * @see ch.iserver.ace.net.discovery.dnssd.DNSSDCall#makeCall()
	 */
	protected Object makeCall() throws DNSSDCallException {
		try {
			return DNSSD.queryRecord(0, ifIndex, hostName, rrtype, 1 /* ns_c_in */, listener);
		} catch (DNSSDException de) {
			throw new DNSSDCallException(de);
		}
	}
	
	protected Logger getLogger() {
		return LOG;
	}

}
