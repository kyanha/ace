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

package ch.iserver.ace.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;

/**
 * An ExecuteVisitor is a special node visitor implementation that executes a
 * scenario. The ExecuteVistor makes use of a
 * {@link ch.iserver.ace.test.AlgorithmTestFactory} to create algorithms,
 * timestamps and documents. ExecuteVisitor should only be used on a sequence of
 * nodes that is correctly ordered (e.g. operations are generated before they
 * are received).
 * <p>
 * A {@link ch.iserver.ace.test.Scenario} contains such an ordered sequence of
 * nodes. Use {@link ch.iserver.ace.test.Scenario#accept(NodeVisitor)} to visit
 * this sequence of nodes.
 * </p>
 */
public class ExecuteVisitor implements NodeVisitor {
	/** logger class */
	private static final Logger LOG = Logger.getLogger(ExecuteVisitor.class);
	
	/** the factory used to create the needed objects */
	private AlgorithmTestFactory factory;

	/** map from site ids to algorithms */
	private Map algorithms;

	/**
	 * Creates a new execute visitor using the given <var>factory</var>.
	 * 
	 * @param factory
	 *            the factory to create needed components
	 */
	public ExecuteVisitor(AlgorithmTestFactory factory) {
		this.factory = factory;
		this.algorithms = new HashMap();
	}

	/**
	 * Gets the algorithm test factory, which is needed to create algorithms,
	 * timestamps and documents.
	 * 
	 * @return the algorithm test factory
	 */
	public AlgorithmTestFactory getFactory() {
		return factory;
	}
	
	/**
	 * Adds an algorithm for the given site id. This method exists manly
	 * for testing purposes. It helps to circumvent calling visit for
	 * a start node in order to create an algorithm.
	 * 
	 * @param siteId the site id
	 * @param algorithm the algorithm
	 */
	void addAlgorithm(String siteId, Algorithm algorithm) {
		setAlgorithm(siteId, algorithm);
	}

	/**
	 * Visits a start node. It initializes the algorithm at the site represented
	 * by this node.
	 * 
	 * @param node
	 *            the node to visit
	 */
	public void visit(StartNode node) {
		LOG.info("visit: " + node);
		String state = node.getState();
		Algorithm algorithm = getFactory().createAlgorithm(
				Integer.parseInt(node.getSiteId()), null);
		DocumentModel document = getFactory().createDocument(state);
		Timestamp timestamp = getFactory().createTimestamp();
		algorithm.init(document, timestamp);
		setAlgorithm(node.getSiteId(), algorithm);
	}
	
	/**
	 * Visits a generation node. It passes the stored operation to the algorithm
	 * to create a request. The request is then set on all the remote successor
	 * nodes (i.e. reception nodes).
	 * 
	 * @param node
	 *            the node to visit
	 */
	public void visit(DoNode node) {
		LOG.info("visit: " + node);
		Operation op = node.getOperation();
		Algorithm algo = getAlgorithm(node.getSiteId());
		Request request = algo.generateRequest(op);
		Iterator it = node.getRemoteSuccessors().iterator();
		while (it.hasNext()) {
			ReceptionNode remote = (ReceptionNode) it.next();
			remote.setRequest(request);
		}
	}

	/**
	 * Visits an undo node. Calls undo on the local algorithm to get a request
	 * that is then sent to all remote successors.
	 * 
	 * @param node the node to visit
	 */
	public void visit(UndoNode node) {
		LOG.info("visit: " + node);
		Algorithm algo = getAlgorithm(node.getSiteId());
		Request request = algo.undo();
		Iterator it = node.getRemoteSuccessors().iterator();
		while (it.hasNext()) {
			ReceptionNode remote = (ReceptionNode) it.next();
			remote.setRequest(request);
		}
	}

	/**
	 * Visits a redo node. Calls redo on the local algorithm to get a request
	 * that is then sent to all remote successors.
	 * 
	 * @param node the node to visit
	 */
	public void visit(RedoNode node) {
		LOG.info("visit: " + node);
		Algorithm algo = getAlgorithm(node.getSiteId());
		Request request = algo.redo();
		Iterator it = node.getRemoteSuccessors().iterator();
		while (it.hasNext()) {
			ReceptionNode remote = (ReceptionNode) it.next();
			remote.setRequest(request);
		}
	}

	/**
	 * Visits a reception node. The stored request is retrieved from the node
	 * and passed to the correct algorithm.
	 * 
	 * @param node
	 *            the node to visit
	 */
	public void visit(ReceptionNode node) {
		LOG.info("visit: " + node);
		Request request = node.getRequest();
		Algorithm algo = getAlgorithm(node.getSiteId());
		algo.receiveRequest(request);
	}

	/**
	 * Visits a relay node. Here the server side processing must be handled.
	 */
	public void visit(RelayNode node) {
		// this type of visitor does not handle relay nodes
	}

	/**
	 * Visits a verification node. This node type is used to verify the document
	 * content at an arbitrary point in the sites lifecycle.
	 * 
	 * @param node
	 *            the node to visit
	 * @throws VerificationException
	 *             if the document state does not match the expected state
	 */
	public void visit(VerificationNode node) {
		LOG.info("visit: " + node);
		verify(node.getSiteId(), node.getState());
	}

	/**
	 * Visits an end node. This is the place where actual verification takes
	 * place. The end node stores the expected content. This content is compared
	 * to the actual document at the site. If they do not match a
	 * {@link VerificationException} is thrown.
	 * 
	 * @param node
	 *            the node to visit
	 * @throws VerificationException
	 *             if the document state does not match the expected state
	 */
	public void visit(EndNode node) {
		LOG.info("visit: " + node);
		verify(node.getSiteId(), node.getState());
	}

	/**
	 * Verifies that the current state at the given site corresponds to the
	 * given state.
	 * 
	 * @param siteId
	 *            the site to verify
	 * @param state
	 *            the expected state
	 * @throws VerificationException
	 *             if the document state does not match the expected state
	 */
	protected void verify(final String siteId, final String state) {
		Algorithm algo = getAlgorithm(siteId);
		DocumentModel expected = getFactory().createDocument(state);
		DocumentModel actual = algo.getDocument();
		if (!expected.equals(actual)) {
			throw new VerificationException(siteId, 
					nullSafeToString(expected), 
					nullSafeToString(actual));
		}
	}
	
	private String nullSafeToString(Object o) {
		return o == null ? "" : o.toString();
	}

	/**
	 * Gets the algorithm for the given site. Throws a ScenarioException if an
	 * algorithm for the given site does not exist.
	 * 
	 * @param siteId
	 *            the site for which the algorithm is requested
	 * @return the algorithm for the site
	 * @throws ScenarioException
	 *             if there is no algorithm for the site
	 */
	public Algorithm getAlgorithm(String siteId) {
		Algorithm algo = (Algorithm) algorithms.get(siteId);
		if (algo == null) {
			throw new ScenarioException("unknown site: " + siteId);
		}
		return algo;
	}
	
	/**
	 * Sets the algorithm for the given site.
	 * 
	 * @param siteId the site id of the site
	 * @param algorithm the algorithm
	 */
	protected void setAlgorithm(String siteId, Algorithm algorithm) {
		algorithms.put(siteId, algorithm);
	}

}
