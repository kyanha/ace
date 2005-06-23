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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
 * TODO: comments
 */
public class NWayExecuteVisitor extends ExecuteVisitor {

	/** map from site ids to server side algorithms */
	private Map serverAlgorithms;

	public NWayExecuteVisitor(AlgorithmTestFactory factory) {
		super(factory);
		serverAlgorithms = new HashMap();
	}

	public void visit(StartNode node) {
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
		String siteId = "" + node.getRequest().getSiteId();
		Algorithm algo = getServerAlgorithm(siteId);
		algo.receiveRequest(node.getRequest());
		OperationExtractDocumentModel doc = (OperationExtractDocumentModel) 
				algo.getDocument();
		Operation op = doc.getOperation();
		Iterator it = getOtherServerAlgorithms(siteId);
		while (it.hasNext()) {
			algo = (Algorithm) it.next();
			Request request = algo.generateRequest(op);
			Iterator succ = node.getRemoteSuccessors().iterator();
			while (succ.hasNext()) {
				ReceptionNode remote = (ReceptionNode) succ.next();
				remote.setRequest(request);
			}
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

	protected Iterator getOtherServerAlgorithms(String siteId) {
		List result = new LinkedList();
		Iterator keys = serverAlgorithms.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (!siteId.equals(key)) {
				result.add(serverAlgorithms.get(key));
			}
		}
		return result.iterator();
	}

}