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

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.collaboration.JoinRequest;
import ch.iserver.ace.collaboration.RemoteUserStub;
import ch.iserver.ace.collaboration.jupiter.JoinRequestImpl;
import ch.iserver.ace.collaboration.jupiter.PublisherConnection;
import ch.iserver.ace.collaboration.jupiter.UserRegistry;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.RemoteUserProxyStub;
import ch.iserver.ace.util.CallerThreadDomain;

/**
 *
 */
public class ServerLogicImplTest extends TestCase {
	
	public void testJoin() throws Exception {
		MockControl connectionCtrl = MockControl.createStrictControl(PublisherConnection.class);
		PublisherConnection connection = (PublisherConnection) connectionCtrl.getMock();
		
		DocumentModel document = new DocumentModel("", 0, 0, new DocumentDetails("XYZ"));
		
		MockControl registryCtrl = MockControl.createControl(UserRegistry.class);
		UserRegistry registry = (UserRegistry) registryCtrl.getMock();
		
		MockControl participantCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection participant = (ParticipantConnection) participantCtrl.getMock();
		
		RemoteUserProxy user = new RemoteUserProxyStub("X");
		
		JoinRequest request = new JoinRequestImpl(null, new RemoteUserStub("X"), participant);
		
		// define mock behavior
		registry.getUser(user.getId());
		registryCtrl.setReturnValue(new RemoteUserStub("X"));

		participant.getUser();
		participantCtrl.setReturnValue(user);
		
		connection.sendJoinRequest(request);
		
		// replay
		connectionCtrl.replay();
		participantCtrl.replay();
		registryCtrl.replay();

		// test
		ServerLogicImpl logic = new ServerLogicImpl(new CallerThreadDomain(), connection, document, registry);
		logic.start();
		logic.join(participant);
		
		// verify
		connectionCtrl.verify();
		participantCtrl.verify();
		registryCtrl.verify();
	}
	
	public void testJoinShutdown() throws Exception {
		MockControl connectionCtrl = MockControl.createStrictControl(PublisherConnection.class);
		PublisherConnection connection = (PublisherConnection) connectionCtrl.getMock();
		
		DocumentModel document = new DocumentModel("", 0, 0, new DocumentDetails("XYZ"));
		
		MockControl registryCtrl = MockControl.createControl(UserRegistry.class);
		UserRegistry registry = (UserRegistry) registryCtrl.getMock();
		
		MockControl participantCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection participant = (ParticipantConnection) participantCtrl.getMock();
						
		// define mock behavior
		participant.joinRejected(JoinRequest.SHUTDOWN);
				
		// replay
		connectionCtrl.replay();
		participantCtrl.replay();
		registryCtrl.replay();

		// test
		ServerLogicImpl logic = new ServerLogicImpl(new CallerThreadDomain(), connection, document, registry);
		logic.start();
		logic.prepareShutdown();
		logic.getBlacklist().add("X");
		logic.join(participant);
		
		// verify
		connectionCtrl.verify();
		participantCtrl.verify();
		registryCtrl.verify();
	}
	
	public void testJoinBlacklisted() throws Exception {
		MockControl connectionCtrl = MockControl.createStrictControl(PublisherConnection.class);
		PublisherConnection connection = (PublisherConnection) connectionCtrl.getMock();
		
		DocumentModel document = new DocumentModel("", 0, 0, new DocumentDetails("XYZ"));
		
		MockControl registryCtrl = MockControl.createControl(UserRegistry.class);
		UserRegistry registry = (UserRegistry) registryCtrl.getMock();
		
		MockControl participantCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection participant = (ParticipantConnection) participantCtrl.getMock();
						
		// define mock behavior
		participant.getUser();
		participantCtrl.setReturnValue(new RemoteUserProxyStub("X"));
		participant.joinRejected(JoinRequest.BLACKLISTED);
				
		// replay
		connectionCtrl.replay();
		participantCtrl.replay();
		registryCtrl.replay();

		// test
		ServerLogicImpl logic = new ServerLogicImpl(new CallerThreadDomain(), connection, document, registry);
		logic.start();
		logic.getBlacklist().add("X");
		logic.join(participant);
		
		// verify
		connectionCtrl.verify();
		participantCtrl.verify();
		registryCtrl.verify();
	}
	
	public void testJoinUnknownUser() throws Exception {
		MockControl connectionCtrl = MockControl.createStrictControl(PublisherConnection.class);
		PublisherConnection connection = (PublisherConnection) connectionCtrl.getMock();
		
		DocumentModel document = new DocumentModel("", 0, 0, new DocumentDetails("XYZ"));
		
		MockControl registryCtrl = MockControl.createControl(UserRegistry.class);
		UserRegistry registry = (UserRegistry) registryCtrl.getMock();
		
		MockControl participantCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection participant = (ParticipantConnection) participantCtrl.getMock();
						
		// define mock behavior
		registry.getUser("X");
		registryCtrl.setReturnValue(null);

		participant.getUser();
		participantCtrl.setReturnValue(new RemoteUserProxyStub("X"));
		participant.joinRejected(JoinRequest.UNKNOWN_USER);
				
		// replay
		connectionCtrl.replay();
		participantCtrl.replay();
		registryCtrl.replay();

		// test
		ServerLogicImpl logic = new ServerLogicImpl(new CallerThreadDomain(), connection, document, registry);
		logic.start();
		logic.join(participant);
		
		// verify
		connectionCtrl.verify();
		participantCtrl.verify();
		registryCtrl.verify();
	}
	
}
