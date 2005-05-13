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
import java.util.Map;

import junit.framework.TestCase;
import ch.iserver.ace.algorithm.jupiter.JupiterRequest;
import ch.iserver.ace.algorithm.jupiter.JupiterVectorTime;
import ch.iserver.ace.text.InsertOperation;
import ch.iserver.ace.util.SynchronizedQueue;

/**
 *
 */
public class JupiterServerTest extends TestCase {
    
    private static final int NUM_CLIENTS = 2;
    private JupiterServer server;
    private int initialClientCount;
    private ClientProxy[] proxies;
    private TestNetService[] netService;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
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
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        
    }
    
    public void testInitialization() throws Exception {
        assertEquals(initialClientCount+NUM_CLIENTS, server.getClientCount());
        
    }
    
    public void testServerWithOneClientProxy() throws Exception {
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
                new JupiterVectorTime(0,0), 
                new InsertOperation(0, "a"));
        testClient.receiveRequest(req);

        Thread.sleep(2000);
        
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
        			assertEquals(id.intValue(), jupi.getSiteId());
        			assertEquals(jupi.getJupiterVectorTime().getLocalOperationCount(), 0);
        			assertEquals(jupi.getJupiterVectorTime().getRemoteOperationCount(), 1);
        		}
        }
    }
}
