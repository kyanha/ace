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

/**
 * An interceptor that allows the target object to be closed. Calling
 * a closed object results in an IllegalStateException beeing thrown.
 */
public class CloseableAdvice implements MethodInterceptor {

	/**
	 * The closed flag of this object.
	 */
	private boolean closed;
	
	/**
	 * Determines whether this object is closed.
	 * 
	 * @return true iff the object is closed
	 */
	public boolean isClosed() {
		return closed;
	}
	
	/**
	 * Closes this object. Calling further methods result in an
	 * {@link IllegalStateException} beeing thrown.
	 */
	public synchronized void close() {
		closed = true;
	}
	
	/**
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	public synchronized Object invoke(MethodInvocation invocation) throws Throwable {
		if (isClosed()) {
			throw new IllegalStateException("cannot access closed object");
		}
		return invocation.proceed();
	}

}
