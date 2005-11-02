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

import java.util.Collection;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.util.ParameterValidator;

public class RemoteUserProxyImpl implements RemoteUserProxyExt {
	
	private String id;
	private UserDetails details;
	
	public RemoteUserProxyImpl(String id, UserDetails details) {
		ParameterValidator.notNull("id", id);
		ParameterValidator.notNull("details", details);
		this.id = id;
		this.details = details;
	}

	public String getId() {
		return id;
	}

	public UserDetails getUserDetails() {
		return details;
	}

	public Collection getSharedDocuments() {
		// TODO Auto-generated method stub
		return null;
	}

	public void invite(DocumentServerLogic logic) {
		// TODO Auto-generated method stub

	}

	public void setUserDetails(UserDetails details) {
		ParameterValidator.notNull("details", details);
		this.details = details;
	}
}
