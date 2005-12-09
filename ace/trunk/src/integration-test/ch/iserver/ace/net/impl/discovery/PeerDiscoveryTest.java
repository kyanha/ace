/*
 * $Id$
 *
 * ace - a collaborative editor
 * Copyright (C) 2005 Mark Bigler, Simon Raess, Lukas Zbinden
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package ch.iserver.ace.net.impl.discovery;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.ArgumentsMatcher;
import org.easymock.MockControl;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.core.Discovery;
import ch.iserver.ace.net.core.DiscoveryCallback;
import ch.iserver.ace.net.core.MutableUserDetails;
import ch.iserver.ace.net.core.RemoteUserProxyExt;
import ch.iserver.ace.net.core.RemoteUserProxyFactory;
import ch.iserver.ace.net.discovery.AbstractQueryListener;
import ch.iserver.ace.net.discovery.Bonjour;
import ch.iserver.ace.net.discovery.BrowseListenerImpl;
import ch.iserver.ace.net.discovery.DiscoveryCallbackAdapter;
import ch.iserver.ace.net.discovery.DiscoveryManagerImpl;
import ch.iserver.ace.net.discovery.IPQueryListener;
import ch.iserver.ace.net.discovery.PeerDiscovery;
import ch.iserver.ace.net.discovery.PeerDiscoveryImpl;
import ch.iserver.ace.net.discovery.ResolveListenerImpl;
import ch.iserver.ace.net.discovery.TXTQueryListener;
import ch.iserver.ace.net.discovery.UserRegistration;
import ch.iserver.ace.net.discovery.UserRegistrationImpl;
import ch.iserver.ace.net.impl.protocol.LogFilter;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.ResolveListener;

public class PeerDiscoveryTest extends TestCase {

	private PeerDiscovery peerDiscovery;
	private UserRegistration registration;
	
	/**
	 * Note: Before running this test, make shure that no instances of ACE are running 
	 * on this hosts subnet, otherwise the test will fail.
	 */
	public void testPeerDiscoveryTest() {
		//register 4 (local) peers
		List users = new ArrayList();
		users.add("peer1");
		users.add("peer2");
		users.add("peer3");
		users.add("peer4");
		List services = new ArrayList();
		services.add("peer-machine1");
		services.add("peer-machine2");
		services.add("peer-machine3");
		services.add("peer-machine4");
		int[] ports = new int[]{55890, 55981, 55982, 55893};
		TestuserRegistrar registrar = new TestuserRegistrar(users, services, ports);
		registrar.execute();
		
		try {
			Thread.sleep(3000);
		} catch (Exception e) {}
		
		//register local user
		MockControl discoveryCallbackCtrl = MockControl.createControl(DiscoveryCallback.class);
		discoveryCallbackCtrl.setDefaultMatcher(new RemoteUserProxyMatcher());
		DiscoveryCallback callback = (DiscoveryCallback)discoveryCallbackCtrl.getMock();
		Discovery discovery = createDiscovery(callback);
		discovery.setUserDetails(new UserDetails("user1"));
		discovery.setUserId("user-id");
		
		//problem: ordering of userDiscovered calls not predictable
		RemoteUserProxyFactory.init(new LogFilter(null, false));
		RemoteUserProxyExt rem1 = RemoteUserProxyFactory.getInstance().createProxy("peer1"+ports[0], new MutableUserDetails("peer1", null, ports[0]));
		callback.userDiscovered(rem1);
		//note: InetAddress will not be compared in comparison, c.f. RemoteUserProxyMatcher
		callback.userDiscoveryCompleted(rem1);
		RemoteUserProxyExt rem2 = RemoteUserProxyFactory.getInstance().createProxy("peer2"+ports[1], new MutableUserDetails("peer2", null, ports[1]));
		callback.userDiscovered(rem2);
		callback.userDiscoveryCompleted(rem2);
		RemoteUserProxyExt rem3 = RemoteUserProxyFactory.getInstance().createProxy("peer3"+ports[2], new MutableUserDetails("peer3", null, ports[2]));
		callback.userDiscovered(rem3);
		callback.userDiscoveryCompleted(rem3);
		RemoteUserProxyExt rem4 = RemoteUserProxyFactory.getInstance().createProxy("peer4"+ports[3], new MutableUserDetails("peer4", null, ports[3]));
		callback.userDiscovered(rem4);
		callback.userDiscoveryCompleted(rem4);
		
		discoveryCallbackCtrl.replay();
		
		discovery.execute();
		
		try {
			Thread.sleep(2500);
		} catch (Exception e) {}
		
		discoveryCallbackCtrl.verify();
		
		assertTrue(registration.isRegistered());
		
		discoveryCallbackCtrl.reset();
		discoveryCallbackCtrl.setDefaultMatcher(new RemoteUserProxyMatcher());
		
		callback.userDiscarded(rem1);
		callback.userDiscarded(rem2);
		callback.userDiscarded(rem3);
		callback.userDiscarded(rem4);
		
		discoveryCallbackCtrl.replay();
		
		registrar.stop();
		
		try {
			Thread.sleep(2500);
		} catch (Exception e) {}
		
		discoveryCallbackCtrl.verify();
		
		discovery.abort();
		assertFalse(registration.isRegistered());
	}
	
	public Discovery createDiscovery(DiscoveryCallback callback) {
		registration = new UserRegistrationImpl();
		peerDiscovery = createPeerDiscovery(callback);
		
		Bonjour b = new Bonjour(registration, peerDiscovery);
		Bonjour.setLocalServiceName(System.getProperty("user.name"));
		return b;
	}
	
	
	private PeerDiscovery createPeerDiscovery(DiscoveryCallback callback) {
		DiscoveryCallbackAdapter adapter = new DiscoveryManagerImpl(callback);
		AbstractQueryListener ipListener = new IPQueryListener(adapter);
		AbstractQueryListener txtListener = new TXTQueryListener(adapter);
		ResolveListener resolveListener = new ResolveListenerImpl(adapter, ipListener, txtListener);
		BrowseListener browseListener = new BrowseListenerImpl(adapter, resolveListener);
		PeerDiscovery discovery = new PeerDiscoveryImpl(browseListener);
		return discovery;
	}
	
class RemoteUserProxyMatcher implements ArgumentsMatcher {

		//InetAddresss of UserDetails is ignored because of portability for this integration test
		public boolean matches(Object[] arg0, Object[] arg1) {
			RemoteUserProxyExt proxy0 = (RemoteUserProxyExt) arg0[0];
			RemoteUserProxyExt proxy1 = (RemoteUserProxyExt) arg1[0];
			boolean result = proxy0.getId().equals(proxy1.getId()) &&
				proxy0.getMutableUserDetails().getUsername().equals(proxy1.getMutableUserDetails().getUsername()) &&
				proxy0.getMutableUserDetails().getPort() == proxy1.getMutableUserDetails().getPort();
			return result;
		}

		public String toString(Object[] arg0) {		
			return ((RemoteUserProxy)arg0[0]).toString();
		}
		
	}
	
}
