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

package ch.iserver.ace.net.protocol;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.RemoteUserProxy;

/**
 *
 */
public class SessionHandler {

	private Map sessions;
	
	private static SessionHandler theInstance;
	
	private SessionHandler() {
		sessions = new HashMap();
	}
	
	public static SessionHandler getInstance() {
		if (theInstance == null) {
			theInstance = new SessionHandler();
		}
		return theInstance;
	}
	
	public synchronized RemoteUserSession createSession(RemoteUserProxy user) {
		String id = user.getId();
		UserDetails details = user.getUserDetails();
		RemoteUserSession newSession = new RemoteUserSession(details.getAddress(), details.getPort(), user);
		sessions.put(id, newSession);
		return newSession;
	}
	
	public RemoteUserSession getSession(String id) {
		return (RemoteUserSession)sessions.get(id);
	}
	
	
	
}
