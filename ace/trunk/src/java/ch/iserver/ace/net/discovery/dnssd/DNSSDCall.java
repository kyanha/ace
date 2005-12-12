/*
 * $Id:DNSSDCall.java 1205 2005-11-14 07:57:10Z zbinl $
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

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.net.core.NetworkServiceImpl;



/**
 * <code>DNSSDCall</code> is an abstract base class for all DNSSD API calls. 
 * A subclass defines which API call is executed. <code>DNSSDCall</code> 
 * incorporates advanced <code>Exception</code> and <code>Error</code> handling. 
 * That is, in case of failure, it uses a <code>RetryStrategy</code> 
 * to determine how many times and in what intervals a call is repeated.
 * If Bonjour is not installed on the local host an error is thrown from the 
 * DNSSD API call. <code>DNSSDCall</code> notifies the <code>NetworkServiceExt</code>
 * of that runtime-unrecoverable error.
 *
 * @see ch.iserver.ace.net.discovery.dnssd.RetryStrategy
 */
abstract class DNSSDCall {
	
	/**
	 * Flag to indicate that DNSSD is not available.
	 */
	private boolean dnssdNotAvailable = false;
	
	/**
	 * Executes the DNSSD call. In case of failure, a {@link RetryStrategy}
	 * determines how many times the call should be retried. If an <code>Error</code>
	 * is thrown, {@link ch.iserver.ace.net.core.NetworkServiceExt#serviceFailure()}
	 * is called.
	 * 
	 * @return Object	the value returned by the DNSSD call
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
					throw new DNSSDUnavailable("repeated attempts to communicate with DNSSD failed.");
				}
			} catch (Error error) {
				if (!dnssdNotAvailable) {
					dnssdNotAvailable = true;
					getLogger().fatal("caught error, DNSSD not installed [" + error + "]");
					NetworkServiceImpl.getInstance().getCallback().
							serviceFailure(FailureCodes.DNSSD_NOT_AVAILABLE, "DNSSD not installed", null);
				}
			}
		}
		return null;
	}
	
	/**
	 * Implements the concrete DNSSD call.
	 * 
	 * @return Object	the value returned by the DNSSD call
	 * @throws DNSSDCallException if the call fails
	 */
	protected abstract Object makeCall() throws DNSSDCallException;
	
	/**
	 * Gets the logger of the concrete subclass.
	 * 
	 * @return the logger to use
	 */
	protected abstract Logger getLogger();
	
	/**
	 * Gets the {@link RetryStrategy}.
	 * 
	 * @return the retry strategy
	 */
	private RetryStrategy getRetryStrategy() {
		return RetryStrategyFactory.create();
	}

}
