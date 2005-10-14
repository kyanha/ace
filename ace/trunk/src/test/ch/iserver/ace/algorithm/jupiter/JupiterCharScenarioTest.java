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
package ch.iserver.ace.algorithm.jupiter;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.DefaultRequestEngine;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.jupiter.server.ClientProxy;
import ch.iserver.ace.algorithm.jupiter.server.JupiterServer;
import ch.iserver.ace.algorithm.jupiter.server.TestNetService;
import ch.iserver.ace.test.TestDocumentModel;
import ch.iserver.ace.text.DeleteOperation;
import ch.iserver.ace.text.GOTOInclusionTransformation;
import ch.iserver.ace.text.InsertOperation;
import ch.iserver.ace.util.SynchronizedQueue;

/**
 * This class tests the Jupiter algorithm with several
 * different scenarios complementing the scenarios taken
 * from papers and implemented in 
 * {@link ch.iserver.ace.algorithm.jupiter.JupiterAgainstCounterExamplesTest}.
 */
public class JupiterCharScenarioTest extends TestCase {
	
	private static Logger LOG = Logger.getLogger(JupiterCharScenarioTest.class);
	
	/**
	 * This scenario includes an editing session with 5 participating sites.
	 * 
	 * Initial string: 				"I thinken,therethus i worse"
	 *
	 * After 1. concurrent action: 	"I think,therefore  "  
	 *
	 * After 2. concurrent action:	"I think, therefore III am "
	 *
	 * After 3.concurrent action: 	"I think, therefore I am "
	 * 
	 * Final string:					"I think, therefore I am! "
	 *
	 * Timeline:
	 * SITE 	|   STEP 1				|	STEP 2			 | STEP 3	| STEP 4
	 * -------------------------------+---------------------+-----------+-------
	 * 1		| del en					| ins " " after comma | del 3. I	|
	 * 2		| del i					| ins I				 |			|
	 * 3		| ins fore				| ins I				 |			|
	 * 4		| del en					| 					 | del 2. I	|
	 * 5		| del thus, del worse		| ins "I am"			 |			| ins !
	 *  
	 * 
	 * @throws Exception
	 */
	public void testFullScenario1() throws Exception {
		final String INITIAL = "I thinken,therethus i worse";
		final String FINAL   = "I think, therefore I am! ";
		
		/** initialize system **/
		JupiterServer server = createServer();
		TestNetService[] net = new TestNetService[] {
								new TestNetService(),
								new TestNetService(),
								new TestNetService(),
								new TestNetService(),
								new TestNetService() };
		ClientProxy[] proxies = new ClientProxy[5];
		proxies[0] = server.addClient(net[0]); //belongs to site 1
		proxies[1] = server.addClient(net[1]); //belongs to site 2
		proxies[2] = server.addClient(net[2]); //belongs to site 3
		proxies[3] = server.addClient(net[3]); //belongs to site 4
		proxies[4] = server.addClient(net[4]); //belongs to site 5	
		
		DefaultRequestEngine eng1 = createClientEngine(18, proxies[0].getSiteId(), INITIAL);
		DefaultRequestEngine eng2 = createClientEngine(18, proxies[1].getSiteId(), INITIAL);
		DefaultRequestEngine eng3 = createClientEngine(18, proxies[2].getSiteId(), INITIAL);
		DefaultRequestEngine eng4 = createClientEngine(18, proxies[3].getSiteId(), INITIAL);
		DefaultRequestEngine eng5 = createClientEngine(18, proxies[4].getSiteId(), INITIAL);
		SynchronizedQueue queue1 = eng1.getOutgoingRequestBuffer();
		SynchronizedQueue queue2 = eng2.getOutgoingRequestBuffer();
		SynchronizedQueue queue3 = eng3.getOutgoingRequestBuffer();
		SynchronizedQueue queue4 = eng4.getOutgoingRequestBuffer();
		SynchronizedQueue queue5 = eng5.getOutgoingRequestBuffer();
		
		/** concurrent step 1 **/
		//generate requests at site 1
		Operation op1 = new DeleteOperation(7, "e");
		Operation op2 = new DeleteOperation(7, "n");
		eng1.generateRequest(op1);
		eng1.generateRequest(op2);
		
		//generate requests at site 2
		Operation op3 = new DeleteOperation(20, "i");
		eng2.generateRequest(op3);

		//generate requests at site 3
		Operation op4 = new InsertOperation(15, "f");
		Operation op5 = new InsertOperation(16, "o");
		Operation op6 = new InsertOperation(17, "r");
		Operation op7 = new InsertOperation(18, "e");
		eng3.generateRequest(op4);
		eng3.generateRequest(op5);
		eng3.generateRequest(op6);
		eng3.generateRequest(op7);
		
		//generate requests at site 4
		Operation op8 = new DeleteOperation(7, "e");
		Operation op9 = new DeleteOperation(7, "n");
		eng4.generateRequest(op8);
		eng4.generateRequest(op9);
		
		//generate requests at site 5
		Operation op10 = new DeleteOperation(15, "t");
		Operation op11 = new DeleteOperation(15, "h");
		Operation op12 = new DeleteOperation(15, "u");
		Operation op13 = new DeleteOperation(15, "s");
		Operation op14 = new DeleteOperation(22, "e");
		Operation op15 = new DeleteOperation(21, "s");
		Operation op16 = new DeleteOperation(20, "r");
		Operation op17 = new DeleteOperation(19, "o");
		Operation op18 = new DeleteOperation(18, "w");
		eng5.generateRequest(op10);
		eng5.generateRequest(op11);
		eng5.generateRequest(op12);
		eng5.generateRequest(op13);
		eng5.generateRequest(op14);
		eng5.generateRequest(op15);
		eng5.generateRequest(op16);
		eng5.generateRequest(op17);
		eng5.generateRequest(op18);
		
		//receive and distribute operations at proxy for site 1 
		proxies[0].receiveRequest((Request)queue1.get());
		Request r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		r1 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r1);
		r1 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r1);
		r1 = (Request)net[4].getRequests().remove(0);
		eng5.receiveRequest(r1);
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		r1 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r1);
		r1 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r1);
		r1 = (Request)net[4].getRequests().remove(0);
		eng5.receiveRequest(r1);
		
		
		//receive and distribute operations at proxy for site 2  
		proxies[1].receiveRequest((Request)queue2.get());
		Request r2 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r2);
		r2 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r2);
		r2 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r2);
		r2 = (Request)net[4].getRequests().remove(0);
		eng5.receiveRequest(r2);
		
		//receive and distribute operations at proxy for site 4
		proxies[2].receiveRequest((Request)queue3.get());
		Request r3 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r3);
		r3 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r3);
		r3 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r3);
		r3 = (Request)net[4].getRequests().remove(0);
		eng5.receiveRequest(r3);
		proxies[2].receiveRequest((Request)queue3.get());
		r3 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r3);
		r3 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r3);
		r3 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r3);
		r3 = (Request)net[4].getRequests().remove(0);
		eng5.receiveRequest(r3);
		proxies[2].receiveRequest((Request)queue3.get());
		r3 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r3);
		r3 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r3);
		r3 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r3);
		r3 = (Request)net[4].getRequests().remove(0);
		eng5.receiveRequest(r3);
		proxies[2].receiveRequest((Request)queue3.get());
		r3 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r3);
		r3 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r3);
		r3 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r3);
		r3 = (Request)net[4].getRequests().remove(0);
		eng5.receiveRequest(r3);
		
		//receive and distribute operations at proxy for site 4  
		proxies[3].receiveRequest((Request)queue4.get());
		Request r4 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r4);
		r4 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r4);
		r4 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r4);
		r4 = (Request)net[4].getRequests().remove(0);
		eng5.receiveRequest(r4);
		proxies[3].receiveRequest((Request)queue4.get());
		r4 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r4);
		r4 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r4);
		r4 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r4);
		r4 = (Request)net[4].getRequests().remove(0);
		eng5.receiveRequest(r4);
		
		//receive and distribute operations at proxy for site
		LOG.info("********** receive at site 5");
		proxies[4].receiveRequest((Request)queue5.get());
		LOG.info("---------> get request for site 0");
		Request r5 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r5);
		r5 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r5);
		r5 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r5);
		r5 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r5);
		proxies[4].receiveRequest((Request)queue5.get());
		r5 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r5);
		r5 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r5);
		r5 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r5);
		r5 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r5);
		proxies[4].receiveRequest((Request)queue5.get());
		r5 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r5);
		r5 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r5);
		r5 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r5);
		r5 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r5);
		proxies[4].receiveRequest((Request)queue5.get());
		r5 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r5);
		r5 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r5);
		r5 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r5);
		r5 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r5);
		proxies[4].receiveRequest((Request)queue5.get());
		r5 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r5);
		r5 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r5);
		r5 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r5);
		r5 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r5);
		proxies[4].receiveRequest((Request)queue5.get());
		r5 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r5);
		r5 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r5);
		r5 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r5);
		r5 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r5);
		proxies[4].receiveRequest((Request)queue5.get());
		r5 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r5);
		r5 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r5);
		r5 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r5);
		r5 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r5);
		proxies[4].receiveRequest((Request)queue5.get());
		r5 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r5);
		r5 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r5);
		r5 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r5);
		r5 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r5);
		proxies[4].receiveRequest((Request)queue5.get());
		r5 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r5);
		r5 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r5);
		r5 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r5);
		r5 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r5);
		
		/** end of concurrent step 1 **/
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng2.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng3.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng4.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng5.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		
		((DelegateTestJupiter)eng1.getQueueHandler().getAlgorithm()).setExpectedOperations(7);
		((DelegateTestJupiter)eng2.getQueueHandler().getAlgorithm()).setExpectedOperations(7);
		((DelegateTestJupiter)eng3.getQueueHandler().getAlgorithm()).setExpectedOperations(7);
		((DelegateTestJupiter)eng4.getQueueHandler().getAlgorithm()).setExpectedOperations(7);
		((DelegateTestJupiter)eng5.getQueueHandler().getAlgorithm()).setExpectedOperations(7);
		
		/** concurrent step 2 **/
		//generate requests at site 1
		Operation op19 = new InsertOperation(8, " ");
		eng1.generateRequest(op19);
		
		//generate requests at site 2
		Operation op20 = new InsertOperation(18, "I");
		eng2.generateRequest(op20);
		
//		generate requests at site 3
		Operation op21 = new InsertOperation(18, "I");
		eng3.generateRequest(op21);
		
//		generate requests at site 5
		Operation op22 = new InsertOperation(18, "I");
		Operation op23 = new InsertOperation(19, " ");
		Operation op24 = new InsertOperation(20, "a");
		Operation op25 = new InsertOperation(21, "m");
		eng5.generateRequest(op22);
		eng5.generateRequest(op23);
		eng5.generateRequest(op24);
		eng5.generateRequest(op25);
		
		
//		receive and distribute operations at proxy for site 3  
		proxies[2].receiveRequest((Request)queue3.get());
		r3 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r3);
		r3 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r3);
		r3 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r3);
		r3 = (Request)net[4].getRequests().remove(0);
		eng5.receiveRequest(r3);
		
//		receive and distribute operations at proxy for site 1 
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		r1 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r1);
		r1 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r1);
		r1 = (Request)net[4].getRequests().remove(0);
		eng5.receiveRequest(r1);
		
//		receive and distribute operations at proxy for site 5  
		proxies[4].receiveRequest((Request)queue5.get());
		r5 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r5);
		r5 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r5);
		r5 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r5);
		r5 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r5);
		proxies[4].receiveRequest((Request)queue5.get());
		r5 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r5);
		r5 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r5);
		r5 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r5);
		r5 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r5);
		proxies[4].receiveRequest((Request)queue5.get());
		r5 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r5);
		r5 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r5);
		r5 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r5);
		r5 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r5);
		proxies[4].receiveRequest((Request)queue5.get());
		r5 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r5);
		r5 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r5);
		r5 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r5);
		r5 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r5);
		
//		receive and distribute operations at proxy for site 2  
		proxies[1].receiveRequest((Request)queue2.get());
		r2 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r2);
		r2 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r2);
		r2 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r2);
		r2 = (Request)net[4].getRequests().remove(0);
		eng5.receiveRequest(r2);
		
		/** end of concurrent step 2 **/
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng2.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng3.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng4.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng5.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		
		((DelegateTestJupiter)eng1.getQueueHandler().getAlgorithm()).setExpectedOperations(2);
		((DelegateTestJupiter)eng2.getQueueHandler().getAlgorithm()).setExpectedOperations(2);
		((DelegateTestJupiter)eng3.getQueueHandler().getAlgorithm()).setExpectedOperations(2);
		((DelegateTestJupiter)eng4.getQueueHandler().getAlgorithm()).setExpectedOperations(2);
		((DelegateTestJupiter)eng5.getQueueHandler().getAlgorithm()).setExpectedOperations(2);
		
		/** concurrent step 3 **/
		//generate requests at site 1
		Operation op26 = new DeleteOperation(21, "I");
		eng1.generateRequest(op26);
		
//		generate requests at site 4
		Operation op27 = new DeleteOperation(20, "I");
		eng4.generateRequest(op27);
		
//		receive and distribute operations at proxy for site 1 
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		r1 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r1);
		r1 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r1);
		r1 = (Request)net[4].getRequests().remove(0);
		eng5.receiveRequest(r1);
		
		//receive and distribute operations at proxy for site 4  
		proxies[3].receiveRequest((Request)queue4.get());
		r4 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r4);
		r4 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r4);
		r4 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r4);
		r4 = (Request)net[4].getRequests().remove(0);
		eng5.receiveRequest(r4);
		
		/** end of concurrent step 3 **/
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng2.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng3.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng4.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng5.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		
		((DelegateTestJupiter)eng1.getQueueHandler().getAlgorithm()).setExpectedOperations(1);
		((DelegateTestJupiter)eng2.getQueueHandler().getAlgorithm()).setExpectedOperations(1);
		((DelegateTestJupiter)eng3.getQueueHandler().getAlgorithm()).setExpectedOperations(1);
		((DelegateTestJupiter)eng4.getQueueHandler().getAlgorithm()).setExpectedOperations(1);
		((DelegateTestJupiter)eng5.getQueueHandler().getAlgorithm()).setExpectedOperations(1);
 		
		/** concurrent step 4 **/
//		generate requests at site 5
		Operation op28 = new InsertOperation(23, "!");
		eng5.generateRequest(op28);
		
//		receive and distribute operations at proxy for site 5  
		proxies[4].receiveRequest((Request)queue5.get());
		r5 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r5);
		r5 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r5);
		r5 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r5);
		r5 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r5);
 		
		/** end of concurrent step 4 **/
		String contentSite1 = ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		String contentSite2 = ((TestDocumentModel)eng2.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		String contentSite3 = ((TestDocumentModel)eng3.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		String contentSite4= ((TestDocumentModel)eng4.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		String contentSite5 = ((TestDocumentModel)eng5.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		assertTrue(	contentSite1.equals(contentSite2) );
		assertTrue(	contentSite1.equals(contentSite3) );
		assertTrue(	contentSite1.equals(contentSite4) );
		assertTrue(	contentSite1.equals(contentSite5) );
		
		/** analyze results **/
		LOG.info("'"+contentSite1+"'");
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
		assertEquals(FINAL, contentSite3);
		assertEquals(FINAL, contentSite4);
		assertEquals(FINAL, contentSite5);
	}
	
	/**
	 * Small scenario with 3 sites.
	 * site 1: ins(0, 'I')
	 * site 2: ins(0, 'I')
	 * site 3: ins(0, 'I'), ins(1, ' '), ins(2, 'a'), ins(3, 'm')
	 * 
	 * @throws Exception
	 */
	public void testScenario() throws Exception {
		final String INITIAL = "";
		final String FINAL   = "III am";
		
		/** initialize system **/
		JupiterServer server = createServer();
		TestNetService[] net = new TestNetService[] {
								new TestNetService(),
								new TestNetService(),
								new TestNetService() };
		ClientProxy[] proxies = new ClientProxy[3];
		proxies[0] = server.addClient(net[0]); //belongs to site 1
		proxies[1] = server.addClient(net[1]); //belongs to site 2
		proxies[2] = server.addClient(net[2]); //belongs to site 3
		
		DefaultRequestEngine eng1 = createClientEngine(6, proxies[0].getSiteId(), INITIAL);
		DefaultRequestEngine eng2 = createClientEngine(6, proxies[1].getSiteId(), INITIAL);
		DefaultRequestEngine eng3 = createClientEngine(6, proxies[2].getSiteId(), INITIAL);
		SynchronizedQueue queue1 = eng1.getOutgoingRequestBuffer();
		SynchronizedQueue queue2 = eng2.getOutgoingRequestBuffer();
		SynchronizedQueue queue3 = eng3.getOutgoingRequestBuffer();
		
		//generate requests at site 1
		Operation op = new InsertOperation(0, "I");
		eng1.generateRequest(op);
		
		//generate requests at site 2
		op = new InsertOperation(0, "I");
		eng2.generateRequest(op);
		
//		generate requests at site 3
		Operation op22 = new InsertOperation(0, "I");
		Operation op23 = new InsertOperation(1, " ");
		Operation op24 = new InsertOperation(2, "a");
		Operation op25 = new InsertOperation(3, "m");
		eng3.generateRequest(op22);
		eng3.generateRequest(op23);
		eng3.generateRequest(op24);
		eng3.generateRequest(op25);
		
		
//		receive and distribute operations at proxy for site 3  
		proxies[2].receiveRequest((Request)queue3.get());
		Request r3 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r3);
		r3 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r3);
		proxies[2].receiveRequest((Request)queue3.get());
		r3 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r3);
		r3 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r3);
		proxies[2].receiveRequest((Request)queue3.get());
		r3 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r3);
		r3 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r3);
		proxies[2].receiveRequest((Request)queue3.get());
		r3 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r3);
		r3 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r3);
		
//		receive and distribute operations at proxy for site 1 
		proxies[0].receiveRequest((Request)queue1.get());
		Request r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		r1 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r1);
		
//		receive and distribute operations at proxy for site 2  
		proxies[1].receiveRequest((Request)queue2.get());
		Request r2 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r2);
		r2 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r2);

		/** analyze results **/
		String contentSite1 = ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		String contentSite2 = ((TestDocumentModel)eng2.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		String contentSite3 = ((TestDocumentModel)eng3.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		LOG.info(contentSite1 + " == " + contentSite2 + " == " + contentSite3);
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
		assertEquals(FINAL, contentSite3);
	}
	
	private DefaultRequestEngine createClientEngine(int expectedOps, int siteId, String content) {
		return new DefaultRequestEngine(createClient(expectedOps, siteId, content));
	}
	
	private DelegateTestJupiter createClient(int expectedOps, int siteId, String initialDocContent) {
		DelegateTestJupiter j = new DelegateTestJupiter(new GOTOInclusionTransformation(),
							new TestDocumentModel(siteId, initialDocContent), 
							siteId, true);
		j.setExpectedOperations(expectedOps);
		return j;
	}
	
	private JupiterServer createServer() {
		return new JupiterServer();
	}

}
