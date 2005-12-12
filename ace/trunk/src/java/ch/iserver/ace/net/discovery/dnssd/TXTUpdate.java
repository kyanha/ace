/*
 * $Id:TXTUpdate.java 2412 2005-12-09 13:15:29Z zbinl $
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

import com.apple.dnssd.DNSRecord;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDRegistration;

/**
 * <code>DNSSDCall</code> implementation for a DNSSD TXT update call. This call updates the
 * TXT record contents for the local user. Other users browsing in the local area network will
 * receive a notification of the updated TXT record. 
 * The <code>TXTUpdate</code> call is currently used for user name changes only.
 * 
 * <p>Note: executing <code>TXTUpdate</code> many times may result in inconsistent TXT record
 * contents at the peer users because the mDNSResponder service uses a cache which will not update
 * very frequently and the service also implements a DOS like security-algorithm for TXT update flooding.</p>
 * 
 * @see ch.iserver.ace.net.discovery.dnssd.DNSSDCall
 */
public class TXTUpdate extends DNSSDCall {

	private Logger LOG = Logger.getLogger(TXTUpdate.class);
	
	/**
	 * The DNSSDRegistration reference
	 */
	private DNSSDRegistration registration;
	
	/**
	 * The raw TXT record data
	 */
	private byte[] rawTXT;
	
	/**
	 * Creates a new TXTUpdate DNSSD call.
	 * 
	 * @param registration	the registration object to get the TXT record reference from
	 * @param rawTXT			the raw TXT record data 
	 */
	public TXTUpdate(DNSSDRegistration registration, byte[] rawTXT) {
		this.registration = registration;
		this.rawTXT = rawTXT;
	}
	
	
	/**
	 * @see ch.iserver.ace.net.discovery.dnssd.DNSSDCall#makeCall()
	 */
	protected Object makeCall() throws DNSSDCallException {
		try {
			DNSRecord record = registration.getTXTRecord();
			record.update(0, rawTXT, 0);
			return null;
		} catch (DNSSDException de) {
			throw new DNSSDCallException(de);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected Logger getLogger() {
		return LOG;
	}

}
