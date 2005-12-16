/*
 * $Id:RetryStrategyFactory.java 2412 2005-12-09 13:15:29Z zbinl $
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

import ch.iserver.ace.net.core.NetworkProperties;

/**
 * Factory to create instances of a <code>RetryStrategy</code> implementation.
 * 
 * @see ch.iserver.ace.net.discovery.dnssd.RetryStrategy 
 */
public class RetryStrategyFactory {

	/**
	 * Creates a new instance of type {@link RetryStrategy}.
	 * 
	 * @return a retry strategy
	 */
	public static RetryStrategy create() {
		return new AdditiveWaitRetryStrategy(
				Integer.parseInt(NetworkProperties.get(NetworkProperties.KEY_NUMBER_OF_RETRIES)),
				Long.parseLong(NetworkProperties.get(NetworkProperties.KEY_INITIAL_WAITINGTIME)), 
				Long.parseLong(NetworkProperties.get(NetworkProperties.KEY_SUBSEQUENT_WAITINGTIME)));
	}
}
