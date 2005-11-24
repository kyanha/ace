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

/**
 *
 */
public class ThreadIdInterceptor implements MethodInterceptor {

	private static final Logger LOG = Logger.getLogger(ThreadIdInterceptor.class);
	
	/**
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		LOG.info(getSignature(invocation) + " called by " + Thread.currentThread().getName());
		return invocation.proceed();
	}
	
	private String getSignature(MethodInvocation invocation) {
		StringBuffer buf = new StringBuffer();
		buf.append(invocation.getThis().getClass().getName());
		buf.append("#");
		buf.append(invocation.getMethod().getName());
		return buf.toString();
	}

}
