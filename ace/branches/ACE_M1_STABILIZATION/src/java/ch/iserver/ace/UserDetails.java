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

package ch.iserver.ace;

import java.net.InetAddress;

import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.util.ParameterValidator;

/**
 * The UserDetails object contains information about a user.
 */
public class UserDetails {
	
	/**
	 * The username of the user.
	 */
	protected String username;
	
	/**
	 * Creates a new UserDetails object.
	 * 
	 * @param username the username of the user
	 */
	public UserDetails(String username) {
		ParameterValidator.notNull("username", username);
		this.username = username;
	}
	
//	/**
//	 * Creates a new UserDetails object.
//	 * 
//	 * @param username the username of the user
//	 */
//	public UserDetails(String username, InetAddress address, int port) {
//		this(username);
//		this.address = address;
//		this.port = port;
//	}
	
	/**
	 * @return gets the username of the user
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * 
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
//	/**
//	 * @see java.lang.Object#equals(java.lang.Object)
//	 */
//	public boolean equals(Object obj) {
//		if (this == obj) {
//			return true;
//		} else if (obj instanceof UserDetails) {
//			UserDetails details = (UserDetails) obj;
//			boolean result = getUsername().equals(details.getUsername()) &&
//					getPort() == details.getPort();
//			InetAddress a1 = getAddress();
//			InetAddress a2 = details.getAddress();
//			return result && ( (a1 != null && a2 != null) && a1.equals(a2) || a1 == null && a2 == null );
//		}
//		return false;
//	}
//	
//	/**
//	 * @see java.lang.Object#hashCode()
//	 */
//	public int hashCode() {
//		int val = 13;
//		val += getUsername().hashCode();
//		val += getPort();
//		val += getAddress().hashCode();
//		return val;
//	}
//	
//	public String toString() {
//		return "UserDetails("+getUsername()+", "+getAddress()+", "+getPort()+")";
//	}
}
