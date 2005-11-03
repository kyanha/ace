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

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.net.RemoteUserProxy;

public class RemoteUserTest extends TestCase {

	public void testEquals() throws Exception {
		MockControl proxy1Ctrl = MockControl.createControl(RemoteUserProxy.class);
		RemoteUserProxy proxy1 = (RemoteUserProxy) proxy1Ctrl.getMock();
		MockControl proxy2Ctrl = MockControl.createControl(RemoteUserProxy.class);
		RemoteUserProxy proxy2 = (RemoteUserProxy) proxy2Ctrl.getMock();
		MockControl proxy3Ctrl = MockControl.createControl(RemoteUserProxy.class);
		RemoteUserProxy proxy3 = (RemoteUserProxy) proxy3Ctrl.getMock();

		RemoteUserImpl user1 = new RemoteUserImpl(proxy1);
		RemoteUserImpl user2 = new RemoteUserImpl(proxy2);
		RemoteUserImpl user3 = new RemoteUserImpl(proxy2);
		
		// define mock behavior
		proxy1.getId();
		proxy1Ctrl.setDefaultReturnValue("ABCDEFG");
		proxy2.getId();
		proxy2Ctrl.setDefaultReturnValue("ABCDE");
		proxy3.getId();
		proxy3Ctrl.setDefaultReturnValue("ABCDE");
		
		// replay
		proxy1Ctrl.replay();
		proxy2Ctrl.replay();
		proxy3Ctrl.replay();
		
		// test
		assertFalse(user1.equals(user2));
		assertFalse(user3.equals(user1));
		assertTrue(user2.equals(user3));
		assertTrue(user3.equals(user2));
		assertFalse(user1.equals(user3));
		assertFalse(user3.equals(user1));
		
		// verify
		proxy1Ctrl.verify();
		proxy2Ctrl.verify();
		proxy3Ctrl.verify();
	}

}
