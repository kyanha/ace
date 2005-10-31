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
package ch.iserver.ace.test.jupiter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.test.AlgorithmTestFactory;
import ch.iserver.ace.test.DefaultDocument;
import ch.iserver.ace.test.ExecuteVisitor;
import ch.iserver.ace.test.ReceptionNode;
import ch.iserver.ace.test.RelayNode;
import ch.iserver.ace.test.ScenarioException;
import ch.iserver.ace.test.StartNode;

/**
 * This execute visitor is to be used with jupiter scenarios with a central
 * server. First of all, it creates additional algorithms for the server side
 * and second it knows how to handle relay nodes.
 */
public class NWayExecuteVisitor extends ExecuteVisitor {
	/** private logger instance. */
	private static final Logger LOG = 
			Logger.getLogger(NWayExecuteVisitor.class);
	
	/** map from site ids to server side algorithms. */
	private Map serverAlgorithms;

	/**
	 * Creates a new NWayExecuteVisitor that uses the given
	 * AlgorithmTestFactory to create algorithms, documents and
	 * initial timestamps.
	 * 
	 * @param factory the factory
	 */
	public NWayExecuteVisitor(AlgorithmTestFactory factory) {
		super(factory);
		serverAlgorithms = new HashMap();
	}

	/**
	 * Visits a StartNode. This method creates all the necessary
	 * algorithms using the AlgorithmTestFactory. 
	 * 
	 * @param node the node to visit
	 */
	public void visit(StartNode node) {
		LOG.info("visit: " + node);
		setDocument(node.getParticipantId(), new DefaultDocument(node.getState()));
		Algorithm algorithm = getFactory().createAlgorithm(
				Integer.parseInt(node.getParticipantId()), Boolean.FALSE);
		setAlgorithm(node.getParticipantId(), algorithm);

		algorithm = getFactory().createAlgorithm(
				Integer.parseInt(node.getParticipantId()), Boolean.TRUE);
		setServerAlgorithm(node.getParticipantId(), algorithm);
	}

	/**
	 * Visits a RelayNode. This method shows how the
	 * server conceptually works. The implementation for the real server
	 * uses threads and synchronization techniques that are not necessary
	 * here.
	 * 
	 * @param node the node to visit
	 */
	public void visit(RelayNode node) {
		LOG.info("visit: " + node);
		String participantId = "" + node.getSenderParticipantId();
		Algorithm algo = getServerAlgorithm(participantId);
		Operation op = algo.receiveRequest(node.getRequest());
		LOG.info("receive from " + participantId + ": " + op);
		Iterator succ = node.getRemoteSuccessors().iterator();
		while (succ.hasNext()) {
			ReceptionNode remote = (ReceptionNode) succ.next();
			algo = getServerAlgorithm(remote.getParticipantId());
			Request request = algo.generateRequest(op);
			remote.setRequest(participantId, request);
		}
	}

	/**
	 * Gets the server side algorithm for the given site. This method
	 * throws a {@link ScenarioException} if there is no server-side
	 * algorithm for the given side. The return value of this method
	 * is never null.
	 * 
	 * @param siteId the site id of the site
	 * @return the server side algorithm for the site
	 * @throws ScenarioException if there is no server side algorithm
	 */
	protected Algorithm getServerAlgorithm(String siteId) {
		Algorithm algo = (Algorithm) serverAlgorithms.get(siteId);
		if (algo == null) {
			throw new ScenarioException("unknown site: " + siteId);
		}
		return algo;
	}
	
	/**
	 * Sets the server side algorithm for the given site.
	 * 
	 * @param siteId the site
	 * @param algorithm the server side algorithm for the site
	 */
	protected void setServerAlgorithm(String siteId, Algorithm algorithm) {
		serverAlgorithms.put(siteId, algorithm);
	}
	
}