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

import org.easymock.AbstractMatcher;

/**
 *
 */
public class ListDataEventMatcher extends AbstractMatcher {

	protected boolean argumentMatches(Object arg0, Object arg1) {
		if (arg0 == arg1) {
			return true;
		} else if (arg0 == null || arg1 == null) {
			return false;
		} else if (arg0 instanceof ListDataEvent && arg1 instanceof ListDataEvent) {
			ListDataEvent e0 = (ListDataEvent) arg0;
			ListDataEvent e1 = (ListDataEvent) arg1;
			return e0.getSource() == e1.getSource() 
					&& e0.getIndex0() == e1.getIndex0()
					&& e0.getIndex1() == e1.getIndex1()
					&& e0.getType() == e1.getType();	
		} else {
			return arg0.equals(arg1);
		}
	}
	
	protected String argumentToString(Object arg0) {
		return arg0 == null ? "" : arg0.toString();
	}
	
}
