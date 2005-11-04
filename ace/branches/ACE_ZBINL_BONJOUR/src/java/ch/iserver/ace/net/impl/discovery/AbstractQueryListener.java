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

import ch.iserver.ace.util.BlockingQueue;
import ch.iserver.ace.util.LinkedBlockingQueue;
import ch.iserver.ace.util.ParameterValidator;

import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.QueryListener;

/**
 *
 */
abstract class AbstractQueryListener extends BaseListenerImpl implements QueryListener {
	
	private BlockingQueue serviceQueue;
	
	/**
	 * 
	 * @param adapter
	 */
	public AbstractQueryListener(DiscoveryCallbackAdapter adapter) {
		super(adapter);
		this.serviceQueue = new LinkedBlockingQueue();
	}
	
	/**
	 * @see com.apple.dnssd.QueryListener#queryAnswered(com.apple.dnssd.DNSSDService, int, int, java.lang.String, int, int, byte[], int)
	 */
	public void queryAnswered(DNSSDService query, int flags, int ifIndex,
			String fullName, int rrtype, int rrclass, byte[] rdata, int ttl) {

		processQuery(query, flags, ifIndex, fullName, rrtype, rrclass, rdata, ttl);

	}
	
	/**
	 * 
	 * @param servicename
	 */
	public void addNextService(String servicename) {
		ParameterValidator.notNull("servicename", servicename);
		serviceQueue.add(servicename);
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getNextService() {
		String result = null;
		try {
			result = (String)serviceQueue.get();
		} catch (InterruptedException ie) {
			
		}
		return result;
	}
	
	/**
	 * 
	 * @param query
	 * @param flags
	 * @param ifIndex
	 * @param fullName
	 * @param rrtype
	 * @param rrclass
	 * @param rdata
	 * @param ttl
	 */
	protected abstract void processQuery(DNSSDService query, int flags, int ifIndex,
			String fullName, int rrtype, int rrclass, byte[] rdata, int ttl);

}
