package ch.iserver.ace.net.impl;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.net.NetworkServiceCallback;

public class DiscoveryCallbackImplTest extends TestCase {
	
	public void testCallbacks() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(NetworkServiceCallback.class);
		NetworkServiceCallback callback = (NetworkServiceCallback) callbackCtrl.getMock();
		MockControl serviceCtrl = MockControl.createControl(NetworkServiceExt.class);
		NetworkServiceExt service = (NetworkServiceExt)serviceCtrl.getMock();
		
		DiscoveryCallbackImpl discoveryCallback = new DiscoveryCallbackImpl(callback, service);
		
		RemoteUserProxyExt proxy = new RemoteUserProxyImpl("testid", new MutableUserDetails("testuser"));
		
		//define mock behavior
		callback.userDetailsChanged(proxy);
		callback.userDiscarded(proxy);
		callback.userDiscovered(proxy);
		
		service.discoverDocuments(proxy);
		
		// replay
		callbackCtrl.replay();
		serviceCtrl.replay();
		
		// test
		discoveryCallback.userDetailsChanged(proxy);
		discoveryCallback.userDiscarded(proxy);
		discoveryCallback.userDiscovered(proxy);
		discoveryCallback.userDiscoveryCompleted(proxy);
		
		
		// verify
		callbackCtrl.verify();
		serviceCtrl.verify();
	}

}
