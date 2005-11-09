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

import ch.iserver.ace.collaboration.DocumentListener;
import ch.iserver.ace.collaboration.RemoteDocument;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.BasicEventList;



public class BrowseViewController extends ViewControllerImpl implements DocumentListener {

	private EventList browseSourceList;

	public BrowseViewController() {
		browseSourceList = new BasicEventList();
	}
	
	public void documentsDiscarded(RemoteDocument[] documents) {
		// remove discarded document from the list
		for(int i = 0; i < documents.length; i++ ) {
			browseSourceList.getReadWriteLock().writeLock().lock();
			browseSourceList.remove(new BrowseItem(documents[i]));
			browseSourceList.getReadWriteLock().writeLock().unlock();
		}
	}
	
	public void documentsDiscovered(RemoteDocument[] documents) {
		// add discovered document to the list
		for(int i = 0; i < documents.length; i++ ) {
			browseSourceList.getReadWriteLock().writeLock().lock();
			browseSourceList.add(new BrowseItem(documents[i]));
			browseSourceList.getReadWriteLock().writeLock().unlock();
		}
	}
		
	private BrowseView getBrowseView() {
		if(view == null) throw new IllegalStateException("View have to be set before using getView()!");
		return (BrowseView)view;
	}
	
	public EventList getBrowseSourceList() {
		return browseSourceList;
	}
	
	public BrowseItem getSelectedBrowseItem() {
		return (BrowseItem)getBrowseView().getSelectedItem();
	}

}

