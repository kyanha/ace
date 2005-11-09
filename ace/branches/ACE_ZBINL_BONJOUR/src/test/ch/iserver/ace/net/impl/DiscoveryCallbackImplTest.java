package ch.iserver.ace.net.impl;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.impl.protocol.DocumentDiscovery;

public class DiscoveryCallbackImplTest extends TestCase {
	
	public void testCallbacks() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(NetworkServiceCallback.class);
		NetworkServiceCallback callback = (NetworkServiceCallback) callbackCtrl.getMock();
		MockControl docDiscoveryCtrl = MockControl.createControl(DocumentDiscovery.class);
		DocumentDiscovery docDisc = (DocumentDiscovery)docDiscoveryCtrl.getMock();
		DiscoveryCallbackImpl discoveryCallback = new DiscoveryCallbackImpl(callback, docDisc);
		RemoteUserProxyExt proxy = new RemoteUserProxyImpl("testid", new UserDetails("testuser"));
		
		//define mock behavior
		callback.userDetailsChanged(proxy);
		callback.userDiscarded(proxy);
		callback.userDiscovered(proxy);
		
		docDisc.execute(proxy);
		
		// replay
		callbackCtrl.replay();
		docDiscoveryCtrl.replay();
		
		// test
		discoveryCallback.userDetailsChanged(proxy);
		discoveryCallback.userDiscarded(proxy);
		discoveryCallback.userDiscovered(proxy);
		
		
		// verify
		callbackCtrl.verify();
		docDiscoveryCtrl.verify();
	}

}
