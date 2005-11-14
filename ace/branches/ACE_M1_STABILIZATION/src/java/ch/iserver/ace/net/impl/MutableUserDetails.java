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

import java.net.InetAddress;

import ch.iserver.ace.UserDetails;

public class MutableUserDetails extends UserDetails {

	
	public MutableUserDetails(String username) {
		super(username);
	}
	
	public MutableUserDetails(String username, InetAddress address, int port) {
		super(username, address, port);
	}
	
	/**
	 * Sets the address of the user.
	 * 
	 * @param address the address of the user
	 */
	public void setAddress(InetAddress address) {
		this.address = address;
	}
	
	/**
	 * Sets the port for the user.
	 * 
	 * @param port the port of the user
	 */
	public void setPort(int port) {
		this.port = port;
	}

}
