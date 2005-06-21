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

import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * A queue class that contains objects.
 * Use the {@link #add(Object)} method to add <code>obj</code> and the 
 * {@link #get()} method to get <code>obj</code>. The {@link #get()} method
 * blocks until there is an object in the queue. There is also a version
 * {@link #get(int)} that blocks only for the specified amount of time.
 * <code>get(0)</code> is effectively non-blocking.
 * <p>The object queue is completely thread safe, all the important blocks
 * are synchronized.</p>
 */
public class SynchronizedQueue {
	
	private static Logger LOG = Logger.getLogger(SynchronizedQueue.class);
	
	/**
	 * the list used to implement the queue.
	 */
	private LinkedList queue;
	
	/**
	 * The object to notify when the queue turns nonempty.
	 */
	private Object synchObj;
	
	/**
	 * Creates a new empty object queue.
	 * @param snychObj the object to notify when this queue turns nonempty.
	 */
	public SynchronizedQueue(Object synchObj) {
		this.queue = new LinkedList();
		this.synchObj = synchObj;
	}
	
	/**
	 * Creates a new empty object queue.
	 */
	public SynchronizedQueue() {
		this.queue = new LinkedList();
	}

	/**
	 * Adds obj to the end of this obj queue.
	 * 
	 * @param obj the obj to add
	 */
	public synchronized void add(Object obj) {
		queue.addLast(obj);
		if (synchObj != null) {
//			LOG.info("get lock");
			synchronized(synchObj) {
//				LOG.info("calling notify()");
				synchObj.notify();
			}
		}
		notify();
	}
	
	/**
	 * Removes the object from the beginning of this queue if there is
	 * an object or blocks otherwise until there is an object in the queue.
	 * 
	 * @return Object an object from the beginning of the queue
	 * @throws InterruptedException
	 * @see #getObject(int)
	 */
	public synchronized Object get() throws InterruptedException {
		while (queue.isEmpty()) {
//			LOG.info("going to wait...");
			wait();
		}
		return (Object) queue.removeFirst();
	}
	
	/**
	 * Removes obj from the beginning of this queue if there is
	 * obj or blocks for the specified amount <var>millis</var> of
	 * milliseconds. Calling this method with a value 0 is basically
	 * an atomic command for:
	 * 
	 * <pre>
	 *   SynchronizedQueue queue = ...;
	 *   Object obj = null;
	 *   if (!queue.isEmpty()) {
	 *     obj = queue.removeFirst();
	 *   }
	 * </pre>
	 * 
	 * If there is no obj in the queue within the specified amount of time,
	 * this method returns 0.
	 * 
	 * @param millis the amount of milliseconds to wait
	 * @return a obj object or null if there is no obj within the specified
	 *         amount of time in the queue
	 * @throws InterruptedException
	 */
	public synchronized Object get(int millis) throws InterruptedException {
		if (queue.isEmpty() && millis > 0) {
			wait(millis);
		}
		return queue.isEmpty() ? null : (Object) queue.removeFirst();
	}
	
	/**
	 * @return the size of the obj queue
	 */
	public synchronized int size() {
		return queue.size();
	}
	
	/**
	 * @return true iff this queue is empty
	 */
	public synchronized boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public String toString() {
		return "SynchronizedQueue(" + queue + ",'" + synchObj + "')";
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj.getClass().equals(getClass())) {
			SynchronizedQueue q = (SynchronizedQueue) obj;
			return q.queue.equals(queue) && 
				(q.synchObj == null ? synchObj == null : q.synchObj.equals(synchObj));
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		int hashcode = 17;
		hashcode = 37 * hashcode + queue.hashCode();
		hashcode = 37 * hashcode + ((synchObj != null) ? synchObj.hashCode() : 0);
		return hashcode;
	}
}
