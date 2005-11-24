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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;

/**
 * AsyncInterceptor is a special method interceptor that adds the intercepted
 * MethodInvocation objects to a BlockingQueue and then returns immediately.
 * Together with a worker thread that reads and executes invocations from
 * the same queue this can be used to execute methods asynchronously.
 * 
 * @see ch.iserver.ace.util.AsyncWorker
 */
public class AsyncInterceptor implements MethodInterceptor {

	/**
	 * The logger for instances of this class.
	 */
	private static final Logger LOG = Logger.getLogger(AsyncInterceptor.class);
	
	/**
	 * The queue used to store MethodInvocation objects.
	 */
	private final BlockingQueue queue;
	
	/**
	 * Creates a new AsyncInterceptor that adds MethodInvocations to the given
	 * <var>queue</var>.
	 * 
	 * @param queue the queue used to store method invocation objects
	 */
	public AsyncInterceptor(BlockingQueue queue) {
		ParameterValidator.notNull("queue", queue);
		this.queue = queue;
	}
	
	/**
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	public synchronized Object invoke(MethodInvocation invocation) throws Throwable {
		if (!invocation.getMethod().getReturnType().equals(Void.TYPE)) {
			LOG.warn("WARN: invoking non-void return type method - " + invocation.getMethod());
		}
		queue.add(invocation);
		return null;
	}
	
}
