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

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;

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
	 * 
	 */
	private final AsyncQueueManager[] managers;
		
	/**
	 * The workers used by this thread domain.
	 */
	private final Worker[] workers;
	
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
		ParameterValidator.notNegative("maxWorkers", maxWorkers);
		this.maxWorkers = maxWorkers;
		this.managers = new AsyncQueueManager[maxWorkers];
		this.workers = new Worker[maxWorkers];
	}
	
	/**
	 * @see ch.iserver.ace.util.ThreadDomain#wrap(java.lang.Object, java.lang.Class, boolean)
	 */
	public synchronized Object wrap(Object target, Class clazz, boolean ignoreVoidMethods) {
		if (managers[index] == null) {
			managers[index] = new AsyncQueueManager();
			workers[index] = new AsyncWorker(managers[index].getTargetQueue());
			workers[index].start();
		}
		
		BlockingQueue queue = managers[index].getQueue();
		index = (index + 1) % maxWorkers;
		
		if (ignoreVoidMethods) {
			return wrap(target, clazz, queue, new VoidMethodMatcherPointcut());
		} else {
			return wrap(target, clazz, queue);
		}
	}
	
	/**
	 * @see ch.iserver.ace.util.ThreadDomain#dispose()
	 */
	public void dispose() {
		for (int i = 0; i < workers.length; i++) {
			AsyncQueueManager manager = managers[i];
			if (manager != null) {
				manager.close();
			}
			Worker worker = (Worker) workers[i];
			if (worker != null) {
				worker.kill();
			}
		}
	}
	
}
