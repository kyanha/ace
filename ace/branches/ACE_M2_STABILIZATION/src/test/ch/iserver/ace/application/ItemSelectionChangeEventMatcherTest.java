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

import junit.framework.TestCase;

import org.easymock.ArgumentsMatcher;

import ch.iserver.ace.collaboration.RemoteUserStub;

/**
 *
 */
public class ItemSelectionChangeEventMatcherTest extends TestCase {
	
	public void testMatchesSame() throws Exception {
		ArgumentsMatcher matcher = new ItemSelectionChangeEventMatcher();
		ItemSelectionChangeEvent evt = new ItemSelectionChangeEvent("X", new UserItem(new RemoteUserStub("X")));
		assertTrue(matcher.matches(new Object[] { evt }, new Object[] { evt }));
	}
	
	public void testMatchesNotOneNull() throws Exception {
		ArgumentsMatcher matcher = new ItemSelectionChangeEventMatcher();
		ItemSelectionChangeEvent evt = new ItemSelectionChangeEvent("X", new UserItem(new RemoteUserStub("X")));
		assertFalse(matcher.matches(new Object[] { null }, new Object[] { evt }));
		assertFalse(matcher.matches(new Object[] { evt }, new Object[] { null }));
	}
	
	public void testMatchesOtherTypesOnEquality() throws Exception {
		ArgumentsMatcher matcher = new ItemSelectionChangeEventMatcher();
		assertTrue(matcher.matches(new Object[] { "X" }, new Object[] { "X" }));
		assertFalse(matcher.matches(new Object[] { "Y" }, new Object[] { "X" }));
		assertFalse(matcher.matches(new Object[] { "X" }, new Object[] { "Y" }));
		assertFalse(matcher.matches(new Object[] { "X" }, new Object[] { "Y" }));
	}
	
	public void testMatches() throws Exception {
		ArgumentsMatcher matcher = new ItemSelectionChangeEventMatcher();
		Object source = "X"; 
		ItemSelectionChangeEvent e0 = new ItemSelectionChangeEvent(source, new UserItem(new RemoteUserStub("X")));
		ItemSelectionChangeEvent e1 = new ItemSelectionChangeEvent(source, new UserItem(new RemoteUserStub("X")));
		assertTrue(matcher.matches(new Object[] { e0 }, new Object[] { e1 }));
	}
	
	public void testPropertyMatchesNot() throws Exception {
		ArgumentsMatcher matcher = new ItemSelectionChangeEventMatcher();
		ItemSelectionChangeEvent e0 = new ItemSelectionChangeEvent("X", new UserItem(new RemoteUserStub("X")));
		ItemSelectionChangeEvent e1 = new ItemSelectionChangeEvent("Y", new UserItem(new RemoteUserStub("X")));
		assertFalse(matcher.matches(new Object[] { e0 }, new Object[] { e1 }));
	}

	public void testItemMatchesNot() throws Exception {
		ArgumentsMatcher matcher = new ItemSelectionChangeEventMatcher();
		Object source = "X";
		ItemSelectionChangeEvent e0 = new ItemSelectionChangeEvent(source, new UserItem(new RemoteUserStub("X")));
		ItemSelectionChangeEvent e1 = new ItemSelectionChangeEvent(source, new UserItem(new RemoteUserStub("Y")));
		assertFalse(matcher.matches(new Object[] { e0 }, new Object[] { e1 }));
	}
	
}
