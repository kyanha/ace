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

public class AsyncInterceptor implements MethodInterceptor {

	private static final Logger LOG = Logger.getLogger(AsyncInterceptor.class);
	
	private final BlockingQueue queue;
	
	public AsyncInterceptor(BlockingQueue queue) {
		ParameterValidator.notNull("queue", queue);
		this.queue = queue;
	}
	
	public synchronized Object invoke(MethodInvocation invocation) throws Throwable {
		if (!invocation.getMethod().getReturnType().equals(Void.TYPE)) {
			LOG.warn("WARN: invoking non-void return type method - " + invocation.getMethod());
		}
		queue.add(invocation);
		return null;
	}
	
}