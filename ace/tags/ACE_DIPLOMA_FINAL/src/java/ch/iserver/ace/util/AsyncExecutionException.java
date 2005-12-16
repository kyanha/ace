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
 * Execution passed to an AsyncExceptionHandler whenever an async invocation
 * causes failures.
 */
public class AsyncExecutionException extends RuntimeException {

	private AsyncMethodInvocation invocation;
	
	/**
	 * 
	 */
	public AsyncExecutionException(Throwable cause, AsyncMethodInvocation invocation) {
		super(cause);
		this.invocation = invocation;
	}
	
	public AsyncMethodInvocation getInvocation() {
		return invocation;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer(super.toString());
		buf.append("\n\tAsync Method Invocation:\n\t");
		buf.append(getInvocation());
		return buf.toString();
	}

}
