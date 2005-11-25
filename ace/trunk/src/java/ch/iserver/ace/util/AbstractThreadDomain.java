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

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;

/**
 * Abstract base class for ThreadDomain implementations.
 */
abstract class AbstractThreadDomain implements ThreadDomain {
	
	/**
	 * The name of the thread domain.
	 */
	private String name = "";
	
	/**
	 * @see ch.iserver.ace.util.ThreadDomain#getName()
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @see ch.iserver.ace.util.ThreadDomain#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Wraps the given <var>target</var> object and returns a proxy that
	 * implements the specified interface <var>clazz</var>. It is thus
	 * safe to cast the returned object to the specified class. The
	 * <var>queue</var> serves as target for the AsyncInterceptor that
	 * intercepts all the calls to the target object.
	 * 
	 * @param target the target object to be wrapped
	 * @param clazz the interface the proxy should implement
	 * @param queue the queue used to queue MethodInvocation objects
	 * @return a dynamic proxy wrapping the given target object
	 */
	protected Object wrap(Object target, Class clazz, BlockingQueue queue) {
		return wrap(target, clazz, queue, null, new Advice[0]);
	}
	
	/**
	 * @param target
	 * @param clazz
	 * @param queue
	 * @param advice
	 * @return
	 */
	protected Object wrap(Object target, Class clazz, BlockingQueue queue, Advice advice) {
		return wrap(target, clazz, queue, null, new Advice[] { advice });
	}
	
	/**
	 * @param target
	 * @param clazz
	 * @param queue
	 * @param pointcut
	 * @param advices
	 * @return
	 */
	protected Object wrap(Object target, Class clazz, BlockingQueue queue, Pointcut pointcut, Advice[] advices) {
		ProxyFactory factory = initFactory(target, clazz, queue, pointcut);
		for (int i = 0; i < advices.length; i++) {
			if (pointcut != null) {
				factory.addAdvisor(new DefaultPointcutAdvisor(pointcut, advices[i]));
			} else {
				factory.addAdvice(advices[i]);
			}
		}
		return factory.getProxy();
	}
	
	/**
	 * @param target
	 * @param clazz
	 * @param queue
	 * @param pointcut
	 * @return
	 */
	protected Object wrap(Object target, Class clazz, BlockingQueue queue, Pointcut pointcut) {
		return wrap(target, clazz, queue, pointcut, new Advisor[0]);
	}
	
	/**
	 * @param target
	 * @param clazz
	 * @param queue
	 * @param pointcut
	 * @param advisors
	 * @return
	 */
	protected Object wrap(Object target, Class clazz, BlockingQueue queue, Pointcut pointcut, Advisor[] advisors) {
		ProxyFactory factory = initFactory(target, clazz, queue, pointcut);
		for (int i = 0; i < advisors.length; i++) {
			factory.addAdvisor(advisors[i]);
		}
		return factory.getProxy();
	}
	
	/**
	 * @param target
	 * @param clazz
	 * @param queue
	 * @param pointcut
	 * @return
	 */
	protected ProxyFactory initFactory(Object target, Class clazz, BlockingQueue queue, Pointcut pointcut) {
		ProxyFactory factory = new ProxyFactory();
		factory.addInterface(clazz);
		factory.setTarget(target);
		MethodInterceptor interceptor = createAsyncInterceptor(queue);
		if (pointcut != null) {
			factory.addAdvisor(new DefaultPointcutAdvisor(pointcut, interceptor));
		} else {
			factory.addAdvice(interceptor);
		}
		return factory;
	}
	
	/**
	 * @param queue
	 * @return
	 */
	protected MethodInterceptor createAsyncInterceptor(BlockingQueue queue) {
		return new AsyncInterceptor(queue);
	}

	/**
	 * @see ch.iserver.ace.util.ThreadDomain#wrap(java.lang.Object, java.lang.Class)
	 */
	public synchronized Object wrap(Object target, Class clazz) {
		return wrap(target, clazz, false);
	}
		
}
