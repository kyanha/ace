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
	
	/**
	 * This example is taken from ecscw03.pdf figure 4. 
	 * However, this example forces the transformation functions 
	 * of Ressels adOPTed algorithm to violate TP 2. 
	 * 
	 * @throws Exception
	 */
	public void testWithCounterexampleAgainstResselOT() throws Exception {
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
		
		//send the operations op2 and op3 to the server
		proxies[0].receiveRequest(req2);
		proxies[1].receiveRequest(req3);
		
		//First of all, op2 is integrated on site 2
		assertEquals(1, net[1].getRequests().size());
		req2 = (Request)net[1].getRequests().remove(0); 	//get op from server
		site2.receiveRequest(req2);  						//pass op to client
		
		//next, integrate op3 on site 1
		assertEquals(1, net[0].getRequests().size());
		req3 = (Request)net[0].getRequests().remove(0);
		site1.receiveRequest(req3);
		
		//now, op1 is sent to the server
		proxies[2].receiveRequest(req1);
		
		//op1 is integrated on site 1
		assertEquals(1, net[0].getRequests().size());
		req1 = (Request)net[0].getRequests().remove(0);
		site1.receiveRequest(req1);
		
		//finally, op1 is integrated on site 2
		assertEquals(1, net[1].getRequests().size());
		req1 = (Request)net[1].getRequests().remove(0);
		site2.receiveRequest(req1);
		
		//some additional test
		assertEquals(2, net[2].getRequests().size());
		//here we could apply op2 and op3 also to site 3 and compare
		//with the other site. However, this is not done in the 
		//original example.
		
		/** analyze results **/
		//lets see if sites 1 and 2 converge and if their document
		//contents equals the expected content.
		String contentSite1 = ((TestDocumentModel)site1.getDocument()).getText();
		String contentSite2 = ((TestDocumentModel)site2.getDocument()).getText();
		System.out.println(contentSite1 + " == " + contentSite2);
		assertEquals(contentSite1, contentSite2);
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
	}
	
	private Jupiter createClient(int siteId, String initialDocContent) {
		return new Jupiter(new GOTOInclusionTransformation(),
							new TestDocumentModel(initialDocContent), 
							siteId);
	}
	
	private JupiterServer createServer() {
		return new JupiterServer();
	}

}
