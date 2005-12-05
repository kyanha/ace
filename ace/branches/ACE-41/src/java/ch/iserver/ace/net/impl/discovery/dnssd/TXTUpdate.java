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

import org.apache.log4j.Logger;

import com.apple.dnssd.DNSRecord;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDRegistration;

/**
 *
 */
public class TXTUpdate extends DNSSDCall {

	private Logger LOG = Logger.getLogger(TXTUpdate.class);
	
	private DNSSDRegistration registration;
	private byte[] rawTXT;
	
	public TXTUpdate(DNSSDRegistration registration, byte[] rawTXT) {
		this.registration = registration;
		this.rawTXT = rawTXT;
	}
	
	
	/**
	 * @see ch.iserver.ace.net.impl.discovery.dnssd.DNSSDCall#makeCall()
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
	
	protected Logger getLogger() {
		return LOG;
	}

}
