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

package ch.iserver.ace.util;

import javax.swing.text.PlainDocument;

import ch.iserver.ace.util.CaretHandler;

import junit.framework.TestCase;

public class CaretHandlerTest extends TestCase {

	private PlainDocument document;
	private CaretHandler handler;

	protected void assertEquals(int dot, int mark, CaretHandler handler) {
		assertEquals(dot, handler.getDot());
		assertEquals(mark, handler.getMark());	
	}
	
	public void setUp() {
		document = new PlainDocument();
		handler = new CaretHandler(-1, -1);
		document.addDocumentListener(handler);
	}
		
	public void testInsertBefore() throws Exception {
		document.insertString(0, "hello world", null);
		handler.setDot(5);
		handler.setMark(3);
		assertEquals(5, 3, handler);
		document.insertString(2, "XYZ", null);
		assertEquals(8, 6, handler);
	}
	
	public void testInsertAfter() throws Exception {
		document.insertString(0, "hello world", null);
		handler.setDot(5);
		handler.setMark(3);
		document.insertString(6, "XYZ", null);
		assertEquals(5, 3, handler);
	}
	
	public void testInsertAtPosition() throws Exception {
		document.insertString(0, "hello world", null);
		handler.setDot(5);
		handler.setMark(5);
		document.insertString(5, "XYZ", null);
		assertEquals(8, 8, handler);
	}
	
	public void testRemoveBefore() throws Exception {
		document.insertString(0, "hello world", null);
		handler.setDot(5);
		handler.setMark(5);
		document.remove(0, 3);
		assertEquals(2, 2, handler);
	}
	
	public void testRemoveAfter() throws Exception {
		document.insertString(0, "hello world", null);
		handler.setDot(5);
		handler.setMark(5);
		document.remove(6, 3);
		assertEquals(5, 5, handler);
	}
	
	public void testRemoveInside() throws Exception {
		document.insertString(0, "hello world", null);
		handler.setDot(5);
		handler.setMark(5);
		document.remove(3, 3);
		assertEquals(3, 3, handler);
	}
	
	public void tearDown() {
		document.removeDocumentListener(handler);
	}
	
}
