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
 * SingleThreadDomain creates one single worker thread and associated queue
 * that is used by all proxies returned from {@link #wrap(Object, Class)}
 * invocations.
 */
public class SingleThreadDomain extends AbstractThreadDomain {
	
	/**
	 * The queue used for the single worker.
	 */
	private final BlockingQueue queue;
	
	/**
	 * The single worker thread.
	 */
	private AsyncWorker worker;
	
	/**
	 * Exception handler for the async workers.
	 */
	private final AsyncExceptionHandler handler;
	
	/**
	 * Creates a new SingleThreadDomain object.
	 */
	public SingleThreadDomain() {
		this(null);
	}
	
	/**
	 * Creates a new SingleThreadDomain object.
	 */
	public SingleThreadDomain(AsyncExceptionHandler handler) {
		this.queue = new LinkedBlockingQueue();
		this.handler = handler;
	}
	
	/**
	 * @see ch.iserver.ace.util.ThreadDomain#wrap(java.lang.Object, java.lang.Class)
	 */
	public Object wrap(Object target, Class clazz) {
		if (worker == null) {
			this.worker = new AsyncWorker(queue);
			this.worker.setExceptionHandler(handler);
		}
		Advice advice = new LoggingInterceptor(SingleThreadDomain.class);
		return wrap(target, clazz, queue, advice);
	}

}
