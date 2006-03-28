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

package ch.iserver.ace.net;

import java.util.Collection;

import ch.iserver.ace.UserDetails;

public class RemoteUserProxyStub implements RemoteUserProxy {
	final String id;
	final String name;
	public RemoteUserProxyStub(String id) {
		this.id = id;
		this.name = "";
	}
	public RemoteUserProxyStub(String id, String name) {
		this.id = id;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public Collection getSharedDocuments() {
		return null;
	}
	public UserDetails getUserDetails() {
		return new UserDetails(name);
	}
	public void invite(DocumentServerLogic logic) {
		// ignore
	}
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof RemoteUserProxy) {
			RemoteUserProxy user = (RemoteUserProxy) obj;
			return id.equals(user.getId());
		}
		return super.equals(obj);
	}
	public String toString() {
		return getClass().getName() + "[id=" + id + "]";
	}
}