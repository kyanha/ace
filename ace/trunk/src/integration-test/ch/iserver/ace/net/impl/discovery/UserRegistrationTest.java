/*
 * $Id:UserRegistrationTest.java 1205 2005-11-14 07:57:10Z zbinl $
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

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.easymock.MockControl;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.core.Discovery;
import ch.iserver.ace.net.core.DiscoveryCallback;
import ch.iserver.ace.net.core.DiscoveryCallbackImpl;
import ch.iserver.ace.net.core.NetworkProperties;
import ch.iserver.ace.net.discovery.Bonjour;
import ch.iserver.ace.net.discovery.PeerDiscovery;
import ch.iserver.ace.net.discovery.TXTRecordProxy;
import ch.iserver.ace.net.discovery.UserRegistration;
import ch.iserver.ace.net.discovery.UserRegistrationImpl;
import ch.iserver.ace.net.protocol.LogFilter;
import ch.iserver.ace.net.protocol.RequestFilter;

import com.apple.dnssd.TXTRecord;

public class UserRegistrationTest extends TestCase {
	
	private MockControl peerDiscoveryCtrl;
	private UserRegistration registration;
	private String username, userid;
	
	//TODO: this test does not deterministically work due to mDNSResponder caching
	public void testUserRegistrationAndUserDetailsUpdate() throws Exception {
		final String USER = "testuser";
		final String USER_ID = "test-id_1";
		username = USER;
		userid = USER_ID;
		MockControl networkServiceCallbackCtrl = MockControl.createControl(NetworkServiceCallback.class);
		NetworkServiceCallback nsc = (NetworkServiceCallback)networkServiceCallbackCtrl.getMock();
		
		RequestFilter filter = new LogFilter(null ,false);
		DiscoveryCallback callback = new DiscoveryCallbackImpl(nsc, null, filter); //DocumentDiscovery not of concern -> null
		Discovery discovery = createDiscovery(callback);
		discovery.setUserDetails(new UserDetails(USER));
		discovery.setUserId(USER_ID);
		
		discovery.execute();
		
		peerDiscoveryCtrl.verify();

		//System.out.println("before sleep");
		Thread.sleep(3000);
		//System.out.println("after sleep");
		
		assertTrue(registration.isRegistered());
		TXTRecord rec = ((UserRegistrationImpl)registration).getTXTRecord();
		assertEquals(NetworkProperties.get(NetworkProperties.KEY_TXT_VERSION), TXTRecordProxy.get(TXTRecordProxy.TXT_VERSION, rec));
//		assertEquals(props.get(Bonjour.KEY_USER), TXTRecordProxy.get(TXTRecordProxy.TXT_USER, rec));
//		assertEquals(props.get(Bonjour.KEY_USERID), TXTRecordProxy.get(TXTRecordProxy.TXT_USERID, rec));
		assertEquals(NetworkProperties.get(NetworkProperties.KEY_PROTOCOL_VERSION), TXTRecordProxy.get(TXTRecordProxy.TXT_PROTOCOL_VERSION, rec));
		
		//TODO: updateUserDetails does not work on zbinl's Mac due to mDNSResponder failure
//		final String NEW_USER = "new-testuser";
//		registration.updateUserDetails(new UserDetails(NEW_USER));
//		
//		try {
//			Thread.sleep(5000);
//		} catch (Exception e) {}
//		
//		assertTrue(registration.isRegistered());
//		rec = ((UserRegistrationImpl)registration).getTXTRecord();
//		assertEquals(props.get(Bonjour.KEY_TXT_VERSION), TXTRecordProxy.get(TXTRecordProxy.TXT_VERSION, rec));
//		assertEquals(NEW_USER, TXTRecordProxy.get(TXTRecordProxy.TXT_USER, rec));
//		assertEquals(props.get(Bonjour.KEY_USERID), TXTRecordProxy.get(TXTRecordProxy.TXT_USERID, rec));
//		assertEquals(props.get(Bonjour.KEY_PROTOCOL_VERSION), TXTRecordProxy.get(TXTRecordProxy.TXT_PROTOCOL_VERSION, rec));
		
		registration.stop();
		assertFalse(registration.isRegistered());
	}
	
	public Discovery createDiscovery(DiscoveryCallback callback) {
		registration = new UserRegistrationImpl();
		
		peerDiscoveryCtrl = MockControl.createControl(PeerDiscovery.class);
		PeerDiscovery discovery = (PeerDiscovery)peerDiscoveryCtrl.getMock();
		discovery.browse();
		peerDiscoveryCtrl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
		peerDiscoveryCtrl.replay();
		Bonjour b = new Bonjour(registration, discovery);
		return b;
	}
}
