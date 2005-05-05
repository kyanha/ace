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

import java.util.ArrayList;
import java.util.List;

import ch.iserver.ace.Operation;

/**
 * A generation node represents the local generation of a 
 * new operation. Such a node must have the following properties.
 * 
 * <ul>
 *  <li>exactly one predecessor node</li>
 *  <li>exactly n successor nodes where n is the number of sites</li>
 *  <li>exactly one successor must be a local successor (i.e. from the same site)</li>
 * </ul>
 */
public class GenerationNode extends AbstractNode {
	private List remoteSuccessors = new ArrayList();
	private Operation operation;
	private String reference;
	
	public GenerationNode(String siteId, String ref, Operation op) {
		super(siteId);
		this.operation = op;
		this.reference = ref;
	}

	public Operation getOperation() {
		return operation;
	}
	
	public String getReference() {
		return reference;
	}

	public void addRemoteSuccessor(Node successor) {
		remoteSuccessors.add(successor);
	}
	
	public List getRemoteSuccessors() {
		return remoteSuccessors;
	}
	
	public List getSuccessors() {
		List result = new ArrayList();
		if (getLocalSuccessor() != null) {
			result.add(getLocalSuccessor());
		}
		result.addAll(getRemoteSuccessors());
		return result;
	}

	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
	
	public String toString() {
		return getClass().getName() + "[site=" + getSiteId() + ",ref=" 
				+ getReference() + "]";
	}
	
}
