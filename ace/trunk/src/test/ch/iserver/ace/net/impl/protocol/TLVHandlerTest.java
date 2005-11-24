package ch.iserver.ace.net.impl.protocol;

import java.util.Iterator;

import junit.framework.TestCase;
import ch.iserver.ace.Fragment;
import ch.iserver.ace.net.impl.PortableDocumentExt;
import ch.iserver.ace.net.impl.PortableDocumentImpl;

public class TLVHandlerTest extends TestCase {

	/*
	 * Test method for 'ch.iserver.ace.net.impl.protocol.TLVHandler.create(PortableDocument)'
	 */
	public void testCreate() {
		//TODO:
	}

	/*
	 * Test method for 'ch.iserver.ace.net.impl.protocol.TLVHandler.parse(String, PortableDocumentExt)'
	 */
	public void testParse() {

		String data = "0 11 Los gehts:  1 15 ich habe durst. 2 18  das sagst du mir? 1 20  dir sage ich alles!";
		
		PortableDocumentExt document = new PortableDocumentImpl();
		TLVHandler.parse(data, document);
		
		Iterator iter = document.getFragments();
		int size = 0;
		int[] ids = new int[] {0, 1, 2, 1};
		String[] texts = new String[] { "Los gehts: ", "ich habe durst.", " das sagst du mir?", " dir sage ich alles!" };
		while (iter.hasNext()) {
			Fragment fragment = (Fragment) iter.next();
			assertEquals(ids[size], fragment.getParticipantId());
			assertEquals(texts[size], fragment.getText());
			size++;
		}
		assertEquals(4, size);
	}

}
