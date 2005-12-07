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

import java.util.Map;

import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.impl.protocol.DiscoveryException;

/**
 * Interface extension of <code>RemoteUserProxy</code>
 * for the network layer.
 *
 * @see ch.iserver.ace.net.RemoteUserProxy
 */
public interface RemoteUserProxyExt extends RemoteUserProxy {

	void setMutableUserDetails(MutableUserDetails details);
	
	MutableUserDetails getMutableUserDetails();
	
	/**
	 * Returns a synchronized map upon which to synchronize properly.
	 */
	Map getDocuments();
	
	void addSharedDocument(RemoteDocumentProxyExt doc);
	
	RemoteDocumentProxyExt removeSharedDocument(String id);
	
	RemoteDocumentProxyExt getSharedDocument(String id);
	
	boolean hasDocumentShared(String id);
	
	void setSessionEstablished(boolean value);
	
	boolean isSessionEstablished();
	
	boolean isDNSSDdiscovered();
	
	void setDNSSDdiscovered(boolean value);
	
	/**
	 * Explicit discovery of this user. As precondition, 
	 * the address and the port need to be set. Tries
	 * to establish a <code>RemoteUserSession</code> with
	 * the given information and requests the users coordinates,
	 *  e.g. the user id and the user name.
	 *
	 * @throws DiscoveryException if the discovery fails
	 */
	void discover() throws DiscoveryException;
}
