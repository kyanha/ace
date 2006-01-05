package ch.iserver.ace.net.core;

import java.net.InetAddress;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.discovery.DiscoveryManagerFactory;
import ch.iserver.ace.net.protocol.SessionManager;
import ch.iserver.ace.net.protocol.filter.LogFilter;
import ch.iserver.ace.net.protocol.filter.RequestFilter;
import ch.iserver.ace.util.SingleThreadDomain;

public class DiscoveryCallbackImplTest extends TestCase {
	
	public void testCallbacks() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(NetworkServiceCallback.class);
		NetworkServiceCallback callback = (NetworkServiceCallback) callbackCtrl.getMock();
		MockControl serviceCtrl = MockControl.createControl(NetworkServiceExt.class);
		NetworkServiceExt service = (NetworkServiceExt)serviceCtrl.getMock();
		
		RequestFilter filter = new LogFilter(null ,false);
		DiscoveryCallbackImpl discoveryCallback = new DiscoveryCallbackImpl(callback, service, filter);
		DiscoveryManagerFactory.init(discoveryCallback, new SingleThreadDomain());
		DiscoveryManagerFactory.getDiscoveryManager();
		RemoteUserProxyExt proxy = RemoteUserProxyFactory.getInstance().createProxy("testid", new MutableUserDetails("testuser", InetAddress.getLocalHost(), 4123));
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
