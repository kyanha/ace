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

import junit.framework.TestCase;

/**
 *
 */
public class PropertyChangeEventMatcherTest extends TestCase {
	
	public void testMatchesSame() throws Exception {
		PropertyChangeEventMatcher matcher = new PropertyChangeEventMatcher();
		PropertyChangeEvent evt = new PropertyChangeEvent("X", "name", null, "blub");
		assertTrue(matcher.matches(new Object[] { evt }, new Object[] { evt }));
	}
	
	public void testMatchesNotOneNull() throws Exception {
		PropertyChangeEventMatcher matcher = new PropertyChangeEventMatcher();
		PropertyChangeEvent evt = new PropertyChangeEvent("X", "name", null, "blub");
		assertFalse(matcher.matches(new Object[] { null }, new Object[] { evt }));
		assertFalse(matcher.matches(new Object[] { evt }, new Object[] { null }));
	}
	
	public void testMatchesOtherTypesOnEquality() throws Exception {
		PropertyChangeEventMatcher matcher = new PropertyChangeEventMatcher();
		assertTrue(matcher.matches(new Object[] { "X" }, new Object[] { "X" }));
		assertFalse(matcher.matches(new Object[] { "Y" }, new Object[] { "X" }));
		assertFalse(matcher.matches(new Object[] { "X" }, new Object[] { "Y" }));
		assertFalse(matcher.matches(new Object[] { "X" }, new Object[] { "Y" }));
	}
	
	public void testMatches() throws Exception {
		PropertyChangeEventMatcher matcher = new PropertyChangeEventMatcher();
		Object source = "X"; 
		PropertyChangeEvent e0 = new PropertyChangeEvent(source, "name", null, "hullo");
		PropertyChangeEvent e1 = new PropertyChangeEvent(source, "name", null, "hullo");
		assertTrue(matcher.matches(new Object[] { e0 }, new Object[] { e1 }));
	}
	
	public void testSourceMatchesNot() throws Exception {
		PropertyChangeEventMatcher matcher = new PropertyChangeEventMatcher();
		PropertyChangeEvent e0 = new PropertyChangeEvent("X", "name", null, "hullo");
		PropertyChangeEvent e1 = new PropertyChangeEvent("Y", "name", null, "hullo");
		assertFalse(matcher.matches(new Object[] { e0 }, new Object[] { e1 }));
	}
	
	public void testPropertyMatchesNot() throws Exception {
		PropertyChangeEventMatcher matcher = new PropertyChangeEventMatcher();
		Object source = "X"; 
		PropertyChangeEvent e0 = new PropertyChangeEvent(source, "name", null, "hullo");
		PropertyChangeEvent e1 = new PropertyChangeEvent(source, "age", null, "hullo");
		assertFalse(matcher.matches(new Object[] { e0 }, new Object[] { e1 }));
	}

	public void testOldValueMatchesNot() throws Exception {
		PropertyChangeEventMatcher matcher = new PropertyChangeEventMatcher();
		Object source = "X"; 
		PropertyChangeEvent e0 = new PropertyChangeEvent(source, "name", "blub", "hullo");
		PropertyChangeEvent e1 = new PropertyChangeEvent(source, "name", null, "hullo");
		assertFalse(matcher.matches(new Object[] { e0 }, new Object[] { e1 }));
	}

	public void testNewValueMatchesNot() throws Exception {
		PropertyChangeEventMatcher matcher = new PropertyChangeEventMatcher();
		Object source = "X"; 
		PropertyChangeEvent e0 = new PropertyChangeEvent(source, "name", null, "hullo");
		PropertyChangeEvent e1 = new PropertyChangeEvent(source, "name", null, "blub");
		assertFalse(matcher.matches(new Object[] { e0 }, new Object[] { e1 }));
	}

}
