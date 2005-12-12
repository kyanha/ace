/*
 * $Id:RetryException.java 2412 2005-12-09 13:15:29Z zbinl $
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

/**
 * Exception thrown if a retry strategy failed.
 * 
 * @see ch.iserver.ace.net.discovery.dnssd.RetryStrategy
 */
public class RetryException extends Exception {

	/**
	 * Default Constructor.
	 */
	public RetryException() { }
	
	/**
	 * Constructor with an error message.
	 * 
	 * @param message an error message
	 */
	public RetryException(String message) {
		super(message);
	}
}
