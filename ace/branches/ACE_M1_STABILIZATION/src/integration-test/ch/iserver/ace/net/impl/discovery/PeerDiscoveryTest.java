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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.easymock.ArgumentsMatcher;
import org.easymock.MockControl;

import ch.iserver.ace.ApplicationError;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.impl.Discovery;
import ch.iserver.ace.net.impl.DiscoveryCallback;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.RemoteUserProxyImpl;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.ResolveListener;

public class PeerDiscoveryTest extends TestCase {

	Properties props;
	private PeerDiscovery peerDiscovery;
	private UserRegistration registration;
	
	public void testPeerDiscoveryTest() {
		props = loadConfig();
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
		TestuserRegistrar registrar = new TestuserRegistrar(users, services, ports, props);
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
		RemoteUserProxyExt rem1 = new RemoteUserProxyImpl("peer1"+ports[0], new UserDetails("peer1", null, ports[0]));
		callback.userDiscovered(rem1);
		//note: InetAddress will not be compared in comparison, c.f. RemoteUserProxyMatcher
		callback.userDetailsChanged(rem1);
		RemoteUserProxyExt rem2 = new RemoteUserProxyImpl("peer2"+ports[1], new UserDetails("peer2", null, ports[1]));
		callback.userDiscovered(rem2);
		callback.userDetailsChanged(rem2);
		RemoteUserProxyExt rem3 = new RemoteUserProxyImpl("peer3"+ports[2], new UserDetails("peer3", null, ports[2]));
		callback.userDiscovered(rem3);
		callback.userDetailsChanged(rem3);
		RemoteUserProxyExt rem4 = new RemoteUserProxyImpl("peer4"+ports[3], new UserDetails("peer4", null, ports[3]));
		callback.userDiscovered(rem4);
		callback.userDetailsChanged(rem4);
		
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
		
		System.out.println("stop registered peers.");
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
		
		Bonjour b = new Bonjour(registration, peerDiscovery, props);
		Bonjour.setLocalServiceName(System.getProperty("user.name"));
		return b;
	}
	
	
	private PeerDiscovery createPeerDiscovery(DiscoveryCallback callback) {
		DiscoveryCallbackAdapter adapter = new DiscoveryCallbackAdapter(callback);
		AbstractQueryListener ipListener = new IPQueryListener(adapter);
		AbstractQueryListener txtListener = new TXTQueryListener(adapter);
		ResolveListener resolveListener = new ResolveListenerImpl(adapter, ipListener, txtListener);
		BrowseListener browseListener = new BrowseListenerImpl(adapter, resolveListener);
		PeerDiscovery discovery = new PeerDiscoveryImpl(browseListener);
		return discovery;
	}
	
	protected Properties loadConfig() {
	    Properties properties = new Properties();
	    try {
	        properties.load(getClass().getResourceAsStream("zeroconf.properties"));
	    } catch (IOException e) {
	    		throw new ApplicationError(e);
	    }
		return properties;
	}
	
class RemoteUserProxyMatcher implements ArgumentsMatcher {

		//InetAddresss of UserDetails is ignored because of portability for this integration test
		public boolean matches(Object[] arg0, Object[] arg1) {
			RemoteUserProxy proxy0 = (RemoteUserProxy)arg0[0];
			RemoteUserProxy proxy1 = (RemoteUserProxy)arg1[0];
			boolean result = proxy0.getId().equals(proxy1.getId()) &&
				proxy0.getUserDetails().getUsername().equals(proxy1.getUserDetails().getUsername()) &&
				proxy0.getUserDetails().getPort() == proxy1.getUserDetails().getPort();
			return result;
		}

		public String toString(Object[] arg0) {		
			return ((RemoteUserProxy)arg0[0]).toString();
		}
		
	}
	
}
