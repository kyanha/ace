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
package ch.iserver.ace.algorithm;

import junit.framework.TestCase;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.algorithm.jupiter.JupiterRequest;
import ch.iserver.ace.algorithm.jupiter.JupiterVectorTime;
import ch.iserver.ace.text.DeleteOperation;
import ch.iserver.ace.text.GOTOInclusionTransformation;
import ch.iserver.ace.text.InsertOperation;
import ch.iserver.ace.util.SynchronizedQueue;

/**
 *
 */
public class DefaultRequestEngineTest extends TestCase {

	private static final int SITE_ID = 1;
	private static final String TEXT = "a";
	private static final int POSITION = 0;
	
	private static final int NUM_REQUESTS = 100;
	private static final int NUM_OPERATIONS = 150;
	
	private DummyDocumentModel doc;
	private Algorithm algo;
	private DefaultRequestEngine engine;
	private SynchronizedQueue outgoing;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		doc = new DummyDocumentModel();
		algo = new Jupiter(new GOTOInclusionTransformation(), doc, SITE_ID);
		engine = new DefaultRequestEngine(algo);
		outgoing = engine.getOutgoingRequestBuffer();
	}
	
	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		engine.getQueueHandler().interrupt();
	}


	public void testGenerateRequest() throws Exception {
		Operation op = new InsertOperation(POSITION, TEXT);
		//let the queue handler go into wait state on queues
		Thread.sleep(1000);
		//pass the operation
		engine.generateRequest(op);
		//wait to let the execution finish
		Thread.sleep(1000);
		Request req = (Request)outgoing.get();
		assertNotNull(req);
		assertEquals(SITE_ID, req.getSiteId());
		assertEquals(new JupiterVectorTime(0,0), (JupiterVectorTime)req.getTimestamp());
		assertEquals(TEXT, ((InsertOperation)req.getOperation()).getText());
		assertEquals(POSITION, ((InsertOperation)req.getOperation()).getPosition());
		
		InsertOperation ins = (InsertOperation)doc.getOperations().remove(0);
		assertEquals(TEXT, ins.getText());
		assertEquals(POSITION, ins.getPosition());
		
		op = new DeleteOperation(POSITION, TEXT);
		engine.generateRequest(op);
		Thread.sleep(1000);
		req = (Request)outgoing.get();
		assertNotNull(req);
		assertEquals(SITE_ID, req.getSiteId());
		assertEquals(new JupiterVectorTime(1,0), (JupiterVectorTime)req.getTimestamp());
		assertEquals(TEXT, ((DeleteOperation)req.getOperation()).getText());
		
		DeleteOperation del = (DeleteOperation)doc.getOperations().remove(0);
		assertEquals(TEXT, del.getText());
	}
	
	public void testReceiveRequest() throws Exception {
		int otherMsgsBefore = ((Jupiter)algo).getVectorTime().getRemoteOperationCount();
		Operation op = new InsertOperation(POSITION, TEXT);
		Request req = new JupiterRequest(SITE_ID+1, new JupiterVectorTime(0,0), op);
		//let the queue handler go into wait state on queues
		Thread.sleep(1000);
		//receive the request
		engine.receiveRequest(req);
		//wait enough time to let the execution finish
		Thread.sleep(1000);
		
		InsertOperation ins = (InsertOperation)doc.getOperations().remove(0);
		assertEquals(TEXT, ins.getText());
		assertEquals(otherMsgsBefore + 1, ((Jupiter)algo).getVectorTime().getRemoteOperationCount());
	}
	
	public void testGenerateRequestReceiveRequestAlternatively() throws Exception {
		Operation[] ops = new Operation[NUM_OPERATIONS];
		for (int i=0; i < NUM_OPERATIONS; i++) {
			ops[i] = new InsertOperation(i, "a");
		}
		Request[] reqs = new Request[NUM_REQUESTS];
		for (int i=0; i < NUM_REQUESTS; i++) {
			reqs[i] = new JupiterRequest(i%10, new JupiterVectorTime(i, i), 
											new InsertOperation(i, "b"));
		}
		
		int cnt = 0;
		while (cnt < NUM_OPERATIONS || cnt < NUM_REQUESTS) {
			if (cnt < NUM_OPERATIONS) {
				engine.generateRequest(ops[cnt]);
			}
			if (cnt < NUM_REQUESTS) {
				engine.receiveRequest(reqs[cnt]);
			}
			cnt++;
		}
		
		int t = 2;
		while (!engine.getLocalOperationBuffer().isEmpty() || 
				!engine.getRemoteRequestBuffer().isEmpty()) {
			System.out.println("sleep "+t+"s "+engine.getLocalOperationBuffer().size()+" "+
					engine.getRemoteRequestBuffer().size());
			Thread.sleep(2000);
			t += 2;
		}
		assertTrue(engine.getLocalOperationBuffer().isEmpty());
		assertTrue(engine.getRemoteRequestBuffer().isEmpty());
		assertEquals(NUM_OPERATIONS+NUM_REQUESTS, doc.getOperations().size());
		assertEquals(NUM_OPERATIONS, ((Jupiter)algo).getVectorTime().getLocalOperationCount());
		assertEquals(NUM_REQUESTS, ((Jupiter)algo).getVectorTime().getRemoteOperationCount());
		System.out.println("Document text:\n"+doc.getText());
	}

}
