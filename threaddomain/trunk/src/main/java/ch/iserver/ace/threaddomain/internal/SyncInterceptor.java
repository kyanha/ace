/*
 * $Id$
 *
 * threaddomain
 * Copyright (C) 2005 Simon Raess
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

package ch.iserver.ace.threaddomain.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import ch.iserver.ace.threaddomain.ExceptionHandler;

/**
 * @author sir
 *
 */
public class SyncInterceptor implements MethodInterceptor {

	private BlockingQueue<Invocation> queue;
	
	private ExceptionHandler exceptionHandler;
	
	public SyncInterceptor(BlockingQueue<Invocation> queue, ExceptionHandler exceptionHandler) {
		this.queue = queue;
		this.exceptionHandler = exceptionHandler;
	}
	
	/**
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		final BlockingQueue<Object> resultQueue = new SynchronousQueue<Object>(); 
		queue.put(new Invocation() {
			public void proceed() throws InterruptedException {
				try {
					resultQueue.put(invocation.proceed());
				} catch (Throwable th) {
					exceptionHandler.handleException(th);
					resultQueue.put(new ExceptionWrapper(th));
				}
			}
		});
		Object result = resultQueue.take();
		if (result != null && ExceptionWrapper.class.equals(result.getClass())) {
			throw ((ExceptionWrapper) result).getCause();
		} else {
			return result;
		}
	}
	
	private static class ExceptionWrapper {
		private Throwable cause;
		public ExceptionWrapper(Throwable cause) {
			this.cause = cause;
		}
		public Throwable getCause() {
			return cause;
		}
	}

}
