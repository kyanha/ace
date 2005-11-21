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
public class LoggingInterceptor implements MethodInterceptor {
	
	private Logger LOG;
	
	private boolean loggingArguments = true;
	
	public LoggingInterceptor(Class category) {
		LOG = Logger.getLogger(category);
	}
	
	public boolean isLoggingArguments() {
		return loggingArguments;
	}
	
	public void setLoggingArguments(boolean loggingArguments) {
		this.loggingArguments = loggingArguments;
	}
	
	/**
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		logBefore(invocation);
		try {
			Object result = invocation.proceed();
			logAfter(invocation, result);
			return result;
		} catch (Throwable t) {
			logThrowable(invocation, t);
			throw t;
		}
	}
	
	protected void logBefore(MethodInvocation invocation) {
		StringBuffer buf = new StringBuffer();
		buf.append("--> entering: ");
		buf.append(getSignature(invocation));
		if (isLoggingArguments()) {
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
		}
		LOG.info(buf.toString());
	}
	
	protected void logAfter(MethodInvocation invocation, Object result) {
		StringBuffer buf = new StringBuffer();
		buf.append("<-- leaving: ");
		buf.append(getSignature(invocation));
		if (!void.class.equals(invocation.getMethod().getReturnType())) {
			buf.append(": ");
			buf.append(result);
		}
		LOG.info(buf.toString());
	}
	
	protected void logThrowable(MethodInvocation invocation, Throwable th) {
		StringBuffer buf = new StringBuffer();
		buf.append("<-- ");
		buf.append(getSignature(invocation));
		buf.append(" threw ");
		buf.append(th.getClass().getName());
		if (LOG.isDebugEnabled()) {
			buf.append("\n");
			buf.append(th.toString());
		}
		LOG.info(buf.toString());
	}
	
	private String getSignature(MethodInvocation invocation) {
		StringBuffer buf = new StringBuffer();
		buf.append(invocation.getThis().getClass().getName());
		buf.append("#");
		buf.append(invocation.getMethod().getName());
		return buf.toString();
	}

}
