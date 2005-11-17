package ch.iserver.ace.net.impl;

import java.net.InetAddress;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.impl.protocol.SessionManager;

public class DiscoveryCallbackImplTest extends TestCase {
	
	public void testCallbacks() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(NetworkServiceCallback.class);
		NetworkServiceCallback callback = (NetworkServiceCallback) callbackCtrl.getMock();
		MockControl serviceCtrl = MockControl.createControl(NetworkServiceExt.class);
		NetworkServiceExt service = (NetworkServiceExt)serviceCtrl.getMock();
		
		DiscoveryCallbackImpl discoveryCallback = new DiscoveryCallbackImpl(callback, service);
		
		RemoteUserProxyExt proxy = new RemoteUserProxyImpl("testid", new MutableUserDetails("testuser", InetAddress.getLocalHost(), 4123));
		SessionManager.getInstance().createSession(proxy);
		
		//define mock behavior
		callbackCtrl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
		callback.userDetailsChanged(proxy);
		callback.documentDiscarded(null);
		callback.userDiscarded(proxy);
		callback.userDiscovered(proxy);
		
		service.hasPublishedDocuments();
		serviceCtrl.setReturnValue(false);
//		service.discoverDocuments(proxy);
		
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
