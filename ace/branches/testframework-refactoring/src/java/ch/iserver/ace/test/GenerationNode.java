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

import java.util.Set;

/**
 * A generation node represents a node that generates an operation
 * that is sent as a request to all remote successors.
 */
public interface GenerationNode extends Node {
	
	/**
	 * Adds a remote successor to this node. A remote successor is
	 * the receiver of the request generated by this node.
	 * 
	 * @param successor the successor to add
	 */
	public void addRemoteSuccessor(ReceptionNode successor);
	
	/**
	 * Gets a set of remote successors.
	 * 
	 * @return a list of remote successors
	 */
	public Set getRemoteSuccessors();
	
}
