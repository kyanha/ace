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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Interceptor that logs the method invocations to the given logger category.
 * The log level used to log can be specified as constructor argument. It
 * defaults to Level.INFO.
 */
public class LoggingInterceptor implements MethodInterceptor {
	
	/**
	 * The Logger used by this instance.
	 */
	private Logger LOG;
	
	/**
	 * Specifies whether method arguments should be logged. Defaults to true.
	 */
	private boolean loggingArguments = true;
	
	/**
	 * The level to log messages.
	 */
	private Level level;
	
	/**
	 * Creates a LoggingInterceptor using the given category. Standard
	 * constructor logs with log level info.
	 * 
	 * @param category the log category
	 */
	public LoggingInterceptor(Class category) {
		this(category, Level.INFO);
	}
	
	/**
	 * Creates a LoggingInterceptor using the given category and log
	 * level to log.
	 * 
	 * @param category the log category
	 * @param level the log level under which to log
	 */
	public LoggingInterceptor(Class category, Level level) {
		LOG = Logger.getLogger(category);
		this.level = level;
	}
	
	/**
	 * Specifies whether this interceptor logs the method arguments.
	 * 
	 * @return true iff method arguments are logged
	 */
	public boolean isLoggingArguments() {
		return loggingArguments;
	}
	
	/**
	 * Sets the loggingArguments properties which specifies whether arguments
	 * are logged.
	 * 
	 * @param loggingArguments true iff arguments should be logged
	 */
	public void setLoggingArguments(boolean loggingArguments) {
		this.loggingArguments = loggingArguments;
	}
	
	/**
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (!LOG.isEnabledFor(level)) {
			return invocation.proceed();
			
		} else {
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
	}
	
	/**
	 * Prints a log statement before the invocation.
	 * 
	 * @param invocation the method invocation
	 */
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
		LOG.log(level, buf.toString());
	}
	
	/**
	 * Logs the result of the invocation.
	 * 
	 * @param invocation the method invocation
	 * @param result the result
	 */
	protected void logAfter(MethodInvocation invocation, Object result) {
		StringBuffer buf = new StringBuffer();
		buf.append("<-- leaving: ");
		buf.append(getSignature(invocation));
		if (!Void.TYPE.equals(invocation.getMethod().getReturnType())) {
			buf.append(": ");
			buf.append(result);
		}
		LOG.log(level, buf.toString());
	}
	
	/**
	 * Logs the throwing of an exception.
	 * 
	 * @param invocation the method invocation
	 * @param th the exception thrown by the invocation
	 */
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
		LOG.log(level, buf.toString());
	}
	
	/**
	 * Constructs the method signature.
	 * 
	 * @param invocation the method invocation
	 * @return a String of the signature
	 */
	private String getSignature(MethodInvocation invocation) {
		StringBuffer buf = new StringBuffer();
		buf.append(invocation.getThis().getClass().getName());
		buf.append("#");
		buf.append(invocation.getMethod().getName());
		return buf.toString();
	}

}
