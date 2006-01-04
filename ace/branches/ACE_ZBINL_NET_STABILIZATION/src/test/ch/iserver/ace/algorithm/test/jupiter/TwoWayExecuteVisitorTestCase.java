package ch.iserver.ace.algorithm.test.jupiter;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.test.AlgorithmTestFactory;
import ch.iserver.ace.algorithm.test.StartNode;
import ch.iserver.ace.algorithm.test.jupiter.TwoWayExecuteVisitor;

/**
 * Test case for TwoWayExecuteVisitor.
 */
public class TwoWayExecuteVisitorTestCase extends TestCase {

	/**
	 * Tests the process of visiting a start node.
	 */
	public void testVisitStartNode() {
		// setup mock objects
		MockControl control = MockControl.createControl(AlgorithmTestFactory.class);
		MockControl algoCtrl1 = MockControl.createControl(Algorithm.class);
		MockControl algoCtrl2 = MockControl.createControl(Algorithm.class);
		MockControl algoCtrl3 = MockControl.createControl(Algorithm.class);
		MockControl algoCtrl4 = MockControl.createControl(Algorithm.class);
		
		AlgorithmTestFactory factory = (AlgorithmTestFactory) control.getMock();
		Algorithm algo1 = (Algorithm) algoCtrl1.getMock();
		Algorithm algo2 = (Algorithm) algoCtrl2.getMock();
		Algorithm algo3 = (Algorithm) algoCtrl3.getMock();
		Algorithm algo4 = (Algorithm) algoCtrl4.getMock();
		
		// create scenario
		StartNode s1 = new StartNode("0", "abc", 0);
		StartNode s2 = new StartNode("1", "abc", 1);
		StartNode s3 = new StartNode("2", "abc", 2);
		StartNode s4 = new StartNode("3", "abc", 3);
		
		// define mock behavior
		factory.createAlgorithm(0, Boolean.TRUE);
		control.setReturnValue(algo1);
		
		factory.createAlgorithm(1, Boolean.FALSE);
		control.setReturnValue(algo2);

		factory.createAlgorithm(2, Boolean.TRUE);
		control.setReturnValue(algo3);

		factory.createAlgorithm(3, Boolean.FALSE);
		control.setReturnValue(algo4);
						
		// create test object
		TwoWayExecuteVisitor visitor = new TwoWayExecuteVisitor(factory);
		
		// replay
		control.replay();
		algoCtrl1.replay();
		algoCtrl2.replay();
		algoCtrl3.replay();
		algoCtrl4.replay();
		
		// execute scenario
		s1.accept(visitor);
		s2.accept(visitor);
		s3.accept(visitor);
		s4.accept(visitor);
		
		// verify
		control.verify();
		algoCtrl1.verify();
		algoCtrl2.verify();
		algoCtrl3.verify();
		algoCtrl4.verify();
	}
	
}
