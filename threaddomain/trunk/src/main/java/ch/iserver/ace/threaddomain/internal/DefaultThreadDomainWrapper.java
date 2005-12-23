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

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;

import ch.iserver.ace.threaddomain.ExceptionHandler;
import ch.iserver.ace.threaddomain.ThreadDomainWrapper;

public class DefaultThreadDomainWrapper implements ThreadDomainWrapper {
	
	boolean synchronous;
	
	boolean swapEnabled;
	
	private final InterceptorFactory interceptorFactory;
	
	private ExceptionHandler exceptionHandler = new DefaultExceptionHandler();
	
	public DefaultThreadDomainWrapper(InterceptorFactory interceptorFactory) {
		this.interceptorFactory = interceptorFactory;
	}
	
	public void setSynchronous(boolean sync) {
		this.synchronous = sync;
	}
	
	public boolean isSynchronous() {
		return synchronous;
	}
	
	public void setSwapEnabled(boolean swapEnabled) {
		this.swapEnabled = swapEnabled;
	}
	
	public boolean isSwapEnabled() {
		return swapEnabled;
	}
	
	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
	
	public Object wrap(Object target, Class clazz) {
		ProxyFactory factory = new ProxyFactory();
		factory.addInterface(clazz);
		factory.setTarget(target);
		MethodInterceptor guard = interceptorFactory.createInterceptor(isSynchronous(), exceptionHandler);
		factory.addAdvice(guard);
		if (isSwapEnabled()) {
			factory.addAdvice(new SwapInterceptor());
		}
		return factory.getProxy();
	}
	
}