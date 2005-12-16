/*
 * $Id:DNSSDCallException.java 2412 2005-12-09 13:15:29Z zbinl $
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

import com.apple.dnssd.DNSSDException;


/**
 * Application exception. It wraps a DNSSD specific exception <code>DNSSDException</code>.
 * This exception is thrown if a DNSSD call failed.
 * 
 * @see com.apple.dnssd.DNSSDException
 */
public class DNSSDCallException extends Exception {

	/**
	 * The actual exception (wrapped by this exception).
	 */
	private DNSSDException exception;
	
	/**
	 * Creates a new DNSSDCallException given the original cause.
	 * 
	 * @param exception the exception that caused the creation
	 */
	public DNSSDCallException(DNSSDException exception) {
		this.exception = exception;
	}
	
	/**
	 * Creates a new DNSSDCallException object with a 
	 * message.
	 * 
	 * @param message the message for this exception
	 */
	public DNSSDCallException(String message) {
		super(message);
	}
	
	/**
	 * Default Constructor.
	 */
	public DNSSDCallException() {}
	
	/**
	 * Gets the DNSSDException that this object wraps.
	 * 
	 * @return DNSSDException the cause
	 */
	public DNSSDException getDNSSDException() {
		return exception; 
	}
	
}
