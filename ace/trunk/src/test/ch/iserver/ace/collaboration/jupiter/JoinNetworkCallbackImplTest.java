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

import ch.iserver.ace.collaboration.JoinCallback;
import ch.iserver.ace.collaboration.ParticipantSessionCallback;
import ch.iserver.ace.net.JoinNetworkCallback;
import ch.iserver.ace.net.SessionConnection;

/**
 *
 */
public class JoinNetworkCallbackImplTest extends TestCase {
	
	public void testRejected() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(JoinCallback.class);
		JoinCallback callback = (JoinCallback) callbackCtrl.getMock();
		MockControl factoryCtrl = MockControl.createControl(SessionFactory.class);
		SessionFactory factory = (SessionFactory) factoryCtrl.getMock();
		
		// define mock behavior
		callback.rejected(1);
		
		// replay
		callbackCtrl.replay();
		factoryCtrl.replay();
		
		// test
		JoinNetworkCallback impl = new JoinNetworkCallbackImpl(callback, factory);
		impl.rejected(1);
		
		// verify
		callbackCtrl.verify();
		factoryCtrl.verify();
	}
	
	public void testAccepted() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(JoinCallback.class);
		JoinCallback callback = (JoinCallback) callbackCtrl.getMock();
		MockControl factoryCtrl = MockControl.createControl(SessionFactory.class);
		SessionFactory factory = (SessionFactory) factoryCtrl.getMock();
		MockControl sessionCtrl = MockControl.createControl(ConfigurableSession.class);
		ConfigurableSession session = (ConfigurableSession) sessionCtrl.getMock();
		MockControl connectionCtrl = MockControl.createControl(SessionConnection.class);
		SessionConnection connection = (SessionConnection) connectionCtrl.getMock();
		MockControl sessionCallbackCtrl = MockControl.createControl(ParticipantSessionCallback.class);
		ParticipantSessionCallback sessionCallback = (ParticipantSessionCallback) sessionCallbackCtrl.getMock();
		
		// define mock behavior
		factory.createSession();
		factoryCtrl.setReturnValue(session);
		session.setConnection(connection);
		callback.accepted(session);
		callbackCtrl.setReturnValue(sessionCallback);
		session.setSessionCallback(sessionCallback);
		
		// replay
		callbackCtrl.replay();
		factoryCtrl.replay();
		sessionCtrl.replay();
		
		// test
		JoinNetworkCallback impl = new JoinNetworkCallbackImpl(callback, factory);
		assertSame(session, impl.accepted(connection));
		
		// verify
		callbackCtrl.verify();
		factoryCtrl.verify();
		sessionCtrl.verify();
	}
	
}
