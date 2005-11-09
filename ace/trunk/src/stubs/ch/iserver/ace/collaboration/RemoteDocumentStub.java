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

import ch.iserver.ace.collaboration.jupiter.MutableRemoteDocument;
import ch.iserver.ace.util.CompareUtil;

/**
 *
 */
public class RemoteDocumentStub implements MutableRemoteDocument {
	
	private final PropertyChangeSupport support = new PropertyChangeSupport(this);
	
	private final String id;
	
	private String title;
	
	private final RemoteUser publisher;
	
	public RemoteDocumentStub(String id, String title, RemoteUser publisher) {
		this.id = id;
		this.title = title;
		this.publisher = publisher;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.MutableRemoteDocument#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		String old = this.title;
		if (CompareUtil.nullSafeEquals(old, title)) {
			this.title = title;
			support.firePropertyChange(TITLE_PROPERTY, old, title);
		}
	}

	/**
	 * @see ch.iserver.ace.collaboration.RemoteDocument#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * @see ch.iserver.ace.collaboration.RemoteDocument#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @see ch.iserver.ace.collaboration.RemoteDocument#getPublisher()
	 */
	public RemoteUser getPublisher() {
		return publisher;
	}

	/**
	 * @see ch.iserver.ace.collaboration.RemoteDocument#join(ch.iserver.ace.collaboration.JoinCallback)
	 */
	public void join(JoinCallback callback) {
		// not implemented
	}

	/**
	 * @see ch.iserver.ace.collaboration.RemoteDocument#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	/**
	 * @see ch.iserver.ace.collaboration.RemoteDocument#removePropertyChangeListener(java.beans.PropertyChangeListener)
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
		} else if (obj instanceof RemoteDocument) {
			RemoteDocument doc = (RemoteDocument) obj;
			return getId().equals(doc.getId());
		} else {
			return false;
		}
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getId().hashCode();
	}

}
