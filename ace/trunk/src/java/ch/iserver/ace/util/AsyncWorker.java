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

import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;

/**
 * Worker thread that services a queue of MethodInvocation objects. It takes
 * MethodInvocation objects from a BlockingQueue and calls the
 * {@link org.aopalliance.intercept.MethodInvocation#proceed()} method.
 * 
 * @see ch.iserver.ace.util.AsyncInterceptor
 */
public class AsyncWorker extends Worker implements AsyncExceptionHandler {
	
	private static final Logger LOG = Logger.getLogger(AsyncWorker.class);
	
	private final BlockingQueue queue;
	
	private AsyncExceptionHandler handler;
	
	public AsyncWorker(BlockingQueue queue) {
		super("async-worker");
		ParameterValidator.notNull("queue", queue);
		this.queue = queue;
		this.handler = this;
		setDaemon(true);
	}
	
	public void setExceptionHandler(AsyncExceptionHandler handler) {
		this.handler = handler;
	}
	
	protected void doWork() throws InterruptedException {
		MethodInvocation invocation = (MethodInvocation) queue.take();
		try {
			invocation.proceed();
		} catch (Throwable e) {
			handler.handleException(e);
		}
	}
	
	public void handleException(Throwable th) {
		LOG.warn(th);
	}
	
}
