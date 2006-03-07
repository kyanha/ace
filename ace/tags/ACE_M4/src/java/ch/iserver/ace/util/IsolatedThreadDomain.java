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

import java.lang.ref.ReferenceQueue;
import java.util.LinkedList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

/**
 * ThreadDomain that creates a new worker thread per invocation of the
 * {@link #wrap(Object, Class)} method. When an IsolatedThreadDomain
 * is created it is important to pass in a ReferenceQueue which is
 * also passed to a {@link ch.iserver.ace.util.WorkerCollector}. See
 * the following code for an example:
 * 
 * <pre>
 *  ReferenceQueue queue = new ReferenceQueue();
 *  ThreadDomain domain = new IsolatedThreadDomain(queue);
 *  WorkerCollector collector = new WorkerCollector(queue);
 *  Thread thread = new Thread(collector);
 *  thread.start();
 * </pre>
 * 
 * Of course you can schedule the execution of the collector on a
 * separate thread.
 * 
 * @see java.lang.ref.ReferenceQueue
 */
public class IsolatedThreadDomain extends AbstractThreadDomain {
	
	/**
	 * The reference queue to which references to wrapped objects
	 * are added.
	 */
	private final ReferenceQueue queue;
	
	/**
	 * The list of weak references that are kept around.
	 */
	private List references = new LinkedList();
	
	/**
	 * Creates a new IsolatedThreadDomain instance.
	 * 
	 * @param queue the reference queue
	 */
	public IsolatedThreadDomain(ReferenceQueue queue) {
		this.queue = queue;
	}
		
	/**
	 * Adds a reference to the given referent.
	 * 
	 * @param worker the worker thread
	 * @param referent the referent object
	 */
	protected void addReference(Worker worker, Object referent) {
		references.add(new AsyncReference(referent, queue, worker));
	}
	
	/**
	 * @see ch.iserver.ace.util.ThreadDomain#wrap(java.lang.Object, java.lang.Class, boolean)
	 */
	public synchronized Object wrap(Object target, Class clazz, boolean ignoreVoidMethods) {
		BlockingQueue queue = new LinkedBlockingQueue();
		Worker worker = new AsyncWorker(queue);
		worker.start();
		Object wrapped;
		if (ignoreVoidMethods) {
			wrapped = wrap(target, clazz, queue, new VoidMethodMatcherPointcut());
		} else {
			wrapped = wrap(target, clazz, queue);
		}
		addReference(worker, wrapped);
		return wrapped;
	}
	
	/**
	 * @see ch.iserver.ace.util.ThreadDomain#dispose()
	 */
	public void dispose() {
		// dispose happens through weak references
	}
	
}
