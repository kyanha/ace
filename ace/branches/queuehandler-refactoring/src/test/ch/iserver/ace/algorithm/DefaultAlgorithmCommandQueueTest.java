package ch.iserver.ace.algorithm;

import junit.framework.TestCase;

public class DefaultAlgorithmCommandQueueTest extends TestCase {

	public void testAddCommand() {
		AlgorithmCommand local1 = new MockCommand(true);
		AlgorithmCommand local2 = new MockCommand(true);
		AlgorithmCommand remote = new MockCommand(false);		
		
		// test
		DefaultAlgorithmCommandQueue queue = new DefaultAlgorithmCommandQueue();
		assertEquals(0, queue.getNextLocalIndex());
		queue.addCommand(local1);
		assertEquals(1, queue.getNextLocalIndex());
		queue.addCommand(remote);
		assertEquals(1, queue.getNextLocalIndex());
		queue.addCommand(local2);
		assertEquals(2, queue.getNextLocalIndex());
	}
	
	public void testGetCommand() throws InterruptedException {
		AlgorithmCommand local1 = new MockCommand(true);
		AlgorithmCommand local2 = new MockCommand(true);
		AlgorithmCommand remote = new MockCommand(false);
				
		// test
		DefaultAlgorithmCommandQueue queue = new DefaultAlgorithmCommandQueue();
		queue.addCommand(local1);
		queue.addCommand(remote);
		assertTrue(local1 == queue.getCommand());
		assertEquals(0, queue.getNextLocalIndex());
		queue.addCommand(local2);
		assertEquals(1, queue.getNextLocalIndex());
		assertTrue(local2 == queue.getCommand());
		assertEquals(0, queue.getNextLocalIndex());
		assertTrue(remote == queue.getCommand());
		assertEquals(0, queue.getNextLocalIndex());
		assertTrue(queue.isEmpty());
	}
	
	protected static class MockCommand implements AlgorithmCommand {
		private final boolean local;
		protected MockCommand(boolean local) {
			this.local = local;
		}
		public void execute(Algorithm algorithm, RequestConsumer consumer) { }
		public boolean isLocal() {
			return local;
		}
	}

}
