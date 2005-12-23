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

package ch.iserver.ace.threaddomain;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

/**
 * MethodInvocation used by the AsyncInterceptor that contains information
 * about the callers stack trace.
 */
public final class AsyncMethodInvocation implements MethodInvocation {
	
	/**
	 * The real method invocation.
	 */
	private MethodInvocation invocation;
	
	/**
	 * The stack trace of the caller (if enabled).
	 */
	private StackTraceElement[] callerStackTrace = new StackTraceElement[0];
	
	/**
	 * Creates a new AsyncMethodInvocation wrapping the passed in invocation.
	 * 
	 * @param invocation the target method invocation
	 */
	public AsyncMethodInvocation(MethodInvocation invocation) {
		this.invocation = invocation;
	}
	
	/**
	 * @return the caller's stack trace
	 */
	public StackTraceElement[] getCallerStackTrace() {
		return callerStackTrace;
	}
	
	/**
	 * @param stackTrace the caller stack trace to be set
	 */
	public void setCallerStackTrace(StackTraceElement[] stackTrace) {
		this.callerStackTrace = stackTrace;
	}
	
	/**
	 * @see org.aopalliance.intercept.Invocation#getArguments()
	 */
	public Object[] getArguments() {
		return invocation.getArguments();
	}
	
	/**
	 * @see org.aopalliance.intercept.MethodInvocation#getMethod()
	 */
	public Method getMethod() {
		return invocation.getMethod();
	}
	
	/**
	 * @see org.aopalliance.intercept.Joinpoint#getStaticPart()
	 */
	public AccessibleObject getStaticPart() {
		return invocation.getStaticPart();
	}
	
	/**
	 * @see org.aopalliance.intercept.Joinpoint#getThis()
	 */
	public Object getThis() {
		return invocation.getThis();
	}
	
	/**
	 * @see org.aopalliance.intercept.Joinpoint#proceed()
	 */
	public Object proceed() throws Throwable {
		return invocation.proceed();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getSignature(this));
		buf.append(getArguments(this));
		buf.append("\n");
		for (int i = 0; i < callerStackTrace.length; i++) {
			buf.append("\t");
			buf.append(callerStackTrace[i]);
			buf.append("\n");
		}
		return buf.toString();
	}
	
	private String getSignature(MethodInvocation invocation) {
		StringBuffer buf = new StringBuffer();
		buf.append(invocation.getThis().getClass().getName());
		buf.append("#");
		buf.append(invocation.getMethod().getName());
		return buf.toString();
	}
	
	private String getArguments(MethodInvocation invocation) {
		StringBuffer buf = new StringBuffer();
		buf.append("(");
		Object[] args = invocation.getArguments();
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				buf.append(arg);
				if (i + 1 < args.length) {
					buf.append(",");
				}
			}
		}
		buf.append(")");
		return buf.toString();
	}
	
}