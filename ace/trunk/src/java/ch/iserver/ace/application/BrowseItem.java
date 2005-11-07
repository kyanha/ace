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

import ch.iserver.ace.collaboration.RemoteDocument;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;



public class BrowseItem extends ItemImpl implements Comparable, PropertyChangeListener {

	private String title, publisher;
	private RemoteDocument document;

	public BrowseItem(RemoteDocument document) {
		this.document = document;
		document.addPropertyChangeListener(this);
		title = document.getTitle();
		publisher = document.getPublisher().getName();
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
	}

	public int compareTo(Object o) {
		return -((BrowseItem)o).getTitle().compareTo(title);
	}

}

