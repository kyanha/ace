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
	
	/** the number of sites generated so far */
	private int siteCount;
	
	/**
	 * @inheritDoc
	 */
	public void init(String initialState, String finalState) {
		this.initialState = initialState;
		this.finalState = finalState;
		this.localPredecessors = new HashMap();
		this.startNodes = new HashMap();
		this.siteHelpers = new HashMap();
		this.generationNodes = new HashMap();
		this.receptionNodes = new ArrayList();
		this.nodes = new HashSet();
		this.siteCount = 0;
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
		StartNode node = new StartNode(siteId, initialState, siteCount++);
		addNode(node);
		startNodes.put(siteId, node);
		setPredecessor(siteId, node);
		siteHelpers.put(siteId, new SiteHelper());
	}

	/**
	 * @inheritDoc
	 */
	public void addGeneration(final String id, final Operation op) {
		if (siteId == null) {
			throw new ScenarioException("no previous startSite call");
		}

		Node node = new DoNode(siteId, id, op);
		addNode(node);
		Node pred = getPredecessor(siteId);
		pred.setLocalSuccessor(node);
		addGeneratedOperation(id, node);
		setPredecessor(siteId, node);
		addToSiteGeneration(siteId, id);
	}
	
	/**
	 * @inheritDoc
	 */
	public void addUndoGeneration(String id) {
		Node node = new UndoNode(siteId, id);
		addNode(node);
		Node pred = getPredecessor(siteId);
		pred.setLocalSuccessor(node);
		addGeneratedOperation(id, node);
		setPredecessor(siteId, node);
		addToSiteGeneration(siteId, id);
	}
	
	/**
	 * @inheritDoc
	 */
	public void addRedoGeneration(String id) {
		Node node = new RedoNode(siteId, id);
		addNode(node);
		Node pred = getPredecessor(siteId);
		pred.setLocalSuccessor(node);
		addGeneratedOperation(id, node);
		setPredecessor(siteId, node);
		addToSiteGeneration(siteId, id);
	}

	/**
	 * @inheritDoc
	 */
	public void addReception(String ref) {
		if (siteId == null) {
			throw new ScenarioException("no previous startSite call");
		}

		Node node = new SimpleReceptionNode(siteId, ref);
		addNode(node);
		Node pred = getPredecessor(siteId);
		pred.setLocalSuccessor(node);
		receptionNodes.add(node);
		setPredecessor(siteId, node);
		addToSiteReception(siteId, ref);
	}
	
	/**
	 * @inheritDoc
	 */
	public void addVerification(String expect) {
		if (siteId == null) {
			throw new ScenarioException("no previous startSite call");
		}
		
		Node node = new VerificationNode(siteId, expect);
		addNode(node);
		Node pred = getPredecessor(siteId);
		pred.setLocalSuccessor(node);
		setPredecessor(siteId, node);
	}
	
	/**
	 * @inheritDoc
	 */
	public void addRelay(String ref, String id) {
		if (ref == null) {
			throw new ScenarioException("operation reference cannot be null");
		}
		if (id == null) {
			throw new ScenarioException("id cannot be null");
		}
		RelayNode node = new RelayNode("server", ref, id);
		addNode(node);
		// 1) reception
		receptionNodes.add(node);
		// 2) local successors
		Node pred = getPredecessor(siteId);
		if (pred != null) {
			pred.setLocalSuccessor(node);
			node.setPredecessor(pred);
		}
		setPredecessor(siteId, node);
		// 3) generation
		addGeneratedOperation(id, node);
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
		setPredecessor(siteId, null);
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
		List nodes = validateDAG();
		
		// 3) create result
		return new Scenario(initialState, finalState, nodes);
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
		
	private Node getPredecessor(String siteId) {
		return (Node) localPredecessors.get(siteId);
	}
	
	private void setPredecessor(String siteId, Node node) {
		localPredecessors.put(siteId, node);
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
