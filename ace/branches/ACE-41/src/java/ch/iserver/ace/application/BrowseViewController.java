/*
 * $Id:BrowseViewController.java 1091 2005-11-09 13:29:05Z zbinl $
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
import ch.iserver.ace.collaboration.DocumentListener;
import ch.iserver.ace.collaboration.RemoteDocument;



public class BrowseViewController extends ViewControllerImpl implements DocumentListener {

	private EventList browseSourceList;
	private DocumentViewController docViewController;
	private DialogController dialogController;

	public BrowseViewController() {
		this.browseSourceList = new BasicEventList();
	}
	
	public BrowseViewController(EventList browseSourceList, DialogController dialogController) {
		this.browseSourceList = browseSourceList;// = new BasicEventList();
		this.dialogController = dialogController;
	}

	public void documentsDiscarded(RemoteDocument[] documents) {
		// remove discarded document from the list
		//System.out.println("documentsDiscarded (" + documents.length + ")");
		for(int i = 0; i < documents.length; i++ ) {
			browseSourceList.getReadWriteLock().writeLock().lock();
			try {
				browseSourceList.remove(new DocumentItem(documents[i], dialogController));
			} finally {
				browseSourceList.getReadWriteLock().writeLock().unlock();
			}
		}
	}
	
	public void documentsDiscovered(RemoteDocument[] documents) {
		// add discovered document to the list
		//System.out.println("documentsDiscovered (" + documents.length + ")");
		for(int i = 0; i < documents.length; i++ ) {
			browseSourceList.getReadWriteLock().writeLock().lock();
			try {
				browseSourceList.add(new DocumentItem(documents[i], dialogController));
			} finally {
				browseSourceList.getReadWriteLock().writeLock().unlock();
			}
		}
	}
	
	public synchronized DocumentItem findItem(RemoteDocument document) {
		browseSourceList.getReadWriteLock().readLock().lock();
		try {
			//System.out.println("remote item: " + document.getTitle() + "   (publisher: " + document.getPublisher() + ")");
			//DocumentItem item = (DocumentItem)browseSourceList.get(browseSourceList.indexOf(new DocumentItem(document)));
			//System.out.println("found item: " + item.getTitle() + "");
			int index = browseSourceList.indexOf(new DocumentItem(document, dialogController));
			try {
				return (DocumentItem)browseSourceList.get(index);
			} catch(IndexOutOfBoundsException e) {
				return null;
			}
		} finally {
			browseSourceList.getReadWriteLock().readLock().unlock();
		}
	}
		
	public BrowseView getBrowseView() {
		if(view == null) throw new IllegalStateException("View have to be set before using getView()!");
		return (BrowseView)view;
	}
	
	public EventList getSourceList() {
		return browseSourceList;
	}
	
	public DocumentItem getSelectedBrowseItem() {
		return (DocumentItem)getBrowseView().getSelectedItem();
	}

}

