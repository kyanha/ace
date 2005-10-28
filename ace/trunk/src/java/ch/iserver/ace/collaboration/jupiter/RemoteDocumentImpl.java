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

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.collaboration.JoinCallback;
import ch.iserver.ace.collaboration.RemoteDocument;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.collaboration.SessionCallback;
import ch.iserver.ace.net.JoinNetworkCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.SessionConnection;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class RemoteDocumentImpl implements RemoteDocument {
	
	private final RemoteDocumentProxy proxy;
	
	private RemoteUser publisher;
	
	public RemoteDocumentImpl(RemoteDocumentProxy proxy) {
		ParameterValidator.notNull("proxy", proxy);
		this.proxy = proxy;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.RemoteDocument#getId()
	 */
	public String getId() {
		return proxy.getId();
	}

	/**
	 * @see ch.iserver.ace.collaboration.RemoteDocument#getDocumentDetails()
	 */
	public DocumentDetails getDocumentDetails() {
		return proxy.getDocumentDetails();
	}

	/**
	 * @see ch.iserver.ace.collaboration.RemoteDocument#getPublisher()
	 */
	public RemoteUser getPublisher() {
		if (publisher == null) {
			publisher = new RemoteUserImpl(proxy.getPublisher());
		}
		return publisher;
	}

	public void join(final JoinCallback joinCallback, final SessionCallback callback) {
		proxy.join(new JoinNetworkCallback() {
			public void accepted(SessionConnection connection) {
				SessionImpl session = new SessionImpl(callback);
				session.setConnection(connection);
				joinCallback.accepted(session);
			}	
			public void rejected() {
				joinCallback.rejected();
			}
		});
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof RemoteDocument) {
			RemoteDocument document = (RemoteDocument) obj;
			return getId().equals(document.getId());
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
