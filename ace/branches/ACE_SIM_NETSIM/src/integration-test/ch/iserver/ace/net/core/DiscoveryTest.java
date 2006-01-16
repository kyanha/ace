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

package ch.iserver.ace.net.core;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.discovery.PeerDiscovery;
import ch.iserver.ace.net.discovery.UserRegistration;
import ch.iserver.ace.util.SingleThreadDomain;
import ch.iserver.ace.util.UUID;

/**
 *
 */
public class DiscoveryTest extends TestCase {

	
	public void testSetUserIdAndSetUserDetails() {
		MockControl callbackCtrl = MockControl.createControl(DiscoveryCallback.class);
		DiscoveryCallback callback = (DiscoveryCallback) callbackCtrl.getMock();
		callbackCtrl.replay();
		MockControl registrationCtrl = MockControl.createControl(UserRegistration.class);
		UserRegistration registration = (UserRegistration) registrationCtrl.getMock();
		MockControl discoveryCtrl = MockControl.createControl(PeerDiscovery.class);
		PeerDiscovery peerDiscovery = (PeerDiscovery) discoveryCtrl.getMock();
		DiscoveryFactory.getInstance().init(registration, peerDiscovery);
		Discovery discovery = DiscoveryFactory.getInstance().createDiscovery(callback, new SingleThreadDomain());
		String username = "asšdlfjašsdfjšlasdjflškjsadf";
		UserDetails details = new UserDetails(username);
		String uuid = UUID.nextUUID();
		
		registration.isRegistered();
		registrationCtrl.setDefaultReturnValue(false);
		registration.register(username, uuid);
		registrationCtrl.setDefaultMatcher(MockControl.EQUALS_MATCHER);
		registrationCtrl.replay();
		
		peerDiscovery.browse();
		discoveryCtrl.replay();
		
		discovery.setUserDetails(details);
		discovery.setUserId(uuid);
		discovery.execute();
		
		discoveryCtrl.verify();
		discoveryCtrl.verify();
	}
	
}
