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
 *
 */
public class LinkedBlockingQueue implements BlockingQueue {
	
	private final LinkedList queue;
	
	public LinkedBlockingQueue() {
		this.queue = new LinkedList();
	}
	
	/**
	 * @see ch.iserver.ace.util.BlockingQueue#add(java.lang.Object)
	 */
	public synchronized void add(Object o) {
		queue.addLast(o);
		notify();
	}

	/**
	 * @see ch.iserver.ace.util.BlockingQueue#get()
	 */
	public synchronized Object get() throws InterruptedException {
		while (queue.isEmpty()) {
			wait();
		}
		return queue.removeFirst();
	}

	/**
	 * @see ch.iserver.ace.util.BlockingQueue#size()
	 */
	public synchronized int size() {
		return queue.size();
	}

	/**
	 * @see ch.iserver.ace.util.BlockingQueue#isEmpty()
	 */
	public synchronized boolean isEmpty() {
		return queue.isEmpty();
	}

}
