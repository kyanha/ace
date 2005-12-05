/*
 * $Id:UserItem.java 1091 2005-11-09 13:29:05Z zbinl $
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ch.iserver.ace.collaboration.RemoteUser;



public class UserItem extends ItemImpl implements Comparable, PropertyChangeListener {

	private String name;
	private RemoteUser user;

	public UserItem(RemoteUser user) {
		this.user = user;
		user.addPropertyChangeListener(this);
		name = user.getName();
	}

	public String getName() {
		return name;
	}
	
	public RemoteUser getUser() {
		return user;
	}
	
	public void cleanUp() {
		//System.out.println("UserItem::cleanUp()");
		user.removePropertyChangeListener(this);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(RemoteUser.NAME_PROPERTY)) {
			name = (String)evt.getNewValue();
			firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
	}

	public int compareTo(Object o) {
		return -((UserItem)o).getName().compareTo(name);
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof UserItem) {
			UserItem userItem = (UserItem)obj;
			return getUser().equals(userItem.getUser());
		}
		return super.equals(obj);
	}
	
	public int hashCode() {
		return getUser().hashCode();
	}

}

