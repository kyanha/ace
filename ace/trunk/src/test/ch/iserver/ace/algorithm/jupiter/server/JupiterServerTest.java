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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.jupiter.JupiterRequest;
import ch.iserver.ace.algorithm.jupiter.JupiterVectorTime;
import ch.iserver.ace.text.InsertOperation;
import ch.iserver.ace.util.SynchronizedQueue;

/**
 * This class has test methods to test the JupiterServer.
 * 
 * @see ch.iserver.ace.algorithm.jupiter.server.JupiterServer
 */
public class JupiterServerTest extends TestCase {
    
    private static final int NUM_CLIENTS = 2;
    private static final int VECTORTIME_LOCALOP_COUNT = 0;
    private static final int VECTORTIME_REMOTEOP_COUNT = 0;
    private JupiterServer server;
    private int initialClientCount;
    private ClientProxy[] proxies;
    private TestNetService[] netService;
    
    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        server = new JupiterServer();
        initialClientCount = server.getClientCount(); 
        netService = new TestNetService[NUM_CLIENTS];
        proxies = new ClientProxy[NUM_CLIENTS];
        for (int i = 0; i < NUM_CLIENTS; i++) {
            netService[i] = new TestNetService();
            proxies[i] = server.addClient(netService[i]);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected void tearDown() throws Exception {
        Map forwarders = server.getRequestForwarders();
        Iterator iter = forwarders.keySet().iterator();
        //shutdown all request forwarders 
        while (iter.hasNext()) {
            Integer id = (Integer)iter.next();
            RequestForwarder f = (RequestForwarder)forwarders.get(id);
            f.interrupt();
        }
        //shutdown the request serializer
        server.getRequestSerializer().interrupt();
        server = null;
        proxies = null;
        netService = null;
    }
    
    /** 
     * @throws Exception
     */
    public void testInitialization() throws Exception {
        assertEquals(initialClientCount+NUM_CLIENTS, server.getClientCount());
        RequestSerializer serializer = server.getRequestSerializer();
        Map clientProxies = serializer.getClientProxies();
        assertEquals(NUM_CLIENTS, clientProxies.size());
        assertEquals(NUM_CLIENTS, serializer.getOutgoingQueues().size());
        assertEquals(NUM_CLIENTS, server.getRequestForwarders().size());
        //test that all client proxies have the same requestForwardQueue as
        //the request serializer
        SynchronizedQueue requestQueue = serializer.getRequestQueue();
        Iterator iter = clientProxies.values().iterator();
        while (iter.hasNext()) {
        		ClientProxy c = (ClientProxy)iter.next();
        		assertEquals(requestQueue, ((DefaultClientProxy)c).getRequestForwardQueue());
        }
    }
    
    /** 
     * @throws Exception
     */
    public void testRemoveClient() throws Exception {
    		int siteId = proxies[0].getSiteId();
    		server.removeClient(siteId);
    		
    		proxies[1].receiveRequest(new JupiterRequest(proxies[1].getSiteId(),
    				new JupiterVectorTime(0,0),new InsertOperation(0, "a")));
    		
    		assertNull(server.getRequestForwarders().get(new Integer(siteId)));
    		
    		RequestSerializer serializer = server.getRequestSerializer();
    		assertNull(serializer.getClientProxies().get(new Integer(siteId)));
    		assertNull(serializer.getOutgoingQueues().get(new Integer(siteId)));
    		assertEquals(NUM_CLIENTS - 1, serializer.getClientProxies().size());
    		assertEquals(NUM_CLIENTS - 1, serializer.getOutgoingQueues().size());
    }
    
    /**
     * Use case: there are NUM_CLIENTS registered. One client proxy receives a request.
     * Test: observe the execution flow.
     * 
     * @throws Exception
     */
    public void testServerWithOneRequest() throws Exception {
        Map forwarders = server.getRequestForwarders();
        assertEquals(NUM_CLIENTS, forwarders.size());
        Iterator iter = forwarders.keySet().iterator();
        //shutdown all request forwarders 
        while (iter.hasNext()) {
            Integer id = (Integer)iter.next();
            RequestForwarder f = (RequestForwarder)forwarders.get(id);
            f.interrupt();
        }
        ClientProxy testClient = proxies[0];
        JupiterRequest req = new JupiterRequest(testClient.getSiteId(), 
                new JupiterVectorTime(VECTORTIME_LOCALOP_COUNT, VECTORTIME_REMOTEOP_COUNT), 
                new InsertOperation(0, "a"));
        testClient.receiveRequest(req);
        
        RequestSerializer serializer = server.getRequestSerializer();
        Map outgoing = serializer.getOutgoingQueues();
        assertEquals(NUM_CLIENTS, outgoing.size());
        iter = outgoing.keySet().iterator();
        while (iter.hasNext()) {
        		Integer id = (Integer)iter.next();
        		if ( id.intValue() == testClient.getSiteId() ) {
        			assertTrue( ((SynchronizedQueue)outgoing.get(id)).isEmpty() );
        		} else {
        			assertEquals(1, ((SynchronizedQueue)outgoing.get(id)).size());
        			JupiterRequest jupi = (JupiterRequest)
								((SynchronizedQueue)outgoing.get(id)).get();
        			assertTrue(id.intValue() != jupi.getSiteId());
        			assertEquals(0, jupi.getJupiterVectorTime().getLocalOperationCount());
        			assertEquals(0, jupi.getJupiterVectorTime().getRemoteOperationCount());
        		}
        }
    }
    
    /** 
     * @throws Exception
     */
    public void testServerWithNRequests() throws Exception {
    		//create requests.
    		JupiterRequest[] requests = new JupiterRequest[NUM_CLIENTS];
    		for (int i=0; i < NUM_CLIENTS; i++) {
    			requests[i] = new JupiterRequest(proxies[i].getSiteId(), 
    	                		new JupiterVectorTime(0,0), 
						new InsertOperation(0, "a"+proxies[i].getSiteId()));
    		}
    		
    		//receive requests
    		for (int i=0; i < NUM_CLIENTS; i++) {
    			proxies[i].receiveRequest(requests[i]);
    		}
    		
    		//check that each client (i.e. through the TestNetService class for each client) has
    		//received the correct set of operations, i.e. all operations execept the one it itself
    		//created.
    		for (int i=0; i < NUM_CLIENTS; i++) {
    			int currSiteId = proxies[i].getSiteId();
    			List reqList = netService[i].getRequests();
    			assertEquals(NUM_CLIENTS-1, reqList.size());
    			Iterator iter = reqList.iterator();  
    			while (iter.hasNext()) {
    				Request req = (Request)iter.next();
    				JupiterVectorTime vector = (JupiterVectorTime)req.getTimestamp();
    				//each client has generated one more operation, so either it is already
    				//processed before the other operations coming from the other clients or
    				//it has not been processed yet.
    				assertTrue( vector.getLocalOperationCount() <= VECTORTIME_LOCALOP_COUNT + 1 );
    				assertTrue( vector.getRemoteOperationCount() <= VECTORTIME_REMOTEOP_COUNT + 1 );
    				assertTrue(req.getSiteId() != currSiteId);
    			}
    		}
    		
    }
}
