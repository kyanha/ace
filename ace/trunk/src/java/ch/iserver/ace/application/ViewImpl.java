/*
 * $Id:ViewImpl.java 1091 2005-11-09 13:29:05Z zbinl $
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

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import java.awt.Dimension;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.EventListModel;
import ca.odell.glazedlists.swing.EventSelectionModel;



public abstract class ViewImpl extends JPanel implements View {

	protected ViewController controller;
	protected LocaleMessageSource messageSource;
	private EventListenerList eventListenerList;
	private JList list;
	private EventList sourceList;
	private EventListModel eventListModel;
	private EventSelectionModel eventSelectionModel;

	public ViewImpl(ViewController controller, LocaleMessageSource messageSource) {
		this.controller = controller;
		this.messageSource = messageSource;
		eventListenerList = new EventListenerList();
		addItemSelectionChangeListener(controller);
		setPreferredSize(new Dimension(200, 180));
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
	
	public abstract Item getSelectedItem();
	
	public void setSelectedIndex(int index) {
		getList().setSelectedIndex(index);
		if (index != -1) {
			getList().ensureIndexIsVisible(index);
		}
	}
	
	public int getSelectedIndex() {
		return getList().getSelectedIndex();
	}

	protected void setList(JList list) {
		this.list = list;
	}

	protected JList getList() {
		return list;
	}

	protected void setSourceList(EventList sourceList) {
		this.sourceList = sourceList;
	}

	protected EventList getSourceList() {
		return sourceList;
	}

	protected void setEventListModel(EventListModel eventListModel) {
		this.eventListModel = eventListModel;
	}

	protected EventListModel getEventListModel() {
		return eventListModel;
	}

	protected void setEventSelectionModel(EventSelectionModel eventSelectionModel) {
		this.eventSelectionModel = eventSelectionModel;
	}

	protected EventSelectionModel getEventSelectionModel() {
		return eventSelectionModel;
	}

}
