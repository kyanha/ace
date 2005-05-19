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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.text.GOTOInclusionTransformation;
import ch.iserver.ace.util.SynchronizedQueue;

/**
 *
 */
public class JupiterServer {
    
	private static Logger LOG = Logger.getLogger(JupiterServer.class);
	
    private int siteIdCounter;
    private RequestSerializer serializer;
    private SynchronizedQueue requestForwardQueue;
    private Map requestForwarders;
    
    /**
     * 
     *
     */
    public JupiterServer() {
        siteIdCounter = 0;
        requestForwardQueue = new SynchronizedQueue();
        requestForwarders = new HashMap();
        serializer = new RequestSerializer(requestForwardQueue);
        serializer.start();
    }
    

    /**
     * Adds a new client to this Jupiter server instance.
     * 
     * @param net
     * @return	the client proxy just created.
     */
    public synchronized ClientProxy addClient(NetService net) {
        //create client proxy
        Jupiter algo = new Jupiter(new GOTOInclusionTransformation(),
                			new OperationExtractDocumentModel(), 
                			++siteIdCounter);
        ClientProxy client = new DefaultClientProxy(siteIdCounter, net, algo, requestForwardQueue);
        
        //add client proxy to request serializer
        SynchronizedQueue outgoingQueue = new SynchronizedQueue();
        serializer.addClientProxy(client, outgoingQueue);

        //create and start request forwarder
        RequestForwarder forwarder = new RequestForwarder(outgoingQueue, client);
        requestForwarders.put(new Integer(siteIdCounter), forwarder);
        forwarder.start();
        
        LOG.info("addClient #"+siteIdCounter);
        return client;
    }
    
    /**
     * 
     * @param siteId
     */
    public void removeClient(int siteId) {
    		Integer id = new Integer(siteId);
    		DefaultClientProxy proxy = (DefaultClientProxy)serializer.getClientProxies().get(id);
        if (proxy != null) {
        		proxy.closeNetServiceConnection();
        		((RequestForwarder)requestForwarders.remove(id)).interrupt();
        		serializer.removeClientProxy(siteId);
        		LOG.info("removeClient #"+siteId);
        }
    }
    
    /**
     * 
     * @return
     */
    Map getRequestForwarders() {
        return requestForwarders;
    }

	/**
	* Originaly intended for test use.
	* Returns the client count.
	*/
	int getClientCount() {
		return siteIdCounter;
	}
	
	/**
	* Originaly intended for test use.
	* Returns to RequestSerializer.
	*/
	RequestSerializer getRequestSerializer() {
		return serializer;
	}
	
}
