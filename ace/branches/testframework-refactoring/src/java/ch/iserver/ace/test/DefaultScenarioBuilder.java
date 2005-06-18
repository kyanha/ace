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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ch.iserver.ace.Operation;

/**
 * The default scenario builder implementation creates a scenario object.
 */
public class DefaultScenarioBuilder implements ScenarioBuilder {

	/** the initial state of the scenario */
	private String initialState;
	
	/** the final state of the scenario */
	private String finalState;
	
	/** the current site id */
	private String siteId;
	
	/** map of operation (maps operation id to operation) */
	private Map operations;
	
	/** map of local predecessor (maps site ids to the last node at the site) */
	private Map localPredecessors;
	
	/** map of start nodes (maps site ids to StartNode objects) */
	private Map startNodes;
	
	/** map of site helper objects (maps site ids to SiteHelper objects) */
	private Map siteHelpers;
	
	/** map of generation nodes (maps operation ids to generation nodes) */
	private Map generationNodes;
	
	/** list of reception nodes */
	private List receptionNodes;
	
	/** set of all nodes */
	private Set nodes;
	
	/**
	 * @inheritDoc
	 */
	public void init(String initialState, String finalState) {
		this.initialState = initialState;
		this.finalState = finalState;
		this.operations = new HashMap();
		this.localPredecessors = new HashMap();
		this.startNodes = new HashMap();
		this.siteHelpers = new HashMap();
		this.generationNodes = new HashMap();
		this.receptionNodes = new ArrayList();
		this.nodes = new HashSet();
	}
	
	/**
	 * @inheritDoc
	 */
	public void addOperation(String id, Operation op) {
		operations.put(id, op);
	}
	
	public void addUndo(String id) {
	
	}
	
	public void addRedo(String redo) {
		
	}

	protected void addNode(Node node) {
		nodes.add(node);
	}
	
	protected Set getNodes() {
		return nodes;
	}
	
	/**
	 * @inheritDoc
	 */
	public void startSite(String siteId) {
		if (this.siteId != null) {
			throw new ScenarioException("sites cannot nest");
		}
		this.siteId = siteId;
		StartNode node = new StartNode(siteId, initialState);
		addNode(node);
		startNodes.put(siteId, node);
		localPredecessors.put(siteId, node);
		siteHelpers.put(siteId, new SiteHelper());
	}

	/**
	 * @inheritDoc
	 */
	public void addGeneration(String ref) {
		if (siteId == null) {
			throw new ScenarioException("no previous startSite call");
		}
		if (isGenerated(ref)) {
			throw new ScenarioException("operation already generated");
		}

		Operation op = getOperation(ref);
		Node node = new DoNode(siteId, ref, op);
		addNode(node);
		Node pred = getPredecessor(siteId);
		pred.setLocalSuccessor(node);
		addGeneratedOperation(ref, node);
		localPredecessors.put(siteId, node);
		addToSiteGeneration(siteId, ref);
	}
	
	/**
	 * @inheritDoc
	 */
	public void addUndoGeneration(String ref) {
		Node node = new UndoNode(siteId);
		addNode(node);
		Node pred = getPredecessor(siteId);
		pred.setLocalSuccessor(node);
		addGeneratedOperation(ref, node);
		localPredecessors.put(siteId, node);
		addToSiteGeneration(siteId, ref);
	}
	
	/**
	 * @inheritDoc
	 */
	public void addRedoGeneration(String ref) {
		Node node = new RedoNode(siteId);
		addNode(node);
		Node pred = getPredecessor(siteId);
		pred.setLocalSuccessor(node);
		addGeneratedOperation(ref, node);
		localPredecessors.put(siteId, node);
		addToSiteGeneration(siteId, ref);
	}

	/**
	 * @inheritDoc
	 */
	public void addReception(String ref) {
		if (siteId == null) {
			throw new ScenarioException("no previous startSite call");
		}

		Node node = new ReceptionNode(siteId, ref);
		addNode(node);
		Node pred = getPredecessor(siteId);
		pred.setLocalSuccessor(node);
		receptionNodes.add(node);
		localPredecessors.put(siteId, node);
		addToSiteReception(siteId, ref);
	}
	
	/**
	 * @inheritDoc
	 */
	public void endSite() {
		if (siteId == null) {
			throw new ScenarioException("no previous startSite call");
		}
		
		Node node = new EndNode(siteId, finalState);
		addNode(node);
		Node pred = getPredecessor(siteId);
		pred.setLocalSuccessor(node);
		localPredecessors.put(siteId, null);
		siteId = null;
	}
	
	/**
	 * Finishes the build process and returns the scenario object.
	 * 
	 * @return the built scenario object
	 */
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
	
	/**
	 * Validates some final conditions on all the nodes. These include that:
	 *
	 * <ul>
	 *  <li>an operation is received (n - 1) times (where n is the number of sites)</li>
	 *  <li>an operation is generated</li>
	 * </ul>
	 */
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
	
	/**
	 * Validates that all the operations form a directed acyclic graph 
	 * and returns a topologically ordered list of nodes.
	 * 
	 * @return a topologically ordered list of nodes
	 * @throws ScenarioException if the graph is not a DAG
	 */
	protected List validateDAG() {
		return GraphUtil.topologicalSort(getNodes());
	}
	
	
	// --> internal helper methods <--
	
	private int getSitesCount() {
		return startNodes.size();
	}
	
	private Operation getOperation(String id) {
		Operation op = (Operation) operations.get(id);
		if (op == null) {
			throw new ScenarioException("unkown operation '" + id + "'");
		}
		return op;
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
	
	/**
	 * Basic helper object for site related checks.
	 */
	private static class SiteHelper {
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
