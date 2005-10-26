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

import ch.iserver.ace.util.ParameterValidator;

/**
 * A DiscoveryResult object contains the result of an explicit
 * discovery request through the method
 * {@link ch.iserver.ace.collaboration.CollaborationService#discoverUser(DiscoveryCallback, InetAddress, int)}.
 * The status specifies whether the discovery was successful or not.
 */
public final class DiscoveryResult {
	
	/**
	 * The status code that signifies a successful discovery. 
	 */
	public static final int SUCCESS = 0;
	
	/**
	 * The status code that signifies a failed disocvery.
	 */
	public static final int FAILED = 1;
	
	/**
	 * The discovered RemoteUser. Is null if the discovery failed.
	 */
	private RemoteUser user;
	
	/**
	 * The status code of the discovery.
	 */
	private int status = SUCCESS;
	
	/**
	 * The status message of the discovery (especially interesting if the
	 * discovery failed).
	 */
	private String statusMessage = "OK";
	
	/**
	 * Creates a new DiscoveryResult for a successful discovery.
	 * 
	 * @param user the discovered RemoteUser
	 */
	public DiscoveryResult(RemoteUser user) {
		ParameterValidator.notNull("user", user);
		this.user = user;
	}
	
	/**
	 * Creates a new DiscoveryResult for a failed discovery.
	 * 
	 * @param status the status code
	 * @param message the status message
	 */
	public DiscoveryResult(int status, String message) {
		this.status = status;
		this.statusMessage = message;
	}
	
	/**
	 * @return true iff the discovery was successful
	 */
	public boolean isSuccess() {
		return status == SUCCESS;
	}
	
	/**
	 * @return the discovered RemoteUser (is null, if the discovery failed)
	 */
	public RemoteUser getRemoteUser() {
		return user;
	}
	
	/**
	 * @return the status code
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * @return the status message
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
	
}
