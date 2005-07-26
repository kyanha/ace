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

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.InclusionTransformation;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.StateSpace;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.text.SplitOperation;

/**
 * This class implements the client-side core of the Jupiter control algorithm.
 */
public class Jupiter implements Algorithm {

	private static final Logger LOG = Logger.getLogger(Jupiter.class);

	private InclusionTransformation inclusion;

	private DocumentModel document;

	private int siteId;

	private boolean isClientSide;
	
	private StateSpace statespace;

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
		this(siteId, isClientSide);
		inclusion = it;
		init(document, new JupiterVectorTime(0, 0));
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
		statespace = new JupiterStateSpace();
	}

	/**
	 * {@inheritDoc}
	 */
	public Request generateRequest(Operation op) {
		return generateRequest(op, false);
	}

	private Request generateRequest(Operation op, boolean isUndoRedo) {
		apply(op);
		return statespace.insert(op);
	}

	/**
	 * {@inheritDoc}
	 */
	public void receiveRequest(Request req) {
		// TODO: it is possible for the traffic to one side (client/server) to
		// be one-sided,
		// e.g. only one client writes text and the other sites are idle.
		// Therefore, each side must periodically generate explicit
		// acknowledgments
		// (i.e. no-op messages) to prevent the outgoing queues from growing
		// forever.
		apply(statespace.insert(req));
	}

	/**
	 * Applies an operation to the document model.
	 * 
	 * @param newOp
	 *            the operation to be applied
	 */
	private void apply(Operation newOp) {
//		if (isClientSide && newOp instanceof SplitOperation) {
		if (newOp instanceof SplitOperation) {
			SplitOperation split = (SplitOperation) newOp;
			document.apply(split.getSecond());
			document.apply(split.getFirst());
		} else {
			document.apply(newOp);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void init(DocumentModel doc, Timestamp timestamp) {
		//TODO: timestamp no longer used -> ask Räss
		if (doc == null || timestamp == null) {
			throw new IllegalArgumentException("null parameter not allowed.");
		}
		document = doc;
//		vectorTime = (JupiterVectorTime) timestamp;
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
	public void siteAdded(int siteId) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public void siteRemoved(int siteId) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public Request undo() {
		return statespace.undo();
	}

	/**
	 * {@inheritDoc}
	 */
	public Request redo() {
		return statespace.redo();	
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
		return (isClientSide()) ? statespace.canUndo() : false;
	}

	/**
	 * Returns true if this algorithm can redo operations.
	 * 
	 * @return true if this algorithm can redo operations
	 */
	public boolean canRedo() {
		return (isClientSide()) ? statespace.canRedo() : false;
	}
}
