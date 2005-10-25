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

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.apache.log4j.Logger;

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

	private static final Logger LOG = Logger.getLogger(Jupiter.class);

	private InclusionTransformation inclusion;

	private JupiterVectorTime vectorTime;

	private boolean isClientSide;

	/**
	 * A list that contains the requests sent to the server which are to be
	 * acknowledged by the server before they can be removed. This list
	 * corresponds to the 'outgoing' list in the Jupiter pseudo code
	 * description.
	 */
	private List ackRequestList;

	/**
	 * Class constructor that creates a new Jupiter algorithm.
	 * @param isClientSide
	 *            true if the algorithm resides on the client side
	 */
	public Jupiter(boolean isClientSide) {
		this.inclusion = new GOTOInclusionTransformation();
		this.vectorTime = new JupiterVectorTime(0, 0);
		this.isClientSide = isClientSide;
		ackRequestList = new ArrayList();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Request generateRequest(Operation op) {
		// send(op, myMsgs, otherMsgs);
		Request req = new JupiterRequest(
						getSiteId(), 
						(JupiterVectorTime) vectorTime.clone(), 
						op);

		// add(op, myMsgs) to outgoing;
		if (op instanceof SplitOperation) {
			SplitOperation split = (SplitOperation) op;
			ackRequestList.add(new OperationWrapper(split.getFirst(),
							vectorTime.getLocalOperationCount()));
			ackRequestList.add(new OperationWrapper(split.getSecond(),
							vectorTime.getLocalOperationCount()));
		} else {
			ackRequestList.add(new OperationWrapper(op, vectorTime
							.getLocalOperationCount()));
		}

		// myMsgs = myMsgs + 1;
		vectorTime.incrementLocalOperationCount();

		return req;
	}

	/**
	 * {@inheritDoc}
	 */
	public Operation receiveRequest(Request req) {
		LOG.info(">>> recv");
		JupiterRequest jupReq = (JupiterRequest) req;
		checkPreconditions(jupReq.getJupiterVectorTime());
		LOG.info("ini:" + ackRequestList);

		// Discard acknowledged messages.
		discardOperations(jupReq.getJupiterVectorTime());

		LOG.info("dsc:" + ackRequestList);

		Operation newOp = transform(jupReq.getOperation());

		LOG.info("tnf:" + ackRequestList);

		vectorTime.incrementRemoteRequestCount();
		LOG.info("<<< recv");
		return newOp;
	}
	
	public int[] transformIndices(Timestamp timestamp, int[] indices) {
		checkPreconditions((JupiterVectorTime) timestamp);
		discardOperations((JupiterVectorTime) timestamp);
		int[] result = new int[indices.length]; 
		System.arraycopy(indices, 0, result, 0, indices.length);
		for (int i = 0; i < ackRequestList.size(); i++) {
			OperationWrapper wrap = (OperationWrapper) ackRequestList.get(i);
			Operation ack = wrap.getOperation();
			for (int k = 0; k < indices.length; k++) {
				result[k] = transformIndex(result[k], ack);
			}
		}
		return result;
	}
	
	private int transformIndex(int index, Operation op) {
		if (isClientSide()) {
			return inclusion.transformIndex(index, op, Boolean.TRUE);
		} else {
			return inclusion.transformIndex(index, op, Boolean.FALSE);
		}
	}

	/**
	 * Discard from the other site (client/server) acknowledged operations.
	 * 
	 * @param jupReq
	 *            the request to the remote operation count from
	 */
	private void discardOperations(JupiterVectorTime time) {
		Iterator iter = ackRequestList.iterator();
		while (iter.hasNext()) {
			OperationWrapper wrap = (OperationWrapper) iter.next();
			if (wrap.getLocalOperationCount() < time.getRemoteOperationCount()) {
				iter.remove();
			}
		}
		// ASSERT msg.myMsgs == otherMsgs
		assert time.getLocalOperationCount() == vectorTime
						.getRemoteOperationCount() : "msg.myMsgs != otherMsgs !!";
	}

	/**
	 * Transforms an operation with the operations in the outgoing queue
	 * {@link #ackRequestList}.
	 * 
	 * @param newOp
	 *            the operation to be transformed
	 * @return the transformed operation
	 * @see #ackRequestList
	 */
	private Operation transform(Operation newOp) {
		for (int ackRequestListCnt = 0; ackRequestListCnt < ackRequestList
						.size(); ackRequestListCnt++) {
			OperationWrapper wrap = (OperationWrapper) ackRequestList
							.get(ackRequestListCnt);
			Operation existingOp = wrap.getOperation();

			Operation transformedOp;
			if (newOp instanceof SplitOperation) {
				SplitOperation split = (SplitOperation) newOp;
				if (isClientSide()) {
					split.setFirst(inclusion.transform(split.getFirst(),
									existingOp, Boolean.TRUE));
					split.setSecond(inclusion.transform(split.getSecond(),
									existingOp, Boolean.TRUE));
					existingOp = inclusion.transform(existingOp, split
									.getFirst(), Boolean.FALSE);
					existingOp = inclusion.transform(existingOp, split
									.getSecond(), Boolean.FALSE);
				} else {
					split.setFirst(inclusion.transform(split.getFirst(),
									existingOp, Boolean.FALSE));
					split.setSecond(inclusion.transform(split.getSecond(),
									existingOp, Boolean.FALSE));
					existingOp = inclusion.transform(existingOp, split
									.getFirst(), Boolean.TRUE);
					existingOp = inclusion.transform(existingOp, split
									.getSecond(), Boolean.TRUE);
				}
				transformedOp = split;
			} else {
				if (isClientSide()) {
					transformedOp = inclusion.transform(newOp, existingOp,
									Boolean.TRUE);
					existingOp = inclusion.transform(existingOp, newOp,
									Boolean.FALSE);
				} else {
					transformedOp = inclusion.transform(newOp, existingOp,
									Boolean.FALSE);
					existingOp = inclusion.transform(existingOp, newOp,
									Boolean.TRUE);
				}
			}
			ackRequestList.set(ackRequestListCnt, new OperationWrapper(
							existingOp, wrap.getLocalOperationCount()));

			newOp = transformedOp;
		}
		return newOp;
	}

	/**
	 * Test 3 preconditions that must be fulfilled before transforming. They are
	 * taken from the Jupiter paper.
	 * 
	 * @param jupReq
	 *            the request to be tested.
	 */
	private void checkPreconditions(JupiterVectorTime time) {
		if (!ackRequestList.isEmpty()
						&& time.getRemoteOperationCount() < ((OperationWrapper) ackRequestList
							   .get(0)).getLocalOperationCount()) {
			throw new JupiterException("precondition #1 violated.");
		} else if (time.getRemoteOperationCount() > vectorTime
						.getLocalOperationCount()) {
			throw new JupiterException("precondition #2 violated.");
		} else if (time.getLocalOperationCount() != vectorTime
						.getRemoteOperationCount()) {
			throw new JupiterException("precondition #3 violated: " + time + " , " + vectorTime);
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

		/**
		 * {@inheritDoc}
		 */
		public String toString() {
			return ("OperationWrapper(" + op + ", " + count + ")");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Request undo() {
		throw new CannotUndoException();
	}

	/**
	 * {@inheritDoc}
	 */
	public Request redo() {
		throw new CannotRedoException();
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
		return isClientSide() ? 1 : 0;
	}

	/**
	 * Originally intended for test purpose.
	 * 
	 * @return the algo's vector time.
	 */
	public JupiterVectorTime getVectorTime() {
		return vectorTime;
	}

	/**
	 * Checks if this algorithm locates client side.
	 * 
	 * @return true if this algorithm locates client side
	 */
	public boolean isClientSide() {
		return isClientSide;
	}

	/**
	 * Returns true if this algorithm can undo operations.
	 * 
	 * @return true if this algorithm can undo operations
	 */
	public boolean canUndo() {
		return false;
	}

	/**
	 * Returns true if this algorithm can redo operations.
	 * 
	 * @return true if this algorithm can redo operations
	 */
	public boolean canRedo() {
		return false;
	}
	
}
