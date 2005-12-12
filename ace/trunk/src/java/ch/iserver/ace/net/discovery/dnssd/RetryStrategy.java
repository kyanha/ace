/*
 * $Id:RetryStrategy.java 2412 2005-12-09 13:15:29Z zbinl $
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

/**
 * The <code>RetryStrategy</code> determines how long the executing thread 
 * should wait untill it retries to execute the DNSSD call (by calling @{@link #exceptionOccurred()}. 
 * If the call fails again, the <code>RetryStrategy</code> is asked again how long the thread 
 * should wait untill the next call. 
 * If the RetryStrategy fails, it throws an exception to indicate that the thread should stop 
 * retrying the DNSSD call and abort. 
 * A retry strategy could be applied for all network related method calls, 
 * but yet it is used only for DNSSD calls. 
 * 
 * <p>A subclass implements a retry strategy, i.e. how long the executing thread waits between
 * two DNSSD API calls</p>.
 */
abstract class RetryStrategy {
	
    private static Logger LOG = Logger.getLogger(RetryStrategy.class);
	
    /**
     * Default number of times a retry should be done.
     */
	public static final int DEFAULT_NUMBER_OF_RETRIES = 3;

	/**
	 * Counter for the remaining number of retries.
	 */
    private int numberOfTriesLeft;

    /**
     * Default constructor.
     */
    public RetryStrategy() {
        this(DEFAULT_NUMBER_OF_RETRIES);
    }

    /**
     * Constructor. 
     * 
     * @param numberOfRetries the maximum number of times a retry should be done
     */
    public RetryStrategy(int numberOfRetries) {
        this.numberOfTriesLeft = numberOfRetries;
    }
	
    /**
     * Determines if another try should be taken.
     * 
     * @return true iff another try should be taken
     */
	public boolean shouldRetry() {
		return (numberOfTriesLeft > 0); 
	}
	
	/**
	 * Applies the current running thread to the retry strategy.
	 * <code>exceptionOccurred()</code> determines if a retry
	 * should be done and how long the thread is put to sleep.
	 * Then the executing thread sleeps for the determined time.
	 * If no more retries should be taken, a <code>RetryException</code>
	 * is thrown. 
	 * 
	 * @throws RetryException thrown if no more retries should be taken
	 */
	public void exceptionOccurred() throws RetryException {
		numberOfTriesLeft--;
		if (!shouldRetry()) {
			throw new RetryException();
		}
		waitUntilNextTry();
	}

	/**
	 * Determines how long the next sleep will last
	 * and puts the executing thread to sleep. 
	 */
    private void waitUntilNextTry() {
        long timeToWait = getTimeToWait();
        try {
            Thread.sleep(timeToWait);
        } catch (InterruptedException ie) {
        		LOG.warn(ie);
        }
    }
    
    /**
     * Gets the time the next sleep will last. This
     * method is implemented by the concrete retry 
     * strategy.
     * 
     * @return the time to wait 
     */
    protected abstract long getTimeToWait();
	
}
