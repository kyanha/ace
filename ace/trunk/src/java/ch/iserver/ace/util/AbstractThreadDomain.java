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
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
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
	
	public String getName() {
		return name;
	}
	
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
		return wrap(target, clazz, queue, new Advice[0]);
	}
	
	/**
	 * @param target
	 * @param clazz
	 * @param queue
	 * @param advice
	 * @return
	 */
	protected Object wrap(Object target, Class clazz, BlockingQueue queue, Advice advice) {
		return wrap(target, clazz, queue, new Advice[] { advice });
	}
	
	/**
	 * @param target
	 * @param clazz
	 * @param queue
	 * @param advices
	 * @return
	 */
	protected Object wrap(Object target, Class clazz, BlockingQueue queue, Advice[] advices) {
		return wrap(target, clazz, queue, advices, false);
	}
	
	protected Object wrap(Object target, Class clazz, BlockingQueue queue, Advice[] advices, boolean shortcutVoid) {
		ProxyFactoryBean factory = new ProxyFactoryBean();
		factory.addInterface(clazz);
		AsyncInterceptor interceptor = new AsyncInterceptor(queue);
		if (shortcutVoid) {
			Pointcut pointcut = new VoidMethodMatcherPointcut();
			factory.addAdvisor(new DefaultPointcutAdvisor(pointcut, interceptor));
		} else {
			factory.addAdvice(interceptor);
		}
		for (int i = 0; i < advices.length; i++) {
			factory.addAdvice(advices[i]);
		}
		factory.setTarget(target);
		return factory.getObject();
	}
		
}
