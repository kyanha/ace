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
import java.util.Iterator;
import java.util.Map;

import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.Request;
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
    
    private boolean shutdown, pause;
    
    /**
     * Class Constructor. The requests are fetched from the given
     * queue.
     * 
     * @param queue	the queue from which requests are processed.
     */
    public RequestSerializer(SynchronizedQueue queue) {
        requestQueue = queue;
        clientProxies = new HashMap();
        outgoingQueues = new HashMap();
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
					//TODO:
				}
				ClientProxy client = (ClientProxy) data[0];
				Request req = (Request) data[1];
				int siteId = client.getSiteId();

				Algorithm algo = client.getAlgorithm();
				// switch the vector time (local and remote operation count)
				algo.receiveRequest(switchedVectorTime(req));
				Operation op = ((OperationExtractDocumentModel) algo
						.getDocument()).getOperation();

				//distribute the operation to all clients
				//TODO: this part could be parallelized at a later time.
				Iterator iter = clientProxies.keySet().iterator();
				while (iter.hasNext()) {
					ClientProxy cl = (ClientProxy) iter.next();
					if (siteId != cl.getSiteId()) {
						Request r = cl.getAlgorithm().generateRequest(op);
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
					//TODO:
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
        //TODO: synchronize??
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
        //TODO: synchronize??
        Integer id = new Integer(siteId);
        ClientProxy client = (ClientProxy)clientProxies.remove(id);
        outgoingQueues.remove(id);
        return client;
    }
    
    
    /**
     * Switches the localOperationCount with the RemoteOperationCount
     *
     * @param	request
     */
    private JupiterRequest switchVectorTime(JupiterRequest request) {
    	return new JupiterRequest(request.getSiteId(),
    		new JupiterVectorTime(request.getJupiterVectorTime().getRemoteOperationCount(),
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
	* Originaly intended for test use.
	* Returns the outgoing queues.
	*/
	Map getOutgoingQueues() {
		return outgoingQueues;
	}

}
