/*
 * $Id:BrowseItem.java 1091 2005-11-09 13:29:05Z zbinl $
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

import ch.iserver.ace.collaboration.RemoteDocument;
import ch.iserver.ace.collaboration.RemoteUser;



public class BrowseItem extends ItemImpl implements Comparable, PropertyChangeListener {

	public final static int NORMAL		= 1;
	public final static int AWAITING	= 2;
	public final static int JOINED		= 3;
	public final static String TYPE_PROPERTY = "type";

	private String title, publisher;
	private RemoteDocument document;
	private RemoteUser user;
	private int type;

	public BrowseItem(RemoteDocument document) {
		this.document = document;
		document.addPropertyChangeListener(this);
		title = document.getTitle();
		user = document.getPublisher();
		user.addPropertyChangeListener(this);
		publisher = user.getName();
		type = NORMAL;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		int oldType = this.type;
		this.type = type;
		firePropertyChange(TYPE_PROPERTY, "" + oldType, "" + type);
	}

	public String getTitle() {
		return title;
	}
	
	public String getPublisher() {
		return publisher;
	}
	
	public RemoteDocument getDocument() {
		return document;
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(RemoteDocument.TITLE_PROPERTY)) {
			title = (String)evt.getNewValue();
			firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
		if(evt.getPropertyName().equals(RemoteUser.NAME_PROPERTY)) {
			publisher = (String)evt.getNewValue();
			firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
	}

	public int compareTo(Object o) {
		return -((BrowseItem)o).getTitle().compareTo(title);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof BrowseItem) {
			BrowseItem browseItem = (BrowseItem)obj;
			return getDocument().equals(browseItem.getDocument());
		}
		return super.equals(obj);
	}
	
	public int hashCode() {
		return getDocument().hashCode();
	}

}

