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
import ch.iserver.ace.algorithm.RedoOperation;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.UndoOperation;
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
public class JupiterCharUndoTest extends TestCase {

	private static Logger LOG = Logger.getLogger(JupiterCharUndoTest.class);
	
	/**
	 * A simple test with insert-undo-redo, all generated
	 * at one site.
	 * 
	 * @throws Exception
	 */
	public void test01() throws Exception {
		final String INITIAL = "Hllo";
		final String FINAL   = "Hallo";
		
		/** initialize system **/
		JupiterServer server = createServer();
		TestNetService[] net = new TestNetService[] {
								new TestNetService(),
								new TestNetService() };
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
		Operation op = new InsertOperation(1, "a");
		eng1.generateRequest(op);
		op = new UndoOperation();
		eng1.generateRequest(op);
		Thread.sleep(200);
		assertEquals("Hllo", ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText());
		
		op = new RedoOperation();
		eng1.generateRequest(op);
		Thread.sleep(200);
		assertEquals("Hallo", ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText());
		
//		receive and distribute operations at proxy for site 1 
		proxies[0].receiveRequest((Request)queue1.get());
		Request r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		
		Thread.sleep(500);

		/** analyze results **/
		String contentSite1 = ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		String contentSite2 = ((TestDocumentModel)eng2.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		//System.out.println(contentSite1 + " == " + contentSite2);
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
	}
	
	/**
	 * A simple test with insert-insert-undo-undo-redo-insert all generated 
	 * at one site.
	 * 
	 * @throws Exception
	 */
	public void test02() throws Exception {
		final String INITIAL = "Hllo";
		final String FINAL   = "Hallo?";
		
		/** initialize system **/
		JupiterServer server = createServer();
		TestNetService[] net = new TestNetService[] {
								new TestNetService(),
								new TestNetService() };
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
		Operation op = new InsertOperation(1, "a");
		eng1.generateRequest(op);
		op = new InsertOperation(5, "!");
		eng1.generateRequest(op);
		
		op = new UndoOperation();
		eng1.generateRequest(op);
		Thread.sleep(200);
		assertEquals("Hallo", ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText());
		
		op = new UndoOperation();
		eng1.generateRequest(op);
		Thread.sleep(200);
		assertEquals("Hllo", ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText());
		
		op = new RedoOperation();
		eng1.generateRequest(op);
		Thread.sleep(200);
		assertEquals("Hallo", ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText());
		
		op = new InsertOperation(5, "?");
		eng1.generateRequest(op);
		Thread.sleep(200);
//		receive and distribute operations at proxy for site 1 
		proxies[0].receiveRequest((Request)queue1.get());
		Request r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		
		Thread.sleep(200);

		/** analyze results **/
		String contentSite1 = ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		String contentSite2 = ((TestDocumentModel)eng2.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		//System.out.println(contentSite1 + " == " + contentSite2);
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
	}
	
	/**
	 * This method tests the correct execution of example 3 and 
	 * figure 3, respectively, in paper 'group99-final.pdf'.
	 * 
	 * @throws Exception
	 */
	public void testExample3() throws Exception {
		final String INITIAL = "M";
		final String FINAL   = "MA";
		
		/** initialize system **/
		JupiterServer server = createServer();
		TestNetService[] net = new TestNetService[] {
								new TestNetService(),
								new TestNetService() };
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
		Operation op = new InsertOperation(1, "S");
		eng1.generateRequest(op);
		//receive and distribute operation at proxy for site 1 
		proxies[0].receiveRequest((Request)queue1.get());
		Request r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1);
		
		//concurrently issue two operations, ins(1,A) at site 2 and an undo at site 1
		op = new InsertOperation(1, "A");
		eng2.generateRequest(op);
		op = new UndoOperation();
		eng1.generateRequest(op);
		 
		proxies[1].receiveRequest((Request)queue2.get());
		r1 = (Request)net[0].getRequests().remove(0);
		eng1.receiveRequest(r1);
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		eng2.receiveRequest(r1); 
	
		Thread.sleep(500);

		/** analyze results **/
		String contentSite1 = ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		String contentSite2 = ((TestDocumentModel)eng2.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		//System.out.println(contentSite1 + " == " + contentSite2);
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
	}
	
	/**
	 * This method tests the correct execution of example 4 and 
	 * figure 4, respectively, in paper 'group99-final.pdf'.
	 * 
	 * @throws Exception
	 */
	public void testDelDelUndoPuzzle() throws Exception {
		final String INITIAL = "b";
		final String FINAL   = "";
		
		/** initialize system **/
		JupiterServer server = createServer();
		TestNetService[] net = new TestNetService[] {
								new TestNetService(),
								new TestNetService() };
		ClientProxy[] proxies = new ClientProxy[2];
		proxies[0] = server.addClient(net[0]); //belongs to site 1
		proxies[1] = server.addClient(net[1]); //belongs to site 2
		
		
		DefaultRequestEngine eng1 = new DefaultRequestEngine(
				createClient(proxies[0].getSiteId(), INITIAL));
		DefaultRequestEngine eng2 = new DefaultRequestEngine(
				createClient(proxies[1].getSiteId(), INITIAL));
		SynchronizedQueue queue1 = eng1.getOutgoingRequestBuffer();
		SynchronizedQueue queue2 = eng2.getOutgoingRequestBuffer();
		
		Operation op = new DeleteOperation(0, "b");
		eng1.generateRequest(op);
		op = new UndoOperation();
		eng1.generateRequest(op);
		op = new DeleteOperation(0, "b");
		eng2.generateRequest(op);
		 
		LOG.info("recv s1.pr");
		proxies[0].receiveRequest((Request)queue1.get());
		Request r1 = (Request)net[1].getRequests().remove(0);
		LOG.info("recv s2.cl");
		eng2.receiveRequest(r1);
		LOG.info("recv s2.pr");
		proxies[1].receiveRequest((Request)queue2.get());
		r1 = (Request)net[0].getRequests().remove(0);
		LOG.info("recv s1.cl "+r1);
		eng1.receiveRequest(r1);
		LOG.info("recv s1.pr");
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		LOG.info("recv s2.cl");
		eng2.receiveRequest(r1);
 
	
		Thread.sleep(500);

		/** analyze results **/
		String contentSite1 = ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		String contentSite2 = ((TestDocumentModel)eng2.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		//System.out.println(contentSite1 + " == " + contentSite2);
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
	}
	
	/**
	 * This is the same test as {@link JupiterCharUndoTest#testDelDelUndoPuzzle()} but
	 * has a different order in which the operations arrive at the server.
	 * 
	 * @throws Exception
	 */
	public void testDelDelUndoPuzzle2() throws Exception {
		final String INITIAL = "b";
		final String FINAL   = "";
		
		/** initialize system **/
		JupiterServer server = createServer();
		TestNetService[] net = new TestNetService[] {
								new TestNetService(),
								new TestNetService() };
		ClientProxy[] proxies = new ClientProxy[2];
		proxies[0] = server.addClient(net[0]); //belongs to site 1
		proxies[1] = server.addClient(net[1]); //belongs to site 2
		
		
		DefaultRequestEngine eng1 = new DefaultRequestEngine(
				createClient(proxies[0].getSiteId(), INITIAL));
		DefaultRequestEngine eng2 = new DefaultRequestEngine(
				createClient(proxies[1].getSiteId(), INITIAL));
		SynchronizedQueue queue1 = eng1.getOutgoingRequestBuffer();
		SynchronizedQueue queue2 = eng2.getOutgoingRequestBuffer();
		
		Operation op = new DeleteOperation(0, "b");
		eng1.generateRequest(op);
		op = new UndoOperation();
		eng1.generateRequest(op);
		op = new DeleteOperation(0, "b");
		eng2.generateRequest(op);
		 
		LOG.info("recv s1.pr");
		proxies[0].receiveRequest((Request)queue1.get());
		Request r1 = (Request)net[1].getRequests().remove(0);
		LOG.info("recv s2.cl");
		eng2.receiveRequest(r1);
		LOG.info("recv s1.pr");
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		LOG.info("recv s2.cl");
		eng2.receiveRequest(r1);
		LOG.info("recv s2.pr");
		proxies[1].receiveRequest((Request)queue2.get());
		r1 = (Request)net[0].getRequests().remove(0);
		LOG.info("recv s1.cl "+r1);
		eng1.receiveRequest(r1);
	
		Thread.sleep(500);

		/** analyze results **/
		String contentSite1 = ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		String contentSite2 = ((TestDocumentModel)eng2.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		//System.out.println(contentSite1 + " == " + contentSite2);
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
	}
	
	/**
	 * This is the same test as {@link JupiterCharUndoTest#testDelDelUndoPuzzle()} but
	 * has a different order in which the operations arrive at the server.
	 * 
	 * @throws Exception
	 */
	public void testDelDelUndoPuzzle3() throws Exception {
		final String INITIAL = "b";
		final String FINAL   = "";
		
		/** initialize system **/
		JupiterServer server = createServer();
		TestNetService[] net = new TestNetService[] {
								new TestNetService(),
								new TestNetService() };
		ClientProxy[] proxies = new ClientProxy[2];
		proxies[0] = server.addClient(net[0]); //belongs to site 1
		proxies[1] = server.addClient(net[1]); //belongs to site 2
		
		
		DefaultRequestEngine eng1 = new DefaultRequestEngine(
				createClient(proxies[0].getSiteId(), INITIAL));
		DefaultRequestEngine eng2 = new DefaultRequestEngine(
				createClient(proxies[1].getSiteId(), INITIAL));
		SynchronizedQueue queue1 = eng1.getOutgoingRequestBuffer();
		SynchronizedQueue queue2 = eng2.getOutgoingRequestBuffer();
		
		Operation op = new DeleteOperation(0, "b");
		eng1.generateRequest(op);
		op = new UndoOperation();
		eng1.generateRequest(op);
		op = new DeleteOperation(0, "b");
		eng2.generateRequest(op);
		 
		LOG.info("recv s2.pr");
		proxies[1].receiveRequest((Request)queue2.get());
		Request r1 = (Request)net[0].getRequests().remove(0);
		LOG.info("recv s1.cl "+r1);
		eng1.receiveRequest(r1);
		LOG.info("recv s1.pr");
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		LOG.info("recv s2.cl");
		eng2.receiveRequest(r1);
		LOG.info("recv s1.pr");
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		LOG.info("recv s2.cl");
		eng2.receiveRequest(r1);
 
	
		Thread.sleep(500);

		/** analyze results **/
		String contentSite1 = ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		String contentSite2 = ((TestDocumentModel)eng2.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		//System.out.println(contentSite1 + " == " + contentSite2);
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
	}
	
	/**
	 * This is the same test as {@link JupiterCharUndoTest#testDelDelUndoPuzzle()} but
	 * makes 2 concurrent undo's instead of one.
	 * site 1: | del 0,b ; ins 0,a ; undo ; undo 	|
	 * site 2: | del 0,b							|
	 * 
	 * @throws Exception
	 */
	public void testDelDelUndoPuzzle4() throws Exception {
		final String INITIAL = "b";
		final String FINAL   = "";
		
		/** initialize system **/
		JupiterServer server = createServer();
		TestNetService[] net = new TestNetService[] {
								new TestNetService(),
								new TestNetService() };
		ClientProxy[] proxies = new ClientProxy[2];
		proxies[0] = server.addClient(net[0]); //belongs to site 1
		proxies[1] = server.addClient(net[1]); //belongs to site 2
		
		
		DefaultRequestEngine eng1 = new DefaultRequestEngine(
				createClient(proxies[0].getSiteId(), INITIAL));
		DefaultRequestEngine eng2 = new DefaultRequestEngine(
				createClient(proxies[1].getSiteId(), INITIAL));
		SynchronizedQueue queue1 = eng1.getOutgoingRequestBuffer();
		SynchronizedQueue queue2 = eng2.getOutgoingRequestBuffer();
		
		Operation op = new DeleteOperation(0, "b");
		eng1.generateRequest(op);
		op = new InsertOperation(0, "a");		
		eng1.generateRequest(op);
		op = new UndoOperation();
		eng1.generateRequest(op);
		op = new UndoOperation();
		eng1.generateRequest(op);
		op = new DeleteOperation(0, "b");
		eng2.generateRequest(op);
		 
		LOG.info("recv s1.pr");
		proxies[0].receiveRequest((Request)queue1.get());
		Request r1 = (Request)net[1].getRequests().remove(0);
		LOG.info("recv s2.cl");
		eng2.receiveRequest(r1);
		LOG.info("recv s1.pr");
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		LOG.info("recv s2.cl");
		eng2.receiveRequest(r1);
		LOG.info("recv s2.pr");
		proxies[1].receiveRequest((Request)queue2.get());
		r1 = (Request)net[0].getRequests().remove(0);
		LOG.info("recv s1.cl "+r1);
		eng1.receiveRequest(r1);
		LOG.info("recv s1.pr");
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		LOG.info("recv s2.cl");
		eng2.receiveRequest(r1);
		LOG.info("recv s1.pr");
		proxies[0].receiveRequest((Request)queue1.get());
		r1 = (Request)net[1].getRequests().remove(0);
		LOG.info("recv s2.cl");
		eng2.receiveRequest(r1);
	
		Thread.sleep(500);

		/** analyze results **/
		String contentSite1 = ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		String contentSite2 = ((TestDocumentModel)eng2.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
		//System.out.println(contentSite1 + " == " + contentSite2);
		assertEquals(FINAL, contentSite1);
		assertEquals(FINAL, contentSite2);
	}
	
	/**
	 * This is the same test as in {@link #testDelDelUndoPuzzle()} but uses
	 * only one ClientProxy. This means that the requests from the second site
	 * are directly handed to the ClientProxy instead of having a second one so 
	 * that the request is directed by the RequestSerializer. Finally, it is a 
	 * simplified test.
	 * 
	 * @throws Exception
	 */
	public void testDelDelUndoPuzzle5() throws Exception {
		final String INITIAL = "b";
		final String FINAL   = "";
		
		/** initialize system **/
		JupiterServer server = createServer();
		TestNetService[] net = new TestNetService[] {
								new TestNetService() };
		ClientProxy[] proxies = new ClientProxy[1];
		proxies[0] = server.addClient(net[0]); //belongs to site 1
		
		
		DefaultRequestEngine eng1 = new DefaultRequestEngine(
				createClient(proxies[0].getSiteId(), INITIAL));
		SynchronizedQueue queue1 = eng1.getOutgoingRequestBuffer();
		
		Operation op = new DeleteOperation(0, "b");
		eng1.generateRequest(op);
		op = new UndoOperation();
		eng1.generateRequest(op);
		op = new DeleteOperation(0, "b");
		Request reqServ = proxies[0].getAlgorithm().generateRequest(op);
		 
		LOG.info("recv s1.pr");
		proxies[0].receiveRequest((Request)queue1.get());
		LOG.info("recv s1.pr");
		proxies[0].receiveRequest((Request)queue1.get());
		LOG.info("recv s1.cl "+reqServ);
		eng1.receiveRequest(reqServ);
 
	
		Thread.sleep(500);

		/** analyze results **/
		String contentSite1 = ((TestDocumentModel)eng1.getQueueHandler().getAlgorithm()
				.getDocument()).getText();
//		String contentSite2 = proxies[0].getAlgorithm().getDocument()
//		System.out.println(contentSite1 + " == " + contentSite2);
		assertEquals(FINAL, contentSite1);
//		assertEquals(FINAL, contentSite2);
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
