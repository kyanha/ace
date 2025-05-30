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


/**
 * A node implementation that represents an undo generation event. This
 * is a generation node. In contrast to a DoNode, no explicit operation
 * is required as the operation is implicitely known by the algorithm.
 */
public class UndoNode extends AbstractGenerationNode {

	/**
	 * Creates a new undo node.
	 * 
	 * @param siteId the site id of the site this node resides on
	 * @param eventId the id of the event
	 */
	public UndoNode(String siteId, String eventId) {
		super(siteId, eventId);
	}
	
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * @return a string representation of this node
	 */
	public String toString() {
		return getClass().getName() + "["
				+ "siteId=" + getParticipantId()
				+ ",eventId=" + getEventId()
				+ "]";
	}

}
