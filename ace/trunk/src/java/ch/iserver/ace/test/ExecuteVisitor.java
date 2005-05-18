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

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;

/**
 * An ExecuteVisitor is a special node visitor implementation that executes 
 * a scenario. The ExecuteVistor makes use of a 
 * {@link ch.iserver.ace.test.AlgorithmTestFactory} to create algorithms,
 * timestamps and documents. ExecuteVisitor should only be used on a
 * sequence of nodes that is correctly ordered (e.g. operations are
 * generated before they are received).
 * <p>A {@link ch.iserver.ace.test.Scenario} contains such an ordered
 * sequence of nodes. Use {@link ch.iserver.ace.test.Scenario#accept(NodeVisitor)}
 * to visit this sequence of nodes.</p>
 */
public class ExecuteVisitor implements NodeVisitor {
	/** the factory used to create the needed objects */
	private AlgorithmTestFactory factory;
	/** map form site ids to algorithms */
	private Map algorithms;

	/**
	 * Creates a new execute visitor using the given <var>factory</var>.
	 * 
	 * @param factory the factory to create needed components
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
	 * Visits a start node. It initializes the algorithm at the site
	 * represented by this node.
	 * 
	 * @param node the node to visit
	 */
	public void visit(StartNode node) {
		String siteId = node.getSiteId();
		String state = node.getState();
		Algorithm algorithm = getFactory().createAlgorithm(Integer.parseInt(siteId));		
		DocumentModel document = getFactory().createDocument(state);
		Timestamp timestamp = getFactory().createTimestamp();
		algorithm.init(document, timestamp);
		algorithms.put(siteId, algorithm);
	}

	/**
	 * Visits a generation node. It passes the stored operation to the
	 * algorithm to create a request. The request is then set on all
	 * the remote successor nodes (i.e. reception nodes).
	 * 
	 * @param node the node to visit
	 */
	public void visit(GenerationNode node) {
		String siteId = node.getSiteId();
		Operation op = node.getOperation(); 
		Algorithm algo = getAlgorithm(siteId);
		Request request = algo.generateRequest(op);
		Iterator it = node.getRemoteSuccessors().iterator();
		while (it.hasNext()) {
			ReceptionNode remote = (ReceptionNode) it.next();
			remote.setRequest(request);
		}
	}

	/**
	 * Visits a reception node. The stored request is retrieved from the
	 * node and passed to the correct algorithm.
	 * 
	 * @param node the node to visit
	 */
	public void visit(ReceptionNode node) {
		String siteId = node.getSiteId();
		Request request = node.getRequest();
		Algorithm algo = getAlgorithm(siteId);
		algo.receiveRequest(request);
	}
	
	/**
	 * Visits an end node. This is the place where actual verification takes
	 * place. The end node stores the expected content. This content is
	 * compared to the actual document at the site. If they do not match
	 * a {@link VerificationException} is thrown.
	 * 
	 * @param node the node to visit
	 * @throws VerificationException if the document state does not match the
	 *         expected state
	 */
	public void visit(EndNode node) {
		Algorithm algo = getAlgorithm(node.getSiteId());
		DocumentModel expected = getFactory().createDocument(node.getState());
		if (!expected.equals(algo.getDocument())) {
			throw new VerificationException(node.getSiteId(), 
					expected.toString(), 
					algo.getDocument().toString());
		}
	}
	
	/**
	 * Gets the algorithm for the given site. Throws a ScenarioException
	 * if an algorithm for the given site does not exist.
	 * 
	 * @param siteId the site for which the algorithm is requested
	 * @return the algorithm for the site
	 * @throws ScenarioException if there is no algorithm for the site
	 */
	protected Algorithm getAlgorithm(String siteId) {
		Algorithm algo = (Algorithm) algorithms.get(siteId);
		if (algo == null) {
			throw new ScenarioException("unknown site: " + siteId);
		}
		return algo;
	}
	
}
