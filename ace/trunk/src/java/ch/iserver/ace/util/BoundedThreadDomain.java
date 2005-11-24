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

package ch.iserver.ace.util;

import org.aopalliance.aop.Advice;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

/**
 * BoundedThreadDomain creates only a certain number of worker threads and
 * associated queues. The queues are used in a round-robin style by the
 * proxies returned from {@link #wrap(Object, Class)}.
 * 
 * <p>Note that if the wrap method is called less than maxWorkers times,
 * not all the worker threads are created.</p>
 */
public class BoundedThreadDomain extends AbstractThreadDomain {
	
	/**
	 * The maximum number of worker threads to create.
	 */
	private final int maxWorkers;
	
	/**
	 * The blocking queues used by the maxWorkers worker threads.
	 */
	private final BlockingQueue[] queues;
	
	/**
	 * The current index.
	 */
	private int index;
	
	/**
	 * Creates a new BoundedThreadDomain using the specified number of maximum
	 * workers.
	 * 
	 * @param maxWorkers the maximum number of workers
	 */
	public BoundedThreadDomain(int maxWorkers) {
		this.maxWorkers = maxWorkers;
		this.queues = new BlockingQueue[maxWorkers];
	}
	
	/**
	 * @see ch.iserver.ace.util.ThreadDomain#wrap(java.lang.Object, java.lang.Class)
	 */
	public synchronized Object wrap(Object target, Class clazz) {
		if (queues[index] == null) {
			queues[index] = new LinkedBlockingQueue();
			Worker worker = new AsyncWorker(queues[index]);
			worker.start();
		}
		
		BlockingQueue queue = queues[index];		
		index = (index + 1) % maxWorkers;
		
		return wrap(target, clazz, queue, new Advice[0], true);
	}
	
}
