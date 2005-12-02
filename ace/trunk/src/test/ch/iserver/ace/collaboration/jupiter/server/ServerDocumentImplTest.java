package ch.iserver.ace.collaboration.jupiter.server;


import java.util.Iterator;

import javax.swing.text.Element;

import junit.framework.TestCase;
import ch.iserver.ace.Fragment;
import ch.iserver.ace.collaboration.jupiter.server.ServerDocumentImpl.FragmentElement;

public class ServerDocumentImplTest extends TestCase {
	
	private void assertEquals(int pid, String text, Fragment f) {
		assertEquals(pid, f.getParticipantId());
		assertEquals(text, f.getText());
	}
	
	protected void dump(String indent, Element element) {
		if (element instanceof FragmentElement) {
			FragmentElement fragment = (FragmentElement) element;
			System.err.println(indent + fragment.getParticipantId() + " | " + fragment.getText());
		} else {
			System.err.println(indent + element.getName());
		}
		for (int i = 0; i < element.getElementCount(); i++) {
			dump(indent + "  ", element.getElement(i));
		}
	}
	
	public void testSimpleInsert() {
		ServerDocumentImpl doc = new ServerDocumentImpl();
		
		doc.insertString(1, 0, "x");
		assertEquals("x", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "x", (Fragment) it.next());
		assertFalse(it.hasNext());

		doc = new ServerDocumentImpl();
		doc.insertString(0, 0, "x");
		assertEquals("x", doc.getText());
		it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(0, "x", (Fragment) it.next());
	}
	
	public void testSameParticipantInsertStart() {
		ServerDocumentImpl doc = new ServerDocumentImpl();
		doc.insertString(1, 0, "abcd");
		doc.insertString(1, 0, "X");
		assertEquals("Xabcd", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "Xabcd", (Fragment) it.next());
		assertFalse(it.hasNext());
	}
	
	public void testSameParticipantInsertMiddle() {
		ServerDocumentImpl doc = new ServerDocumentImpl();
		doc.insertString(1, 0, "abcd");
		doc.insertString(1, 2, "X");
		assertEquals("abXcd", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "abXcd", (Fragment) it.next());
		assertFalse(it.hasNext());
	}
	
	public void testSameParticipantInsertEnd() {
		ServerDocumentImpl doc = new ServerDocumentImpl();
		doc.insertString(1, 0, "abcd");
		doc.insertString(1, 4, "XY");
		assertEquals("abcdXY", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "abcdXY", (Fragment) it.next());
		assertFalse(it.hasNext());
	}
	
	public void testInsertMultiple() {
		ServerDocumentImpl doc = new ServerDocumentImpl();
		doc.insertString(1, 0, "xyz");
		doc.insertString(2, 0, "a");
		doc.insertString(3, 4, "b");
		doc.insertString(4, 2, "c");
		assertEquals("axcyzb", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(2, "a", (Fragment) it.next());
		assertEquals(1, "x", (Fragment) it.next());
		assertEquals(4, "c", (Fragment) it.next());
		assertEquals(1, "yz", (Fragment) it.next());
		assertEquals(3, "b", (Fragment) it.next());
		assertFalse(it.hasNext());
	}
	
	public void testInsertRemove() {
		ServerDocumentImpl doc = new ServerDocumentImpl();
		doc.insertString(1, 0, "abcdefghijklmnop");
		doc.removeString(1, 2);
		doc.removeString(4, 4);
		doc.removeString(6, 2);
		assertEquals("adefklop", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "adefklop", (Fragment) it.next());
		assertFalse(it.hasNext());
	}
	
	public void testInsertMultipleRemove1() {
		ServerDocumentImpl doc = new ServerDocumentImpl();
		doc.insertString(1, 0, "test");
		doc.insertString(1, 4, "blub");
		doc.insertString(2, 4, "XY");
		assertEquals("testXYblub", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "test", (Fragment) it.next());
		assertEquals(2, "XY", (Fragment) it.next());
		assertEquals(1, "blub", (Fragment) it.next());
		assertFalse(it.hasNext());

		doc.removeString(3, 3);
		assertEquals("tesblub", doc.getText());
		it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "tesblub", (Fragment) it.next());
		assertFalse(it.hasNext());
	}

	public void testInsertMultipleRemove2() {
		ServerDocumentImpl doc = new ServerDocumentImpl();
		doc.insertString(1, 0, "test");
		doc.insertString(1, 4, "blub");
		doc.insertString(2, 4, "XY");
		assertEquals("testXYblub", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "test", (Fragment) it.next());
		assertEquals(2, "XY", (Fragment) it.next());
		assertEquals(1, "blub", (Fragment) it.next());
		assertFalse(it.hasNext());
		
		doc.removeString(4, 2);
		assertEquals("testblub", doc.getText());
		it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "testblub", (Fragment) it.next());
		assertFalse(it.hasNext());
	}

	public void testInsertMultipleRemove3() {
		ServerDocumentImpl doc = new ServerDocumentImpl();
		doc.insertString(1, 0, "test");
		doc.insertString(1, 4, "blub");
		doc.insertString(2, 4, "XY");
		assertEquals("testXYblub", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "test", (Fragment) it.next());
		assertEquals(2, "XY", (Fragment) it.next());
		assertEquals(1, "blub", (Fragment) it.next());
		assertFalse(it.hasNext());
		
		doc.removeString(0, 6);
		assertEquals("blub", doc.getText());
		it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "blub", (Fragment) it.next());
		assertFalse(it.hasNext());
	}
	
	public void testInsertRemoveAtEnd() {
		ServerDocumentImpl doc = new ServerDocumentImpl();
		doc.insertString(0, 0, "a");
		doc.insertString(0, 1, "b");
		doc.insertString(0, 2, "c");
		doc.insertString(0, 3, "d");
		doc.insertString(0, 4, "e");
		doc.insertString(0, 5, "f");
		doc.insertString(0, 6, "g");
		assertEquals("abcdefg", doc.getText());
		doc.removeString(6, 1);
		assertEquals("abcdef", doc.getText());
		
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(0, "abcdef", (Fragment) it.next());
	}
	
	public void testUpdateCaret() throws Exception {
		ServerDocumentImpl doc = new ServerDocumentImpl();
		doc.insertString(1, 0, "hello world");
		doc.updateCaret(1, 1, 2);
		assertEquals(1, doc.getSelection(1).getDot());
		assertEquals(2, doc.getSelection(1).getMark());
	}

}
