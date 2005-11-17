package ch.iserver.ace.net.impl.protocol;

import junit.framework.TestCase;

import org.beepcore.beep.core.MessageMSG;
import org.easymock.AbstractMatcher;
import org.easymock.MockControl;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.impl.NetworkServiceImpl;

public class PublishedDocumentsRequestFilterTest extends TestCase {

	/*
	 * Test method for 'ch.iserver.ace.net.impl.protocol.GetPublishedDocumentsFilter.process(Request)'
	 */
	public void testProcessOK() throws Exception {
		MockControl succCtrl = MockControl.createControl(RequestFilter.class);
		RequestFilter successor = (RequestFilter)succCtrl.getMock();
		succCtrl.replay();
		
		NetworkServiceImpl service = NetworkServiceImpl.getInstance();
		DocumentDetails details = new DocumentDetails("file1.txt");
		service.publish(null, details);
		details = new DocumentDetails("file2.txt");
		service.publish(null, details);
		details = new DocumentDetails("file3.txt");
		service.publish(null, details);
		
		PublishedDocumentsRequestFilter filter = new PublishedDocumentsRequestFilter(successor);
		
		Request request = new RequestImpl(ProtocolConstants.PUBLISHED_DOCUMENTS, null, null);
		MockControl msgCtrl = MockControl.createControl(MessageMSG.class);
		MessageMSG msg = (MessageMSG)msgCtrl.getMock();
		request.setMessage(msg);
		msg.sendRPY(null);
		msgCtrl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
		msgCtrl.setDefaultReturnValue(null);
		msgCtrl.replay();
		
		filter.process(request);
		
		succCtrl.verify();
		msgCtrl.verify();
	}
	
	public void testProccessForward() {
		MockControl succCtrl = MockControl.createControl(RequestFilter.class);
		RequestFilter successor = (RequestFilter)succCtrl.getMock();
		
		PublishedDocumentsRequestFilter filter = new PublishedDocumentsRequestFilter(successor);
		
		Request request = new RequestImpl(ProtocolConstants.PUBLISH, null, null);
		successor.process(request);
		succCtrl.setDefaultMatcher(new RequestMatcher());
		succCtrl.replay();
		
		filter.process(request);
		
		succCtrl.verify();
	}

}

class RequestMatcher extends AbstractMatcher {
	
	protected boolean argumentMatches(Object arg0, Object arg1) {
		Request req1 = (Request)arg0;
		Request req2 = (Request)arg1;
		return req1.getType() == req2.getType() && req1.equals(req2);
	
	}
}
