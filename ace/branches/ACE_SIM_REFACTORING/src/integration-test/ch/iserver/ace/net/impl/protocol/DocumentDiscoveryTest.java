package ch.iserver.ace.net.impl.protocol;

import java.net.InetAddress;

import junit.framework.TestCase;

import org.easymock.ArgumentsMatcher;
import org.easymock.MockControl;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.impl.MutableUserDetails;
import ch.iserver.ace.net.impl.RemoteDocumentProxyImpl;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.RemoteUserProxyImpl;
import ch.iserver.ace.util.UUID;

public class DocumentDiscoveryTest extends TestCase {

	private final int NUM_DOCS = 5;
	private RemoteDocumentProxy[] docs;
	private RemoteUserProxyExt user;
	private MockControl filterCtrl;
	
	public DocumentDiscoveryTest() throws Exception {
		user = new RemoteUserProxyImpl(UUID.nextUUID(), 
				new MutableUserDetails("test-user", InetAddress.getLocalHost(), 54321));
		docs = new RemoteDocumentProxy[NUM_DOCS];
		filterCtrl = MockControl.createControl(RequestFilter.class);
		RequestFilter requestFilter = (RequestFilter) filterCtrl.getMock();
		filterCtrl.replay();
		for (int i=0; i < NUM_DOCS; i++) {
			DocumentDetails details = new DocumentDetails("doc"+i);
			docs[i] = new RemoteDocumentProxyImpl("id"+i, details, user, requestFilter);
		}
	}
	
	public void testExecute() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(NetworkServiceCallback.class);
		NetworkServiceCallback callback = (NetworkServiceCallback)callbackCtrl.getMock();
		
		Serializer serializer = SerializerImpl.getInstance();

		//TODO: finish class RemoteDocumentProxyArrayMatcher()
		fail("finish class RemoteDocumentProxyArrayMatcher() first.");
		callbackCtrl.setDefaultMatcher(new RemoteDocumentProxyArrayMatcher());
		
		callbackCtrl.replay();
		
		//discovery.execute(user);
		
		callbackCtrl.verify();
		filterCtrl.verify();
	}
	
}

class RemoteDocumentProxyArrayMatcher implements ArgumentsMatcher {

	//InetAddresss of UserDetails is ignored because of portability for this integration test
	public boolean matches(Object[] arg0, Object[] arg1) {
		RemoteDocumentProxy[] proxy0 = (RemoteDocumentProxy[])arg0;
		RemoteDocumentProxy[] proxy1 = (RemoteDocumentProxy[])arg1;
//		boolean result = proxy0.getId().equals(proxy1.getId()) &&
//			proxy0.getUserDetails().getUsername().equals(proxy1.getUserDetails().getUsername()) &&
//			proxy0.getUserDetails().getPort() == proxy1.getUserDetails().getPort();
		return false;
	}

	public String toString(Object[] arg0) {		
		return ((RemoteUserProxy)arg0[0]).toString();
	}
	
}
