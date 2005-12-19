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
import ch.iserver.ace.net.RemoteUserProxy;

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
	
	public synchronized MessagePort register(UserImpl user) {
		Iterator it = listenerMap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			MessageListener listener = (MessageListener) listenerMap.get(key);
			listener.userRegistered(user);
			user.userRegistered(key, getUserDetails(key));
		}
		addListener(id, details, l);
		return new MessagePortImpl(id);
	}
		
	public void unregister(String id) {
		removeListener(id);
		Iterator it = listenerMap.values().iterator();
		while (it.hasNext()) {
			MessageListener listener = (MessageListener) it.next();
			listener.userUnregistered(id);
		}
	}
	
	private UserDetails getUserDetails(String id) {
		return (UserDetails) userDetailsMap.get(id);
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
		private String id;
		protected MessagePortImpl(String userId) {
			this.id = userId;
		}
		public void setUserDetails(UserDetails details) {
			Iterator it = listenerMap.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				if (!id.equals(key)) {
					MessageListener listener = (MessageListener) listenerMap.get(key);
					listener.userChanged(id, details);
				}
			}
		}
		public void publishDocument(PublishedDocument document) {
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
