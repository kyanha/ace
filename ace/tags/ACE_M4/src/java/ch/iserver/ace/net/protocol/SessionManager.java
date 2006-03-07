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

package ch.iserver.ace.net.protocol;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.transport.tcp.TCPSession;

import ch.iserver.ace.algorithm.TimestampFactory;
import ch.iserver.ace.net.core.MutableUserDetails;
import ch.iserver.ace.net.core.RemoteUserProxyExt;
import ch.iserver.ace.net.discovery.DiscoveryManagerFactory;

/**
 * The SessionManager is the central class that keeps all RemoteUserSession objects
 * managed in a central place.
 */
public class SessionManager {

	private Logger LOG = Logger.getLogger(SessionManager.class);
	
	/**
	 * A map of userId to RemoteUserSession mappings.
	 */
	private Map sessions;
	
	private static SessionManager theInstance;
	
	private TimestampFactory factory;
	
	/**
	 * Private constructor.
	 */
	private SessionManager() {
		sessions = Collections.synchronizedMap(new LinkedHashMap());
	}
	
	/**
	 * Gets the singleton instance.
	 * 
	 * @return the SessionManager instance
	 */
	public static SessionManager getInstance() {
		if (theInstance == null) {
			theInstance = new SessionManager();
		}
		return theInstance;
	}
	
	/**
	 * Sets the timestamp factory.
	 * 
	 * @param factory
	 */
	public void setTimestampFactory(TimestampFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * Creates a new RemotUserSession from the give remote user proxy.
	 * 
	 * @param user
	 * @return the RemoteUserSession
	 */
	public RemoteUserSession createSession(RemoteUserProxyExt user) {
		String id = user.getId();
		MutableUserDetails details = user.getMutableUserDetails();
		RemoteUserSession newSession = new RemoteUserSession(details.getAddress(), details.getPort(), user);
		newSession.setTimestampFactory(factory);
		sessions.put(id, newSession);
		return newSession;
	}
	
	/**
	 * Creates a new RemoteUserSession.
	 * 
	 * @param user
	 * @param session
	 * @param mainChannel
	 * @return	the created RemoteUserSession
	 */
	public RemoteUserSession createSession(RemoteUserProxyExt user, TCPSession session, Channel mainChannel) {
		RemoteUserSession newSession = new RemoteUserSession(session, new MainConnection(mainChannel), user);
		newSession.setTimestampFactory(factory);
		sessions.put(user.getId(), newSession);
		//TODO: test discoveryManager
		DiscoveryManagerFactory.getDiscoveryManager().setSessionEstablished(user.getId());
		return newSession;
	}
	
	public RemoteUserSession removeSession(String userid) {
		return (RemoteUserSession) sessions.remove(userid);
	}
	
	/**
	 * Closes all sessions.
	 * To be called on network layer shutdown.
	 */
	public void closeSessions() {
		LOG.debug("--> closeSessions()");
		synchronized(sessions) {
			Iterator iter = sessions.values().iterator();
			while (iter.hasNext()) {
				RemoteUserSession session = (RemoteUserSession) iter.next();
				String user = session.getUser().getUserDetails().getUsername();
				session.close();
				LOG.debug("closed session for [" + user + "]");
			}
			sessions.clear();
		}
		LOG.debug("<-- closeSessions()");
	}
	
	/**
	 * Gets the session for the user.
	 * 
	 * @param id the user id
	 * @return the session of the user or null if not found
	 */
	public RemoteUserSession getSession(String id) {
		LOG.debug("getSession for [" + id + "]");
		RemoteUserSession session = (RemoteUserSession) sessions.get(id);
		if (session == null) {
			LOG.warn("session for ["+id+"] not found");
		} else {
			LOG.debug("[" + session.getUser().getUserDetails().getUsername() + "]");
		}
		return session; 
	}
	
	/**
	 * Returns the number of available RemoteUserSession's.
	 * 
	 * @return the number of RemoteUserSessions
	 */
	public int size() {
		return sessions.size();
	}
	
	/**
	 * Gets the sessions as a id-session map.
	 * 
	 * @return the sessions as a id-session map
	 */
	public Map getSessions() {
		return sessions;
	}
	
	
}
