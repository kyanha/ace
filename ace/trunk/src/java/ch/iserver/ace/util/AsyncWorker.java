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
 * Worker thread that services a queue of MethodInvocation objects. It takes
 * MethodInvocation objects from a BlockingQueue and calls the
 * {@link org.aopalliance.intercept.MethodInvocation#proceed()} method.
 * 
 * @see ch.iserver.ace.util.AsyncInterceptor
 */
public class AsyncWorker extends Worker implements AsyncExceptionHandler {
	
	/**
	 * The queue from which invocations are taken.
	 */
	private final BlockingQueue queue;
	
	/**
	 * The AsyncExceptionHandler used by this class. 
	 */
	private AsyncExceptionHandler handler;
	
	/**
	 * Creates a new AsyncWorker taking invocations from the given
	 * queue.
	 * 
	 * @param	 queue the queue from which invocations are taken
	 */
	public AsyncWorker(BlockingQueue queue) {
		this("async-worker", queue);
	}
	
	/**
	 * Creates a new AsyncWorker with the given name. It listens on the
	 * given queue for AsyncMethodInvocation objects, which are then
	 * executed on this worker thread.
	 * 
	 * @param name the name of the worker
	 * @param queue the queue from which invocations are taken
	 */
	public AsyncWorker(String name, BlockingQueue queue) {
		super("async-worker:" + name);
		ParameterValidator.notNull("queue", queue);
		this.queue = queue;
		this.handler = this;
		setDaemon(true);
	}
	
	/**
	 * @see ch.iserver.ace.util.Worker#kill()
	 */
	public void kill() {
		while (queue.size() > 0) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		super.kill();
	}
	
	/**
	 * Sets the exception handler for this worker.
	 * 
	 * @param handler the new exception handler
	 */
	public void setExceptionHandler(AsyncExceptionHandler handler) {
		this.handler = handler == null ? this : handler;
	}
	
	/**
	 * Extracts a method invocation from the queue and executes it.
	 * 
	 * @see ch.iserver.ace.util.Worker#doWork()
	 */
	protected void doWork() throws InterruptedException {
		AsyncMethodInvocation invocation = (AsyncMethodInvocation) queue.take();
		try {
			invocation.proceed();
		} catch (Throwable e) {
			AsyncExecutionException ex = new AsyncExecutionException(e, invocation);
			handler.handleException(ex);
		}
	}
	
	/**
	 * Default exception handler. Prints a stack trace in the console.
	 * @see ch.iserver.ace.util.AsyncExceptionHandler#handleException(ch.iserver.ace.util.AsyncExecutionException)
	 */
	public void handleException(AsyncExecutionException e) {
		e.printStackTrace();
	}
	
}
