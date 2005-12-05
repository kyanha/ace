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

package ch.iserver.ace.net.impl.discovery;

import ch.iserver.ace.net.impl.RemoteUserProxyExt;

/**
 * Handles the managment of RemoteUserProxy and RemoteUserSession.
 */
public interface DiscoveryManager {

	/**
	 * Returns an array of users which do not have
	 * a RemoteUserSession established yet.
	 * 
	 * @return an array of RemoteUserProxy instances without a RemoteUserSession established
	 */
	public RemoteUserProxyExt[] getPeersWithNoSession();
	
	/**
	 * Notifies the DiscoverManager that for the given user
	 * the RemoteUserSession (i.e. its TCPSession) has been 
	 * established. 
	 * After that call, channels to the that peer can be
	 * created.
	 * 
	 * @param userId
	 */
	public void setSessionEstablished(String userId);
	
	/**
	 * Called when the RemoteUserSession for the given
	 * user is removed.
	 * 
	 * @param userId
	 */
	public void setSessionTerminated(String userId);
	
	/**
	 * Determines whether the RemoteUserSession for the
	 * given user is established.
	 * 
	 * @param userId the user id
	 * @return true iff the RemoteUserSession is established
	 */
	public boolean hasSessionEstablished(String userId);
	
	/**
	 * Gets the RemoteUserProxy for the given user.
	 * 
	 * @param userId the user id
	 * @return the RemoteUserProxy instance or null if the instance is not available
	 */
	public RemoteUserProxyExt getUser(String userId);
	
	/**
	 * Gets the number of discovered users.
	 * 
	 * @return the number of discovered users
	 */
	public int getSize();
	
}
