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

import ch.iserver.ace.threaddomain.internal.DefaultThreadDomainWrapper;
import ch.iserver.ace.threaddomain.internal.Invocation;
import ch.iserver.ace.threaddomain.internal.InvocationWorker;
import ch.iserver.ace.threaddomain.internal.WorkerInfo;

/**
 * @author sir
 *
 */
public class BoundedThreadDomain implements ThreadDomain {

	private String name = "";
	
	private boolean disposed;
	
	private final int max;
	
	private final WorkerInfo[] workers;

	private int position;
	
	private static final int MAX_QUEUE_SIZE = 20;
	
	public BoundedThreadDomain(int max) {
		this.max = max;
		this.position = 0;
		this.workers = new WorkerInfo[max];
	}
	
	public int getMaxQueueSize() {
		return MAX_QUEUE_SIZE;
	}
	
	/**
	 * @see ch.iserver.ace.threaddomain.ThreadDomain#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see ch.iserver.ace.threaddomain.ThreadDomain#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @see ch.iserver.ace.threaddomain.ThreadDomain#createWrapper()
	 */
	public synchronized ThreadDomainWrapper createWrapper() {
		if (disposed) {
			throw new IllegalStateException("ThreadDomain is disposed");
		}
		int current = position;
		if (workers[current] == null) {
			BlockingQueue<Invocation> sync = new SynchronousQueue<Invocation>();
			BlockingQueue<Invocation> async = new ArrayBlockingQueue<Invocation>(getMaxQueueSize());
			List<BlockingQueue<Invocation>> queues = new LinkedList<BlockingQueue<Invocation>>();
			queues.add(sync);
			queues.add(async);
			InvocationWorker worker = new InvocationWorker(queues);
			workers[current] = new WorkerInfo(worker, sync, async);
			workers[current].start();
		}
		this.position = (current + 1) % max;
		return new DefaultThreadDomainWrapper(workers[current]);
	}

	/**
	 * @see ch.iserver.ace.threaddomain.ThreadDomain#dispose()
	 */
	public synchronized void dispose() {
		disposed = true;
		for (WorkerInfo info : workers) {
			if (info != null) {
				info.stop();
			}
		}
	}

}
