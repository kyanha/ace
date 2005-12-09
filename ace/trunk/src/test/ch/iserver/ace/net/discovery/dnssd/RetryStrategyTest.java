package ch.iserver.ace.net.discovery.dnssd;

import junit.framework.TestCase;

public class RetryStrategyTest extends TestCase {

	public void testAdditiveWaitRetryStrategy() throws Exception {
		
		CallStub call = new CallStub();
		
		try {
			call.execute();
		} catch (DNSSDUnavailable du) {}
		
		assertEquals(3, call.getCallCount());
		//TODO: configure retry strategy and assert time intervals deterministically
	}
	
}
