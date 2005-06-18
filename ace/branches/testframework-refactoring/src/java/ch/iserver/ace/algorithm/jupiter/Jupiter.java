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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.InclusionTransformation;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.text.GOTOInclusionTransformation;
import ch.iserver.ace.text.SplitOperation;

/**
 * This class implements the client-side core of the Jupiter control algorithm.
 */
public class Jupiter implements Algorithm {

	private static Logger LOG = Logger.getLogger(Jupiter.class);

	private List operationBuffer;

	private GOTOInclusionTransformation inclusion;

	private DocumentModel document;

	private JupiterVectorTime vectorTime;

	private int siteId;
	
	private boolean isClientSide;

	/**
	 * A list that contains the requests sent to the server which are to be
	 * acknowledged by the server before they can be removed. This list
	 * corresponds to the 'outgoing' list in the Jupiter pseudo code
	 * description.
	 */
	private OutgoingQueue ackRequestList;
	//TODO: limit size of undo candidates
	private List undoCandidates;
	
	private UndoManager undoManager;

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
	 * @param isClientSide
	 * 			 true if the algorithm resides on the client side
	 */
	public Jupiter(InclusionTransformation it, DocumentModel document,
			int siteId, boolean isClientSide) {
		inclusion = (GOTOInclusionTransformation)it;
		this.siteId = siteId;
		init(document, new JupiterVectorTime(0, 0));
		operationBuffer = new ArrayList();
		ackRequestList = new OutgoingQueue();
		undoCandidates = new ArrayList();
		this.isClientSide = isClientSide;
		if (isClientSide()) {
			undoManager = new UndoManager();
		}
	}

	/**
	 * Class constructor that creates a new Jupiter algorithm. Afterwards, the
	 * method {@link #init(DocumentModel, Timestamp)}needs to be called to
	 * initialize the algorithm.
	 * 
	 * @param siteId
	 *            the site Id of this algorithm.
	 * @param isClientSide
	 * 			 true if the algorithm resides on the client side
	 */
	public Jupiter(int siteId, boolean isClientSide) {
		this.siteId = siteId;
		this.isClientSide = isClientSide;
		operationBuffer = new ArrayList();
		ackRequestList = new OutgoingQueue();
		undoCandidates = new ArrayList();
		if (isClientSide()) {
			undoManager = new UndoManager();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.iserver.ace.algorithm.Algorithm#generateRequest(ch.iserver.ace.Operation)
	 */
	public Request generateRequest(Operation op) {
		return generateRequest(op, false);
	}
	
	private Request generateRequest(Operation op, boolean isUndoRedo) {
		//apply op locally;
        if (op instanceof SplitOperation) {
    			SplitOperation split = (SplitOperation)op;
    			document.apply(split.getFirst());
    			document.apply(split.getSecond());
        } else {
    			document.apply(op);
    		}

		//send(op, myMsgs, otherMsgs);
		Request req = new JupiterRequest(siteId, (JupiterVectorTime) vectorTime
				.clone(), op);

		//add(op, myMsgs) to outgoing;
		if (op instanceof SplitOperation) {
			SplitOperation split = (SplitOperation)op;
			ackRequestList.add(new OperationWrapper(split.getFirst(), vectorTime
					.getLocalOperationCount()));
			ackRequestList.add(new OperationWrapper(split.getSecond(), vectorTime
					.getLocalOperationCount()));
		} else {
			ackRequestList.add(new OperationWrapper(op, vectorTime
					.getLocalOperationCount()));
		}

		//myMsgs = myMsgs + 1;
		vectorTime.incrementLocalOperationCount();
		
		//save request in history list (for local undo)
		if (isClientSide && !isUndoRedo) {
			undoManager.addUndo(req);
		}
		return req;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.iserver.ace.algorithm.Algorithm#receiveRequest(ch.iserver.ace.algorithm.Request)
	 */
	public void receiveRequest(Request req) {
		LOG.info(">>> recv");
        JupiterRequest jupReq = (JupiterRequest)req;
        checkPreconditions(jupReq);
        LOG.info("ini:"+ackRequestList);
        
        //TODO: it is possible for the traffic to one side (client/server) to be one-sided, 
        //e.g. only one client writes text and the other sites are idle.
        //Therefore, each side must periodically generate explicit acknowledgments 
        //(i.e. no-op messages) to prevent the outgoing queues from growing forever.
        
        //Discard acknowledged messages.
        discardOperations(jupReq);
        
        LOG.info("dsc:"+ackRequestList);
        		
        Operation newOp = jupReq.getOperation();
        if (newOp.isUndo()) {
        		LOG.info("undo: "+newOp);
        		//always the last operation in the undoCandidates list belongs
        		//to the current undo operation.
        		OperationWrapper wrap1  = (OperationWrapper)undoCandidates.remove(undoCandidates.size()-1);
        		LOG.info("undo opposite: "+wrap1.getOperation());
        		newOp = wrap1.getOperation().inverse();
        		//transform newOp against remaining operations
        		Iterator iter = ackRequestList.getOperations().iterator();
        		while (iter.hasNext()) {
        			OperationWrapper wrap2 = (OperationWrapper)iter.next();
        			Operation op = wrap2.getOperation();
        			if (wrap2.getLocalOperationCount() >= wrap1.getLocalOperationCount()) {
        				//TODO: which operation is privileged??
        				newOp = inclusion.transform(newOp, op);
        			}
        		}
        		LOG.info("transformed op: "+newOp);
        } else {
        		newOp = transform(newOp); 
        }
        
		//if server side, add newOp to undoBuffer
//		if (!isClientSide() && !jupReq.getOperation().isUndo()) {
		if (!jupReq.getOperation().isUndo()) {
			//local operation count means, that this newOp was already transformed against
			//localOperationCount operations. So when an undo operation arrives, we 
			//invert this operation (mirror) and transform it with the remaining operations
			//in the outgoing queue, i.e. the operations, which have a
			//localOperationCount greater or equals to the localOperationCount belonging to
			//this newOp.
			undoCandidates.add(new OperationWrapper(newOp, vectorTime.getLocalOperationCount()));
//			LOG.info("undo candidates: "+undoCandidates);
		}
		
        apply(newOp);
        LOG.info("tnf:"+ackRequestList);
        
        //save remote request in history (for local undo)
        if (isClientSide()) {
        		undoManager.addRemote(
        				new JupiterRequest(jupReq.getSiteId(), 
        						(JupiterVectorTime)jupReq.getJupiterVectorTime().clone(), newOp)
        		);
        }
        
        vectorTime.incrementRemoteRequestCount();
        LOG.info("<<< recv");
    }
	
	/**
	 * Discard from the other site (client/server) acknowledged operations.
	 * 
	 * @param jupReq the request to the remote operation count from
	 */
	private void discardOperations(JupiterRequest jupReq) {
		Iterator iter = ackRequestList.getOperations().iterator();
        while(iter.hasNext()) {
        		OperationWrapper wrap = (OperationWrapper)iter.next();
        		if (wrap.getLocalOperationCount() < jupReq.getJupiterVectorTime().getRemoteOperationCount()) {
        			iter.remove();
        		}
        }
        //ASSERT msg.myMsgs == otherMsgs
        assert jupReq.getJupiterVectorTime().getLocalOperationCount() == 
        				vectorTime.getRemoteOperationCount() : "msg.myMsgs != otherMsgs !!";
	}

	/**
	 * Applies an operation to the document model.
	 * 
	 * @param newOp the operation to be applied
	 */
	private void apply(Operation newOp) {
		if (isClientSide && newOp instanceof SplitOperation) {
        		SplitOperation split = (SplitOperation)newOp;
        		document.apply(split.getSecond());
        		document.apply(split.getFirst());
        } else {
        		document.apply(newOp);
        }
	}

	/**
	 * Transforms an operation with the operations in the outgoing queue {@link #ackRequestList}
	 * that need to be transformed, i.e. undo operation pairs are excluded (fold mechanism).
	 * 
	 * @param newOp 		the operation to be transformed
	 * @return 			the transformed operation
	 * @see #ackRequestList
	 */
	private Operation transform(Operation newOp) {
		LOG.info("--> transform("+newOp+")");
		List transformSet = ackRequestList.getTransformationSet();
		//TODO: log transformSet
		LOG.info("TF size: "+transformSet.size());
		
        for (int cnt = 0; cnt < transformSet.size(); cnt++) {
        		Object[] data = (Object[])transformSet.get(cnt);
	        	OperationWrapper wrap = (OperationWrapper)data[0];
	        	Operation existingOp = wrap.getOperation();
	        	int opIndex = ((Integer)data[1]).intValue();
	        	
	        	Operation transformedOp;
	        	if (existingOp.isUndo() && newOp.getOriginalOperation() != null) {
	        		Operation origin = newOp.getOriginalOperation();
	        		LOG.info("use original operation: "+newOp+" ==> "+origin);
	        		transformedOp = origin;
	        	} else if (newOp instanceof SplitOperation) {
	        		SplitOperation split = (SplitOperation)newOp;
	        		if (isClientSide()) {
	        			inclusion.setTransformOpPrivileged(true);
	        			split.setFirst( inclusion.transform(split.getFirst(), existingOp) );
	        			split.setSecond( inclusion.transform(split.getSecond(), existingOp) );
	        			inclusion.setTransformOpPrivileged(false);
	        			existingOp = inclusion.transform(existingOp, split.getFirst());
	        			existingOp = inclusion.transform(existingOp, split.getSecond());
	        		} else {
	        			inclusion.setTransformOpPrivileged(false);
	        			split.setFirst( inclusion.transform(split.getFirst(), existingOp) );
	        			split.setSecond( inclusion.transform(split.getSecond(), existingOp) );
	        			inclusion.setTransformOpPrivileged(true);
	        			existingOp = inclusion.transform(existingOp, split.getFirst());
	        			existingOp = inclusion.transform(existingOp, split.getSecond());
	        		}
	        		transformedOp = split;
	        	} else {
	        		if (isClientSide()) {
	        			inclusion.setTransformOpPrivileged(true);
	        			transformedOp = inclusion.transform(newOp, existingOp);
	        			inclusion.setTransformOpPrivileged(false);
		        		existingOp = inclusion.transform(existingOp, newOp);
	        		} else {
	        			inclusion.setTransformOpPrivileged(false);
	        			transformedOp = inclusion.transform(newOp, existingOp);
	        			inclusion.setTransformOpPrivileged(true);
	        			existingOp = inclusion.transform(existingOp, newOp);
	        		}
	        	}
	        	//TODO: is this correct?? the transformSet list is in reverse order.
	        	assert wrap.getLocalOperationCount() == ((OperationWrapper)ackRequestList.getOperations().
	        			get(opIndex)).getLocalOperationCount() : "getTransformationSet failed";
	        	ackRequestList.set(opIndex, new OperationWrapper(existingOp, wrap.getLocalOperationCount()));
	
	        	newOp = transformedOp;
        }
        LOG.info("<-- transform("+newOp+")");
		return newOp;
	}

	/**
	 * Test 3 preconditions that must be fulfilled before transforming. They are taken 
	 * from the Jupiter paper.
	 * 
	 * @param jupReq the request to be tested.
	 */
	private void checkPreconditions(JupiterRequest jupReq) {
		if (!ackRequestList.getOperations().isEmpty() && 
				jupReq.getJupiterVectorTime().getRemoteOperationCount() 
					< ((OperationWrapper)ackRequestList.getOperations().get(0)).getLocalOperationCount()) {
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
	
	private class OutgoingQueue {
		
		/**
		 * A list of operation wrappers.
		 * 
		 * @see OperationWrapper
		 */
		private List operations;
		
		OutgoingQueue() {
			operations = new ArrayList();
		}
		
		public void add(OperationWrapper wrap) {
			operations.add(wrap);
		}
		
		public void set(int index, OperationWrapper wrap) {
			operations.set(index, wrap);
		}
		
		public List getOperations() {
			return operations;
		}
		
		/**
		 * Returns a list of OperationWrapper whose operations must be included in 
		 * the transformation of the received operation. All undo pairs
		 * in the list are excluded (this represents the 'fold' mechanism described in 
		 * the Ressel Undo paper).
		 * 
		 * @return
		 */
		public List getTransformationSet() {
			//TODO: test this method thorougly
			List result;
			if (operations.size() <= 1) {
				result = new ArrayList();
				if (!operations.isEmpty()) {
					result.add(new Object[]{(OperationWrapper)operations.get(0), 
							new Integer(0)});
				}
			} else {
				int undos = 0, pairs = 0;
				List tmp = new ArrayList(operations);
				//reverse list so that undo pairs can be recognized
				Collections.reverse(tmp);
				Iterator iter = tmp.iterator();
				//count the number of undo pairs
				//operations list examples: {d,u,u,d,d} {d,d,u,u} {u,u,d}; u=undo, d=do 
				while (iter.hasNext()) {
					OperationWrapper wrap = (OperationWrapper)iter.next();
					Operation op = wrap.getOperation();
					if (op.isUndo()) {
						undos++;
					} else if (undos > 0) {
						pairs++;
						undos--;
					}
				}
				LOG.info(pairs+" undo pairs in operation list.");
				iter = tmp.iterator();
				result = new ArrayList();
				int undoCnt = 0, opIndex = 0;
				while (iter.hasNext()) {
					OperationWrapper wrap = (OperationWrapper)iter.next();
					Operation op = wrap.getOperation();
					//TODO: does it work with the pairs variable?
					if (op.isUndo() && pairs > 0) { 
						undoCnt++;
						pairs--;
					} else if (undoCnt == 0) {
						//TODO: this is a hack, but we have to remember the index of this wrap 
						//into the operation list, so that it can be updated later
						//the list we iterate in is in reverse order, hence the position 
						//is calculated: operations.size()-1-opIndex
						result.add(new Object[]{wrap, new Integer(operations.size()-1-opIndex)});
					} else {
						undoCnt--;
					}
					++opIndex;
				}
			}
			//TODO: revert result list??????????
			Collections.reverse(result);
			return result;
		}
		
		public String toString() {
			return "OutgoingQueue("+operations.toString()+")";
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
		Request req = undoManager.nextUndo();
		
		// inverse the operation to be undone
		//the mirror function from Ressel undo is done by the
		//inverse() (-> invert operation) and 
		//generateRequest() (-> update vector time) calls.
		Operation op = req.getOperation().inverse();
	
		// get list of remote operation the undo operation has to transform with
		int baseOpCount = ((JupiterVectorTime)req.getTimestamp()).getRemoteOperationCount();
		List transformTargets = undoManager.getRemoteRequests(baseOpCount);
		
		// transform op with all operations in the list
		Iterator iter = transformTargets.iterator();
		while(iter.hasNext()) {
			op = inclusion.transform(op, (Operation)iter.next());
		}
	
		// generate request
		return generateRequest(op, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.iserver.ace.algorithm.Algorithm#redo()
	 */
	public Request redo() {
		//get the users request that is to be redone
		Request req = undoManager.nextRedo();
		
		// get the redo operation
		Operation op = req.getOperation();
		
		// get list of remote operations the redo operation has to transform against
		int baseOpCount = ((JupiterVectorTime)req.getTimestamp()).getRemoteOperationCount();
		List transformTargets = undoManager.getRemoteRequests(baseOpCount);
		
		// transform op with all operations in the list
		Iterator iter = transformTargets.iterator();
		while(iter.hasNext()) {
			Operation other = ((Request) iter.next()).getOperation();
			op = inclusion.transform(op, other);
		}
		
		// generate request
		return generateRequest(op, true);
	}

	/**
	 * Set an inclusion transformation function.
	 * 
	 * @param it
	 *            the inclusion transformation function to set.
	 */
	public void setInclusionTransformation(InclusionTransformation it) {
		this.inclusion = (GOTOInclusionTransformation)it;
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
	
	public boolean isClientSide() {
		return isClientSide;
	}
	
	public boolean canUndo() {
		return (isClientSide()) ? undoManager.canUndo() : false;
	}
	
	public boolean canRedo() {
		return (isClientSide()) ? undoManager.canRedo() : false;
	}
}