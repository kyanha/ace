package ch.iserver.ace;


import java.util.Iterator;

import ch.iserver.ace.DefaultDocumentModel;

import junit.framework.TestCase;

public class DefaultDocumentModelTest extends TestCase {
	
	private void assertEquals(int pid, String text, Fragment f) {
		assertEquals(pid, f.getParticipantId());
		assertEquals(text, f.getText());
	}
	
	public void testSimpleInsert() {
		DefaultDocumentModel doc = new DefaultDocumentModel();
		doc.insertString(0, "x", 1);
		assertEquals("x", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "x", (Fragment) it.next());
		assertFalse(it.hasNext());
	}
	
	public void testSameParticipantInsertStart() {
		DefaultDocumentModel doc = new DefaultDocumentModel();
		doc.insertString(0, "abcd", 1);
		doc.insertString(0, "X", 1);
		assertEquals("Xabcd", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "Xabcd", (Fragment) it.next());
		assertFalse(it.hasNext());
	}
	
	public void testSameParticipantInsertMiddle() {
		DefaultDocumentModel doc = new DefaultDocumentModel();
		doc.insertString(0, "abcd", 1);
		doc.insertString(2, "X", 1);
		assertEquals("abXcd", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "abXcd", (Fragment) it.next());
		assertFalse(it.hasNext());
	}
	
	public void testSameParticipantInsertEnd() {
		DefaultDocumentModel doc = new DefaultDocumentModel();
		doc.insertString(0, "abcd", 1);
		doc.insertString(4, "XY", 1);
		assertEquals("abcdXY", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "abcdXY", (Fragment) it.next());
		assertFalse(it.hasNext());
	}
	
	public void testInsertMultiple() {
		DefaultDocumentModel doc = new DefaultDocumentModel();
		doc.insertString(0, "xyz", 1);
		doc.insertString(0, "a", 2);
		doc.insertString(4, "b", 3);
		doc.insertString(2, "c", 4);
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
		DefaultDocumentModel doc = new DefaultDocumentModel();
		doc.insertString(0, "abcdefghijklmnop", 1);
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
		DefaultDocumentModel doc = new DefaultDocumentModel();
		doc.insertString(0, "test", 1);
		doc.insertString(4, "blub", 1);
		doc.insertString(4, "XY", 2);
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
		DefaultDocumentModel doc = new DefaultDocumentModel();
		doc.insertString(0, "test", 1);
		doc.insertString(4, "blub", 1);
		doc.insertString(4, "XY", 2);
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
		DefaultDocumentModel doc = new DefaultDocumentModel();
		doc.insertString(0, "test", 1);
		doc.insertString(4, "blub", 1);
		doc.insertString(4, "XY", 2);
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
	
	public void testUpdateCaret() throws Exception {
		DefaultDocumentModel doc = new DefaultDocumentModel();
		doc.participantJoined(1);
		doc.insertString(0, "hello world", 1);
		doc.updateCaret(1, 1, 2);
		assertEquals(1, doc.getDot(1));
		assertEquals(2, doc.getMark(1));
		doc.participantLeft(1);
	}

}
