/*
 * $Id:SessionHandler.java 1095 2005-11-09 13:56:51Z zbinl $
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

package ch.iserver.ace.net.impl.protocol;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.iserver.ace.net.impl.MutableUserDetails;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;

/**
 *
 */
public class SessionManager {

	/**
	 * A map of userId to RemoteUserSession mappings.
	 */
	private Map sessions;
	
	private static SessionManager theInstance;
	
	private SessionManager() {
		sessions = new HashMap();
	}
	
	public static SessionManager getInstance() {
		if (theInstance == null) {
			theInstance = new SessionManager();
		}
		return theInstance;
	}
	
	public synchronized RemoteUserSession createSession(RemoteUserProxyExt user) {
		String id = user.getId();
		MutableUserDetails details = user.getMutableUserDetails();
		RemoteUserSession newSession = new RemoteUserSession(details.getAddress(), details.getPort(), user);
		sessions.put(id, newSession);
		return newSession;
	}
	
	public synchronized RemoteUserSession removeSession(String userid) {
		return (RemoteUserSession) sessions.remove(userid);
	}
	
	/**
	 * Gets the session for the user.
	 * 
	 * @param id the user id
	 * @return the session of the user
	 */
	public RemoteUserSession getSession(String id) {
		return (RemoteUserSession)sessions.get(id);
	}
	
	public int size() {
		return sessions.size();
	}
	
	public Collection getSessions() {
		return sessions.values();
	}
	
	
}
