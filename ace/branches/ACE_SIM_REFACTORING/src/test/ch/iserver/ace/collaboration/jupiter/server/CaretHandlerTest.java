package ch.iserver.ace.collaboration.jupiter.server;

import javax.swing.text.PlainDocument;

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
	
	public void testNegativePositions() throws Exception {
		document.insertString(0, "hallo", null);
		assertEquals(-1, -1, handler);
		document.insertString(2, "blub", null);
		assertEquals(-1, -1, handler);
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
