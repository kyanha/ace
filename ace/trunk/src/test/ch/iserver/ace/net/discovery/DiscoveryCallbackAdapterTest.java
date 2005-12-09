package ch.iserver.ace.net.discovery;

import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;

import org.easymock.ArgumentsMatcher;
import org.easymock.MockControl;

import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.core.DiscoveryCallback;
import ch.iserver.ace.net.core.MutableUserDetails;
import ch.iserver.ace.net.core.RemoteUserProxyExt;
import ch.iserver.ace.net.core.RemoteUserProxyFactory;
import ch.iserver.ace.net.core.RemoteUserProxyImpl;
import ch.iserver.ace.net.protocol.LogFilter;

/**
 * 
 *
 */
public class DiscoveryCallbackAdapterTest extends TestCase {

	private MockControl callbackCtrl;
	private DiscoveryCallback callback;
	private DiscoveryCallbackAdapter adapter;
	
	public void setUp() {
		callbackCtrl = MockControl.createControl(DiscoveryCallback.class);
		callbackCtrl.setDefaultMatcher(new RemoteUserProxyMatcher());
		callback = (DiscoveryCallback)callbackCtrl.getMock();
		adapter = new DiscoveryManagerImpl(callback);
	}
	
	/**
	 * Tests the DiscoveryCallbackAdapter.
	 * 
	 * @throws Exception
	 */
	public void testAll() throws Exception {
		//define mock behavior
		MutableUserDetails details = new MutableUserDetails("testuser");
		int port = 8888;
		details.setPort(port);
		RemoteUserProxyFactory.init(new LogFilter(null, false));
		RemoteUserProxyExt proxy = RemoteUserProxyFactory.getInstance().createProxy("test-id", details);
		
		testUserDiscovered(port, proxy);
		
		testUserNameChanged(proxy);
		
		testUserAddressResolved(details, proxy);
		
		testUserDiscarded(proxy);
	}

	/**
	 * Test method for 'ch.iserver.ace.net.impl.discovery.DiscoveryCallbackAdapter.userDiscovered(..)'
	 *
	 * @param port
	 * @param proxy
	 */
	private void testUserDiscovered(int port, RemoteUserProxyExt proxy) {
		callback.userDiscovered(proxy);
		
		// replay
		callbackCtrl.replay();
		
		// test
		adapter.userDiscovered("testservice", "testuser", "test-id", port);
		
		// verify
		callbackCtrl.verify();
	}

	/**
	 * Test method for 'ch.iserver.ace.net.impl.discovery.DiscoveryCallbackAdapter.userAddressResolved(..)'
	 * 
	 * @param details
	 * @param proxy
	 * @throws UnknownHostException
	 */
	private void testUserAddressResolved(MutableUserDetails details, RemoteUserProxyExt proxy) throws UnknownHostException {
		callbackCtrl.reset();
		callbackCtrl.setDefaultMatcher(new RemoteUserProxyMatcher());
		InetAddress addr = InetAddress.getByName("147.87.14.145");
		details.setAddress(addr);
		proxy.setMutableUserDetails(details);
		callback.userDiscoveryCompleted(proxy);
		
		callbackCtrl.replay();
		
		adapter.userAddressResolved("testservice", addr);
		
		callbackCtrl.verify();
	}

	/**
	 * Test method for 'ch.iserver.ace.net.impl.discovery.DiscoveryCallbackAdapter.userDetailsChanged(..)'
	 * 
	 * @param proxy
	 */
	private void testUserNameChanged(RemoteUserProxyExt proxy) {
		callbackCtrl.reset();
		callbackCtrl.setDefaultMatcher(new RemoteUserProxyMatcher());

		proxy.getUserDetails().setUsername("testuser2");
		callback.userDetailsChanged(proxy);
		
		callbackCtrl.replay();
		
		adapter.userNameChanged("testservice", "testuser2");
		
		callbackCtrl.verify();
	}

	/**
	 * Test method for 'ch.iserver.ace.net.impl.discovery.DiscoveryCallbackAdapter.userDiscarded(..)'
	 * 
	 * @param proxy
	 */
	private void testUserDiscarded(RemoteUserProxyExt proxy) {
		callbackCtrl.reset();
		callbackCtrl.setDefaultMatcher(new RemoteUserProxyMatcher());

		callback.userDiscarded(proxy);
		
		callbackCtrl.replay();
		
		adapter.userDiscarded("testservice");
		
		callbackCtrl.verify();
	}
}

class RemoteUserProxyMatcher implements ArgumentsMatcher {

	public boolean matches(Object[] arg0, Object[] arg1) {
		RemoteUserProxy proxy0 = (RemoteUserProxy)arg0[0];
		RemoteUserProxy proxy1 = (RemoteUserProxy)arg1[0];
		boolean result = proxy0.getId().equals(proxy1.getId()) &&
			proxy0.getUserDetails().equals(proxy1.getUserDetails());
		return result;
	}

	public String toString(Object[] arg0) {
		return ((RemoteUserProxyImpl)arg0[0]).toString();
	}
	
}
