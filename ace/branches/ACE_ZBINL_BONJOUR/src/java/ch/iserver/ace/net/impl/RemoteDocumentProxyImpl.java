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

package ch.iserver.ace.net.impl;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.JoinNetworkCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.RemoteUserProxy;

/**
 *
 */
public class RemoteDocumentProxyImpl implements RemoteDocumentProxy {

	private String id;
	private DocumentDetails details;
	private RemoteUserProxy publisher;
	
	public RemoteDocumentProxyImpl(String id, DocumentDetails details, RemoteUserProxy publisher) {
		this.id = id;
		this.details = details;
		this.publisher = publisher;
	}
	
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.RemoteDocumentProxy#getId()
	 */
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.RemoteDocumentProxy#getDocumentDetails()
	 */
	public DocumentDetails getDocumentDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.RemoteDocumentProxy#getPublisher()
	 */
	public RemoteUserProxy getPublisher() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.RemoteDocumentProxy#join(ch.iserver.ace.net.JoinNetworkCallback)
	 */
	public void join(JoinNetworkCallback callback) {
		// TODO Auto-generated method stub

	}

}
