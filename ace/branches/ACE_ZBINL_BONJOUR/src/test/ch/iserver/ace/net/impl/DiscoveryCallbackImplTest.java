package ch.iserver.ace.net.impl;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.NetworkServiceCallback;

public class DiscoveryCallbackImplTest extends TestCase {
	
	public void testCallbacks() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(NetworkServiceCallback.class);
		NetworkServiceCallback callback = (NetworkServiceCallback) callbackCtrl.getMock();
		DiscoveryCallbackImpl discoveryCallback = new DiscoveryCallbackImpl(callback);
		RemoteUserProxyExt proxy = new RemoteUserProxyImpl("testid", new UserDetails("testuser"));
		
		//define mock behavior
		callback.userDetailsChanged(proxy);
		callback.userDiscarded(proxy);
		callback.userDiscovered(proxy);
		
		// replay
		callbackCtrl.replay();
		
		// test
		discoveryCallback.userDetailsChanged(proxy);
		discoveryCallback.userDiscarded(proxy);
		discoveryCallback.userDiscovered(proxy);
		
		
		// verify
		callbackCtrl.verify();
	}

}
