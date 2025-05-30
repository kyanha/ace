/*
 * $Id:DocumentItem.java 1091 2005-11-09 13:29:05Z zbinl $
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
import ch.iserver.ace.collaboration.Session;



public class DocumentItem extends ItemImpl implements Comparable, PropertyChangeListener {

	public final static int LOCAL		= 1;
	public final static int REMOTE		= 2;
	public final static int PUBLISHED	= 3;

	private int type = LOCAL;
	private String title;
	private String id;
	//private Document editorDocument;
	private RemoteDocument document;
	private Session session;
	//private SessionCallback sessionCallback;

	public DocumentItem(String title) {
		// create local document
		// TODO: id-generator
		id = "" + Math.round(1000000 * Math.random());
		this.title = title;
		type = LOCAL;
	}
	
	public DocumentItem(RemoteDocument document) {
		// create remote document
		id = document.getId();
		title = document.getTitle();
		type = REMOTE;
		document.addPropertyChangeListener(this);
		document.getPublisher().addPropertyChangeListener(this);
	}

	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getType() {
		return type;
	}
	
	public RemoteDocument getDocument() {
		return document;
	}
	
	public Session getSession() {
		return session;
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(RemoteDocument.TITLE_PROPERTY)) {
			title = (String)evt.getNewValue();
			firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		} else if (RemoteUser.NAME_PROPERTY.equals(evt.getPropertyName())) {
			firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
	}

	public int compareTo(Object o) {
		return -((DocumentItem)o).getTitle().compareTo(title);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof DocumentItem) {
			DocumentItem documentItem = (DocumentItem)obj;
			return getId().equals(documentItem.getId());
		}
		return super.equals(obj);
	}
	
	public int hashCode() {
		return getId().hashCode();
	}

}

