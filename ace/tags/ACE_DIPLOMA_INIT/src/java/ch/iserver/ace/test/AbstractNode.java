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
 * Abstract base class for node implementations. Both site id and
 * local successor properties are handled in this base class.
 */
public abstract class AbstractNode implements Node {
	/** the site this node belongs to. */
	private final String siteId;
	/** the local successor of this node. */
	private Node localSuccessor;
	
	/**
	 * Creates a new abstract node belonging to the given site.
	 * 
	 * @param siteId the site this node belongs to
	 */
	protected AbstractNode(String siteId) {
		if (siteId == null) {
			throw new IllegalArgumentException("siteId cannot be null");
		}
		this.siteId = siteId;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getSiteId() {
		return siteId;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setLocalSuccessor(Node successor) {
		this.localSuccessor = successor;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Node getLocalSuccessor() {
		return localSuccessor;
	}
	
}
