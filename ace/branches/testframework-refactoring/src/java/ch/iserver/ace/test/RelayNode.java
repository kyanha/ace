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
import java.util.LinkedList;
import java.util.List;

import ch.iserver.ace.algorithm.Request;

/**
 * TODO: add javadoc comments
 */
public class RelayNode extends AbstractNode implements ReceptionNode, GenerationNode {
	/** the incoming request */
	private Request request;
	/** the referenced operation */
	private String reference;
	/** the id of this generation event */
	private String id;
	/** list of remote successors */
	private List remoteSuccessors;
	
	public RelayNode(String siteId, String reference, String id) {
		super(siteId);
		this.reference = reference;
		this.id = id;
		this.remoteSuccessors = new LinkedList();
	}
	
	public String getReference() {
		return reference;
	}
	
	public Request getRequest() {
		return request;
	}
	
	public void setRequest(Request request) {
		this.request = request;
	}
	
	public void addRemoteSuccessor(ReceptionNode successor) {
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
		result.addAll(remoteSuccessors);
		return result;
	}

	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
