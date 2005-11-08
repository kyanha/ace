package ch.iserver.ace.util;

import edu.emory.mathcs.backport.java.util.concurrent.ScheduledExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledThreadPoolExecutor;

public class IsolatedThreadDomainTest {
	
	public static void main(String[] args) throws Exception {
		ScheduledExecutorService ses = new ScheduledThreadPoolExecutor(1);
		ThreadDomain domain = new IsolatedThreadDomain(ses);
		
		for (int i = 0; i < 100; i++) {
			TargetImpl target = new TargetImpl();
			domain.wrap(target, Target.class);
			Thread.sleep(100);
			System.gc();
		}
		
		System.out.println("finished ...");
		ses.shutdown();
		System.out.println("shut down ...");
	}
	
	public static interface Target {
		void sendHello(String msg);
	}
	
	public static class TargetImpl implements Target {
		public void sendHello(String msg) {
			System.out.println("hello " + msg);
		}
	}
	
}
