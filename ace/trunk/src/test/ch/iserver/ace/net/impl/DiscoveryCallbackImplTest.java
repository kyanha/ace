package ch.iserver.ace.net.impl;

import java.net.InetAddress;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.core.DiscoveryCallbackImpl;
import ch.iserver.ace.net.core.MutableUserDetails;
import ch.iserver.ace.net.core.NetworkServiceExt;
import ch.iserver.ace.net.core.RemoteUserProxyExt;
import ch.iserver.ace.net.core.RemoteUserProxyFactory;
import ch.iserver.ace.net.discovery.DiscoveryManagerFactory;
import ch.iserver.ace.net.impl.protocol.LogFilter;
import ch.iserver.ace.net.impl.protocol.RequestFilter;
import ch.iserver.ace.net.impl.protocol.SessionManager;

public class DiscoveryCallbackImplTest extends TestCase {
	
	public void testCallbacks() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(NetworkServiceCallback.class);
		NetworkServiceCallback callback = (NetworkServiceCallback) callbackCtrl.getMock();
		MockControl serviceCtrl = MockControl.createControl(NetworkServiceExt.class);
		NetworkServiceExt service = (NetworkServiceExt)serviceCtrl.getMock();
		
		RequestFilter filter = new LogFilter(null ,false);
		DiscoveryCallbackImpl discoveryCallback = new DiscoveryCallbackImpl(callback, service, filter);
		DiscoveryManagerFactory.init(discoveryCallback);
		DiscoveryManagerFactory.getDiscoveryManager();
		RemoteUserProxyFactory.init(new LogFilter(null, false));
		RemoteUserProxyExt proxy = RemoteUserProxyFactory.getInstance().
					createProxy("testid", new MutableUserDetails("testuser", InetAddress.getLocalHost(), 4123));
		SessionManager.getInstance().createSession(proxy);
		
		//define mock behavior
		callbackCtrl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
		callback.userDetailsChanged(proxy);
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
		//TODO: write test where proxy has documents and documentDiscarded(..) is invoked
		discoveryCallback.userDiscovered(proxy);
		discoveryCallback.userDiscoveryCompleted(proxy);
		
		
		// verify
		callbackCtrl.verify();
		serviceCtrl.verify();
	}

}
