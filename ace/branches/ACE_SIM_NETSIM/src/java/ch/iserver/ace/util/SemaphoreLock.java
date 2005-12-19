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

/**
 * Non-reentrant Lock implementation based on a Semaphore. 
 */
public class SemaphoreLock implements Lock {
	
	/**
	 * The Semaphore used to implement the lock.
	 */
	private final Semaphore semaphore;

	/**
	 * The name of the lock.
	 */
	private final String name;
	
	/**
	 * The current owner of the lock.
	 */
	private Thread owner;
	
	/**
	 * Creats a SemaphoreLock with the given name.
	 * 
	 * @param name the name of the lock
	 */
	public SemaphoreLock(String name) {
		this.semaphore = new CountingSemaphore(1);
		this.name = name;
	}
	
	/**
	 * @return the name of the lock
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @see ch.iserver.ace.util.Lock#isOwner(java.lang.Thread)
	 */
	public boolean isOwner(Thread thread) {
		return thread == owner;
	}
	
	/**
	 * @see ch.iserver.ace.util.Lock#lock()
	 */
	public void lock() throws InterruptedRuntimeException {
		try {
			semaphore.acquire();
			owner = Thread.currentThread();
		} catch (InterruptedException e) {
			throw new InterruptedRuntimeException(e);
		}
	}

	/**
	 * @see ch.iserver.ace.util.Lock#unlock()
	 */
	public synchronized void unlock() {
		if (Thread.currentThread() != owner) {
			throw new IllegalMonitorStateException("thread is not owner of lock");
		}
		semaphore.release();
		owner = null;
	}

}
