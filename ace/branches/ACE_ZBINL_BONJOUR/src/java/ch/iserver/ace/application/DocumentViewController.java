/*
 * $Id:DocumentViewController.java 1091 2005-11-09 13:29:05Z zbinl $
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



public class DocumentViewController extends ViewControllerImpl {

	private EventList documentSourceList;

	public DocumentViewController() {
		documentSourceList = new BasicEventList();
	}
	
	public void addDocument(DocumentItem document) {
		documentSourceList.getReadWriteLock().writeLock().lock();
		try {
			documentSourceList.add(document);
		} finally {
			documentSourceList.getReadWriteLock().writeLock().unlock();
		}
	}
	
	public void removeDocument(DocumentItem document) {
		documentSourceList.getReadWriteLock().writeLock().lock();
		try {
			documentSourceList.remove(document);
		} finally {
			documentSourceList.getReadWriteLock().writeLock().unlock();
		}
	}
	
	public boolean containsDocument(DocumentItem document) {
		documentSourceList.getReadWriteLock().readLock().lock();
		try {
			return documentSourceList.contains(document);
		} finally {
			documentSourceList.getReadWriteLock().readLock().unlock();
		}
	}
	
	private DocumentView getDocumentView() {
		if(view == null) throw new IllegalStateException("View have to be set before using getView()!");
		return (DocumentView)view;
	}
	
	public EventList getDocumentSourceList() {
		return documentSourceList;
	}
	
	public DocumentItem getSelectedDocumentItem() {
		return (DocumentItem)getDocumentView().getSelectedItem();
	}

}

