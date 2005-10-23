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

/**
 * The NetworkServiceCallback interface is used by the NetworkService for
 * calling the client of itself. Typically the client is an object from
 * the logic layer that implements this callback interface.
 */
public interface NetworkServiceCallback {
	
	/**
	 * Notifies the callback that a new user was discovered on the network.
	 * The passed in <var>proxy</var> can be used to communicate with that
	 * newly discovered user.
	 * 
	 * @param proxy the proxy for the newly discovered user
	 */
	void userDiscovered(RemoteUserProxy proxy);
	
	/**
	 * Notifies the callback that a previously present user disappeared from
	 * the network.
	 * 
	 * @param proxy 
	 * @todo passed in proxy is useless (i.e. we cannot communicate with it)
	 */
	void userDiscarded(RemoteUserProxy proxy);
	
	/**
	 * Notifies the callback that a new document was discovered on the network.
	 * The passed in <var>proxy</var> can be used to communicate with that
	 * newly discovered document.
	 * 
	 * @param proxy the proxy for the newly discovered document
	 */
	void documentDiscovered(RemoteDocumentProxy proxy);
	
	/**
	 * Notifies the callback that a previously present document disappeared
	 * from the network.
	 * 
	 * @param proxy
	 * @todo passed in proxy is useless (i.e. we cannot communicate with it)
	 */
	void documentDiscarded(RemoteDocumentProxy proxy);
	
	/**
	 * Notifies the callback that an invitation was received. The passed in
	 * <var>invitation</var> can be used to accept or reject the invitation.
	 * 
	 * @param invitation the Invitation object that allows to accept or 
	 *                   reject the invitation
	 */
	void invitationReceived(InvitationProxy invitation);
	
}
