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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.collaboration.PublishedSession;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class RemoteUserImpl implements RemoteUser {

	private final RemoteUserProxy proxy;
	
	public RemoteUserImpl(RemoteUserProxy proxy) {
		ParameterValidator.notNull("proxy", proxy);
		this.proxy = proxy;
	}
	
	public RemoteUserProxy getProxy() {
		return proxy;
	}
		
	/**
	 * @see ch.iserver.ace.collaboration.RemoteUser#getId()
	 */
	public String getId() {
		return getProxy().getId();
	}

	/**
	 * @see ch.iserver.ace.collaboration.RemoteUser#getSharedDocuments()
	 */
	public Collection getSharedDocuments() {
		List result = new LinkedList();
		Iterator it = getProxy().getSharedDocuments().iterator();
		while (it.hasNext()) {
			RemoteDocumentProxy document = (RemoteDocumentProxy) it.next();
			result.add(new RemoteDocumentImpl(document));
		}
		return result;
	}

	/**
	 * @see ch.iserver.ace.collaboration.RemoteUser#getUserDetails()
	 */
	public UserDetails getUserDetails() {
		return getProxy().getUserDetails();
	}

	/**
	 * @see ch.iserver.ace.collaboration.RemoteUser#invite(ch.iserver.ace.collaboration.PublishedSession)
	 */
	public void invite(PublishedSession s) {
		PublishedSessionImpl session = (PublishedSessionImpl) s;
		DocumentServerLogic logic = session.getLogic();
		getProxy().invite(logic);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof RemoteUser) {
			RemoteUser user = (RemoteUser) obj;
			return getId().equals(user.getId());
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
