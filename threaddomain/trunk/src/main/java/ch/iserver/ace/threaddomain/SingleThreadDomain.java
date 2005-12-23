/*
 * $Id$
 *
 * threaddomain
 * Copyright (C) 2005 Simon Raess
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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.aopalliance.intercept.MethodInterceptor;

/**
 *
 */
public class SingleThreadDomain implements ThreadDomain {
	
	private InvocationWorker worker;
	
	private BlockingQueue<Invocation> syncQueue = new SynchronousQueue<Invocation>();
	
	private BlockingQueue<Invocation> asyncQueue;
	
	private List<BlockingQueue<Invocation>> queues;
	
	public SingleThreadDomain(int queueSize) {
		 asyncQueue = new ArrayBlockingQueue<Invocation>(queueSize);
		 queues = new LinkedList<BlockingQueue<Invocation>>();
		 queues.add(syncQueue);
		 queues.add(asyncQueue);
	}
	
	public void dispose() {
		worker.stop();
	}
	
	/**
	 * @see ch.iserver.ace.threaddomain.ThreadDomain#createWrapper()
	 */
	public ThreadDomainWrapper createWrapper() {
		if (worker == null) {
			worker = new InvocationWorker(queues);
			worker.start();
		}
		return new DefaultThreadDomainWrapper(new InterceptorFactory() {
			public MethodInterceptor createInterceptor(boolean sync) {
				if (sync) {
					return new SyncInterceptor(syncQueue);
				} else {
					return new AsyncInterceptor(asyncQueue);
				}
			}
		});
	}

}
