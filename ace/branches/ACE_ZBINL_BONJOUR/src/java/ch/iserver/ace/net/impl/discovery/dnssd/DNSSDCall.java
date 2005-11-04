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



/**
 *
 */
abstract class DNSSDCall {
	
	/**
	 * Executes the DNSSD call. In case of failure, a {@link RetryStrategy}
	 * determines how many times the call should be retried.
	 * 
	 * @return the value returned by the DNSSD call
	 * @throws DNSSDUnavailable if the call failed
	 * @see RetryStrategy
	 */
	public Object execute() throws DNSSDUnavailable {
		RetryStrategy strategy = getRetryStrategy();
		
		while (strategy.shouldRetry()) {
			try {
				return makeCall();
			} catch (DNSSDCallException dex) {
				try {
					strategy.exceptionOccurred();
				} catch (RetryException re) {
					throw new DNSSDUnavailable("repeated attempts to communicate with DNNSD failed.");
				}
			}	
		}
		return null;
	}
	
	/**
	 * Implements the concrete DNNSD call.
	 * 
	 * @return the value returned by the DNSSD call
	 * @throws DNSSDCallException
	 */
	protected abstract Object makeCall() throws DNSSDCallException;
	
	/**
	 * Gets the {@link RetryStrategy}.
	 * @return the retry strategy
	 */
	private RetryStrategy getRetryStrategy() {
		return RetryStrategyFactory.create();
	}

}
