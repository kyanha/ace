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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.jupiter.JupiterRequest;
import ch.iserver.ace.algorithm.jupiter.JupiterVectorTime;
import ch.iserver.ace.util.SynchronizedQueue;

/**
 * RequestSerializer processes requests from a queue in that
 * it passes them to the originating ClientProxy for transformation 
 * first and afterwards distributes them to all other registered 
 * client proxies. 
 *
 * @see ClientProxy
 */
public class RequestSerializer extends Thread {
	
    private static Logger LOG = Logger.getLogger(RequestSerializer.class);

    /**
     * The queue from which this serializer processes
     * the request.
     */
    private SynchronizedQueue requestQueue;
    
    /**
     * A map that contains Integer (site id) to ClientProxy pairs.
     */
    private Map clientProxies;
    
    /**
     * A map that contains Integer (site id) to SynchronizedQueue 
     * (outgoing request queue) pairs.
     */
    private Map outgoingQueues;
    
    /**
     * boolean variables used to control the execution of the
     * serializer.
     */
    private boolean shutdown, pause;
    
    /**
     * Class Constructor. The requests are fetched from the given
     * queue.
     * 
     * @param queue	the queue from which requests are processed.
     */
    public RequestSerializer(SynchronizedQueue queue) {
        requestQueue = queue;
        clientProxies = Collections.synchronizedMap(new HashMap());
        outgoingQueues = Collections.synchronizedMap(new HashMap());
        shutdown = false;
        pause = false;
    }

	public void run() {
		while (!shutdown) {
			if (!pause) {
				Object[] data = null;
				try {
					data = (Object[]) requestQueue.get();
				} catch (InterruptedException ie) {
					LOG.fatal(ie);
					return;
				}
				ClientProxy client = (ClientProxy) data[0];
				Request req = (Request) data[1];
				int siteId = client.getSiteId();

				Algorithm algo = client.getAlgorithm();
				// switch the vector time (local and remote operation count)
				algo.receiveRequest(switchVectorTime(req));
				Operation op = ((OperationExtractDocumentModel) algo
						.getDocument()).getOperation();

				//distribute the operation to all clients
				//TODO: this part could be parallelized at a later time.
				Iterator iter = clientProxies.keySet().iterator();
				while (iter.hasNext()) {
					ClientProxy cl = (ClientProxy)clientProxies.get((Integer) iter.next());
					if (siteId != cl.getSiteId()) {
						Request r = cl.getAlgorithm().generateRequest(op);
						
				        //TODO: if the operation was generated from another client, the site id of the
				        //request may not be changed!!!
						//the following is a quick fix.
						r = new JupiterRequest(siteId,
											(JupiterVectorTime)r.getTimestamp(),r.getOperation());
						
						
						Integer id = new Integer(cl.getSiteId());
						// switch the vector time (local and remote operation count)
						((SynchronizedQueue) outgoingQueues.get(id)).add(switchVectorTime(r));
					}
				}
			} else {
				// pause
				try {
					Thread.sleep(10);
				} catch (InterruptedException ie) {
					LOG.fatal(ie);
				}
            }
        } 
    }
    
    /**
     * Adds a client proxy to this RequestSerializer.
     * 
     * @param client			the client proxy to add.
     * @param queue			the queue for outgoing requests.
     * @see 	 ClientProxy
     * @see	 SynchronizedQueue 
     */
    public void addClientProxy(ClientProxy client, SynchronizedQueue queue) {
        Integer id = new Integer(client.getSiteId());
        clientProxies.put(id, client);
        outgoingQueues.put(id, queue);
    }
    
    /**
     * Removes a ClientProxy from this RequestSerializer. The ClientProxy
     * which was removed is returned.
     * 
     * @param  	siteId
     * @return 	the removed ClientProxy
     * @see	  	ClientProxy
     */
    public ClientProxy removeClientProxy(int siteId) {
        Integer id = new Integer(siteId);
        ClientProxy client = (ClientProxy)clientProxies.remove(id);
        outgoingQueues.remove(id);
        return client;
    }
    
    
    /**
     * Switches the localOperationCount with the remoteOperationCount 
     * component in the given JupiterVectorTime instance.
     *
     * @param	req the request to switch its vector time.
     */
    private JupiterRequest switchVectorTime(Request req) {
    		JupiterRequest request = (JupiterRequest)req;
		return new JupiterRequest(request.getSiteId(), new JupiterVectorTime(
				request.getJupiterVectorTime().getRemoteOperationCount(),
				request.getJupiterVectorTime().getLocalOperationCount()),
				request.getOperation());
	}
    
    public void shutdown() {
		shutdown = true;
    }
    
    public void pause() {
		pause = true;
    }
    
    public void proceed() {
		pause = false;
    }

	/**
	* Originally intended for test use.
	* Returns the outgoing queues.
	*/
	Map getOutgoingQueues() {
		return outgoingQueues;
	}

	/**
	* Originally intended for test use.
	* Returns the client proxies.
	*/
	Map getClientProxies() {
		return clientProxies;
	}
	
	/**
	* Originally intended for test use.
	* Returns the request queue.
	*/
	SynchronizedQueue getRequestQueue() {
		return requestQueue;
	}

}
