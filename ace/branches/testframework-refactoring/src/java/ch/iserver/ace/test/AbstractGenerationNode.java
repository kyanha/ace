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
import java.util.List;

/**
 * A GenerationNode is an abstract base class for all classes that generate an
 * operation. Subclasses include nodes for doing, undoing and redoing
 * operations.
 */
public abstract class AbstractGenerationNode extends AbstractNode 
		implements GenerationNode {
	
	/** list of remote successors (nodes that receive from this node) */
	private List remoteSuccessors = new ArrayList();

	protected AbstractGenerationNode(String siteId) {
		super(siteId);
	}

	/**
	 * Adds a node as remote successor of this node.
	 * 
	 * @param successor
	 *            the remote successor (a reception node)
	 */
	public void addRemoteSuccessor(ReceptionNode successor) {
		remoteSuccessors.add(successor);
	}

	/**
	 * Gets the list of remote successors from this node.
	 * 
	 * @return the list of remote successors
	 */
	public List getRemoteSuccessors() {
		return remoteSuccessors;
	}

	/**
	 * @inheritDoc
	 */
	public List getSuccessors() {
		List result = new ArrayList();
		if (getLocalSuccessor() != null) {
			result.add(getLocalSuccessor());
		}
		result.addAll(getRemoteSuccessors());
		return result;
	}

}