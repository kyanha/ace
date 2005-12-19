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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.event.DocumentEvent;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.util.CaretHandler;



public class PropertyChangeCaretHandlerImpl extends CaretHandler implements PropertyChangeCaretHandler, PropertyChangeListener {

	private PropertyChangeSupport propertyChangeSupport;
	
	public PropertyChangeCaretHandlerImpl(int dot, int mark) {
		super(dot, mark);
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
	}
	
	public void setDot(int dot) {
		CaretUpdate oldCaretUpdate = new CaretUpdate(getDot(), getMark());
		super.setDot(dot);
		CaretUpdate newCaretUpdate = new CaretUpdate(getDot(), getMark());	
		firePropertyChange("setDot", oldCaretUpdate, newCaretUpdate);
	}

	public void setMark(int mark) {
		CaretUpdate oldCaretUpdate = new CaretUpdate(getDot(), getMark());
		super.setMark(mark);
		CaretUpdate newCaretUpdate = new CaretUpdate(getDot(), getMark());	
		firePropertyChange("setMark", oldCaretUpdate, newCaretUpdate);
	}
	
	public void setCaret(int dot, int mark) {
		CaretUpdate oldCaretUpdate = new CaretUpdate(getDot(), getMark());
		super.setDot(dot);
		super.setMark(mark);
		CaretUpdate newCaretUpdate = new CaretUpdate(getDot(), getMark());	
		firePropertyChange("setCaret", oldCaretUpdate, newCaretUpdate);
	}

	public void insertUpdate(DocumentEvent e) {
		CaretUpdate oldCaretUpdate = new CaretUpdate(getDot(), getMark());
		super.insertUpdate(e);
		CaretUpdate newCaretUpdate = new CaretUpdate(getDot(), getMark());	
		firePropertyChange("insertUpdate", oldCaretUpdate, newCaretUpdate);
	}

	public void removeUpdate(DocumentEvent e) {
		CaretUpdate oldCaretUpdate = new CaretUpdate(getDot(), getMark());
		super.removeUpdate(e);
		CaretUpdate newCaretUpdate = new CaretUpdate(getDot(), getMark());	
		firePropertyChange("removeUpdate", oldCaretUpdate, newCaretUpdate);
	}

}

