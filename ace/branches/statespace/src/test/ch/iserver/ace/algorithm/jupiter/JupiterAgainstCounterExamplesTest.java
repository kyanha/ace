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

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentModel;
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

/**
 * This class tests the Jupiter algorithm implementation of ACE against
 * several counter-examples where other OT algorithms failed. The examples
 * are taken from several papers, especially the two papers from Imine et al.
 * 
 * Yet all tests are done with only character wise transformations. 
 */
public class JupiterAgainstCounterExamplesTest extends TestCase {
	
	private static Logger LOG = Logger.getLogger(JupiterAgainstCounterExamplesTest.class);
	
	/**
	 * This example is taken from ecscw03.pdf figure 4/5. 
	 * However, this example forces the transformation functions 
	 * of Ressels adOPTed algorithm and the ones from Sun et al. (GOTO, 1998) 
	 * to violate TP 2. 
	 * 
	 * @throws Exception
	 */
	public void testCounterexample() throws Exception {
		final String INITIAL = "abc";
		final String FINAL   = "axyc";
		
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
		
		Jupiter site1 = createClient(proxies[0].getSiteId(), INITIAL);
		Jupiter site2 = createClient(proxies[1].getSiteId(), INITIAL);
		Jupiter site3 = createClient(proxies[2].getSiteId(), INITIAL);
		
		InsertOperation op1 = new InsertOperation(1, "x");
		DeleteOperation op2 = new DeleteOperation(1, "b");
		InsertOperation op3 = new InsertOperation(2, "y");
		
		/** start scenario **/
		Request req2 = site1.generateRequest(op2);
		Request req3 = site2.generateRequest(op3);
		Request req1 = site3.generateRequest(op1);
		
		//send the operations op2, op3 and op1 (in this order) to the server
		proxies[0].receiveRequest(req2);
		proxies[1].receiveRequest(req3);
		proxies[2].receiveRequest(req1);
		
		//First of all, op2 is integrated on site 2
		assertEquals(2, net[1].getRequests().size());
		req2 = (Request)net[1].getRequests().remove(0); 	//get op from server
		site2.receiveRequest(req2);  						//pass op to client
		
		//next, integrate op3 on site 1
		assertEquals(2, net[0].getRequests().size());
		req3 = (Request)net[0].getRequests().remove(0);
		site1.receiveRequest(req3);
		
		//op1 is integrated on site 1
		assertEquals(1, net[0].getRequests().size());
		req1 = (Request)net[0].getRequests().remove(0);
		site1.receiveRequest(req1);
		
		//finally, op1 is integrated on site 2
		assertEquals(1, net[1].getRequests().size());
		req1 = (Request)net[1].getRequests().remove(0);
		site2.receiveRequest(req1);
		
		/** analyze results **/
		//lets see if sites 1 and 2 converge and if their document
		//contents equals the expected content.
		String contentSite1 = ((TestDocumentModel)site1.getDocument()).getText();
		String contentSite2 = ((TestDocumentModel)site2.getDocument()).getText();
		LOG.info(contentSite1 + " == " + contentSite2);
		assertEquals(contentSite1, contentSite2);
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
		
		
		//some additional tests
		assertEquals(2, net[2].getRequests().size());
		//here we apply op2 and op3 also to site 3 and compare
		//the document content with the other sites. However, this is 
		//not done in the original example.
		req2 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(req2);
		req3 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(req3);
		String contentSite3 = ((TestDocumentModel)site3.getDocument()).getText();
		LOG.info(contentSite3);
		assertEquals(FINAL, contentSite3);
	}
	
	/**
	 * This is the same test as in {@link JupiterAgainstCounterExamplesTest#testCounterexample()}
	 * but without the main classes that constitute the Jupiter server, namely {@link JupiterServer}
	 * and {@link ch.iserver.ace.algorithm.jupiter.server.RequestSerializer}. These are circumvented
	 * by having the logic of distributing the requests in this test method instead of in
	 * {@link ch.iserver.ace.algorithm.jupiter.server.RequestSerializer}.
	 * 
	 * @throws Exception
	 */
	public void testCounterexampleWithoutSerializer() throws Exception {
		final String INITIAL = "abc";
		final String FINAL   = "axyc";

		// sites
		SiteConnection[] sites = new SiteConnection[] {
				new SiteConnection(1, INITIAL),
				new SiteConnection(2, INITIAL),
				new SiteConnection(3, INITIAL)
		};
		
		// operations
		InsertOperation op1 = new InsertOperation(1, "x");
		DeleteOperation op2 = new DeleteOperation(1, "b");
		InsertOperation op3 = new InsertOperation(2, "y");
		
		// generate requests concurrently
		LOG.info("--> s1.cl genr r2");
		Request r1 = sites[0].client.generateRequest(op2);
		LOG.info("--> s2.cl genr r3");
		Request r2 = sites[1].client.generateRequest(op3);
		LOG.info("--> s3.cl genr r1");
		Request r3 = sites[2].client.generateRequest(op1);
		
		// server-side processing of request r1
		LOG.info("--> s1.pr recv r2: "+r1);
		sites[0].proxy.receiveRequest(r1);
		Operation op = (Operation) sites[0].model.getOperation();
		LOG.info("--> s2.pr genr r2");
		Request req1 = sites[1].proxy.generateRequest(op);
		((JupiterRequest)req1).setSiteId(r1.getSiteId());
		LOG.info("--> s3.pr genr r2");
		Request req2 = sites[2].proxy.generateRequest(op);
		((JupiterRequest)req2).setSiteId(r1.getSiteId());
		LOG.info("--> s2.cl recv r2: "+req1);
		sites[1].client.receiveRequest(req1);
		LOG.info("--> s3.cl recv r2: "+req2);
		sites[2].client.receiveRequest(req2);
		
		// server-side processing of request r2
		LOG.info("--> s2.pr recv r3: "+r2);
		sites[1].proxy.receiveRequest(r2);
		op = (Operation) sites[1].model.getOperation();
		LOG.info("--> s1.pr genr r3");
		req1 = sites[0].proxy.generateRequest(op);
		((JupiterRequest)req1).setSiteId(r2.getSiteId());
		LOG.info("--> s3.pr genr r3");
		req2 = sites[2].proxy.generateRequest(op);
		((JupiterRequest)req2).setSiteId(r2.getSiteId());
		LOG.info("--> s1.cl recv r3: "+req1);
		sites[0].client.receiveRequest(req1);
		LOG.info("--> s3.cl recv r3: "+req2);
		sites[2].client.receiveRequest(req2);
		
		// server-side processing of request r3
		LOG.info("--> s3.pr recv r1: "+r3);
		sites[2].proxy.receiveRequest(r3);
		op = (Operation) sites[2].model.getOperation();
		LOG.info("--> s1.pr genr r1");
		req1 = sites[0].proxy.generateRequest(op);
		((JupiterRequest)req1).setSiteId(r3.getSiteId());
		LOG.info("--> s2.pr genr r1");
		req2 = sites[1].proxy.generateRequest(op);
		((JupiterRequest)req2).setSiteId(r3.getSiteId());
		LOG.info("--> s1.cl recv r1: "+req1);
		sites[0].client.receiveRequest(req1);
		LOG.info("--> s2.cl recv r1: "+req2);
		sites[1].client.receiveRequest(req2);
		
		// verify
		String content = ((TestDocumentModel) sites[0].client.getDocument()).getText();
		assertEquals(FINAL, content);
		content = ((TestDocumentModel) sites[1].client.getDocument()).getText();
		assertEquals(FINAL, content);	
		content = ((TestDocumentModel) sites[2].client.getDocument()).getText();
		assertEquals(FINAL, content);
	}
	
	private Jupiter createProxy(DocumentModel model, int siteId) {
		return new Jupiter(new GOTOInclusionTransformation(), model, siteId, false);
	}
	
	private class SiteConnection {
		private Jupiter client;
		private Jupiter proxy;
		private DebugExtractDocumentModel model;
		
		private SiteConnection(int siteId, String initial) {
			client = createClient(siteId, initial);
			model  = new DebugExtractDocumentModel(siteId);
			proxy  = createProxy(model, siteId);
		}
	}
	
	/**
	 * This is a helper class for debugging with the document model implementation.
	 */
	private static class DebugExtractDocumentModel implements DocumentModel {
		private int siteId;
		private Operation operation;
		
		/**
		 * Class constructor.
		 * 
		 * @param siteId the site id
		 */
		public DebugExtractDocumentModel(int siteId) {
			this.siteId = siteId;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public void apply(Operation operation) {
			LOG.info("proxyDoc.apply(" + operation+")");
			this.operation = operation;
		}
		
		/**
		 * Returns the latest operation applied.
		 * 
		 * @return the latest operation applied
		 */
		public Operation getOperation() {
			return operation;
		}
	}
	
	/**
	 * This example is taken from RR-5188.pdf figure 3 and is called 
	 * the C2 puzzle P1. Note that this example caused the following
	 * algorithms and its transformation functions respectively to violate
	 * TP 2 (at least these are mentioned): dOPT, adOPTed and GOTO 
	 * (see references [1], [9] and [15] in the paper).
	 * 
	 * @throws Exception
	 */
	public void testC2puzzleP1() throws Exception {
		final String INITIAL = "core";
		final String FINAL   = "coffe";
		
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
		
		Jupiter site1 = createClient(proxies[0].getSiteId(), INITIAL);
		Jupiter site2 = createClient(proxies[1].getSiteId(), INITIAL);
		Jupiter site3 = createClient(proxies[2].getSiteId(), INITIAL);
		
		InsertOperation op1 = new InsertOperation(3, "f");
		DeleteOperation op2 = new DeleteOperation(2, "r");
		InsertOperation op3 = new InsertOperation(2, "f");
		
		/** start scenario **/
		Request req1 = site1.generateRequest(op1);
		Request req2 = site2.generateRequest(op2);
		Request req3 = site3.generateRequest(op3);
		
		//send the operations op2 and op3 to the server
		proxies[1].receiveRequest(req2);
		proxies[2].receiveRequest(req3);
		
		//First of all, op2 is integrated at site 3
		assertEquals(1, net[2].getRequests().size());
		req2 = (Request)net[2].getRequests().remove(0); 	//get op from server
		site3.receiveRequest(req2);  						//pass op to client
		
		//next, integrate op3 on site 2
		assertEquals(1, net[1].getRequests().size());
		req3 = (Request)net[1].getRequests().remove(0);
		site2.receiveRequest(req3);
		
		//now, op1 from site 1 is sent to the server
		proxies[0].receiveRequest(req1);
		
		//op1 is integrated on site 3
		assertEquals(1, net[2].getRequests().size());
		req1 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(req1);
		
		//finally, op1 is integrated at site 2
		assertEquals(1, net[1].getRequests().size());
		req1 = (Request)net[1].getRequests().remove(0);
		site2.receiveRequest(req1);
		
		/** analyze results **/
		//lets see if sites 2 and 3 converge and if their document
		//contents equals the expected content.
		String contentSite2 = ((TestDocumentModel)site2.getDocument()).getText();
		String contentSite3 = ((TestDocumentModel)site3.getDocument()).getText();
		LOG.info(contentSite2 + " == " + contentSite3);
		assertEquals(contentSite2, contentSite3);
		assertEquals(FINAL, contentSite2);
		assertEquals(FINAL, contentSite3);
		
		//some additional tests
		assertEquals(2, net[0].getRequests().size());
		//here we apply op2 and op3 also to site 1 and compare
		//the document content with the other sites. However, this is 
		//not done in the original example.
		req2 = (Request)net[0].getRequests().remove(0);
		site1.receiveRequest(req2);
		req3 = (Request)net[0].getRequests().remove(0);
		site1.receiveRequest(req3);
		String contentSite1 = ((TestDocumentModel)site1.getDocument()).getText();
		assertEquals(FINAL, contentSite1);
	}
	
	/**
	 * This example is taken from RR-5188.pdf figure 5 and is called 
	 * the C2 puzzle P2. Note that this example caused the following
	 * algorithms and its transformation functions respectively to violate
	 * TP 2 (at least these are mentioned): SOCT2 and SDT. 
	 * (see references [12] and [6] in the paper).
	 * 
	 * Note that it is not possible to completely recreate this example
	 * since Jupiter does not have to satisfy TP2 directly, i.e. with Jupiter
	 * it is not possible that different sites receive requests from other sites
	 * in different order as is the case in this example. Hence operations 3 and 4 arrive
	 * in the same order at sites 2 and 3, respectively.
	 * 
	 * @throws Exception
	 */
	public void testC2puzzleP2() throws Exception {
		final String INITIAL = "abcd";
		final String FINAL   = "acxx";
		
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
		
		Jupiter site1 = createClient(proxies[0].getSiteId(), INITIAL);
		Jupiter site2 = createClient(proxies[1].getSiteId(), INITIAL);
		Jupiter site3 = createClient(proxies[2].getSiteId(), INITIAL);
		Jupiter site4 = createClient(proxies[3].getSiteId(), INITIAL);
		Jupiter site5 = createClient(proxies[4].getSiteId(), INITIAL);
		
		DeleteOperation op1 = new DeleteOperation(1, "b");
		InsertOperation op2 = new InsertOperation(3, "x");
		InsertOperation op3 = new InsertOperation(3, "x");
		DeleteOperation op4 = new DeleteOperation(3, "d");
		
		/** start scenario **/
		Request req1 = site1.generateRequest(op1);
		Request req2 = site1.generateRequest(op2);
		Request req3 = site4.generateRequest(op3);
		Request req4 = site5.generateRequest(op4);
		
		//send the operations op1, op3 and op4 in this order to the server
		proxies[0].receiveRequest(req1);
		proxies[3].receiveRequest(req3);
		proxies[4].receiveRequest(req4);
		proxies[0].receiveRequest(req2);
		
		//op1 is integrated at site 2 and 3
		assertEquals(4, net[1].getRequests().size());
		req1 = (Request)net[1].getRequests().remove(0); 	//get op from server
		site2.receiveRequest(req1);  						//pass op to client
		assertEquals(4, net[2].getRequests().size());
		req1 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(req1);
		
		//op3 is integrated at site 2 and 3
		req3 = (Request)net[1].getRequests().remove(0);
		site2.receiveRequest(req3);
		req3 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(req3);
		
		//op4 is integrated at site 2 and 3
		req4 = (Request)net[1].getRequests().remove(0);
		site2.receiveRequest(req4);
		req4 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(req4);
		
		//op2 is integrated at site 2 and 3
		req2 = (Request)net[1].getRequests().remove(0);
		site2.receiveRequest(req2);
		req2 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(req2);
		
		assertEquals(0, net[1].getRequests().size());
		assertEquals(0, net[2].getRequests().size());
		
		/** analyze results **/
		//lets see if sites 2 and 3 converge and if their document
		//contents equals the expected content.
		String contentSite2 = ((TestDocumentModel)site2.getDocument()).getText();
		String contentSite3 = ((TestDocumentModel)site3.getDocument()).getText();
		LOG.info(contentSite2 + " == " + contentSite3);
		assertEquals(contentSite2, contentSite3);
		assertEquals(FINAL, contentSite2);
		assertEquals(FINAL, contentSite3);
	}
	
	/**
	 * This is the same test as in {@link JupiterAgainstCounterExamplesTest#testC2puzzleP1()	}
	 * but without the main classes that constitute the Jupiter server, basically {@link JupiterServer}
	 * and {@link ch.iserver.ace.algorithm.jupiter.server.RequestSerializer}. These are circumvented
	 * by having the logic of distributing the requests in this test method instead of in
	 * {@link ch.iserver.ace.algorithm.jupiter.server.RequestSerializer}.
	 * 
	 * @throws Exception
	 */
	public void testC2puzzleP2WithoutSerializer() throws Exception {
		final String INITIAL = "abcd";
		final String FINAL   = "acxx";

		// sites
		SiteConnection[] sites = new SiteConnection[] {
				new SiteConnection(1, INITIAL),
				new SiteConnection(2, INITIAL),
				new SiteConnection(3, INITIAL),
				new SiteConnection(4, INITIAL),
				new SiteConnection(5, INITIAL)
		};
		
		DeleteOperation op1 = new DeleteOperation(1, "b");
		InsertOperation op2 = new InsertOperation(3, "x");
		InsertOperation op3 = new InsertOperation(3, "x");
		DeleteOperation op4 = new DeleteOperation(3, "d");
		
		/** start scenario **/
		Request r1 = sites[0].client.generateRequest(op1);
		Request r3 = sites[3].client.generateRequest(op3);
		Request r4 = sites[4].client.generateRequest(op4);
		Request r2 = sites[0].client.generateRequest(op2);
		
		// server-side processing of request r1
		sites[0].proxy.receiveRequest(r1);
		Operation op = (Operation) sites[0].model.getOperation();
		Request req1 = sites[1].proxy.generateRequest(op);
		Request req2 = sites[2].proxy.generateRequest(op);
		Request req3 = sites[3].proxy.generateRequest(op);
		Request req4 = sites[4].proxy.generateRequest(op);
		sites[1].client.receiveRequest(req1);
		sites[2].client.receiveRequest(req2);
		sites[3].client.receiveRequest(req3);
		sites[4].client.receiveRequest(req4);
		
		// server-side processing of request r3
		sites[3].proxy.receiveRequest(r3);
		op = (Operation) sites[3].model.getOperation();
		req3 = sites[0].proxy.generateRequest(op);
		req1 = sites[1].proxy.generateRequest(op);
		req2 = sites[2].proxy.generateRequest(op);
		req4 = sites[4].proxy.generateRequest(op);
		sites[0].client.receiveRequest(req3);
		sites[1].client.receiveRequest(req1);
		sites[2].client.receiveRequest(req2);
		sites[4].client.receiveRequest(req4);
		
		// server-side processing of request r4
		sites[4].proxy.receiveRequest(r4);
		op = (Operation) sites[4].model.getOperation();
		req4 = sites[0].proxy.generateRequest(op);
		req1 = sites[1].proxy.generateRequest(op);
		req2 = sites[2].proxy.generateRequest(op);
		req3 = sites[3].proxy.generateRequest(op);
		sites[0].client.receiveRequest(req4);
		sites[1].client.receiveRequest(req1);
		sites[2].client.receiveRequest(req2);
		sites[3].client.receiveRequest(req3);
		
		// server-side processing of request r2
		LOG.info("r2: "+r2);
		sites[0].proxy.receiveRequest(r2);
		op = (Operation) sites[0].model.getOperation();
		LOG.info("op2: "+op);
		req1 = sites[1].proxy.generateRequest(op);
		LOG.info(req1);
		req2 = sites[2].proxy.generateRequest(op);
		LOG.info(req2);
		req3 = sites[3].proxy.generateRequest(op);
		LOG.info(req3);
		req4 = sites[4].proxy.generateRequest(op);
		LOG.info(req4);
		sites[1].client.receiveRequest(req1);
		sites[2].client.receiveRequest(req2);
		sites[3].client.receiveRequest(req3);
		sites[4].client.receiveRequest(req4);
		
		// verify
		String content = ((TestDocumentModel) sites[1].client.getDocument()).getText();
		assertEquals(FINAL, content);	
		content = ((TestDocumentModel) sites[2].client.getDocument()).getText();
		assertEquals(FINAL, content);
		content = ((TestDocumentModel) sites[0].client.getDocument()).getText();
		assertEquals(FINAL, content);
		content = ((TestDocumentModel) sites[3].client.getDocument()).getText();
		assertEquals(FINAL, content);
		content = ((TestDocumentModel) sites[4].client.getDocument()).getText();
		assertEquals(FINAL, content);
	}
	
	/**
	 * This example is taken from cscw04sdt.pdf figure 1 and is called 
	 * 'a scenario of divergence and ERV (effects relation violation)'. 
	 * It is similar to the examples in figure 4/5 from ecscw03.pdf 
	 * (see {@link #testCounterexample()}).
	 * 
	 * @throws Exception
	 */
	public void testScenarioOfDivergenceAndERV() throws Exception {
		final String INITIAL = "abc";
		final String FINAL   = "a21c";
		
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
		
		Jupiter site1 = createClient(proxies[0].getSiteId(), INITIAL);
		Jupiter site2 = createClient(proxies[1].getSiteId(), INITIAL);
		Jupiter site3 = createClient(proxies[2].getSiteId(), INITIAL);
		
		InsertOperation op1 = new InsertOperation(2, "1");
		InsertOperation op2 = new InsertOperation(1, "2");
		DeleteOperation op3 = new DeleteOperation(1, "b");
		
		/** start scenario **/
		//we generate 3 concurrent operations 
		Request req1 = site1.generateRequest(op1);
		Request req2 = site2.generateRequest(op2);
		Request req3 = site3.generateRequest(op3);
		
		//send the operations op2, op3 and op1 (in this order) to the server
		proxies[1].receiveRequest(req2);
		proxies[2].receiveRequest(req3);
		proxies[0].receiveRequest(req1);
		
		/* The operations arrive at the sites in the following order:
		 * site 1: op2 op3 
		 * site 2: op3 op1
		 * site 3: op2 op1 
		 */
		
		//let the server execute
		Thread.sleep(1000);
		
		//process site 2
		assertEquals(2, net[1].getRequests().size());
		req3 = (Request)net[1].getRequests().remove(0);
		site2.receiveRequest(req3);
		req1 = (Request)net[1].getRequests().remove(0);
		site2.receiveRequest(req1);
		assertEquals(0, net[1].getRequests().size());
		
		//process site 3
		assertEquals(2, net[2].getRequests().size());
		req2 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(req2);
		req1 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(req1);
		assertEquals(0, net[2].getRequests().size());
		
		/** analyze results **/
		//lets see if sites 2 and 3 converge and if their document
		//contents equals the expected content.
		String contentSite2 = ((TestDocumentModel)site2.getDocument()).getText();
		String contentSite3 = ((TestDocumentModel)site3.getDocument()).getText();
		LOG.info(contentSite2 + " == " + contentSite3);
		assertEquals(contentSite2, contentSite3);
		assertEquals(FINAL, contentSite2);
		assertEquals(FINAL, contentSite3);
		
		//process site 1
		/* Note that site 1 is not traced in the original example. 
		 * Nonetheless, we do it to verify that all sites converge. 
		 */
		assertEquals(2, net[0].getRequests().size());
		req2 = (Request)net[0].getRequests().remove(0); 	
		site1.receiveRequest(req2);
		req3 = (Request)net[0].getRequests().remove(0);
		site1.receiveRequest(req3);
		assertEquals(0, net[0].getRequests().size());
		String contentSite1 = ((TestDocumentModel)site1.getDocument()).getText();
		assertEquals(FINAL, contentSite1);
	}
	
	/**
	 * This example is taken from cscw04sdt.pdf figure 12 and is called 
	 * 'A divergence and ERV (effects relation violation) scenario of IMOR'.
	 * It is a tricky counterexample to the transformation functions proposed in
	 * ecscw03.pdf by Imine et al.
	 * 
	 * Note that it is not possible to completely recreate this example with the
	 * Jupiter algorithm since the delivery of operations at all sites happens
	 * in the same order. In this example, operations op2 and op3 arrive in different
	 * order at site 1 and site 4, respectively. We implement it so that operations 
	 * op2 and op3 arrive in the same order at both sites.
	 * 
	 * @throws Exception
	 */
	public void testDivergenceAndERVscenarioOfIMOR() throws Exception {
		final String INITIAL = "0123";
		/* Note that due to the arrival order at the proxies, it happens that
		 * the final string is the expected one. With a different order, i.e. op1
		 * is broadcasted before op5, the final string would be "0abc23" due to the
		 * transformation limit that also depends on the operation arrival order at
		 * the server.
		 */
		final String FINAL   = "0acb23";
		
		/** initialize system **/
		JupiterServer server = createServer();
		TestNetService[] net = new TestNetService[] {
								new TestNetService(),
								new TestNetService(),
								new TestNetService(),
								new TestNetService() };
		ClientProxy[] proxies = new ClientProxy[4];
		proxies[0] = server.addClient(net[0]); //belongs to site 1
		proxies[1] = server.addClient(net[1]); //belongs to site 2
		proxies[2] = server.addClient(net[2]); //belongs to site 3
		proxies[3] = server.addClient(net[3]); //belongs to site 4
		
		
		Jupiter site1 = createClient(proxies[0].getSiteId(), INITIAL);
		Jupiter site2 = createClient(proxies[1].getSiteId(), INITIAL);
		Jupiter site3 = createClient(proxies[2].getSiteId(), INITIAL);
		Jupiter site4 = createClient(proxies[3].getSiteId(), INITIAL);
		
		InsertOperation op1 = new InsertOperation(2, "b");
		DeleteOperation op2 = new DeleteOperation(1, "1");
		InsertOperation op3 = new InsertOperation(2, "c");
		InsertOperation op4 = new InsertOperation(1, "a");
		
		/** start scenario **/
		Request req1 = site1.generateRequest(op1);
		Request req2 = site2.generateRequest(op2);
		Request req4 = site4.generateRequest(op4);
		
		//scenario flow:
		//1. req4 -> proxy, send to and apply at clients 1 + 3
		//2. generate req3 at client 3
		//3. req2 -> proxy, send to and apply at clients 1 + 4
		//4. req3 -> proxy, send to and apply at clients 1 + 4
		//5. req1 -> proxy, send to and apply at client 4
		
		// server-side processing of request req4
		LOG.info("--> s4.pr recv r4: "+req4);
		proxies[3].receiveRequest(req4);
		Thread.sleep(500);
		Request r4 = (Request)net[0].getRequests().remove(0);   
		site1.receiveRequest(r4);
		r4 = (Request)net[1].getRequests().remove(0);
		site2.receiveRequest(r4);
		r4 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(r4);
		
		//generate req3 at client 3
		Request req3 = site3.generateRequest(op3);
		
		//server-side processing of request req2
		LOG.info("--> s2.pr recv r2: "+req2);
		proxies[1].receiveRequest(req2);
		Request r2 = (Request)net[0].getRequests().remove(0);
		site1.receiveRequest(r2);
		r2 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(r2);
		r2 = (Request)net[3].getRequests().remove(0);
		site4.receiveRequest(r2);
		
		//server-side processing of request req3
		LOG.info("--> s3.pr recv r3: "+req3);
		proxies[2].receiveRequest(req3);
		Request r3 = (Request)net[0].getRequests().remove(0);
		LOG.info("--> s1.cl recv r3: "+r3);
		site1.receiveRequest(r3);
		r3 = (Request)net[1].getRequests().remove(0);
		site2.receiveRequest(r3);
		r3 = (Request)net[3].getRequests().remove(0);
		site4.receiveRequest(r3);
		
		//server-side processing of request req1
		LOG.info("--> s1.pr recv r1: "+req1);
		proxies[0].receiveRequest(req1);
		Request r1 = (Request)net[1].getRequests().remove(0);
		site2.receiveRequest(r1);
		r1 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(r1);
		r1 = (Request)net[3].getRequests().remove(0);
		site4.receiveRequest(r1);

		
		/** analyze results **/
		String contentSite1 = ((TestDocumentModel)site1.getDocument()).getText();
		String contentSite2 = ((TestDocumentModel)site2.getDocument()).getText();
		String contentSite3 = ((TestDocumentModel)site3.getDocument()).getText();
		String contentSite4 = ((TestDocumentModel)site4.getDocument()).getText();
		LOG.info(contentSite1 + " == " + contentSite2 + " == " + contentSite3
				+ " == " + contentSite4);
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
		assertEquals(FINAL, contentSite3);
		assertEquals(FINAL, contentSite4);		
	}
	
	/**
	 * This example is taken from 82890036.pdf figure 5 and it is similar to the 
	 * examples in {@link #testScenarioOfDivergenceAndERV()}
	 * and {@link #testCounterexample()}.
	 * 
	 * @throws Exception
	 */
	public void testConsistenceViolation() throws Exception {
		final String INITIAL = "buver";
		final String FINAL   = "buffer";
		
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
		
		Jupiter site1 = createClient(proxies[0].getSiteId(), INITIAL);
		Jupiter site2 = createClient(proxies[1].getSiteId(), INITIAL);
		Jupiter site3 = createClient(proxies[2].getSiteId(), INITIAL);
		
		InsertOperation op1 = new InsertOperation(2, "f");
		DeleteOperation op2 = new DeleteOperation(2, "v");
		InsertOperation op3 = new InsertOperation(3, "f");
		
		/** start scenario **/
		//we generate 3 concurrent operations 
		Request req1 = site1.generateRequest(op1);
		Request req2 = site2.generateRequest(op2);
		Request req3 = site3.generateRequest(op3);
		
		//scenario flow:
		//1. req1 -> proxy -> client 2 + 3
		//2. req2 -> proxy -> client 1 + 3
		//3. req3 -> proxy -> client 1 + 2
		
		// server-side processing of request req1
		proxies[0].receiveRequest(req1);
		Thread.sleep(500);
		Request r1 = (Request)net[1].getRequests().remove(0);   
		site2.receiveRequest(r1);
		r1 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(r1);
		
		// server-side processing of request req2
		proxies[1].receiveRequest(req2);
		Request r2 = (Request)net[0].getRequests().remove(0);   
		site1.receiveRequest(r2);
		r2 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(r2);
		
		// server-side processing of request req3
		proxies[2].receiveRequest(req3);
		Request r3 = (Request)net[0].getRequests().remove(0);   
		site1.receiveRequest(r3);
		r3 = (Request)net[1].getRequests().remove(0);
		site2.receiveRequest(r3);
		
		/** analyze results **/
		String contentSite1 = ((TestDocumentModel)site1.getDocument()).getText();
		String contentSite2 = ((TestDocumentModel)site2.getDocument()).getText();
		String contentSite3 = ((TestDocumentModel)site3.getDocument()).getText();
		LOG.info(contentSite1 + " == " + contentSite2 + " == " + contentSite3);
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
		assertEquals(FINAL, contentSite3);
	}
	
	/**
	 * This test is taken from sun98operational.pdf figure 2 and is called
	 * the 'dOPT puzzle'.
	 *  
	 * @throws Exception
	 */
	public void testdOPTpuzzle2() throws Exception {
		final String INITIAL = "";
		final String FINAL   = "xzy";
		
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

		Jupiter site1 = createClient(proxies[0].getSiteId(), INITIAL);
		Jupiter site2 = createClient(proxies[1].getSiteId(), INITIAL);
		Jupiter site3 = createClient(proxies[2].getSiteId(), INITIAL);
		
		InsertOperation op1 = new InsertOperation(0, "x");
		InsertOperation op2 = new InsertOperation(0, "y");
		InsertOperation op3 = new InsertOperation(0, "z");
		
		/** start scenario **/
		//we generate 3 concurrent operations 
		Request req2 = site2.generateRequest(op2);
		Request req3 = site3.generateRequest(op3);
		
		//scenario flow:
		//1. req3 -> proxy -> client 1 + 2
		//2. generate req1
		//3. req1 -> proxy -> client 2 + 3 
		//4. req2 -> proxy -> client 1 + 3
		
		// server-side processing of request req3
		proxies[2].receiveRequest(req3);
		Thread.sleep(500);
		Request r3 = (Request)net[0].getRequests().remove(0);   
		site1.receiveRequest(r3);
		r3 = (Request)net[1].getRequests().remove(0);
		site2.receiveRequest(r3);
		
		Request req1 = site1.generateRequest(op1);
		
		// server-side processing of request req1
		proxies[0].receiveRequest(req1);
		Request r1 = (Request)net[1].getRequests().remove(0);   
		site2.receiveRequest(r1);
		r1 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(r1);
		
		// server-side processing of request req2
		proxies[1].receiveRequest(req2);
		Request r2 = (Request)net[0].getRequests().remove(0);   
		site1.receiveRequest(r2);
		r2 = (Request)net[2].getRequests().remove(0);
		site3.receiveRequest(r2);
		
		
		/** analyze results **/
		String contentSite1 = ((TestDocumentModel)site1.getDocument()).getText();
		String contentSite2 = ((TestDocumentModel)site2.getDocument()).getText();
		String contentSite3 = ((TestDocumentModel)site3.getDocument()).getText();
		LOG.info(contentSite1 + " == " + contentSite2 + " == " + contentSite3);
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
		assertEquals(FINAL, contentSite3);
	}
	
	/**
	 * This is the same test as in {@link #testdOPTpuzzle2()} but uses
	 * the {@link DefaultRequestEngine} at the client side instead of only
	 * the algorithm {@link Jupiter}.
	 * 
	 * @throws Exception
	 */
	public void testdOPTpuzzle2WithRequestEngine() throws Exception {
		final String INITIAL = "";
		final String FINAL   = "xzy";
		
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
		
		InsertOperation op1 = new InsertOperation(0, "x");
		InsertOperation op2 = new InsertOperation(0, "y");
		InsertOperation op3 = new InsertOperation(0, "z");
		
		/** start scenario **/
		//we generate 3 concurrent operations 
		eng2.generateRequest(op2);
		eng3.generateRequest(op3);
		
		//scenario flow:
		//1. req3 -> proxy -> client 1 + 2
		//2. generate req1
		//3. req1 -> proxy -> client 2 + 3 
		//4. req2 -> proxy -> client 1 + 3
		
		// server-side processing of request req3
		proxies[2].receiveRequest((Request)eng3.getOutgoingRequestBuffer().get());
		Request r3 = (Request)net[0].getRequests().remove(0);   
		eng1.receiveRequest(r3);
		r3 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r3);
		
		eng1.generateRequest(op1);
		
		// server-side processing of request req1
		proxies[0].receiveRequest((Request)eng1.getOutgoingRequestBuffer().get());
		Request r1 = (Request)net[1].getRequests().remove(0);   
		eng2.receiveRequest(r1);
		r1 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r1);
		
		// server-side processing of request req2
		proxies[1].receiveRequest((Request)eng2.getOutgoingRequestBuffer().get());
		Request r2 = (Request)net[0].getRequests().remove(0);   
		eng1.receiveRequest(r2);
		r2 = (Request)net[2].getRequests().remove(0);
		eng3.receiveRequest(r2);
		
		Thread.sleep(1000);
		
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
	
	private Jupiter createClient(int siteId, String initialDocContent) {
		return new Jupiter(new GOTOInclusionTransformation(),
							new TestDocumentModel(siteId, initialDocContent), 
							siteId, true);
	}
	
	private JupiterServer createServer() {
		return new JupiterServer();
	}
}
