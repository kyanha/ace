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
import ch.iserver.ace.text.SplitOperation;

/**
 * This class implements the client-side core of the Jupiter control algorithm.
 */
public class Jupiter implements Algorithm {

	private static final Logger LOG = Logger.getLogger(Jupiter.class);

	private InclusionTransformation inclusion;

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
	private List ackRequestList;

	/**
	 * Class constructor that creates a new Jupiter algorithm. The parameters
	 * fully initialize the algorithm.
	 * 
	 * @param it
	 *            the inclusion transformation to be used
	 * @param document
	 *            the inital document model
	 * @param siteId
	 *            the site id
	 * @param isClientSide
	 *            true if the algorithm resides on the client side
	 */
	public Jupiter(InclusionTransformation it, DocumentModel document,
					int siteId, boolean isClientSide) {
		inclusion = it;
		this.siteId = siteId;
		init(document, new JupiterVectorTime(0, 0));
		ackRequestList = new ArrayList();
		this.isClientSide = isClientSide;
	}

	/**
	 * Class constructor that creates a new Jupiter algorithm. Afterwards, the
	 * method {@link #init(DocumentModel, Timestamp)}needs to be called to
	 * initialize the algorithm.
	 * 
	 * @param siteId
	 *            the site Id of this algorithm.
	 * @param isClientSide
	 *            true if the algorithm resides on the client side
	 */
	public Jupiter(int siteId, boolean isClientSide) {
		this.siteId = siteId;
		this.isClientSide = isClientSide;
		ackRequestList = new ArrayList();
	}

	/**
	 * {@inheritDoc}
	 */
	public Request generateRequest(Operation op) {
		// apply op locally;
		if (op instanceof SplitOperation) {
			SplitOperation split = (SplitOperation) op;
			document.apply(split.getFirst());
			document.apply(split.getSecond());
		} else {
			document.apply(op);
		}

		// send(op, myMsgs, otherMsgs);
		Request req = new JupiterRequest(siteId, (JupiterVectorTime) vectorTime
						.clone(), op);

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
	public void receiveRequest(Request req) {
		LOG.info(">>> recv");
		JupiterRequest jupReq = (JupiterRequest) req;
		checkPreconditions(jupReq);
		LOG.info("ini:" + ackRequestList);

		// Discard acknowledged messages.
		discardOperations(jupReq);

		LOG.info("dsc:" + ackRequestList);

		Operation newOp = transform(jupReq.getOperation());

		apply(newOp);
		LOG.info("tnf:" + ackRequestList);

		vectorTime.incrementRemoteRequestCount();
		LOG.info("<<< recv");
	}

	/**
	 * Discard from the other site (client/server) acknowledged operations.
	 * 
	 * @param jupReq
	 *            the request to the remote operation count from
	 */
	private void discardOperations(JupiterRequest jupReq) {
		Iterator iter = ackRequestList.iterator();
		while (iter.hasNext()) {
			OperationWrapper wrap = (OperationWrapper) iter.next();
			if (wrap.getLocalOperationCount() < jupReq.getJupiterVectorTime()
							.getRemoteOperationCount()) {
				iter.remove();
			}
		}
		// ASSERT msg.myMsgs == otherMsgs
		assert jupReq.getJupiterVectorTime().getLocalOperationCount() == vectorTime
						.getRemoteOperationCount() : "msg.myMsgs != otherMsgs !!";
	}

	/**
	 * Applies an operation to the document model.
	 * 
	 * @param newOp
	 *            the operation to be applied
	 */
	private void apply(Operation newOp) {
		if (isClientSide && newOp instanceof SplitOperation) {
			SplitOperation split = (SplitOperation) newOp;
			if (split.getSecond() instanceof SplitOperation) {
				apply(split.getSecond());
			} else {
				document.apply(split.getSecond());
			}
			if (split.getFirst() instanceof SplitOperation) {
				apply(split.getFirst());
			} else {
				document.apply(split.getFirst());
			}
		} else {
			document.apply(newOp);
		}
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
	private void checkPreconditions(JupiterRequest jupReq) {
		if (!ackRequestList.isEmpty()
						&& jupReq.getJupiterVectorTime()
										.getRemoteOperationCount() < ((OperationWrapper) ackRequestList
										.get(0)).getLocalOperationCount()) {
			throw new JupiterException("precondition #1 violated.");
		} else if (jupReq.getJupiterVectorTime().getRemoteOperationCount() > vectorTime
						.getLocalOperationCount()) {
			throw new JupiterException("precondition #2 violated.");
		} else if (jupReq.getJupiterVectorTime().getLocalOperationCount() != vectorTime
						.getRemoteOperationCount()) {
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
	public void init(DocumentModel doc, Timestamp timestamp) {
		if (doc == null || timestamp == null) {
			throw new IllegalArgumentException("null parameter not allowed.");
		}
		document = doc;
		// TODO: remove nasty cast
		vectorTime = (JupiterVectorTime) timestamp;
	}

	/**
	 * {@inheritDoc}
	 */
	public DocumentModel getDocument() {
		return document;
	}

	/**
	 * {@inheritDoc}
	 */
	public Request undo() {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public Request redo() {
		throw new UnsupportedOperationException();
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
