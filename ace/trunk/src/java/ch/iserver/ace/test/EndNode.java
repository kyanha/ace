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
 *
 */
public class EndNode extends AbstractNode {
	private String state;
	
	public EndNode(String siteId, String state) {
		super(siteId);
		this.state = state;
	}
	
	public String getState() {
		return state;
	}

	public void setLocalSuccessor(Node successor) {
		throw new UnsupportedOperationException("EndNode has no successors");
	}
	
	public List getSuccessors() {
		return new ArrayList();
	}
	
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
	
}
