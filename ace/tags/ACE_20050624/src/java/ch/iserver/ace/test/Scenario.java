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

import java.util.Collection;
import java.util.Iterator;

/**
 * 
 *
 */
public class Scenario {
	private String initialState;
	private String finalState;
	private Collection nodes;
	
	/**
	 * 
	 * @param initialState
	 * @param finalState
	 * @param nodes
	 */
	public Scenario(String initialState, String finalState, Collection nodes) { 
		this.initialState = initialState;
		this.finalState = finalState;
		this.nodes = nodes;
	}
		
	/**
	 * 
	 * @return String
	 */
	public String getInitialState() {
		return initialState;
	}
	
	/**
	 * 
	 * @return String
	 */
	public String getFinalState() {
		return finalState;
	}
			
	/**
	 * 
	 * @param visitor
	 */
	public void accept(NodeVisitor visitor) {
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			node.accept(visitor);
		}
	}
	
}
