package ch.iserver.ace.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;

public class AsyncInterceptor extends Thread implements MethodInterceptor {

	private static final Logger LOG = Logger.getLogger(AsyncInterceptor.class);
	
	private final BlockingQueue queue;
	
	public AsyncInterceptor(BlockingQueue queue) {
		setDaemon(true);
		this.queue = queue;
	}
	
	public synchronized Object invoke(MethodInvocation invocation) throws Throwable {
		if (!invocation.getMethod().getReturnType().equals(Void.TYPE)) {
			LOG.warn("WARN: invoking non-void return type method - " + invocation.getMethod());
		}
		queue.add(invocation);
		return null;
	}
	
	public void init() {
		start();
	}
	
	public void destroy() {
		System.out.println("destroy method");
	}

	public void run() {
		try {
			while (true) {
				MethodInvocation invocation = (MethodInvocation) queue.take();
				try {
					invocation.proceed();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

}
