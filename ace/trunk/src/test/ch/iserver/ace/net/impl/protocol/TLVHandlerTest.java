package ch.iserver.ace.net.impl.protocol;

import java.net.InetAddress;
import java.util.Iterator;

import junit.framework.TestCase;
import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Fragment;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.core.FragmentImpl;
import ch.iserver.ace.net.core.MutableUserDetails;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.core.PortableDocumentExt;
import ch.iserver.ace.net.core.PortableDocumentImpl;
import ch.iserver.ace.net.core.RemoteUserProxyFactory;
import ch.iserver.ace.net.protocol.TLVHandler;

public class TLVHandlerTest extends TestCase {

	/*
	 * Test method for 'ch.iserver.ace.net.impl.protocol.TLVHandler.parse(String, PortableDocumentExt)'
	 */
	public void testCreateAndParse() throws Exception {
		NetworkServiceImpl.getInstance(); //to initialize REmoteUserProxyFactory
		PortableDocumentExt document = new PortableDocumentImpl();
		document.addParticipant(ParticipantConnection.PUBLISHER_ID, null);
		document.addParticipant(1, RemoteUserProxyFactory.getInstance().createProxy("sadfasd-24", 
				new MutableUserDetails("Jimmy Ritter", InetAddress.getByName("123.43.45.21"), 4123)));
		document.addParticipant(2, RemoteUserProxyFactory.getInstance().createProxy("cbvncvvc-24", 
				new MutableUserDetails("Samuel Fuchs", InetAddress.getByName("123.43.12.197"), 4123)));
		document.setSelection(0, new CaretUpdate(0, 0));
		document.setSelection(1, new CaretUpdate(456, 456));
		document.setSelection(2, new CaretUpdate(7, 7));
		Fragment fragment = new FragmentImpl(ParticipantConnection.PUBLISHER_ID, "Los gehts: ");
		document.addFragment(fragment);
		fragment = new FragmentImpl(1, "ich habe durst.");
		document.addFragment(fragment);
		fragment = new FragmentImpl(2, " das sagst du mir?");
		document.addFragment(fragment);
		fragment = new FragmentImpl(1, " dir sage ich alles!");
		document.addFragment(fragment);
		
		char[] encoded = TLVHandler.create(document);
		PortableDocumentExt resultDoc = new PortableDocumentImpl();
		TLVHandler.parse(new String(encoded), resultDoc);
		Iterator iter = resultDoc.getFragments();
		int size = 0;
		int[] ids = new int[] {0, 1, 2, 1};
		String[] texts = new String[] { "Los gehts: ", "ich habe durst.", " das sagst du mir?", " dir sage ich alles!" };
		while (iter.hasNext()) {
			fragment = (Fragment) iter.next();
			assertEquals(ids[size], fragment.getParticipantId());
			assertEquals(texts[size], fragment.getText());
			size++;
		}
		assertEquals(4, size);
	}

}
