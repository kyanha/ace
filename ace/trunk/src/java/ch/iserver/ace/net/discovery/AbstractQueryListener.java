/*
 * $Id:AbstractQueryListener.java 1205 2005-11-14 07:57:10Z zbinl $
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

import ch.iserver.ace.util.ParameterValidator;

import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.QueryListener;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

/**
 * AbstractQueryListener gathers functionality used by all DNSSD query listeners.
 * A query can include the IP address of a specific service or the TXT record content
 * of a service. The query listeners receive the response events to these query record calls. 
 * 
 * @see com.apple.dnssd.QueryListener
 */
public abstract class AbstractQueryListener extends BaseListenerImpl implements QueryListener {
	
	/**
	 * A queue to match results of received events to the corresponding services and users, respectively.
	 */
	private BlockingQueue serviceQueue;
	
	/**
	 * Creates a new AbstractQueryListener instance. 
	 * 
	 * @param adapter	the discovery callback adapter
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

		processQueryResult(query, flags, ifIndex, fullName, rrtype, rrclass, rdata, ttl);

	}
	
	/**
	 * Adds a service name to this listener. Events and service names
	 * are matched according to the FIFO principle.
	 * 
	 * @param servicename		the service name to be added
	 */
	public void addNextService(String servicename) {
		ParameterValidator.notNull("servicename", servicename);
		serviceQueue.add(servicename);
	}
	
	/**
	 * Gets the next service name.
	 * Note: this method is blocking if no service name is currently available.
	 * 
	 * @return	the next service name
	 */
	protected String getNextService() {
		String result = null;
		try {
			result = (String)serviceQueue.take();
		} catch (InterruptedException ie) {
			
		}
		return result;
	}
	
	/**
	 * This method is called when a query result was received. To be implemented by
	 * concrete query listeners.
	 * 
	 * @param query			The active query object.
	 * @param flags			Possible values are DNSSD.MORE_COMING.
	 * @param ifIndex		The interface on which the query was resolved.
	 * @param fullName		The resource record's full domain name.
	 * @param rrtype			The resource record's type (e.g. PTR, SRV, etc) as defined by RFC 1035 and its updates.
	 * @param rrclass		The class of the resource record, as defined by RFC 1035 and its updates.
	 * @param rdata			The raw rdata of the resource record.
	 * @param ttl			The resource record's time to live, in seconds
	 */
	protected abstract void processQueryResult(DNSSDService query, int flags, int ifIndex,
			String fullName, int rrtype, int rrclass, byte[] rdata, int ttl);

}
