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

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.algorithm.jupiter.server.OperationExtractDocumentModel;
import ch.iserver.ace.test.AlgorithmTestFactory;
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
	/** private logger instance */
	private static final Logger LOG = Logger.getLogger(NWayExecuteVisitor.class);
	
	/** map from site ids to server side algorithms */
	private Map serverAlgorithms;

	public NWayExecuteVisitor(AlgorithmTestFactory factory) {
		super(factory);
		serverAlgorithms = new HashMap();
	}

	public void visit(StartNode node) {
		LOG.info("visit: " + node);
		String state = node.getState();
		Algorithm algorithm = getFactory().createAlgorithm(
				Integer.parseInt(node.getSiteId()), Boolean.TRUE);
		DocumentModel document = getFactory().createDocument(state);
		Timestamp timestamp = getFactory().createTimestamp();
		algorithm.init(document, timestamp);
		setAlgorithm(node.getSiteId(), algorithm);

		algorithm = getFactory().createAlgorithm(
				Integer.parseInt(node.getSiteId()), Boolean.FALSE);
		timestamp = getFactory().createTimestamp();
		document = new OperationExtractDocumentModel();
		algorithm.init(document, timestamp);
		setServerAlgorithm(node.getSiteId(), algorithm);
	}

	public void visit(RelayNode node) {
		LOG.info("visit: " + node);
		String siteId = "" + node.getRequest().getSiteId();
		Algorithm algo = getServerAlgorithm(siteId);
		algo.receiveRequest(node.getRequest());
		OperationExtractDocumentModel doc = (OperationExtractDocumentModel) 
				algo.getDocument();
		Operation op = doc.getOperation();
		LOG.info("receive from " + siteId + ": " + op);
		Iterator succ = node.getRemoteSuccessors().iterator();
		while (succ.hasNext()) {
			ReceptionNode remote = (ReceptionNode) succ.next();
			algo = getServerAlgorithm(remote.getSiteId());
			Request request = algo.generateRequest(op);
			LOG.info("send " + node.getId() + " to " + remote.getSiteId() + " (" + request + ")");
			remote.setRequest(request);
		}
	}

	protected Algorithm getServerAlgorithm(String siteId) {
		Algorithm algo = (Algorithm) serverAlgorithms.get(siteId);
		if (algo == null) {
			throw new ScenarioException("unknown site: " + siteId);
		}
		return algo;
	}
	
	protected void setServerAlgorithm(String siteId, Algorithm algorithm) {
		serverAlgorithms.put(siteId, algorithm);
	}

}
