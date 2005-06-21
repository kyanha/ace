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
 *
 */
public class JupiterStringScenarioTest extends TestCase {

	private static Logger LOG = Logger.getLogger(JupiterStringScenarioTest.class);
	
	/**
	 * This is a test scenario which uses strings instead of characters and the 
	 * split operation, because of an insert operation made inside a delete operation.
	 * 
	 * @throws Exception
	 */
	public void testSplitOperation() throws Exception {
		final String INITIAL = "0123456789";
		final String FINAL   = "0ABCD789";
		
		/** initialize system **/
		JupiterServer server = createServer();
		TestNetService[] net = new TestNetService[] {
								new TestNetService(),
								new TestNetService()
								};
		ClientProxy[] proxies = new ClientProxy[2];
		proxies[0] = server.addClient(net[0]); //belongs to site 1
		proxies[1] = server.addClient(net[1]); //belongs to site 2
		
		DefaultRequestEngine eng1 = new DefaultRequestEngine(
				createClient(proxies[0].getSiteId(), INITIAL));
		DefaultRequestEngine eng2 = new DefaultRequestEngine(
				createClient(proxies[1].getSiteId(), INITIAL));
		SynchronizedQueue queue1 = eng1.getOutgoingRequestBuffer();
		SynchronizedQueue queue2 = eng2.getOutgoingRequestBuffer();
		
		//generate requests at site 1
		Operation op = new DeleteOperation(1, "123456");
		eng1.generateRequest(op);
		
		//generate requests at site 2
		op = new InsertOperation(2, "ABCD");
		eng2.generateRequest(op);
						
//		receive and distribute operations at proxy for site 1 
		proxies[0].receiveRequest((Request)queue1.get());
		Request r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		
//		receive and distribute operations at proxy for site 2  
		proxies[1].receiveRequest((Request)queue2.get());
		Request r2 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r2);
		
		Thread.sleep(500);

		/** analyze results **/
		String contentSite1 = ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		String contentSite2 = ((TestDocumentModel)eng2.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		LOG.info(contentSite1 + " == " + contentSite2);
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
	}
	
	/**
	 * This test is a scenario with 5 participating sites. Each site either inserts or 
	 * deletes a string concurrently.
	 * 
	 * Initial string:	"0123456789"
	 * site 1: Delete       "34567"				Delete(3, '34567')
	 * site 2: Insert      "ABC"   				Insert(2, 'ABC')
	 * site 3: Insert         "DEF"				Insert(5, 'DEF')
	 * site 4: Insert	       "GHI"				Insert(7, 'GHI')
	 * site 5: Delete		  "678"				Delete(6, '678')
	 * 
	 * Final string: 	"01ABC2DEFGHI9"  
	 * 
	 * @throws Exception
	 */
	public void testFullScenario01() throws Exception {
		final String INITIAL = "0123456789";
		final String FINAL   = "01ABC2DEFGHI9";
		
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
		
		DefaultRequestEngine eng1 = createClientEngine(proxies[0].getSiteId(), INITIAL);
		DefaultRequestEngine eng2 = createClientEngine(proxies[1].getSiteId(), INITIAL);
		DefaultRequestEngine eng3 = createClientEngine(proxies[2].getSiteId(), INITIAL);
		DefaultRequestEngine eng4 = createClientEngine(proxies[3].getSiteId(), INITIAL);
		DefaultRequestEngine eng5 = createClientEngine(proxies[4].getSiteId(), INITIAL);
		SynchronizedQueue queue1 = eng1.getOutgoingRequestBuffer();
		SynchronizedQueue queue2 = eng2.getOutgoingRequestBuffer();
		SynchronizedQueue queue3 = eng3.getOutgoingRequestBuffer();
		SynchronizedQueue queue4 = eng4.getOutgoingRequestBuffer();
		SynchronizedQueue queue5 = eng5.getOutgoingRequestBuffer();
		
		/** concurrent step 1 **/
		//generate requests at site 1
		Operation op1 = new DeleteOperation(3, "34567");
		eng1.generateRequest(op1);
		
		//generate requests at site 2
		Operation op2 = new InsertOperation(2, "ABC");
		eng2.generateRequest(op2);

		//generate requests at site 3
		Operation op3 = new InsertOperation(5, "DEF");
		eng3.generateRequest(op3);
		
		//generate requests at site 4
		Operation op4 = new InsertOperation(7, "GHI");
		eng4.generateRequest(op4);
		
		//generate requests at site 5
		Operation op5 = new DeleteOperation(6, "678");
		eng5.generateRequest(op5);
		
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
		
		//receive and distribute operations at proxy for site 3
		proxies[2].receiveRequest((Request)queue3.get());
		Request r3 = (Request)net[0].getRequests().remove(0);
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
		
		//receive and distribute operations at proxy for site 5
		proxies[4].receiveRequest((Request)queue5.get());
		Request r5 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r5);
		r5 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r5);
		r5 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r5);
		r5 = (Request)net[3].getRequests().remove(0);
		eng4.receiveRequest(r5);
		
		/** end of concurrent step 1 **/
		Thread.sleep(500);
		
		/** analyze results **/
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
		//test the convergence preservation
		assertTrue(	contentSite1.equals(contentSite2) );
		assertTrue(	contentSite1.equals(contentSite3) );
		assertTrue(	contentSite1.equals(contentSite4) );
		assertTrue(	contentSite1.equals(contentSite5) );
		LOG.info("'"+contentSite1+"'");
		//test the user intention preservation
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
		assertEquals(FINAL, contentSite3);
		assertEquals(FINAL, contentSite4);
		assertEquals(FINAL, contentSite5);
	}
	
	/**
	 * This is the same test as in {@link JupiterCharScenarioTest#testFullScenario1()}
	 * but uses strings instead of characters.
	 * 
	 * Initial string: 				"I thinken,therethus i worse"
	 *
	 * After 1. concurrent action: 	"I think,therefore  "  
	 *
	 * After 2. concurrent action:	"I think, therefore II amI "
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
	 * @throws Exception
	 */ 	
	public void testFullScenario02() throws Exception {
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
		
		DefaultRequestEngine eng1 = createClientEngine(proxies[0].getSiteId(), INITIAL);
		DefaultRequestEngine eng2 = createClientEngine(proxies[1].getSiteId(), INITIAL);
		DefaultRequestEngine eng3 = createClientEngine(proxies[2].getSiteId(), INITIAL);
		DefaultRequestEngine eng4 = createClientEngine(proxies[3].getSiteId(), INITIAL);
		DefaultRequestEngine eng5 = createClientEngine(proxies[4].getSiteId(), INITIAL);
		SynchronizedQueue queue1 = eng1.getOutgoingRequestBuffer();
		SynchronizedQueue queue2 = eng2.getOutgoingRequestBuffer();
		SynchronizedQueue queue3 = eng3.getOutgoingRequestBuffer();
		SynchronizedQueue queue4 = eng4.getOutgoingRequestBuffer();
		SynchronizedQueue queue5 = eng5.getOutgoingRequestBuffer();
		
		/** concurrent step 1 **/
		//generate requests at site 1
		Operation op1 = new DeleteOperation(7, "en");
		eng1.generateRequest(op1);
		
		//generate requests at site 2
		Operation op2 = new DeleteOperation(20, "i");
		eng2.generateRequest(op2);

		//generate requests at site 3
		Operation op3 = new InsertOperation(15, "fore");
		eng3.generateRequest(op3);
		
		//generate requests at site 4
		Operation op4 = new DeleteOperation(7, "en");
		eng4.generateRequest(op4);
		
		//generate requests at site 5
		Operation op5 = new DeleteOperation(15, "thus");
		Operation op6 = new DeleteOperation(18, "worse");
		eng5.generateRequest(op5);
		eng5.generateRequest(op6);
		
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
		
		//receive and distribute operations at proxy for site 3
		proxies[2].receiveRequest((Request)queue3.get());
		Request r3 = (Request)net[0].getRequests().remove(0);
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
		
		//receive and distribute operations at proxy for site 5  
		proxies[4].receiveRequest((Request)queue5.get());
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
		
		/** end of concurrent step 1 **/
		Thread.sleep(500);
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng2.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng3.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng4.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		LOG.info(((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText() + " == " +
				((TestDocumentModel)eng5.getQueueHandler().getAlgorithm()
						.getDocument()).getText());
		assertTrue(	((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText().equals(((TestDocumentModel)eng5.getQueueHandler().getAlgorithm()
				.getDocument()).getText()));
		
		
		/** concurrent step 2 **/
		//generate requests at site 1
		Operation op7 = new InsertOperation(8, " ");
		eng1.generateRequest(op7);
		
		//generate requests at site 2
		Operation op8 = new InsertOperation(18, "I");
		eng2.generateRequest(op8);
		
//		generate requests at site 3
		Operation op9 = new InsertOperation(18, "I");
		eng3.generateRequest(op9);
		
//		generate requests at site 5
		Operation op10 = new InsertOperation(18, "I am");
		eng5.generateRequest(op10);
		
		
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
		Thread.sleep(500);
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
		
		/** concurrent step 3 **/
		//generate requests at site 1
		Operation op11 = new DeleteOperation(24, "I");
		eng1.generateRequest(op11);
		
//		generate requests at site 4
		Operation op12 = new DeleteOperation(20, "I");
		eng4.generateRequest(op12);
		
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
		Thread.sleep(500);
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
 		
		/** concurrent step 4 **/
//		generate requests at site 5
		Operation op13 = new InsertOperation(23, "!");
		eng5.generateRequest(op13);
		
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
		Thread.sleep(500);
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
		
		/** analyze results **/
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
		LOG.info("'"+contentSite1+"'");
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
		assertEquals(FINAL, contentSite3);
		assertEquals(FINAL, contentSite4);
		assertEquals(FINAL, contentSite5);
	}
	
	/**
	 * This is the same test as in {@link JupiterCharScenarioTest#testScenario()}
	 * but uses strings for the operations (insert/delete) instead of characters.
	 * 
	 * @throws Exception
	 */
	public void testScenario() throws Exception {
		final String INITIAL = "";
		final String FINAL   = "II amI";
		
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
		
		
		DefaultRequestEngine eng1 = new DefaultRequestEngine(
				createClient(proxies[0].getSiteId(), INITIAL));
		DefaultRequestEngine eng2 = new DefaultRequestEngine(
				createClient(proxies[1].getSiteId(), INITIAL));
		DefaultRequestEngine eng3 = new DefaultRequestEngine(
				createClient(proxies[2].getSiteId(), INITIAL));
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
		op = new InsertOperation(0, "I am");
		eng3.generateRequest(op);

		//TODO: if you change the order in which the operations are received at
		//the server, the final string may look different:
		//#1: site 3,2,1 results in string: "I amII"
		//#2: site 1,2,3 results in string: "III am"
		//#3: site 1,3,2 results in string: "II amI"
		
//		receive and distribute operations at proxy for site 1
		LOG.info(">>> 1.pr recv");
		proxies[0].receiveRequest((Request)queue1.get());
		Request r1 = (Request)net[1].getRequests().remove(0);
		LOG.info(">>> 2.cl recv");
		eng2.receiveRequest(r1);
		r1 = (Request)net[2].getRequests().remove(0);
		LOG.info(">>> 3.cl recv");
		eng3.receiveRequest(r1);
		
//		receive and distribute operations at proxy for site 3  
		LOG.info(">>> 3.pr recv");
		proxies[2].receiveRequest((Request)queue3.get());
		Request r3 = (Request)net[0].getRequests().remove(0);
		LOG.info(">>> 1.cl recv");
		eng1.receiveRequest(r3);
		r3 = (Request)net[1].getRequests().remove(0);
		LOG.info(">>> 2.cl recv");
		eng2.receiveRequest(r3);
		
//		receive and distribute operations at proxy for site 2
		LOG.info(">>> 2.pr recv");
		proxies[1].receiveRequest((Request)queue2.get());
		Request r2 = (Request)net[0].getRequests().remove(0);
		LOG.info(">>> 1.cl recv");
		eng1.receiveRequest(r2);
		r2 = (Request)net[2].getRequests().remove(0);
		LOG.info(">>> 3.cl recv");
		eng3.receiveRequest(r2);
		
		Thread.sleep(500);

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
	
	private DefaultRequestEngine createClientEngine(int siteId, String content) {
		return new DefaultRequestEngine(createClient(siteId, content));
	}
	
	private Jupiter createClient(int siteId, String initialDocContent) {
		return new Jupiter(new GOTOInclusionTransformation(),
							new TestDocumentModel(siteId, initialDocContent), 
							siteId, true);
	}
	
	private JupiterServer createServer() {
		return new JupiterServer();
	}
	
}
