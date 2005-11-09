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

package ch.iserver.ace.collaboration;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import ch.iserver.ace.collaboration.jupiter.MutableRemoteUser;
import ch.iserver.ace.util.CompareUtil;

/**
 *
 */
public class RemoteUserStub implements MutableRemoteUser {
	
	private final PropertyChangeSupport support = new PropertyChangeSupport(this);
	
	private final String id;
	
	private String name;
	
	/**
	 * @param id
	 */
	public RemoteUserStub(String id) {
		this.id = id;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.RemoteUser#getId()
	 */
	public String getId() {
		return id;
	}
		
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.MutableRemoteUser#setName(java.lang.String)
	 */
	public void setName(String name) {
		String old = this.name;
		if (!CompareUtil.nullSafeEquals(old, name)) {
			this.name = name;
			support.firePropertyChange(NAME_PROPERTY, old, name);
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.RemoteUser#getName()
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.RemoteUser#invite(ch.iserver.ace.collaboration.PublishedSession)
	 */
	public void invite(PublishedSession session) {
		// ignore
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.RemoteUser#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.RemoteUser#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof RemoteUser) {
			RemoteUser user = (RemoteUser) obj;
			return id.equals(user.getId());
		}
		return super.equals(obj);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getId().hashCode();
	}
		
}