package ch.iserver.ace.test.jupiter;

import org.easymock.MockControl;

import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.jupiter.server.OperationExtractDocumentModel;
import ch.iserver.ace.test.AlgorithmTestFactory;
import ch.iserver.ace.test.StartNode;
import junit.framework.TestCase;

public class NWayExecuteVisitorTest extends TestCase {

	public void testVisitStartNode() {
		// setup mock objects
		MockControl control = MockControl.createControl(AlgorithmTestFactory.class);
		MockControl algoCtrl1 = MockControl.createControl(Algorithm.class);
		MockControl algoCtrl2 = MockControl.createControl(Algorithm.class);
		AlgorithmTestFactory factory = (AlgorithmTestFactory) control.getMock();
		Algorithm algo1 = (Algorithm) algoCtrl1.getMock();
		Algorithm algo2 = (Algorithm) algoCtrl2.getMock();
		
		// create scenario
		StartNode s = new StartNode("0", "abc", 0);
		
		// define mock behavior
		factory.createAlgorithm(0, Boolean.TRUE);
		control.setReturnValue(algo1);
		factory.createDocument("abc");
		control.setReturnValue(null);
		factory.createTimestamp();
		control.setReturnValue(null);
		algo1.init(null, null);
		
		factory.createAlgorithm(0, Boolean.FALSE);
		control.setReturnValue(algo2);
		factory.createTimestamp();
		control.setReturnValue(null);
		algo2.init(new OperationExtractDocumentModel(), null);
						
		// create test object
		NWayExecuteVisitor visitor = new NWayExecuteVisitor(factory);
		
		// replay
		control.replay();
		algoCtrl1.replay();
		algoCtrl2.replay();
		
		// execute scenario
		s.accept(visitor);
		
		// verify
		control.verify();
		algoCtrl1.verify();
		algoCtrl2.verify();
		assertTrue(visitor.getAlgorithm("0") == algo1);
		assertTrue(visitor.getServerAlgorithm("0") == algo2);
	}
	
	public void testVisitRelayNode() {
		// TODO
		fail();
	}
	
}
