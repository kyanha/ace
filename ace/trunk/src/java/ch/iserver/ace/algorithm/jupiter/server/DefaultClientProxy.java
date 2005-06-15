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

import org.apache.log4j.Logger;

import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.util.SynchronizedQueue;

/**
 *
 */
public class DefaultClientProxy implements ClientProxy {

	private static Logger LOG = Logger.getLogger(DefaultClientProxy.class);
	
    private int siteId;
    private NetService net;
    private Jupiter algo;
    private SynchronizedQueue requestForwardQueue;
    
    /**
     * 
     * @param net
     * @param algo
     * @param queue
     */
    public DefaultClientProxy(int siteId, NetService net, Jupiter algo, SynchronizedQueue queue) {
        this.siteId = siteId;
        this.net = net;
        this.algo = algo;
        requestForwardQueue = queue;
    }
    
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.jupiter.server.ClientProxy#sendRequest(ch.iserver.ace.algorithm.Request)
     */
    public void sendRequest(Request req) {
    		if (net != null) {
    			net.transmitRequest(req);
    		} else {
    			LOG.warn("net service null");
    		}
    }

    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.jupiter.server.ClientProxy#receiveRequest(ch.iserver.ace.algorithm.Request)
     */
    public void receiveRequest(Request req) {
    		//if the net service connection has been aborted, stop receiving and 
    		//forwarding respectively, requests.
    		if (net != null) {
    			LOG.info("--> recv "+req);
    			requestForwardQueue.add(  new Object[]{this, req} );
    		}
    }

    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.jupiter.server.ClientProxy#getAlgorithm()
     */
    public Jupiter getAlgorithm() {
        return algo;
    }
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.jupiter.server.ClientProxy#getSiteId()
     */
    public int getSiteId() {
        return siteId;
    }
    
    public SynchronizedQueue getRequestForwardQueue() {
    		return requestForwardQueue;
    }
    
    public synchronized void closeNetServiceConnection() {
    		net = null;
    }
}
