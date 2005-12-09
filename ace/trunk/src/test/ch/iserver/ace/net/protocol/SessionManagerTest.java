package ch.iserver.ace.net.protocol;

import java.net.InetAddress;

import junit.framework.TestCase;
import ch.iserver.ace.net.core.MutableUserDetails;
import ch.iserver.ace.net.core.RemoteUserProxyExt;
import ch.iserver.ace.net.core.RemoteUserProxyFactory;
import ch.iserver.ace.net.protocol.LogFilter;
import ch.iserver.ace.net.protocol.SessionManager;

public class SessionManagerTest extends TestCase {
	
	public void testCreateAndGetSession() throws Exception {
		String id = "ads214";
		InetAddress address = InetAddress.getLocalHost();
		int port = 45123;
		MutableUserDetails details = new MutableUserDetails("test-username", address, port);
		RemoteUserProxyFactory.init(new LogFilter(null, false));
		RemoteUserProxyExt proxy = RemoteUserProxyFactory.getInstance().createProxy(id, details);
		
		SessionManager manager = SessionManager.getInstance();
		manager.closeSessions(); //just to make shure the manager is empty before testing
		manager.createSession(proxy);
		
		assertEquals(1, manager.size());
		assertEquals(id ,manager.getSession(id).getUser().getId());
		assertEquals(address, manager.getSession(id).getHost());
		assertEquals(port, manager.getSession(id).getPort());
		assertFalse(manager.getSession(id).isInitiated());
	}
	
}
