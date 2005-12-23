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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import ch.iserver.ace.threaddomain.ExceptionHandler;

/**
 *
 */
public class AsyncInterceptor implements MethodInterceptor {
	
	private static final Logger LOG = Logger.getLogger(AsyncInterceptor.class);
	
	private final BlockingQueue<Invocation> queue;
	
	private ExceptionHandler exceptionHandler;
	
	public AsyncInterceptor(BlockingQueue<Invocation> queue, ExceptionHandler exceptionHandler) {
		this.queue = queue;
		this.exceptionHandler = exceptionHandler;
	}
	
	/**
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		if (hasNonVoidReturnType(invocation)) {
			LOG.info("calling method with non-void return type asynchronously");
		}
		queue.put(new Invocation() {
			public void proceed() {
				try {
					invocation.proceed();
				} catch (Throwable th) {
					exceptionHandler.handleException(th);
				}
			}
		});
		return null;
	}
	
	private boolean hasNonVoidReturnType(MethodInvocation invocation) {
		return !invocation.getMethod().getReturnType().equals(Void.TYPE);
	}
	
}
