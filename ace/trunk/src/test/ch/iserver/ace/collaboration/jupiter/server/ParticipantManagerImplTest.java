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

package ch.iserver.ace.collaboration.jupiter.server;

import org.easymock.MockControl;

import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.RemoteUserProxyStub;

import junit.framework.TestCase;

public class ParticipantManagerImplTest extends TestCase {
	
	public void testJoinLeaveJoin() throws Exception {
		CompositeForwarder forwarder = new CompositeForwarderImpl();
		
		MockControl connectionCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection connection = (ParticipantConnection) connectionCtrl.getMock();
		
		// define mock behavior
		connection.getUser();
		connectionCtrl.setDefaultReturnValue(new RemoteUserProxyStub("X"));
		
		// replay
		connectionCtrl.replay();
		
		// test
		final String USER_ID = "X";
		
		ParticipantManager manager = new ParticipantManagerImpl(forwarder);
		manager.joinRequested(USER_ID);
		assertTrue(manager.isJoining(USER_ID));
		manager.addParticipant(1, null, connection);
		assertFalse(manager.isJoining(USER_ID));
		assertTrue(manager.isParticipant(USER_ID));
		
		manager.participantLeft(1);
		assertFalse(manager.isParticipant(USER_ID));
		
		manager.joinRequested(USER_ID);
		assertTrue(manager.isJoining(USER_ID));
		manager.addParticipant(1, null, connection);
		assertFalse(manager.isJoining(USER_ID));
		assertTrue(manager.isParticipant(USER_ID));
		
		// verify
		connectionCtrl.verify();
	}
	
	public void testKick() throws Exception {
		CompositeForwarder forwarder = new CompositeForwarderImpl();
		
		MockControl connectionCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection connection = (ParticipantConnection) connectionCtrl.getMock();
		
		// define mock behavior
		connection.getUser();
		connectionCtrl.setDefaultReturnValue(new RemoteUserProxyStub("X"));
		
		// replay
		connectionCtrl.replay();
		
		// test
		ParticipantManager manager = new ParticipantManagerImpl(forwarder);
		manager.addParticipant(1, null, connection);
		manager.participantKicked(1);
		assertTrue(manager.isBlackListed("X"));
		assertFalse(manager.isParticipant("X"));
		
		// verify
		connectionCtrl.verify();
	}
	
}
