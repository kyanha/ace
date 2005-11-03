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

package ch.iserver.ace.collaboration.jupiter;

import java.beans.PropertyChangeListener;
import java.util.Collection;

import ch.iserver.ace.collaboration.PublishedSession;
import ch.iserver.ace.collaboration.RemoteUser;

public class RemoteUserStub implements MutableRemoteUser {
	final String id;
	public RemoteUserStub(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public Collection getSharedDocuments() {
		return null;
	}
	public void setName(String userName) {
		// ignore
	}
	public String getName() {
		return null;
	}
	public void invite(PublishedSession session) {
		// ignore
	}
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof RemoteUser) {
			RemoteUser user = (RemoteUser) obj;
			return id.equals(user.getId());
		}
		return super.equals(obj);
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		
	}
}