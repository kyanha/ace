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

package ch.iserver.ace.collaboration.jupiter;

import org.easymock.MockControl;

import ch.iserver.ace.collaboration.DiscoveryCallback;
import ch.iserver.ace.collaboration.DiscoveryResult;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.collaboration.RemoteUserStub;
import ch.iserver.ace.net.DiscoveryNetworkCallback;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.RemoteUserProxyStub;
import junit.framework.TestCase;

/**
 *
 */
public class DiscoveryNetworkCallbackImplTest extends TestCase {
	
	public void testUserDiscoveryFailed() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(DiscoveryCallback.class);
		DiscoveryCallback callback = (DiscoveryCallback) callbackCtrl.getMock();
		
		// define mock behavior
		callback.discovered(new DiscoveryResult(1, "failed"));
		
		// replay
		callbackCtrl.replay();
		
		// test
		DiscoveryNetworkCallback impl = new DiscoveryNetworkCallbackImpl(callback);
		impl.userDiscoveryFailed(1, "failed");
		
		// verify
		callbackCtrl.verify();
	}
	
	public void testUserDiscovered() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(DiscoveryCallback.class);
		DiscoveryCallback callback = (DiscoveryCallback) callbackCtrl.getMock();
				
		// define mock behavior
		callback.discovered(new DiscoveryResult(DiscoveryResult.SUCCESS));
		
		// replay
		callbackCtrl.replay();
		
		// test
		DiscoveryNetworkCallback impl = new DiscoveryNetworkCallbackImpl(callback);
		impl.userDiscoverySucceeded();
		
		// verify
		callbackCtrl.verify();
	}
	
}
