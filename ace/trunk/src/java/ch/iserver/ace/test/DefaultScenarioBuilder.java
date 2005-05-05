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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ch.iserver.ace.Operation;

/**
 * 
 *
 */
public class DefaultScenarioBuilder implements ScenarioBuilder {

	private String initialState;
	private String finalState;
	
	private Map operations;
	private Map localPredecessors;
	private Map startNodes;
	private Map siteHelpers;
	private Map generationNodes;
	
	private List receptionNodes;
	
	public void init(String initialState, String finalState) {
		this.initialState = initialState;
		this.finalState = finalState;
		this.operations = new HashMap();
		this.localPredecessors = new HashMap();
		this.startNodes = new HashMap();
		this.siteHelpers = new HashMap();
		this.generationNodes = new HashMap();
		this.receptionNodes = new ArrayList();
	}
	
	public void addOperation(String id, Operation op) {
		operations.put(id, op);
	}

	public void addSite(String siteId) {
		StartNode node = new StartNode(siteId);
		startNodes.put(siteId, node);
		localPredecessors.put(siteId, node);
		siteHelpers.put(siteId, new SiteHelper());
	}

	public void addGeneration(String siteId, String ref) {
		Operation op = getOperation(ref);
		assert op != null;
		if (isGenerated(ref)) {
			throw new ScenarioException("operation already generated");
		}
		Node node = new GenerationNode(siteId, ref, op);
		Node pred = getPredecessor(siteId);
		pred.setLocalSuccessor(node);
		addGeneratedOperation(ref, node);
		localPredecessors.put(siteId, node);
		addToSiteGeneration(siteId, ref);
	}

	public void addReception(String siteId, String ref) {
		Node node = new ReceptionNode(siteId, ref);
		Node pred = getPredecessor(siteId);
		pred.setLocalSuccessor(node);
		receptionNodes.add(node);
		localPredecessors.put(siteId, node);
		addToSiteReception(siteId, ref);
	}
	
	public Scenario getScenario() {
		// 1) create 'cross-site' edges
		Iterator it = getReceptionNodes().iterator();
		while (it.hasNext()) {
			ReceptionNode target = (ReceptionNode) it.next();
			String ref = target.getReference();
			GenerationNode source = getGenerationNode(ref);
			source.addRemoteSuccessor(target);
		}
		
		// 2) validate graph
		validateOperationUsage();
		List nodes = validateDAG();
		
		// 3) create result
		return new Scenario(initialState, finalState, nodes);
	}
	
	protected void validateOperationUsage() {
		int sites = getSitesCount();
		Iterator it = operations.keySet().iterator();
		while (it.hasNext()) {
			String id = (String) it.next();
			int count = getReceptionNodes(id).size();
			if (count != sites - 1) {
				throw new ScenarioException("operation must be received " + (sites - 1) + " times");
			}
			if (!isGenerated(id)) {
				throw new ScenarioException("operation must be generated");
			}
		}
	}
	
	protected List validateDAG() {
		List result = new ArrayList();
		
		List stack = new ArrayList();
		Iterator it = getStartNodes().iterator();
		while (it.hasNext()) {
			stack.add(it.next());
		}
		
		int sites = getSitesCount();
		int total = sites + getReceptionNodes().size() + getGenerationNodes().size();
		Map incount = new HashMap();
		
		it = getReceptionNodes().iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			incount.put(node, new Integer(2));
		}
		
		it = getGenerationNodes().iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			incount.put(node, new Integer(1));
		}
		
		while (stack.size() > 0) {
			Node node = (Node) stack.remove(0);
			result.add(node);
			
			Iterator successors = node.getSuccessors().iterator();
			
			while (successors.hasNext()) {
				Node succ = (Node) successors.next();
				int count = ((Integer) incount.get(succ)).intValue() - 1;
				incount.put(succ, new Integer(count));
				if (count == 0) {
					stack.add(succ);
				}
			}
		}
		
		if (result.size() != total) {
			throw new ScenarioException("not a dag: " + result.size() + "," + total);
		}
		
		return result;
	}
	
	
	// --> internal helper methods <--
	
	private int getSitesCount() {
		return startNodes.size();
	}
	
	private Operation getOperation(String id) {
		return (Operation) operations.get(id);
	}
	
	private Node getPredecessor(String siteId) {
		return (Node) localPredecessors.get(siteId);
	}
	
	private Collection getStartNodes() {
		return startNodes.values();
	}
	
	private Collection getReceptionNodes() {
		return receptionNodes;
	}
	
	private List getReceptionNodes(String id) {
		List result = new ArrayList();
		Iterator it = receptionNodes.iterator();
		while (it.hasNext()) {
			ReceptionNode node = (ReceptionNode) it.next();
			if (id.equals(node.getReference())) {
				result.add(node);
			}
		}
		return result;
	}
	
	private Collection getGenerationNodes() {
		return generationNodes.values();
	}
	
	private GenerationNode getGenerationNode(String id) {
		return (GenerationNode) generationNodes.get(id);
	}
	
	private void addGeneratedOperation(String ref, Node node) {
		if (isGenerated(ref)) {
			throw new ScenarioException("operation " + ref + " already generated");
		}
		generationNodes.put(ref, node);
	}
	
	private boolean isGenerated(String id) {
		return generationNodes.containsKey(id);
	}
		
	private void addToSiteGeneration(String siteId, String ref) {
		SiteHelper helper = getSiteHelper(siteId);
		helper.addGeneration(ref);
	}
	
	private void addToSiteReception(String siteId, String ref) {
		SiteHelper helper = getSiteHelper(siteId);
		helper.addReception(ref);
	}
	
	private SiteHelper getSiteHelper(String siteId) {
		return (SiteHelper) siteHelpers.get(siteId);
	}
	
	
	// --> internal helper classes <--
	
	private class SiteHelper {
		private Set generated = new TreeSet();
		private Set received = new TreeSet();
		
		private void addGeneration(String id) {
			generated.add(id);
		}
		
		private void addReception(String ref) {
			if (generated.contains(ref)) {
				throw new ScenarioException("cannot receive local operation");
			}
			if (received.contains(ref)) {
				throw new ScenarioException("cannot receive the same operation twice");
			}
			received.add(ref);
		}
	}

}
