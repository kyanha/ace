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

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.collaboration.RemoteDocumentStub;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.collaboration.RemoteUserStub;
import ch.iserver.ace.collaboration.jupiter.MutableRemoteDocument;

/**
 *
 */
public class BrowseViewTest extends TestCase {
	
	public void testItemSelectionChangeEvent() throws Exception {
		MockControl listenerCtrl = MockControl.createControl(ItemSelectionChangeListener.class);
		listenerCtrl.setDefaultMatcher(new ItemSelectionChangeEventMatcher());
		ItemSelectionChangeListener listener = (ItemSelectionChangeListener) listenerCtrl.getMock();

		// test fixture
		BrowseViewController controller = new BrowseViewController();
		View view = new BrowseView(new LocaleMessageSourceStub(), controller);
		
		Item[] items = new Item[3];
		
		controller.getBrowseSourceList().getReadWriteLock().writeLock().lock();
		for (int i = 0; i < 3; i++) {
			items[i] = new BrowseItem(new RemoteDocumentStub("" + i, "" + i, new RemoteUserStub("" + i)));
			controller.getBrowseSourceList().add(items[i]);
		}
		controller.getBrowseSourceList().getReadWriteLock().writeLock().unlock();
		view.addItemSelectionChangeListener(listener);

		// define mock behavior
		listener.itemSelectionChanged(new ItemSelectionChangeEvent(view, items[1]));
		listener.itemSelectionChanged(new ItemSelectionChangeEvent(view, items[2]));
		listener.itemSelectionChanged(new ItemSelectionChangeEvent(view, items[0]));
		listener.itemSelectionChanged(new ItemSelectionChangeEvent(view, null));
		
		// replay
		listenerCtrl.replay();
		
		// test
		view.setSelectedIndex(1);
		view.setSelectedIndex(2);
		view.setSelectedIndex(0);
		view.setSelectedIndex(-1);
		
		// verify
		listenerCtrl.verify();
	}
	
	public void testItemPropertiesChanged() throws Exception {
		MockControl listenerCtrl = MockControl.createControl(ListDataListener.class);
		listenerCtrl.setDefaultMatcher(new ListDataEventMatcher());
		ListDataListener listener = (ListDataListener) listenerCtrl.getMock();
		
		// test fixture
		BrowseViewController controller = new BrowseViewController();
		BrowseView view = new BrowseView(new LocaleMessageSourceStub(), controller);

		RemoteUser user = new RemoteUserStub("X", "X");
		MutableRemoteDocument document = new RemoteDocumentStub("X", "X", user);
		Item item = new BrowseItem(document);

		controller.getBrowseSourceList().getReadWriteLock().writeLock().lock();
		controller.getBrowseSourceList().add(item);
		controller.getBrowseSourceList().getReadWriteLock().writeLock().unlock();

		Thread.sleep(5);
		view.getList().getModel().addListDataListener(listener);
		
		// define mock behavior
		listener.contentsChanged(new ListDataEvent(view.getList().getModel(), ListDataEvent.CONTENTS_CHANGED, 0, 0));
		
		// replay
		listenerCtrl.replay();
		
		// test
		document.setTitle("NEW TITLE");
		
		// verify
		Thread.sleep(5);
		listenerCtrl.verify();
	}
	
}
