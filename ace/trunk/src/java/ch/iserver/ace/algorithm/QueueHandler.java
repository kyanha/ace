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
package ch.iserver.ace.algorithm;

import ch.iserver.ace.Operation;
import ch.iserver.ace.util.SynchronizedQueue;

/**
 * The QueueHandler class processes operations and requests, respectively, 
 * from the buffers and passes them to the algorithm. The returned requests
 * from the algorithm are inserted into a buffer for outgoing requests.
 */
public class QueueHandler extends Thread {

    /**
     * The priority which is given to local operations when
     * processing the buffers. 
     */
    private static final int LOCAL_OPERATION_PRIORITY = 5;

    /**
     * The priority which is given to remote operations when
     * processing the buffers. 
     */
    private static final int REMOTE_OPERATION_PRIORITY = 2;
    
    private Algorithm algo;
    private SynchronizedQueue localOpBuf;
    private SynchronizedQueue remoteReqBuf;
    private SynchronizedQueue outReqBuf;
    private Object synchObj;
    
    
    /**
     * Creates a new QueueHandler.
     * 
     * @param algo the algorithm to pass the operations
     * @param localOpBuf a buffer containing local operations
     * @param remoteReqBuf a buffer containing remote requests
     * @param outReqBuf a buffer for outgoing requests
     * @param synchObj a synchronization object to be used in conjunction with 
     * 				  the buffers.
     * @see SynchronizedQueue
     */
    public QueueHandler(Algorithm algo, 
            SynchronizedQueue localOpBuf, 
            SynchronizedQueue remoteReqBuf, 
            SynchronizedQueue outReqBuf,
            Object synchObj) {
        this.algo = algo;
        this.localOpBuf = localOpBuf;
        this.remoteReqBuf = remoteReqBuf;
        this.outReqBuf = outReqBuf;
        this.synchObj = synchObj;
    }
    
    public void run() {
        int localOpPrio, remoteOpPrio;
        try {
            while (true) {
                localOpPrio = LOCAL_OPERATION_PRIORITY;
                remoteOpPrio = REMOTE_OPERATION_PRIORITY;

                while (localOpPrio-- > 0 && !localOpBuf.isEmpty()) {
                    // handle local request
                    Request req = algo.generateRequest((Operation) localOpBuf.get());
                    outReqBuf.add(req);
                }

                while (remoteOpPrio-- > 0 && !remoteReqBuf.isEmpty()) {
                    // handle remote request
                    algo.receiveRequest((Request)remoteReqBuf.get());
                }

                // if no more requests -> wait
                if (localOpBuf.isEmpty() && remoteReqBuf.isEmpty()) {
                    synchObj.wait();
                }
            }
        } catch (InterruptedException ie) {
            //TODO:
        }
    }
    
    /**
     * @return Returns the synchObj.
     */
    public Object getSynchronzationObject() {
        return synchObj;
    }
    /**
     * @param obj The object to set.
     */
    public void setSynchronizationObject(Object obj) {
        this.synchObj = obj;
    }
}
