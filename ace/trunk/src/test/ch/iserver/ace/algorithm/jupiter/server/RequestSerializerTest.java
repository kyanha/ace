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
package ch.iserver.ace.algorithm.jupiter.server;

import junit.framework.TestCase;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.algorithm.jupiter.JupiterRequest;
import ch.iserver.ace.algorithm.jupiter.JupiterVectorTime;
import ch.iserver.ace.text.GOTOInclusionTransformation;
import ch.iserver.ace.text.InsertOperation;
import ch.iserver.ace.util.SynchronizedQueue;

/**
 * This class has test methods to test the RequestSerializer class.
 * 
 * @see ch.iserver.ace.algorithm.jupiter.server.RequestSerializer
 */
public class RequestSerializerTest extends TestCase {

	private static final int CLIENT_SITE_ID = 1;
	private RequestSerializer serializer;
	private SynchronizedQueue requestForwardQueue;
	private SynchronizedQueue outgoingQueue;
	private ClientProxy proxy;
	
	
	/**
	 * {@inheritDoc}
	 */
	protected void setUp() throws Exception {
		requestForwardQueue = new SynchronizedQueue();
		outgoingQueue = new SynchronizedQueue();
		serializer = new RequestSerializer(requestForwardQueue);
		serializer.start();
		proxy = new DefaultClientProxy(CLIENT_SITE_ID,
				new TestNetService(),
				new Jupiter(new GOTOInclusionTransformation(),
						new OperationExtractDocumentModel(), CLIENT_SITE_ID, false),
				requestForwardQueue);
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void tearDown() throws Exception {
		serializer.interrupt();
	}
	
	/** 
     * @throws Exception
     */
	public void testAddRemoveClientProxy() throws Exception {
		//add a client proxy
		serializer.addClientProxy(proxy,outgoingQueue);
		assertEquals(proxy, serializer.getClientProxies().get(new Integer(CLIENT_SITE_ID)));
		assertEquals(outgoingQueue, serializer.getOutgoingQueues().get(new Integer(CLIENT_SITE_ID)));
		assertEquals(1, serializer.getClientProxies().size());
		assertEquals(1, serializer.getOutgoingQueues().size());
		
		//add a second client proxy
		DefaultClientProxy p = new DefaultClientProxy(CLIENT_SITE_ID+1,
							new TestNetService(),
							new Jupiter(new GOTOInclusionTransformation(),
									new OperationExtractDocumentModel(), CLIENT_SITE_ID+1, false),
							requestForwardQueue);
		serializer.addClientProxy(p, new SynchronizedQueue());
		assertEquals(2, serializer.getClientProxies().size());
		assertEquals(2, serializer.getOutgoingQueues().size());
		
		//remove a client proxy
		serializer.removeClientProxy(CLIENT_SITE_ID);
		
		//receive a request so that the client proxy gets removed by the 
		//request serializer
		p.receiveRequest(new JupiterRequest(CLIENT_SITE_ID+1,
				new JupiterVectorTime(0,0),new InsertOperation()));
		Thread.sleep(500);
		assertNull(serializer.getClientProxies().get(new Integer(CLIENT_SITE_ID)));
		assertNull(serializer.getOutgoingQueues().get(new Integer(CLIENT_SITE_ID)));
		assertEquals(1, serializer.getClientProxies().size());
		assertEquals(1, serializer.getOutgoingQueues().size());
	}
	
}
