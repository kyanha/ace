package ch.iserver.ace.net.impl.protocol;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.net.DocumentServerLogic;
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
		MockControl docServerLogicCtrl = MockControl.createControl(DocumentServerLogic.class);
		DocumentServerLogic logic = (DocumentServerLogic) docServerLogicCtrl.getMock();
		docServerLogicCtrl.replay();
		PublishedDocument doc = new PublishedDocument(docId, logic, null, null, null);
		Request request = new RequestImpl(ProtocolConstants.CONCEAL, userId, doc);
	}

}
