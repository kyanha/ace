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
 *
 */
public class RequestSerializer extends Thread {

    private SynchronizedQueue requestQueue;
    private Map clientProxies;
    private Map outgoingQueues;
    
    public RequestSerializer(SynchronizedQueue queue) {
        requestQueue = queue;
        clientProxies = new HashMap();
        outgoingQueues = new HashMap();
    }
    
    public void run() {
        try {
            Object[] data = (Object[])requestQueue.get();
            Request req = (Request)data[0];
            ClientProxy client = (ClientProxy)data[1];
            int siteId = client.getSiteId();
            
            Algorithm algo = client.getAlgo(); 
            algo.receiveRequest(req);
            Operation op = ((OperationExtractDocumentModel)
            						algo.getDocument()).getOperation();
            
            Iterator iter = clientProxies.keySet().iterator();
            while (iter.hasNext()) {
                ClientProxy cl = (ClientProxy)iter.next();
                if (siteId != cl.getSiteId()) {
                    Request r = cl.getAlgo().generateRequest(op);
                    Integer id = new Integer(cl.getSiteId());
                    ((SynchronizedQueue)outgoingQueues.get(id)).add(r);
                }
            }
        } catch (InterruptedException ie) {
            //TODO:
        }
    }
    
    public void addClientProxy(int siteId, ClientProxy client, SynchronizedQueue queue) {
        //TODO: synchronize??
        clientProxies.put(new Integer(siteId), client);
        outgoingQueues.put(new Integer(siteId), queue);
    }
    
    public ClientProxy removeClientProxy(int siteId) {
        //TODO: synchronize??
        Integer id = new Integer(siteId);
        ClientProxy client = (ClientProxy)clientProxies.remove(id);
        outgoingQueues.remove(id);
        return client;
    }
}
