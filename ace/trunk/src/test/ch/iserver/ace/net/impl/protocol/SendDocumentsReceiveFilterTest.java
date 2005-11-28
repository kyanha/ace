package ch.iserver.ace.net.impl.protocol;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.beepcore.beep.core.MessageMSG;
import org.easymock.AbstractMatcher;
import org.easymock.MockControl;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.impl.DiscoveryCallbackImpl;
import ch.iserver.ace.net.impl.MutableUserDetails;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.RemoteDocumentProxyImpl;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.RemoteUserProxyFactory;
import ch.iserver.ace.net.impl.RemoteUserProxyImpl;
import ch.iserver.ace.net.impl.discovery.DiscoveryManagerFactory;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

public class SendDocumentsReceiveFilterTest extends TestCase {

	/*
	 * Test method for 'ch.iserver.ace.net.impl.protocol.SendDocumentsReceiveFilter.process(Request)'
	 */
	public void testProcess() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(NetworkServiceCallback.class);
		NetworkServiceCallback callback = (NetworkServiceCallback) callbackCtrl.getMock();
		
		NetworkServiceImpl.getInstance().setCallback(callback);
		
		RemoteUserProxyExt user = RemoteUserProxyFactory.getInstance().createProxy("userid1", new MutableUserDetails("user1", InetAddress.getLocalHost(), 41234));
		DiscoveryCallbackImpl discoveryCallback = new DiscoveryCallbackImpl(callback, NetworkServiceImpl.getInstance());
		DiscoveryManagerFactory.getDiscoveryManager(discoveryCallback);
		SessionManager.getInstance().createSession(user);
		
		MockControl filterCtrl = MockControl.createControl(RequestFilter.class);
		RequestFilter requestFilter = (RequestFilter) filterCtrl.getMock();
		filterCtrl.replay();
		RemoteDocumentProxy[] proxies = new RemoteDocumentProxy[4];
		proxies[0] = new RemoteDocumentProxyImpl("docid1", 
				new DocumentDetails("file1.txt"), 
				user, requestFilter);
		proxies[1] = new RemoteDocumentProxyImpl("docid2", 
				new DocumentDetails("file2.txt"), 
				user, requestFilter);
		proxies[2] = new RemoteDocumentProxyImpl("docid3", 
				new DocumentDetails("file3.txt"), 
				user, requestFilter);
		proxies[3] = new RemoteDocumentProxyImpl("docid4", 
				new DocumentDetails("file4.txt"), 
				user, requestFilter);
		
		SendDocumentsReceiveFilter filter = new SendDocumentsReceiveFilter(null);
		List docs = new ArrayList(4);
		docs.add(new DocumentInfo("docid1", "file1.txt", "userid1"));
		docs.add(new DocumentInfo("docid2", "file2.txt", "userid1"));
		docs.add(new DocumentInfo("docid3", "file3.txt", "userid1"));
		docs.add(new DocumentInfo("docid4", "file4.txt", "userid1"));
		
		
		Request request = new RequestImpl(ProtocolConstants.SEND_DOCUMENTS, null, docs);
		MockControl msgCtrl = MockControl.createControl(MessageMSG.class);
		MessageMSG message = (MessageMSG)msgCtrl.getMock();
		message.sendNUL();
		msgCtrl.setDefaultReturnValue(null);
		msgCtrl.replay();
		request.setMessage(message);
		
		callback.documentDiscovered(proxies);
		callbackCtrl.setDefaultMatcher(new RemoteDocumentProxyMatcher());
		
		callbackCtrl.replay();
		
		filter.process(request);
		
		callbackCtrl.verify();
		msgCtrl.verify();
		filterCtrl.verify();
	}
}

class RemoteDocumentProxyMatcher extends AbstractMatcher {

		protected boolean argumentMatches(Object arg0, Object arg1) {
			RemoteDocumentProxy[] proxies1 = (RemoteDocumentProxy[])arg0;
			RemoteDocumentProxy[] proxies2 = (RemoteDocumentProxy[])arg1;
			if (proxies1.length != proxies2.length) return false;
			
			for (int i = 0; i < proxies1.length; i++) {
				boolean result = proxies1[i].equals(proxies2[i]);
				if (!result) return false;
			}
			return true;
		}
}
