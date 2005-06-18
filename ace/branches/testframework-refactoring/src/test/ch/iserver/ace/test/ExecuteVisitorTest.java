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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.text.InsertOperation;

/**
 * Tests the ExecuteVisitor class.
 */
public class ExecuteVisitorTest extends TestCase {

	/**
	 * Perform a normal execution flow. Check that no exceptions occur.
	 */
	public void testNormalExecution() {
		ExecuteVisitor visitor = new ExecuteVisitor(new AlgorithmTestFactoryStub());
		Iterator it = createTestSequence();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			node.accept(visitor);
		}
	}
	
	/**
	 * The purpose of this test is to check that when visiting a generation node
	 * the remote reception nodes get the generated request.
	 */
	public void testVisitGenerationNode() {
		ExecuteVisitor visitor = new ExecuteVisitor(new AlgorithmTestFactoryStub());
		
		// create nodes
		StartNode s1 = new StartNode("0", "abc");
		StartNode s2 = new StartNode("1", "abc");
		DoNode g1 = new DoNode("0", "1", new InsertOperation(1, "1"));
		DoNode g2 = new DoNode("1", "2", new InsertOperation(1, "2"));
		ReceptionNode r1 = new ReceptionNode("0", "2");
		ReceptionNode r2 = new ReceptionNode("1", "1");
		
		// add remote successors
		g1.addRemoteSuccessor(r2);
		g2.addRemoteSuccessor(r1);
		
		// test
		s1.accept(visitor);
		s2.accept(visitor);
		assertNull(r1.getRequest());
		assertNull(r2.getRequest());
		g1.accept(visitor);
		g2.accept(visitor);
		assertNotNull(r1.getRequest());
		assertNotNull(r2.getRequest());
	}
	
	/**
	 * The main purpose of this test is to test that generateRequest
	 * and receiveRequest are executed by the execute visitor on
	 * the algorithms.
	 */
	public void testVisitReceptionNode() {
		// setup mock objects
		MockControl control = MockControl.createControl(AlgorithmTestFactory.class);
		MockControl algoCtrl1 = MockControl.createControl(Algorithm.class);
		MockControl algoCtrl2 = MockControl.createControl(Algorithm.class);
		AlgorithmTestFactory factory = (AlgorithmTestFactory) control.getMock();
		Algorithm algo1 = (Algorithm) algoCtrl1.getMock();
		Algorithm algo2 = (Algorithm) algoCtrl2.getMock();

		// create test object
		ExecuteVisitor visitor = new ExecuteVisitor(factory);
		
		// create nodes
		StartNode s1 = new StartNode("0", "abc");
		StartNode s2 = new StartNode("1", "abc");
		DoNode g1 = new DoNode("0", "1", new InsertOperation(1, "1"));
		DoNode g2 = new DoNode("1", "2", new InsertOperation(1, "2"));
		ReceptionNode r1 = new ReceptionNode("0", "2");
		ReceptionNode r2 = new ReceptionNode("1", "1");
		g1.addRemoteSuccessor(r2);
		g2.addRemoteSuccessor(r1);
		
		// define mock behavior
		factory.createAlgorithm(0);
		control.setReturnValue(algo1);
		factory.createDocument("abc");
		control.setReturnValue(null);
		factory.createTimestamp();
		control.setReturnValue(null);
		
		factory.createAlgorithm(1);
		control.setReturnValue(algo2);
		factory.createDocument("abc");
		control.setReturnValue(null);
		factory.createTimestamp();
		control.setReturnValue(null);

		algo1.init(null, null);
		algo2.init(null, null);
		algo1.generateRequest(new InsertOperation(1, "1"));
		algoCtrl1.setReturnValue(null);
		algo2.generateRequest(new InsertOperation(1, "2"));
		algoCtrl2.setReturnValue(null);
		algo1.receiveRequest(null);
		algo2.receiveRequest(null);
		
		// replay behavior
		algoCtrl1.replay();
		algoCtrl2.replay();
		control.replay();
		
		// execute test
		s1.accept(visitor);		
		s2.accept(visitor);
		g1.accept(visitor);
		g2.accept(visitor);		
		r1.accept(visitor);
		r2.accept(visitor);
		
		// verify method calls
		control.verify();
		algoCtrl1.verify();
		algoCtrl2.verify();
	}
	
	/**
	 * Creates a normal sequence of nodes.
	 */
	protected Iterator createTestSequence() {
		List result = new ArrayList();
		
		// create nodes
		StartNode s1 = new StartNode("0", "abc");
		StartNode s2 = new StartNode("1", "abc");
		DoNode g1 = new DoNode("0", "1", new InsertOperation(1, "1"));
		DoNode g2 = new DoNode("1", "2", new InsertOperation(2, "2"));
		ReceptionNode r1 = new ReceptionNode("0", "2");
		ReceptionNode r2 = new ReceptionNode("1", "1");
		EndNode e1 = new EndNode("0", "abc");
		EndNode e2 = new EndNode("1", "abc");
		
		// set local successors
		s1.setLocalSuccessor(g1);
		g1.setLocalSuccessor(r1);
		r1.setLocalSuccessor(e1);
		s2.setLocalSuccessor(g2);
		g2.setLocalSuccessor(r2);
		r2.setLocalSuccessor(e2);
		
		// add remote successors
		g1.addRemoteSuccessor(r2);
		g2.addRemoteSuccessor(r1);
		
		// add nodes to result
		result.add(s1);
		result.add(g1);
		result.add(s2);
		result.add(g2);
		result.add(r1);
		result.add(r2);
		result.add(e1);
		result.add(e2);
		
		return result.iterator();
	}
	
	private static class AlgorithmTestFactoryStub implements AlgorithmTestFactory {
		public Algorithm createAlgorithm(int siteId) {
			return new AlgorithmStub();
		}
		public DocumentModel createDocument(String state) {
			return new DocumentModel() {
				public void apply(Operation operation) {
					
				}
				public boolean equals(Object o) {
					return true;
				}
			};
		}
		public Timestamp createTimestamp() {
			return new Timestamp() { };
		}
	}
	
	private static class AlgorithmStub implements Algorithm {
		private DocumentModel doc;
		private Timestamp timestamp;
		public DocumentModel getDocument() {
			return doc;
		}
		public void init(DocumentModel doc, Timestamp timestamp) {
			this.doc = doc;
			this.timestamp = timestamp;
		}			
		public void siteRemoved(int siteId) {
	
		}
		public void siteAdded(int siteId) {
	
		}			
		public void receiveRequest(Request req) {
	
		}
		public Request generateRequest(final Operation op) {
			return new Request() {
				public Timestamp getTimestamp() {
					return new Timestamp() { };
				}
				public Operation getOperation() {
					return op;
				}
			
				public int getSiteId() {
					return 0;
				}
			
			};
		}
		public Request undo() {
			return null;
		}
		public Request redo() {
			return null;
		}
	}
		
}
