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

import org.easymock.AbstractMatcher;

import ch.iserver.ace.util.CompareUtil;

/**
 *
 */
public class PropertyChangeEventMatcher extends AbstractMatcher {
	
	protected boolean argumentMatches(Object arg0, Object arg1) {
		if (arg0 == arg1) {
			return true;
		} else if (arg0 == null || arg1 == null) {
			return false;
		} else if (PropertyChangeEvent.class.equals(arg0.getClass()) && PropertyChangeEvent.class.equals(arg1.getClass())) {
			PropertyChangeEvent e0 = (PropertyChangeEvent) arg0;
			PropertyChangeEvent e1 = (PropertyChangeEvent) arg1;
			return e0.getSource() == e1.getSource() 
					&& e0.getPropertyName().equals(e1.getPropertyName())
					&& CompareUtil.nullSafeEquals(e0.getOldValue(), e1.getOldValue())
					&& CompareUtil.nullSafeEquals(e0.getNewValue(), e1.getNewValue());	
		} else {
			return arg0.equals(arg1);
		}
	}
	
	protected String argumentToString(Object arg0) {
		return arg0 == null ? "" : arg0.toString();
	}

}
