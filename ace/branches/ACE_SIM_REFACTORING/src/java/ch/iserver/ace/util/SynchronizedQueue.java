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

/**
 * A queue class that contains objects. Use the {@link #add(Object)} method to
 * add <code>obj</code> and the {@link #get()} method to get <code>obj</code>.
 * The {@link #get()} method blocks until there is an object in the queue. There
 * is also a version {@link #get(int)} that blocks only for the specified amount
 * of time. <code>get(0)</code> is effectively non-blocking.
 * <p>
 * The object queue is completely thread safe, all the important blocks are
 * synchronized.
 * </p>
 */
public class SynchronizedQueue {

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
	 * 
	 * @param synchObj
	 *            the object to notify when this queue turns nonempty.
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
	 * @param obj
	 *            the obj to add
	 */
	public synchronized void add(Object obj) {
		queue.addLast(obj);
		if (synchObj != null) {
			// LOG.info("get lock");
			synchronized (synchObj) {
				// LOG.info("calling notify()");
				synchObj.notify();
			}
		}
		notify();
	}

	/**
	 * Removes the object from the beginning of this queue if there is an object
	 * or blocks otherwise until there is an object in the queue.
	 * 
	 * @return Object an object from the beginning of the queue
	 * @throws InterruptedException in case of an interrupt
	 * @see #get(int)
	 */
	public synchronized Object get() throws InterruptedException {
		while (queue.isEmpty()) {
			wait();
		}
		return (Object) queue.removeFirst();
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

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "SynchronizedQueue(" + queue + ",'" + synchObj + "')";
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj.getClass().equals(getClass())) {
			SynchronizedQueue q = (SynchronizedQueue) obj;
			return q.queue.equals(queue)
					&& (q.synchObj == null ? synchObj == null : q.synchObj
							.equals(synchObj));
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		int hashcode = 17;
		hashcode = 37 * hashcode + queue.hashCode();
		hashcode = 37 * hashcode
				+ ((synchObj != null) ? synchObj.hashCode() : 0);
		return hashcode;
	}
	
}
