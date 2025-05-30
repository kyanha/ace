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
	 * acknowledged by the server before they can be removed. This list
	 * corresponds to the 'outgoing' list in the Jupiter pseudo code
	 * description.
	 */
	private List ackRequestList;

	/**
	 * Class constructor that creates a new Jupiter algorithm. The parameters
	 * fully initialize the algorithm.
	 * 
	 * @param it
	 *            the inclusion transformation to be used
	 * @param document
	 *            the inital document model
	 * @param timestamp
	 *            the inital time stamp (state vector)
	 */
	public Jupiter(InclusionTransformation it, DocumentModel document,
			int siteId) {
		inclusion = it;
		this.siteId = siteId;
		init(document, new JupiterVectorTime(0, 0));
		operationBuffer = new ArrayList();
		ackRequestList = new ArrayList();
	}

	/**
	 * Class constructor that creates a new Jupiter algorithm. Afterwards, the
	 * method {@link #init(DocumentModel, Timestamp)}needs to be called to
	 * initialize the algorithm.
	 * 
	 * @param siteId
	 *            the site Id of this algorithm.
	 */
	public Jupiter(int siteId) {
		this.siteId = siteId;
		operationBuffer = new ArrayList();
		ackRequestList = new ArrayList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.iserver.ace.algorithm.Algorithm#generateRequest(ch.iserver.ace.Operation)
	 */
	public Request generateRequest(Operation op) {
		//apply op locally;
		document.apply(op);

		//send(op, myMsgs, otherMsgs);
		Request req = new JupiterRequest(siteId, (JupiterVectorTime) vectorTime
				.clone(), op);

		//add(op, myMsgs) to outgoing;
		ackRequestList.add(new OperationWrapper(op, vectorTime
				.getLocalOperationCount()));

		//myMsgs = myMsgs + 1;
		vectorTime.incrementLocalOperationCount();
		
		//TODO: save request in undo list (local undo)
		
		return req;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.iserver.ace.algorithm.Algorithm#receiveRequest(ch.iserver.ace.algorithm.Request)
	 */
	public void receiveRequest(Request req) {
        JupiterRequest jupReq = (JupiterRequest)req;
        checkPreconditions(jupReq);
        
        //TODO: it is possible for the traffic to one side (client/server) to be one-sided, 
        //e.g. only one client writes text and the other sites are idle.
        //Therefore, each side must periodically generate explicit acknowledgments 
        //(i.e. no-op messages) to prevent the outgoing queues from growing forever.
        
        System.out.println("ini:"+ackRequestList);
        
        //Discard acknowledged messages.
        Iterator iter = ackRequestList.iterator();
        while(iter.hasNext()) {
        		OperationWrapper wrap = (OperationWrapper)iter.next();
        		if (wrap.getLocalOperationCount() < jupReq.getJupiterVectorTime().getRemoteOperationCount()) {
        			iter.remove();
        		}
        }
        System.out.println("dsc:"+ackRequestList);
        
        //ASSERT msg.myMsgs == otherMsgs
        assert jupReq.getJupiterVectorTime().getLocalOperationCount() == 
        				vectorTime.getRemoteOperationCount() : "msg.myMsgs != otherMsgs !!";
        				
        Operation newOp = jupReq.getOperation();
        
		// transform
        for (int ackRequestListCnt = 0; ackRequestListCnt < ackRequestList.size(); ackRequestListCnt++) {
	        	OperationWrapper wrap = (OperationWrapper)ackRequestList.get(ackRequestListCnt);
	        	Operation existingOp = wrap.getOperation();
	
	        	Operation transformedOp = inclusion.transform(newOp, existingOp);
	        	existingOp = inclusion.transform(existingOp, newOp);
	        	ackRequestList.set(ackRequestListCnt, new OperationWrapper(existingOp, wrap.getLocalOperationCount()));
	
	        	newOp = transformedOp;
        }
        
        System.out.println("tnf:"+ackRequestList);
        document.apply(newOp);
        vectorTime.incrementRemoteRequestCount();
    }
	
	/**
	 * Test 3 preconditions that must be fulfilled before transforming. They are taken 
	 * from the Jupiter paper.
	 * 
	 * @param jupReq the request to be tested.
	 */
	private void checkPreconditions(JupiterRequest jupReq) {
		if (!ackRequestList.isEmpty() && 
				jupReq.getJupiterVectorTime().getRemoteOperationCount() 
					< ((OperationWrapper)ackRequestList.get(0)).getLocalOperationCount()) {
				throw new JupiterException("precondition #1 violated.");
		} else if (jupReq.getJupiterVectorTime().getRemoteOperationCount() 
				> vectorTime.getLocalOperationCount()) {
			throw new JupiterException("precondition #2 violated.");
		} else if (jupReq.getJupiterVectorTime().getLocalOperationCount() 
				!= vectorTime.getRemoteOperationCount()) {
			throw new JupiterException("precondition #3 violated.");
		}
	}
	/**
	 * This is a simple helper class used in the implementation of the Jupiter
	 * algorithm. A OperationWrapper instance is created with an operation and
	 * the current local operation count and inserted into the outgoing queue
	 * (see {@link Jupiter#ackRequestList}).
	 * 
	 * @see Jupiter#generateRequest(Operation)
	 * @see Jupiter#receiveRequest(Request)
	 */
	private class OperationWrapper {

		private Operation op;

		private int count;

		OperationWrapper(Operation op, int count) {
			this.op = op;
			this.count = count;
		}

		Operation getOperation() {
			return op;
		}

		int getLocalOperationCount() {
			return count;
		}

		public String toString() {
			return ("OperationWrapper(" + op + ", " + count + ")");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.iserver.ace.algorithm.Algorithm#init(ch.iserver.ace.DocumentModel,
	 *      ch.iserver.ace.algorithm.Timestamp)
	 */
	public void init(DocumentModel doc, Timestamp timestamp) {
		if (doc == null || timestamp == null)
			throw new IllegalArgumentException("null parameter not allowed.");
		document = doc;
		vectorTime = (JupiterVectorTime) timestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.iserver.ace.algorithm.Algorithm#getDocument()
	 */
	public DocumentModel getDocument() {
		return document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.iserver.ace.algorithm.Algorithm#siteAdded(int)
	 */
	public void siteAdded(int siteId) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.iserver.ace.algorithm.Algorithm#siteRemoved(int)
	 */
	public void siteRemoved(int siteId) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.iserver.ace.algorithm.Algorithm#undo()
	 */
	public Request undo() {
		//get the users request that is to be undone
		
		//compute undo request in current state (getUndoRequestInState/translateRequest)
		
		//generateRequest
		
		//save undo request in redo list
		
		//return request to send to other participants
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.iserver.ace.algorithm.Algorithm#redo()
	 */
	public Request redo() {
		//...
		
		//save redo request in undo list
		
		return null;
	}

	/**
	 * Set an inclusion transformation function.
	 * 
	 * @param it
	 *            the inclusion transformation function to set.
	 */
	public void setInclusionTransformation(InclusionTransformation it) {
		this.inclusion = it;
	}

	/**
	 * 
	 * @return the algorithms inclusion transformation
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
	 * @param siteId
	 *            The siteId to set.
	 */
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	/**
	 * Originally intended for test purpose.
	 * 
	 * @return the algo's vector time.
	 */
	public JupiterVectorTime getVectorTime() {
		return vectorTime;
	}
}