package ch.iserver.ace.net.impl.protocol;

import java.net.InetAddress;

import junit.framework.TestCase;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.RemoteUserProxyImpl;

public class SessionManagerTest extends TestCase {

	private SessionManager manager;
	
	public void setUp() {
		manager = SessionManager.getInstance();
	}
	
	public void testCreateAndGetSession() throws Exception {
		String id = "ads214";
		InetAddress address = InetAddress.getLocalHost();
		int port = 45123;
		UserDetails details = new UserDetails("test-username", address, port);
		RemoteUserProxyExt proxy = new RemoteUserProxyImpl(id, details);
		
		manager.createSession(proxy);
		
		assertEquals(manager.size(), 1);
		assertEquals(manager.getSession(id).getUser().getId(), id);
		assertEquals(manager.getSession(id).getHost(), address);
		assertEquals(manager.getSession(id).getPort(), port);
		assertFalse(manager.getSession(id).isInitiated());
	}
	
}
