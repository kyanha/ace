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
public class SplitOperationTest extends TestCase {

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
		System.out.println(contentSite1 + " == " + contentSite2);
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
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
