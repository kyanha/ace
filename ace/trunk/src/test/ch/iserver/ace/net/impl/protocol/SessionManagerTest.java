package ch.iserver.ace.net.impl.protocol;

import java.net.InetAddress;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.impl.DiscoveryCallback;
import ch.iserver.ace.net.impl.DiscoveryCallbackImpl;
import ch.iserver.ace.net.impl.MutableUserDetails;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.RemoteUserProxyFactory;
import ch.iserver.ace.net.impl.RemoteUserProxyImpl;
import ch.iserver.ace.net.impl.discovery.DiscoveryManagerFactory;

public class SessionManagerTest extends TestCase {

	private SessionManager manager;
	
	public void setUp() {
		manager = SessionManager.getInstance();
	}
	
	public void testCreateAndGetSession() throws Exception {
		String id = "ads214";
		InetAddress address = InetAddress.getLocalHost();
		int port = 45123;
		MutableUserDetails details = new MutableUserDetails("test-username", address, port);
		RemoteUserProxyFactory.init(new LogFilter(null, false));
		RemoteUserProxyExt proxy = RemoteUserProxyFactory.getInstance().createProxy(id, details);
		
		MockControl callbackCtrl = MockControl.createControl(NetworkServiceCallback.class);
		NetworkServiceCallback callback = (NetworkServiceCallback)callbackCtrl.getMock();
		callbackCtrl.replay();
		DiscoveryCallbackImpl discoveryCallback = new DiscoveryCallbackImpl(callback, null);
		DiscoveryManagerFactory.getDiscoveryManager(discoveryCallback);
		
		manager.createSession(proxy);
		
		assertEquals(manager.size(), 1);
		assertEquals(manager.getSession(id).getUser().getId(), id);
		assertEquals(manager.getSession(id).getHost(), address);
		assertEquals(manager.getSession(id).getPort(), port);
		assertFalse(manager.getSession(id).isInitiated());
	}
	
}
