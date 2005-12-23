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

package ch.iserver.ace.threaddomain;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

/**
 * Manager class for a blocking queue and another reference to the same
 * queue which has a CloseableAdvice applied to it.
 */
class AsyncQueueManager {
	
	/**
	 * The target queue.
	 */
	private final BlockingQueue targetQueue;
	
	/**
	 * The queue with a CloseableAdvice.
	 */
	private final BlockingQueue queue;
	
	/**
	 * The CloseableAdvice for the queue reference.
	 */
	private final CloseableAdvice closeable;
	
	/**
	 * Creates a new AsyncQueueManager object. 
	 */
	AsyncQueueManager() {
		this.targetQueue = new LinkedBlockingQueue();
		this.closeable = new CloseableAdvice();
		this.queue = (BlockingQueue) AopUtil.wrap(targetQueue, BlockingQueue.class, closeable);
	}
	
	/**
	 * Closes the ClosableAdvice.
	 */
	public void close() {
		closeable.close();
	}
	
	/**
	 * Gets the advised queue instance.
	 * 
	 * @return the advised queue instance
	 */
	public BlockingQueue getQueue() {
		return queue;
	}
	
	/**
	 * Gets the target queue.
	 * 
	 * @return the target queue
	 */
	public BlockingQueue getTargetQueue() {
		return targetQueue;
	}
	
}
