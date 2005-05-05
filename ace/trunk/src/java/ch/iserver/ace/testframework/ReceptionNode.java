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

import ch.iserver.ace.algorithm.Request;

/**
 * A reception node represents the reception of a remote request.
 * Such a node must have the following properties.
 *
 * <ul>
 *  <li>at most one local successor</li>
 *  <li>exactly two predecessor, one local the other remote</li>
 * </ul>
 */
public class ReceptionNode extends AbstractNode {
	private Request request;
	private String ref;
	
	public ReceptionNode(String siteId, String ref) {
		super(siteId);
		this.ref = ref;
	}
	
	public String getReference() {
		return ref;
	}
	
	public void setRequest(Request request) {
		this.request = request;
	}
	
	public Request getRequest() {
		return request;
	}
	
	public List getSuccessors() {
		List result = new ArrayList();
		if (getLocalSuccessor() != null) {
			result.add(getLocalSuccessor());
		}
		return result;
	}
	
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
	
	public String toString() {
		return getClass().getName() + "[site=" + getSiteId() + ",ref=" + ref + "]";
	}
	
}
