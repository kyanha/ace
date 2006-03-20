package ch.iserver.ace.collaboration.jupiter.server.document;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Fragment;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.RemoteUserProxyStub;

public class SimpleServerDocumentTest extends TestCase {
	
	private void assertEquals(int pid, String text, Fragment f) {
		assertEquals(pid, f.getParticipantId());
		assertEquals(text, f.getText());
	}
		
	public void testSimpleInsert() {
		SimpleServerDocument doc = new SimpleServerDocument();
		
		doc.insertString(1, 0, "x");
		assertEquals("x", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "x", (Fragment) it.next());
		assertFalse(it.hasNext());

		doc = new SimpleServerDocument();
		doc.insertString(0, 0, "x");
		assertEquals("x", doc.getText());
		it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(0, "x", (Fragment) it.next());
	}
	
	public void testSameParticipantInsertStart() {
		SimpleServerDocument doc = new SimpleServerDocument();
		doc.insertString(1, 0, "abcd");
		doc.insertString(1, 0, "X");
		assertEquals("Xabcd", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "Xabcd", (Fragment) it.next());
		assertFalse(it.hasNext());
	}
	
	public void testSameParticipantInsertMiddle() {
		SimpleServerDocument doc = new SimpleServerDocument();
		doc.insertString(1, 0, "abcd");
		doc.insertString(1, 2, "X");
		assertEquals("abXcd", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "abXcd", (Fragment) it.next());
		assertFalse(it.hasNext());
	}
	
	public void testSameParticipantInsertEnd() {
		SimpleServerDocument doc = new SimpleServerDocument();
		doc.insertString(1, 0, "abcd");
		doc.insertString(1, 4, "XY");
		assertEquals("abcdXY", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "abcdXY", (Fragment) it.next());
		assertFalse(it.hasNext());
	}
	
	public void testInsertMultiple() {
		SimpleServerDocument doc = new SimpleServerDocument();
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
		SimpleServerDocument doc = new SimpleServerDocument();
		doc.insertString(1, 0, "abcdefghijklmnop");
		doc.removeString(1, 2);
		assertEquals("adefghijklmnop", doc.getText());
		doc.removeString(4, 4);
		doc.removeString(6, 2);
		assertEquals("adefklop", doc.getText());
		Iterator it = doc.getFragments();
		assertTrue(it.hasNext());
		assertEquals(1, "adefklop", (Fragment) it.next());
		assertFalse(it.hasNext());
	}
	
	public void testInsertMultipleRemove1() {
		SimpleServerDocument doc = new SimpleServerDocument();
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
		SimpleServerDocument doc = new SimpleServerDocument();
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
		SimpleServerDocument doc = new SimpleServerDocument();
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
		SimpleServerDocument doc = new SimpleServerDocument();
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
		SimpleServerDocument doc = new SimpleServerDocument();
		doc.participantJoined(1, new RemoteUserProxyStub("x"));
		doc.insertString(1, 0, "hello world");
		doc.updateCaret(1, 1, 2);
		assertEquals(1, doc.getSelection(1).getDot());
		assertEquals(2, doc.getSelection(1).getMark());
		
		doc.insertString(1, 0, "blabla");
		assertEquals(7, doc.getSelection(1).getDot());
		assertEquals(8, doc.getSelection(1).getMark());
		
		doc.removeString(4, 4);
		assertEquals(4, doc.getSelection(1).getDot());
		assertEquals(4, doc.getSelection(1).getMark());
	}
	
	public void testToPortableDocument() throws Exception {
		SimpleServerDocument doc = new SimpleServerDocument();
		doc.participantJoined(0, new RemoteUserProxyStub("Y"));
		doc.participantJoined(1, new RemoteUserProxyStub("X"));
		doc.insertString(0, 0, "hello world");
		doc.updateCaret(0, 4, 10);
		doc.insertString(1, 11, "bonjour monde");
		doc.updateCaret(1, 15, 23);
		doc.insertString(0, 24, "hallo welt");
		doc.insertString(1, 34, "blabla");
		
		PortableDocument document = doc.toPortableDocument();
		int[] ids = document.getParticipantIds();
		assertEquals(2, ids.length);

		List idList = new ArrayList();
		idList.add(new Integer(ids[0]));
		idList.add(new Integer(ids[1]));
		assertTrue(idList.contains(new Integer(0)));
		assertTrue(idList.contains(new Integer(1)));
		
		CaretUpdate caret = document.getSelection(0);
		assertEquals(4, caret.getDot());
		assertEquals(10, caret.getMark());
		
		caret = document.getSelection(1);
		assertEquals(15, caret.getDot());
		assertEquals(23, caret.getMark());
		
		Iterator it = document.getFragments();
		assertTrue(it.hasNext());
		assertEquals(0, "hello world", (Fragment) it.next());
		assertTrue(it.hasNext());
		assertEquals(1, "bonjour monde", (Fragment) it.next());
		assertTrue(it.hasNext());
		assertEquals(0, "hallo welt", (Fragment) it.next());
		assertTrue(it.hasNext());
		assertEquals(1, "blabla", (Fragment) it.next());
		assertFalse(it.hasNext());
		
		RemoteUserProxy proxy = document.getUserProxy(0);
		assertEquals(new RemoteUserProxyStub("Y"), proxy);
		proxy = document.getUserProxy(1);
		assertEquals(new RemoteUserProxyStub("X"), proxy);
	}

}
