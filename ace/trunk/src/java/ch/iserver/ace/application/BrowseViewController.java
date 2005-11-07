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
	
	public void documentDiscarded(RemoteDocument document) {
		// remove discarded document from the list
		browseSourceList.remove(new BrowseItem(document));
	}
	
	public void documentDiscovered(RemoteDocument document) {
		// add discovered document to the list
		browseSourceList.add(new BrowseItem(document));
	}
		
	private BrowseView getView() {
		return (BrowseView)view;
	}
	
	public EventList getBrowseSourceList() {
		return browseSourceList;
	}
	
	public BrowseItem getSelectedItem() {
		return (BrowseItem)getView().getSelectedItem();
	}

}

