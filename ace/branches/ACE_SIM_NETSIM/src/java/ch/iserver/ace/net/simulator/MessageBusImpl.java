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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.util.BoundedThreadDomain;
import ch.iserver.ace.util.ThreadDomain;

/**
 *
 */
public class MessageBusImpl implements MessageBus {
	
	private static MessageBus instance = new MessageBusImpl();
	
	private Map listenerMap = new HashMap();
	
	private Map userDetailsMap = new HashMap();
	
	public static MessageBus getInstance() {
		return instance;
	}
	
	private ThreadDomain threadDomain = new BoundedThreadDomain(5);
	
	public ThreadDomain getThreadDomain() {
		return threadDomain;
	}
	
	public synchronized MessagePort register(User u) {
		User user = (User) getThreadDomain().wrap(u, User.class, true);
		Iterator it = listenerMap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			User listener = (User) listenerMap.get(key);
			listener.userRegistered(user);
			user.userRegistered(listener);
		}
		addListener(user.getId(), user.getUserDetails(), user);
		return new MessagePortImpl(user);
	}
		
	public synchronized void unregister(User user) {
		String id = user.getId();
		removeListener(id);
		Iterator it = listenerMap.values().iterator();
		while (it.hasNext()) {
			MessageListener listener = (MessageListener) it.next();
			listener.userUnregistered(user);
		}
	}
	
	private void addListener(String id, UserDetails details, MessageListener l) {
		userDetailsMap.put(id, details);
		listenerMap.put(id, l);
	}
	
	private void removeListener(String id) {
		listenerMap.remove(id);
		userDetailsMap.remove(id);
	}
	
	
	protected class MessagePortImpl implements MessagePort {
		private User user;
		
		protected MessagePortImpl(User user) {
			this.user = user;
		}
		
		public void setUserDetails(UserDetails details) {
			String id = user.getId();
			Iterator it = listenerMap.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				if (!id.equals(key)) {
					MessageListener listener = (MessageListener) listenerMap.get(key);
					listener.userChanged(user);
				}
			}
		}
		
		public void publishDocument(PublishedDocument document) {
			String id = user.getId();
			Iterator it = listenerMap.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				if (!id.equals(key)) {
					MessageListener listener = (MessageListener) listenerMap.get(key);
					listener.documentPublished(document);
				}
			}
		}
	}
}
