package ch.iserver.ace.net.impl.protocol;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.easymock.AbstractMatcher;
import org.easymock.MockControl;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.impl.RemoteDocumentProxyImpl;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.RemoteUserProxyImpl;

public class DocumentDiscoveryCallbackImplTest extends TestCase {

	private static final int NUM_DOCS = 4;
	
	public void testDocumentDiscovered() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(NetworkServiceCallback.class);
		NetworkServiceCallback callback = (NetworkServiceCallback)callbackCtrl.getMock();
		
		String id = "adsf23e-2";
		InetAddress address = InetAddress.getLocalHost();
		int port = 45123;
		UserDetails details = new UserDetails("test-username", address, port);
		RemoteUserProxyExt user = new RemoteUserProxyImpl(id, details);
		SessionManager.getInstance().createSession(user);
		RemoteDocumentProxy[] docs = createDocs(user);
		
		callback.documentDiscovered(docs);
		callbackCtrl.setDefaultMatcher(new RemoteDocumentProxyMatcher());
		DocumentDiscoveryCallback discovery = new DocumentDiscoveryCallbackImpl(callback);
		
		callbackCtrl.replay();
		
		Map data = new LinkedHashMap();
		data.put("id0", "file0");
		data.put("id1", "file1");
		data.put("id2", "file2");
		data.put("id3", "file3");
		discovery.documentsDiscovered(id, data);

		//verify
		callbackCtrl.verify();
		
		Collection c = user.getSharedDocuments();
		assertEquals(c.size(), 4);
		Iterator iter = c.iterator();
		while (iter.hasNext()) {
			RemoteDocumentProxy doc = (RemoteDocumentProxy)iter.next();
			assertTrue(data.containsKey(doc.getId()));
			assertTrue(data.containsValue(doc.getDocumentDetails().getTitle()));
		}
	}
	
	private RemoteDocumentProxy[] createDocs(RemoteUserProxyExt user) {
		RemoteDocumentProxy[] docs = new RemoteDocumentProxy[NUM_DOCS];  
		for (int i=0; i < NUM_DOCS; i++) {
			String docId = "id"+i;
			DocumentDetails details = new DocumentDetails("file"+i);
			docs[i] = new RemoteDocumentProxyImpl(docId, details, user);
		}
		return docs;
	}

	class RemoteDocumentProxyMatcher extends AbstractMatcher {
		
		public boolean argumentMatches(Object expected, Object actual) {
			RemoteDocumentProxy[] docs1 = (RemoteDocumentProxy[])expected;
			RemoteDocumentProxy[] docs2 = (RemoteDocumentProxy[])actual;
			
			if (docs1.length != docs2.length) return false;
			
			for (int i=0; i < docs1.length; i++) {
				RemoteDocumentProxy doc1 = docs1[i];
				RemoteDocumentProxy doc2 = docs2[i];
				boolean isEqual = doc1.getId().equals(doc2.getId()) && 
					doc1.getDocumentDetails().getTitle().equals(doc2.getDocumentDetails().getTitle()) &&
					doc1.getPublisher().getId().equals(doc2.getPublisher().getId());
				if (!isEqual) return false;
			}
			return true;
		}
	}
	
}


