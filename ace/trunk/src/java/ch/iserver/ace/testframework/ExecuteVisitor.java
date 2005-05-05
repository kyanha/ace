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

package ch.iserver.ace.testframework;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;

/**
 *
 */
public class ExecuteVisitor implements NodeVisitor {
	private AlgorithmTestFactory factory;
	private Map algorithms;

	public ExecuteVisitor(AlgorithmTestFactory factory) {
		this.factory = factory;
		this.algorithms = new HashMap();
	}
	
	public AlgorithmTestFactory getFactory() {
		return factory;
	}
	
	public void visit(StartNode node) {
		String siteId = node.getSiteId();
		Algorithm algorithm = getFactory().createAlgorithm();
		// TODO: get real document model
		DocumentModel document = new DocumentModel() {	
			public void apply(Operation operation) {
				
			}
		};
		Timestamp timestamp = getFactory().createTimestamp();
		algorithm.init(document, timestamp);
		algorithms.put(siteId, algorithm);
	}

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

	public void visit(ReceptionNode node) {
		String siteId = node.getSiteId();
		Request request = node.getRequest();
		Algorithm algo = getAlgorithm(siteId);
		algo.receiveRequest(request);
	}

	protected Algorithm getAlgorithm(String siteId) {
		Algorithm algo = (Algorithm) algorithms.get(siteId);
		if (algo == null) {
			throw new ScenarioException("unknown site: " + siteId);
		}
		return algo;
	}
	
}
