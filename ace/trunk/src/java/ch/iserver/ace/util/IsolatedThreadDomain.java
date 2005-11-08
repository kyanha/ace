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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

/**
 * ThreadDomain that creates a new worker thread per invocation of the
 * {@link #wrap(Object, Class)} method.
 */
public class IsolatedThreadDomain extends AbstractThreadDomain implements Runnable {
	
	private Map workers = new HashMap();
	
	private ReferenceQueue queue;
	
	private int id;
	
	private List references = new LinkedList();
	
	public IsolatedThreadDomain(ScheduledExecutorService ses) {
		queue = new ReferenceQueue();
		ses.scheduleWithFixedDelay(this, 1, 1, TimeUnit.SECONDS);
	}
	
	protected Worker getWorker(int id) {
		return (Worker) workers.get(new Integer(id));
	}
	
	protected void addWorker(int id, Worker worker) {
		workers.put(new Integer(id), worker);
	}
	
	protected void addReference(int id, Object referent) {
		references.add(new AsyncReference(referent, queue, id));
	}
	
	/**
	 * @see ch.iserver.ace.util.ThreadDomain#wrap(java.lang.Object, java.lang.Class)
	 */
	public synchronized Object wrap(Object target, Class clazz) {
		BlockingQueue queue = new LinkedBlockingQueue();
		Worker worker = new AsyncWorker(queue);
		worker.start();
		int currentId = id++;
		addWorker(currentId, worker);
		Object wrapped = wrap(target, clazz, queue);
		addReference(currentId, wrapped);
		return wrapped;
	}
	
	public void run() {
		AsyncReference ref = (AsyncReference) queue.poll();
		while (ref != null) {
			System.out.println("... found worker to collect: " + ref.getId());
			Worker worker = getWorker(ref.getId());
			worker.kill();
			ref = (AsyncReference) queue.poll();
		}
	}

}
