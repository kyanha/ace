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

import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ca.odell.glazedlists.EventList;
import ch.iserver.ace.collaboration.ParticipantStub;
import ch.iserver.ace.collaboration.RemoteUserStub;
import ch.iserver.ace.collaboration.jupiter.MutableRemoteUser;

/**
 *
 */
public class ParticipantViewTest extends TestCase {
	
	public void testItemSelectionChangedEvent() throws Exception {
		MockControl listenerCtrl = MockControl.createControl(ItemSelectionChangeListener.class);
		listenerCtrl.setDefaultMatcher(new ItemSelectionChangeEventMatcher());
		ItemSelectionChangeListener listener = (ItemSelectionChangeListener) listenerCtrl.getMock();

		// test fixture
		ParticipantViewController controller = new ParticipantViewController();
		EventList participantList = controller.getCompositeSourceList().createMemberList();
		View view = new ParticipantView(new LocaleMessageSourceStub(), controller);
		controller.setParticipantList(participantList);
		
		Item[] items = new Item[3];
		
		controller.getCompositeSourceList().getReadWriteLock().writeLock().lock();
		for (int i = 0; i < 3; i++) {
			items[i] = new ParticipantItem(new ParticipantStub(new RemoteUserStub("" + i, "" + i), i), Color.RED);
			participantList.add(items[i]);
		}
		controller.getCompositeSourceList().getReadWriteLock().writeLock().unlock();
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
		final ListDataListener listener = (ListDataListener) listenerCtrl.getMock();
		
		// test fixture
		ParticipantViewController controller = new ParticipantViewController();
		EventList participantList = controller.getCompositeSourceList().createMemberList();
		final ParticipantView view = new ParticipantView(new LocaleMessageSourceStub(), controller);
		controller.setParticipantList(participantList);

		final MutableRemoteUser user = new RemoteUserStub("X", "X");
		Item item = new ParticipantItem(new ParticipantStub(user, 1), Color.BLACK);

		controller.getCompositeSourceList().getReadWriteLock().writeLock().lock();
		participantList.add(item);
		participantList.add(new ParticipantItem(new ParticipantStub(new RemoteUserStub("Y", "Y"), 2), Color.BLACK));
		controller.getCompositeSourceList().getReadWriteLock().writeLock().unlock();
				
		// define mock behavior
		listener.contentsChanged(new ListDataEvent(view.getList().getModel(), ListDataEvent.CONTENTS_CHANGED, 0, 0));
		
		// replay
		listenerCtrl.replay();
		
		// test
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				view.getList().getModel().addListDataListener(listener);
				user.setName("JIM");
			}		
		});
				
		// verify
		listenerCtrl.verify();
	}
	
}
