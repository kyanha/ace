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

package ch.iserver.ace.test;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.algorithm.TransformationException;

/**
 * Tests the ExecuteVisitor class.
 */
public class ExecuteVisitorTest extends TestCase {
	
	/**
	 * Tests that visiting a start node does what its supposed to do.
	 */
	public void testStartNode() {
		// setup mock objects
		MockControl control = MockControl.createControl(AlgorithmTestFactory.class);
		MockControl algoCtrl = MockControl.createControl(Algorithm.class);
		AlgorithmTestFactory factory = (AlgorithmTestFactory) control.getMock();
		Algorithm algo = (Algorithm) algoCtrl.getMock();
		
		// create scenario
		StartNode s = new StartNode("0", "abc", 0);
		
		// define mock behavior
		factory.createAlgorithm(0, null);
		control.setReturnValue(algo);
				
		// create test object
		ExecuteVisitor visitor = new ExecuteVisitor(factory);
		
		// replay
		control.replay();
		algoCtrl.replay();
		
		// execute scenario
		s.accept(visitor);
		
		// verify
		control.verify();
		algoCtrl.verify();
	}
	
	/**
	 * Tests that visiting a do node does work correctly.
	 */
	public void testVisitDoNode() {
		// setup mock objects
		MockControl control = MockControl.createControl(AlgorithmTestFactory.class);
		MockControl rcptCtrl1 = MockControl.createControl(ReceptionNode.class);
		MockControl rcptCtrl2 = MockControl.createControl(ReceptionNode.class);
		MockControl algoCtrl = MockControl.createControl(Algorithm.class);
		Algorithm algo = (Algorithm) algoCtrl.getMock();
		AlgorithmTestFactory factory = (AlgorithmTestFactory) control.getMock();
		
		// create scenario
		StartNode start = new StartNode("0", "abc", 2);
		DoNode d = new DoNode("0", "0", null);
		ReceptionNode r1 = (ReceptionNode) rcptCtrl1.getMock();
		ReceptionNode r2 = (ReceptionNode) rcptCtrl2.getMock();
		d.addRemoteSuccessor(r1);
		d.addRemoteSuccessor(r2);

		// helper objects
		Request req = new TestRequest(0, null);
		
		// define mock behavior
		factory.createAlgorithm(0, null);
		control.setReturnValue(algo);
		algo.generateRequest(null);
		algoCtrl.setReturnValue(req);
				
		r1.setRequest("0", req);
		r2.setRequest("0", req);
		
		// create test object
		ExecuteVisitor visitor = new ExecuteVisitor(factory);
		
		// replay
		control.replay();
		algoCtrl.replay();
		rcptCtrl1.replay();
		rcptCtrl2.replay();
		
		// execute scenario
		start.accept(visitor);
		d.accept(visitor);
		
		// verify
		algoCtrl.verify();
		rcptCtrl1.verify();
		rcptCtrl2.verify();
	}
	
	/**
	 * Tests that visiting an undo node works correctly.
	 */
	public void testVisitUndoNode() {
		// setup mock objects
		MockControl control = MockControl.createControl(AlgorithmTestFactory.class);
		MockControl rcptCtrl1 = MockControl.createControl(ReceptionNode.class);
		MockControl rcptCtrl2 = MockControl.createControl(ReceptionNode.class);
		MockControl algoCtrl = MockControl.createControl(Algorithm.class);
		Algorithm algo = (Algorithm) algoCtrl.getMock();
		AlgorithmTestFactory factory = (AlgorithmTestFactory) control.getMock();
		
		// create scenari
		StartNode start = new StartNode("0", "", 2);
		UndoNode g = new UndoNode("0", "0");
		ReceptionNode r1 = (ReceptionNode) rcptCtrl1.getMock();
		ReceptionNode r2 = (ReceptionNode) rcptCtrl2.getMock();
		g.addRemoteSuccessor(r1);
		g.addRemoteSuccessor(r2);

		// helper objects
		Request req = new TestRequest(0, null);
		
		// define mock behavior
		factory.createAlgorithm(0, null);
		control.setReturnValue(algo);
		
		algo.undo();
		algoCtrl.setReturnValue(req);
				
		r1.setRequest("0", req);
		r2.setRequest("0", req);
		
		// create test object
		ExecuteVisitor visitor = new ExecuteVisitor(factory);
		
		// replay
		control.replay();
		algoCtrl.replay();
		rcptCtrl1.replay();
		rcptCtrl2.replay();
		
		// execute scenario
		start.accept(visitor);
		g.accept(visitor);
		
		// verify
		control.verify();
		algoCtrl.verify();
		rcptCtrl1.verify();
		rcptCtrl2.verify();
	}
	
	/**
	 * Tests that the visiting of a redo node works correctly.
	 */
	public void testVisitRedoNode() {
		// setup mock objects
		MockControl control = MockControl.createControl(AlgorithmTestFactory.class);
		MockControl rcptCtrl1 = MockControl.createControl(ReceptionNode.class);
		MockControl rcptCtrl2 = MockControl.createControl(ReceptionNode.class);
		MockControl algoCtrl = MockControl.createControl(Algorithm.class);
		Algorithm algo = (Algorithm) algoCtrl.getMock();
		AlgorithmTestFactory factory = (AlgorithmTestFactory) control.getMock();
		
		// create scenario
		StartNode start = new StartNode("0", "abc", 2);
		RedoNode g = new RedoNode("0", "0");
		ReceptionNode r1 = (ReceptionNode) rcptCtrl1.getMock();
		ReceptionNode r2 = (ReceptionNode) rcptCtrl2.getMock();
		g.addRemoteSuccessor(r1);
		g.addRemoteSuccessor(r2);

		// helper objects
		Request req = new TestRequest(0, null);
		
		// define mock behavior
		factory.createAlgorithm(0, null);
		control.setReturnValue(algo);
		
		algo.redo();
		algoCtrl.setReturnValue(req);
				
		r1.setRequest("0", req);
		r2.setRequest("0", req);
		
		// create test object
		ExecuteVisitor visitor = new ExecuteVisitor(factory);
		
		// replay
		control.replay();
		algoCtrl.replay();
		rcptCtrl1.replay();
		rcptCtrl2.replay();
		
		// execute scenario
		start.accept(visitor);
		g.accept(visitor);
		
		// verify
		control.verify();
		algoCtrl.verify();
		rcptCtrl1.verify();
		rcptCtrl2.verify();
	}
		
	/**
	 * The main purpose of this test is to test that generateRequest
	 * and receiveRequest are executed by the execute visitor on
	 * the algorithms.
	 * @throws TransformationException 
	 */
	public void testVisitReceptionNode() throws TransformationException {
		// setup mock objects
		MockControl factoryCtrl = MockControl.createControl(AlgorithmTestFactory.class);
		MockControl algoCtrl = MockControl.createControl(Algorithm.class);
		AlgorithmTestFactory factory = (AlgorithmTestFactory) factoryCtrl.getMock();
		Algorithm algo = (Algorithm) algoCtrl.getMock();

		// create test object
		ExecuteVisitor visitor = new ExecuteVisitor(factory);
		
		// create nodes
		StartNode start = new StartNode("0", "abc", 2);
		ReceptionNode r = new SimpleReceptionNode("0", "2");
		
		// helper object
		TestRequest req = new TestRequest(1, null);
		
		// define mock behavior
		algo.receiveRequest(req);
		algoCtrl.setReturnValue(null);
		factory.createAlgorithm(0, null);
		factoryCtrl.setReturnValue(algo);
		
		// replay behavior
		algoCtrl.replay();
		factoryCtrl.replay();
		
		// execute test
		start.accept(visitor);
		r.setRequest("0", req);
		r.accept(visitor);
		
		// verify method calls
		algoCtrl.verify();
		factoryCtrl.verify();
	}
	
	/**
	 * Tests that a verification node does not fail if the verification
	 * of the document state is successful.
	 */
	public void testVerificationNode() {
		// setup mock objects
		MockControl control = MockControl.createControl(AlgorithmTestFactory.class);
		AlgorithmTestFactory factory = (AlgorithmTestFactory) control.getMock();

		// create test object
		ExecuteVisitor visitor = new ExecuteVisitor(factory);
		
		// create nodes
		StartNode start = new StartNode("0", "abc", 2);
		VerificationNode v = new VerificationNode("0", "abc");
		
		// define mock behavior
		factory.createAlgorithm(0, null);
		control.setReturnValue(null);
		
		// replay behavior
		control.replay();
		
		// execute test
		start.accept(visitor);
		v.accept(visitor);
		
		// verify method calls
		control.verify();
	}

	/**
	 * Tests that a verification node fails if the document state does
	 * not match the expected state.
	 */
	public void testVerificationNodeFail() {
		// setup mock objects
		MockControl control = MockControl.createControl(AlgorithmTestFactory.class);
		AlgorithmTestFactory factory = (AlgorithmTestFactory) control.getMock();

		// create test object
		ExecuteVisitor visitor = new ExecuteVisitor(factory);
		
		// create nodes
		StartNode start = new StartNode("0", "", 2);
		VerificationNode v = new VerificationNode("0", "abc");
		
		// define mock behavior
		factory.createAlgorithm(0, null);
		control.setReturnValue(null);
		
		// replay behavior
		control.replay();
		
		// execute test
		start.accept(visitor);
		v.accept(visitor);
		VerificationResult result = visitor.getVerificationResult();
		assertEquals(1, result.getFailures().size());
		
		// verify method calls
		control.verify();
	}
	
	/**
	 * Tests that the verification of an end node does not fail if
	 * the document state is equal to the expected state.
	 */
	public void testEndNode() {
		// setup mock objects
		MockControl control = MockControl.createControl(AlgorithmTestFactory.class);
		AlgorithmTestFactory factory = (AlgorithmTestFactory) control.getMock();

		// create test object
		ExecuteVisitor visitor = new ExecuteVisitor(factory);
		
		// create nodes
		StartNode start = new StartNode("0", "abc", 2);
		EndNode e = new EndNode("0", "abc");

		// define mock behavior
		factory.createAlgorithm(0, null);
		control.setReturnValue(null);
		
		// replay behavior
		control.replay();
		
		// execute test
		start.accept(visitor);
		e.accept(visitor);
		
		// verify method calls
		control.verify();
	}

	/**
	 * Tests that the verification of an end node fails if the document
	 * states are not equal.
	 */
	public void testEndNodeFail() {
		// setup mock objects
		MockControl control = MockControl.createControl(AlgorithmTestFactory.class);
		AlgorithmTestFactory factory = (AlgorithmTestFactory) control.getMock();

		// create test object
		ExecuteVisitor visitor = new ExecuteVisitor(factory);
		
		// create nodes
		StartNode start = new StartNode("0", "", 2);
		EndNode v = new EndNode("0", "abc");
		
		// define mock behavior
		factory.createAlgorithm(0, null);
		control.setReturnValue(null);
		
		// replay behavior
		control.replay();
		
		start.accept(visitor);
		v.accept(visitor);
		VerificationResult result = visitor.getVerificationResult();
		assertEquals(1, result.getFailures().size());
		
		// verify method calls
		control.verify();
	}
	
	private static class TestRequest implements Request {
		private int siteId;
		private Operation operation;
		public TestRequest(int siteId, Operation operation) {
			this.siteId = siteId;
			this.operation = operation;
		}
		public Operation getOperation() {
			return operation;
		}
		public int getSiteId() {
			return siteId;
		}
		public Timestamp getTimestamp() {
			return null;
		}
	}
				
}
