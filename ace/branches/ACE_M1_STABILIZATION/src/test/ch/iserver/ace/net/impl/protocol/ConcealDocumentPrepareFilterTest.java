package ch.iserver.ace.net.impl.protocol;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.net.impl.PublishedDocument;

public class ConcealDocumentPrepareFilterTest extends TestCase {

	/*
	 * Test method for 'ch.iserver.ace.net.impl.protocol.ConcealDocumentPrepareFilter.process(Request)'
	 */
	public void testProcess() {
		String userId = "asd-234";
		
		MockControl connectionCtrl = MockControl.createControl(ParticipantConnectionExt.class);
		ParticipantConnectionExt connection = (ParticipantConnectionExt) connectionCtrl.getMock();
		
		//configure session manager, add 1 session
		
		//create published document
		String docId = "afsd-23";
		PublishedDocument doc = new PublishedDocument(docId, null, null, null, null);
		Request request = new RequestImpl(ProtocolConstants.CONCEAL, userId, doc);
	}

}
