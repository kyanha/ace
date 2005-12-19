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

package ch.iserver.ace.net.simulator;

import java.util.ArrayList;
import java.util.Collection;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.RemoteUserProxy;

/**
 *
 */
public class UserImpl implements RemoteUserProxy {
	
	private final String id;
	
	private UserDetails details;
	
	public UserImpl(String id, UserDetails details) {
		this.id = id;
		this.details = details;
	}
	
	/**
	 * @see ch.iserver.ace.net.RemoteUserProxy#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * @see ch.iserver.ace.net.RemoteUserProxy#getUserDetails()
	 */
	public UserDetails getUserDetails() {
		return details;
	}
	
	public void setUserDetails(UserDetails details) {
		this.details = details;
	}
	
	/**
	 * @see ch.iserver.ace.net.RemoteUserProxy#getSharedDocuments()
	 */
	public Collection getSharedDocuments() {
		return new ArrayList();
	}

}
