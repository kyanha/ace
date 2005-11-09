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

package ch.iserver.ace.application;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.collaboration.UserListener;



public class UserViewController extends ViewControllerImpl implements UserListener {

	private EventList userSourceList;
	
	public UserViewController() {
		userSourceList = new BasicEventList();
	}
	
	public void userDiscarded(RemoteUser user) {
		// remove discarded user from the list
		userSourceList.getReadWriteLock().writeLock().lock();
		try {
			userSourceList.remove(new UserItem(user));
		} finally {
			userSourceList.getReadWriteLock().writeLock().unlock();
		}
	}
	
	public void userDiscovered(RemoteUser user) {
		// add discovered user to the list
		userSourceList.getReadWriteLock().writeLock().lock();
		try {
			userSourceList.add(new UserItem(user));
		} finally {
			userSourceList.getReadWriteLock().writeLock().unlock();
		}
	}
	
	private UserView getUserView() {
		if(view == null) throw new IllegalStateException("View have to be set before using getView()!");
		return (UserView)view;
	}
	
	public EventList getUserSourceList() {
		return userSourceList;
	}

	public UserItem getSelectedUserItem() {
		return (UserItem)getUserView().getSelectedItem();
	}

}

