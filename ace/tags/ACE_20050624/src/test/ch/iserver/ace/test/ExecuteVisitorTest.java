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
		GenerationNode g1 = new GenerationNode("0", "1", new InsertOperation(1, "1"));
		GenerationNode g2 = new GenerationNode("1", "2", new InsertOperation(1, "2"));
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
	 * Creates a normal sequence of nodes.
	 * 
	 * @return an iterator for the sequence of nodes.
	 */
	protected Iterator createTestSequence() {
		List result = new ArrayList();
		
		// create nodes
		StartNode s1 = new StartNode("0", "abc");
		StartNode s2 = new StartNode("1", "abc");
		GenerationNode g1 = new GenerationNode("0", "1", new InsertOperation(1, "1"));
		GenerationNode g2 = new GenerationNode("1", "2", new InsertOperation(2, "2"));
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
		/**
		 * {@inheritDoc}
		 */
		public Algorithm createAlgorithm(int siteId) {
			return new AlgorithmStub();
		}
		/**
		 * {@inheritDoc}
		 */
		public DocumentModel createDocument(String state) {
			return new DocumentModel() {
				public void apply(Operation operation) {
					
				}
				public boolean equals(Object o) {
					return true;
				}
			};
		}
		/**
		 * {@inheritDoc}
		 */
		public Timestamp createTimestamp() {
			return new Timestamp() { };
		}
	}
	
	private static class AlgorithmStub implements Algorithm {
		private DocumentModel doc;
		private Timestamp timestamp;
		/**
		 * {@inheritDoc}
		 */
		public DocumentModel getDocument() {
			return doc;
		}
		/**
		 * {@inheritDoc}
		 */
		public void init(DocumentModel doc, Timestamp timestamp) {
			this.doc = doc;
			this.timestamp = timestamp;
		}	
		/**
		 * {@inheritDoc}
		 */
		public void siteRemoved(int siteId) {
	
		}
		/**
		 * {@inheritDoc}
		 */
		public void siteAdded(int siteId) {
	
		}	
		/**
		 * {@inheritDoc}
		 */
		public void receiveRequest(Request req) {
		}
		/**
		 * {@inheritDoc}
		 */
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
		/**
		 * {@inheritDoc}
		 */
		public Request undo() {
			return null;
		}
		/**
		 * {@inheritDoc}
		 */
		public Request redo() {
			return null;
		}
	}
		
}
