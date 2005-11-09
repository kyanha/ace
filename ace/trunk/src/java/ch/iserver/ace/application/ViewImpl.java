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

import ch.iserver.ace.application.ItemSelectionChangeListener;
import ch.iserver.ace.application.ItemSelectionChangeEvent;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;



public class ViewImpl extends JPanel implements View {

	protected ViewController controller;
	protected LocaleMessageSource messageSource;
	private EventListenerList eventListenerList;

	public ViewImpl(ViewController controller, LocaleMessageSource messageSource) {
		this.controller = controller;
		this.messageSource = messageSource;
		eventListenerList = new EventListenerList();
		addItemSelectionChangeListener(controller);
	}

	public void addItemSelectionChangeListener(ItemSelectionChangeListener listener) {
		eventListenerList.add(ItemSelectionChangeListener.class, listener);
	}
	
	public void removeItemSelectionChangeListener(ItemSelectionChangeListener listener) {
		eventListenerList.add(ItemSelectionChangeListener.class, listener);
	}
	
	protected void fireItemSelectionChange(Item newItem) {
		ItemSelectionChangeEvent event = new ItemSelectionChangeEvent(this, newItem);
		Object[] listeners = eventListenerList.getListenerList();
		for(int i = listeners.length-2; i >= 0; i -= 2) {
			if(listeners[i] == ItemSelectionChangeListener.class) {
				((ItemSelectionChangeListener)listeners[i+1]).itemSelectionChanged(event);
			}
		}
	}
	
	public Item getSelectedItem() {
		return null;
	}

}
