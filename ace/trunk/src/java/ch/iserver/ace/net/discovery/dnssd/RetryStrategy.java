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

package ch.iserver.ace.net.discovery.dnssd;

import org.apache.log4j.Logger;

/**
 *
 */
abstract class RetryStrategy {
    private static Logger LOG = Logger.getLogger(RetryStrategy.class);
	
	public static final int DEFAULT_NUMBER_OF_RETRIES = 3;

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
     * @param numberOfRetries
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
	 * 
	 * @throws RetryException
	 */
	public void exceptionOccurred() throws RetryException {
		numberOfTriesLeft--;
		if (!shouldRetry()) {
			throw new RetryException();
		}
		waitUntilNextTry();
	}

	/**
	 * 
	 *
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
     * 
     * @return
     */
    protected abstract long getTimeToWait();
	
}
