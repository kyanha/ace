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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.InclusionTransformation;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;

/**
 * This class implements the client-side core of the Jupiter control algorithm. 
 */
public class Jupiter implements Algorithm {

	private static Logger LOG = Logger.getLogger(Jupiter.class);
	
    private List operationBuffer;
    private InclusionTransformation inclusion;
    private DocumentModel document;
    private JupiterVectorTime vectorTime;
    private int siteId;
    
    /**
     * A list that contains the requests sent to the server which are to be 
     * acknowledged by the server before they can be removed. 
     * This list corresponds to the 'outgoing' list in the Jupiter pseudo 
     * code description.
     */
    private HashMap ackRequestList;
    
    private List outgoing;
    
    /**
     * Class constructor that creates a new Jupiter algorithm. The parameters fully 
     * initialize the algorithm.
     * 
     * @param it the inclusion transformation to be used
     * @param document the inital document model
     * @param timestamp the inital time stamp (state vector)
     */
    public Jupiter(InclusionTransformation it, DocumentModel document, int siteId) {
        inclusion = it;
        this.siteId = siteId;
        init(document, new JupiterVectorTime(0, 0));
        operationBuffer = new ArrayList();
        ackRequestList = new HashMap();
        outgoing = new ArrayList();
    }
    
    /**
     * Class constructor that creates a new Jupiter algorithm. Afterwards, the method 
     * {@link #init(DocumentModel, Timestamp)} needs to be called to initialize 
     * the algorithm.
     * 
     * @param siteId the site Id of this algorithm.
     */
    public Jupiter(int siteId) {
        this.siteId = siteId;
        operationBuffer = new ArrayList();
        ackRequestList = new HashMap();
        outgoing = new ArrayList();
    }
    
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#generateRequest(ch.iserver.ace.Operation)
     */
    public Request generateRequest(Operation op) {
        //apply op locally;
    		System.out.print(siteId+": "); 
        document.apply(op);
        //send(op, myMsgs, otherMsgs);
        Request req = new JupiterRequest(siteId, (JupiterVectorTime)vectorTime.clone(), op);
        //add(op, myMsgs) to outgoing;
        /** ackRequestList.put(new Integer(vectorTime.getLocalOperationCount()), op); **/
        outgoing.add(new Wrapper(op, vectorTime.getLocalOperationCount()));
        //myMsgs = myMsgs + 1;
        vectorTime.incrementLocalOperationCount();
        return req;
    }
    
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#receiveRequest(ch.iserver.ace.algorithm.Request)
     */
    public void receiveRequest(Request req) {
        JupiterRequest jupReq = (JupiterRequest)req;
        //Discard acknowledged messages.
//        System.out.println("bf:"+ackRequestList);
        System.out.println("bf:"+outgoing);
        /**Iterator iter = ackRequestList.keySet().iterator();**/
        Iterator iter = outgoing.iterator();
        while(iter.hasNext()) {
        		/**
        		Integer localOperationCount = (Integer)iter.next();
        		if (localOperationCount.intValue() < 
        			jupReq.getJupiterVectorTime().getRemoteOperationCount()) {
        			iter.remove();
        		}
        		**/
        		Wrapper wrap = (Wrapper)iter.next();
        		if (wrap.getLocalOperationCount() < jupReq.getJupiterVectorTime().getRemoteOperationCount()) {
        			iter.remove();
        		}
        }
//        System.out.println("af:"+ackRequestList);
        System.out.println("af:"+outgoing);
        //ASSERT msg.myMsgs == otherMsgs
        assert jupReq.getJupiterVectorTime().getLocalOperationCount() == 
        				vectorTime.getRemoteOperationCount() : "msg.myMsgs != otherMsgs !!";
        	
        /** iter = ackRequestList.keySet().iterator(); **/
        	iter = outgoing.iterator();
        Operation newOp = jupReq.getOperation();
        
        /** HashMap transformedOps = new HashMap(); **/
        List transformedOperations = new ArrayList();
        while (iter.hasNext()) {
            //transform new operation and the ones in the queue.
            /**
        		Integer key = (Integer)iter.next();
            Operation existingOp = (Operation)ackRequestList.get(key);
            Operation transformedOp = inclusion.transform(newOp, existingOp);
            transformedOps.put(key, inclusion.transform(existingOp, newOp));
            newOp = transformedOp;
            **/
            Wrapper wrap = (Wrapper)iter.next();
            Operation existingOp = wrap.getOperation();
            Operation transformedOp = inclusion.transform(newOp, existingOp);
            existingOp = inclusion.transform(existingOp, newOp);
            transformedOperations.add(new Wrapper(existingOp, wrap.getLocalOperationCount()));
            newOp = transformedOp;
        }
        
        //put all transformed operations into the 'outgoing' list.
//        ackRequestList.putAll(transformedOps);
        //TODO: could there be a multithreading problem so that operations get lost??
        assert outgoing.size() == transformedOperations.size() : "operations lost!";
        outgoing = transformedOperations;
//        System.out.println("af2:"+ackRequestList);
        System.out.println("af2:"+outgoing);
        System.out.print(siteId+": ");
        document.apply(newOp);
        vectorTime.incrementRemoteRequestCount();
    }
    
    /**
     * This is a simple helper class used in the implementation of the 
     * Jupiter algorithm. A Wrapper instance is created with an 
     * operation and the current local operation count and inserted
     * into the outgoing queue (see {@link Jupiter#outgoing}).
     *
     * @see Jupiter#generateRequest(Operation)
     * @see Jupiter#receiveRequest(Request)
     */
    class Wrapper {
    	
    		private Operation op;
    		private int count;
    	
    		Wrapper(Operation op, int count) {
    			this.op = op;
    			this.count = count;
    		}
    		
    		Operation getOperation() {
    			return op;
    		}
    		
    		int getLocalOperationCount() {
    			return count;
    		}
    }
    
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#init(ch.iserver.ace.DocumentModel, ch.iserver.ace.algorithm.Timestamp)
     */
    public void init(DocumentModel doc, Timestamp timestamp) {
    		if ( doc == null || timestamp == null)
    			throw new IllegalArgumentException("null parameter not allowed.");
        document = doc;
        vectorTime = (JupiterVectorTime)timestamp;
    }
    
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#getDocument()
     */
    public DocumentModel getDocument() {
        return document;
    }
    
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#siteAdded(int)
     */
    public void siteAdded(int siteId) {
        throw new UnsupportedOperationException();
    }
    
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#siteRemoved(int)
     */
    public void siteRemoved(int siteId) {
        throw new UnsupportedOperationException();
    }
    
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#undo()
     */
    public Request undo() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#redo()
     */
    public Request redo() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * Set an inclusion transformation function.
     * 
     * @param it the inclusion transformation function to set.
     */
    public void setInclusionTransformation(InclusionTransformation it) {
        this.inclusion = it;
    }
    
    /**
     * 
     * @return	the algorithms inclusion transformation
     */
    public InclusionTransformation getInclusionTransformation() {
        return inclusion;
    }
    
    /**
     * @return Returns the siteId.
     */
    public int getSiteId() {
        return siteId;
    }
    
    /**
     * @param siteId The siteId to set.
     */
    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }
    
    /**
     * Originally intended for test purpose.
     * @return the algo's vector time.
     */
    public JupiterVectorTime getVectorTime() {
    		return vectorTime;
    }
}
