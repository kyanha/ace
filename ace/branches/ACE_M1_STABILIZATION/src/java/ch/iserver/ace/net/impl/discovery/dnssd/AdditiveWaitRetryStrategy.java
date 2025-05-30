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
public class AdditiveWaitRetryStrategy extends RetryStrategy {

	public static final String KEY_INITIAL_WAITINGTIME = "initial.waitingtime";
	public static final String KEY_SUBSEQUENT_WAITINGTIME = "subsequent.waitingtime";
	public static final String KEY_NUMBER_OF_RETRIES = "number.retries";
	
	public static final long STARTING_WAIT_TIME = 3000;
	public static final long WAIT_TIME_INCREMENT = 4000;

	private long currentTimeToWait;

	private long waitTimeIncrement;

	/**
	 * Default constructor. 
	 */
	public AdditiveWaitRetryStrategy() {
		this(DEFAULT_NUMBER_OF_RETRIES, STARTING_WAIT_TIME, WAIT_TIME_INCREMENT);
	}

	/**
	 * 
	 * @param numberOfRetries
	 * @param startingWaitTime
	 * @param waitTimeIncrement
	 */
	public AdditiveWaitRetryStrategy(int numberOfRetries,
			long startingWaitTime, long waitTimeIncrement) {
		super(numberOfRetries);
		this.currentTimeToWait = startingWaitTime;
		this.waitTimeIncrement = waitTimeIncrement;
	}

	/**
	 * @see ch.iserver.ace.net.impl.discovery.dnssd.RetryStrategy#getTimeToWait()
	 */
	protected long getTimeToWait() {
		long returnValue = currentTimeToWait;
		currentTimeToWait += waitTimeIncrement;
		return returnValue;
	}
}
