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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.collaboration.JoinRequest;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.RemoteUserStub;
import ch.iserver.ace.collaboration.jupiter.JoinRequestImpl;
import ch.iserver.ace.collaboration.jupiter.PublisherConnection;
import ch.iserver.ace.collaboration.jupiter.UserRegistry;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.RemoteUserProxyStub;
import ch.iserver.ace.util.CallerThreadDomain;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.SemaphoreLock;

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
		connection.getUser();
		connectionCtrl.setDefaultReturnValue(null);
		connection.sendJoinRequest(request);
		
		// replay
		connectionCtrl.replay();
		participantCtrl.replay();
		registryCtrl.replay();

		// test
		ServerLogicImpl logic = new ServerLogicImpl(new CallerThreadDomain(), new CallerThreadDomain(), document, registry);
		logic.initPublisherConnection(connection);
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
		registry.getUser("0");
		registryCtrl.setReturnValue(new RemoteUserStub("0"));
		participant.getUser();
		participantCtrl.setReturnValue(new RemoteUserProxyStub("0"));
		connection.getUser();
		connectionCtrl.setDefaultReturnValue(null);
		connection.sendJoinRequest(null);
		connectionCtrl.setMatcher(MockControl.ALWAYS_MATCHER);
				
		// replay
		connectionCtrl.replay();
		participantCtrl.replay();
		registryCtrl.replay();

		// test
		ServerLogicImpl logic = new ServerLogicImpl(new CallerThreadDomain(), new CallerThreadDomain(), document, registry);
		logic.initPublisherConnection(connection);
		logic.start();
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
		participantCtrl.setDefaultReturnValue(new RemoteUserProxyStub("X"));
		participant.joinRejected(JoinRequest.BLACKLISTED);
		participant.sendKicked();
		participant.close();
		connection.getUser();
		connectionCtrl.setDefaultReturnValue(null);
		connection.sendParticipantLeft(1, Participant.KICKED);
		registry.getUser("X");
		registryCtrl.setDefaultReturnValue(new RemoteUserStub("X"));
				
		// replay
		connectionCtrl.replay();
		participantCtrl.replay();
		registryCtrl.replay();

		// test
		ServerLogicImpl logic = new ServerLogicImpl(new CallerThreadDomain(), new CallerThreadDomain(), document, registry);
		logic.initPublisherConnection(connection);
		logic.getParticipantManager().addParticipant(1, null, participant);
		logic.kick(1);
		logic.start();		
		logic.join(participant);
		
		// verify
		connectionCtrl.verify();
		participantCtrl.verify();
		registryCtrl.verify();
	}
	
	public void testJoinInProgress() throws Exception {
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
		participant.joinRejected(JoinRequest.IN_PROGRESS);
		connection.getUser();
		connectionCtrl.setDefaultReturnValue(null);
		registry.getUser("X");
		registryCtrl.setDefaultReturnValue(new RemoteUserStub("X"));
				
		// replay
		connectionCtrl.replay();
		participantCtrl.replay();
		registryCtrl.replay();

		// test
		ServerLogicImpl logic = new ServerLogicImpl(new CallerThreadDomain(), new CallerThreadDomain(), document, registry);
		logic.initPublisherConnection(connection);
		logic.getParticipantManager().joinRequested("X");
		logic.start();
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
		connection.getUser();
		connectionCtrl.setDefaultReturnValue(null);
				
		// replay
		connectionCtrl.replay();
		participantCtrl.replay();
		registryCtrl.replay();

		// test
		ServerLogicImpl logic = new ServerLogicImpl(new CallerThreadDomain(), new CallerThreadDomain(), document, registry);
		logic.initPublisherConnection(connection);
		logic.start();
		logic.join(participant);
		
		// verify
		connectionCtrl.verify();
		participantCtrl.verify();
		registryCtrl.verify();
	}
	
	/*
	 * Tests that a join accepted message works as expected. Test checks that
	 * a JoinCommand is added to the serializer queue.
	 */
	public void testJoinAccepted() throws Exception {
		MockControl connectionCtrl = MockControl.createStrictControl(PublisherConnection.class);
		PublisherConnection connection = (PublisherConnection) connectionCtrl.getMock();
		
		DocumentModel document = new DocumentModel("", 0, 0, new DocumentDetails("XYZ"));
		
		MockControl registryCtrl = MockControl.createControl(UserRegistry.class);
		UserRegistry registry = (UserRegistry) registryCtrl.getMock();
		
		MockControl participantCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection participant = (ParticipantConnection) participantCtrl.getMock();
						
		// define mock behavior
		connection.getUser();
		connectionCtrl.setDefaultReturnValue(null);
		connection.sendParticipantJoined(1, new RemoteUserProxyStub("X"));
		participant.setParticipantId(1);
		participant.joinAccepted(null);
		participantCtrl.setMatcher(MockControl.ALWAYS_MATCHER);
		participant.sendDocument(null);
		participantCtrl.setMatcher(MockControl.ALWAYS_MATCHER);
		participant.getUser();
		participantCtrl.setDefaultReturnValue(new RemoteUserProxyStub("X"));
		
		// replay
		connectionCtrl.replay();
		participantCtrl.replay();
		registryCtrl.replay();

		// lock serializer
		Lock lock = new SemaphoreLock("serializer-lock");
		lock.lock();
		
		// test
		ServerLogicImpl logic = new ServerLogicImpl(new CallerThreadDomain(), new CallerThreadDomain(), document, registry);
		logic.initPublisherConnection(connection);
		logic.start();
		logic.joinAccepted(participant);
				
		// verify
		connectionCtrl.verify();
		participantCtrl.verify();
		registryCtrl.verify();
	}
	
	/*
	 * Tests that a join accepted message is silently ignored when the server is not
	 * accepting joins (i.e. is shuting down).
	 */
	public void testJoinAcceptedNoMoreJoines() throws Exception {
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
		connection.getUser();
		connectionCtrl.setDefaultReturnValue(null);
		
		// replay
		connectionCtrl.replay();
		participantCtrl.replay();
		registryCtrl.replay();

		// test
		ServerLogicImpl logic = new ServerLogicImpl(new CallerThreadDomain(), new CallerThreadDomain(), document, registry);
		logic.initPublisherConnection(connection);
		logic.joinAccepted(participant);
		
		// verify
		connectionCtrl.verify();
		participantCtrl.verify();
		registryCtrl.verify();
	}

	/*
	 * Tests that a join rejected messages is forwarded to the participant
	 * connection if joins are accepted.
	 */
	public void testJoinRejected() throws Exception {
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
		participant.joinRejected(JoinRequest.REJECTED);
		connection.getUser();
		connectionCtrl.setDefaultReturnValue(null);
		
		// replay
		connectionCtrl.replay();
		participantCtrl.replay();
		registryCtrl.replay();

		// test
		ServerLogicImpl logic = new ServerLogicImpl(new CallerThreadDomain(), new CallerThreadDomain(), document, registry);
		logic.initPublisherConnection(connection);
		logic.start();
		logic.joinRejected(participant);
		
		// verify
		connectionCtrl.verify();
		participantCtrl.verify();
		registryCtrl.verify();
	}
	
	/*
	 * Tests that a join rejected message is silently ignored when the server is not
	 * accepting joins (i.e. is shuting down).
	 */
	public void testJoinRejectedNoMoreJoines() throws Exception {
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
		connection.getUser();
		connectionCtrl.setDefaultReturnValue(null);
		
		// replay
		connectionCtrl.replay();
		participantCtrl.replay();
		registryCtrl.replay();

		// test
		ServerLogicImpl logic = new ServerLogicImpl(new CallerThreadDomain(), new CallerThreadDomain(), document, registry);
		logic.initPublisherConnection(connection);
		logic.joinRejected(participant);
		
		// verify
		connectionCtrl.verify();
		participantCtrl.verify();
		registryCtrl.verify();
	}
	
	/*
	 * Tests that a kicked user is added to the black list. 
	 */
	public void testKick() throws Exception {
		MockControl connectionCtrl = MockControl.createStrictControl(PublisherConnection.class);
		PublisherConnection connection = (PublisherConnection) connectionCtrl.getMock();
		
		DocumentModel document = new DocumentModel("", 0, 0, new DocumentDetails("XYZ"));
		
		MockControl registryCtrl = MockControl.createControl(UserRegistry.class);
		UserRegistry registry = (UserRegistry) registryCtrl.getMock();
		
		MockControl participantCtrl = MockControl.createControl(ParticipantConnection.class);
		final ParticipantConnection participant = (ParticipantConnection) participantCtrl.getMock();
		
		// define mock behavior
		participant.getUser();
		participantCtrl.setDefaultReturnValue(new RemoteUserProxyStub("X"));
		participant.sendKicked();
		participant.close();
		connection.getUser();
		connectionCtrl.setDefaultReturnValue(null);
		connection.sendParticipantLeft(1, Participant.KICKED);
				
		// replay
		connectionCtrl.replay();
		participantCtrl.replay();
		registryCtrl.replay();

		// test
		final List removed = new ArrayList();
		ServerLogicImpl logic = new ServerLogicImpl(new CallerThreadDomain(), new CallerThreadDomain(), document, registry) {
			protected synchronized void removeParticipant(int participantId) {
				super.removeParticipant(participantId);
				removed.add(new Integer(participantId));
			}
		};
		logic.initPublisherConnection(connection);
		logic.getParticipantManager().addParticipant(1, null, participant);
		logic.start();
		logic.kick(1);
		
		// assertions
		assertEquals(1, removed.size());
		assertEquals(new Integer(1), removed.get(0));
		
		// TODO: fix test
		//assertEquals("X", logic.getBlacklist().iterator().next());
				
		// verify
		connectionCtrl.verify();
		participantCtrl.verify();
		registryCtrl.verify();
	}

	/*
	 * Tests the leave method. 
	 */
	public void testLeave() throws Exception {
		MockControl connectionCtrl = MockControl.createStrictControl(PublisherConnection.class);
		PublisherConnection connection = (PublisherConnection) connectionCtrl.getMock();
		
		DocumentModel document = new DocumentModel("", 0, 0, new DocumentDetails("XYZ"));
		
		MockControl registryCtrl = MockControl.createControl(UserRegistry.class);
		UserRegistry registry = (UserRegistry) registryCtrl.getMock();
		
		MockControl participantCtrl = MockControl.createControl(ParticipantConnection.class);
		final ParticipantConnection participant = (ParticipantConnection) participantCtrl.getMock();
		
		// define mock behavior
		connection.getUser();
		connectionCtrl.setDefaultReturnValue(null);
		connection.sendParticipantLeft(1, Participant.LEFT);
		participant.getUser();
		participantCtrl.setDefaultReturnValue(new RemoteUserProxyStub("X"));
		participant.close();
				
		// replay
		connectionCtrl.replay();
		participantCtrl.replay();
		registryCtrl.replay();

		// test
		final List removed = new ArrayList();
		ServerLogicImpl logic = new ServerLogicImpl(new CallerThreadDomain(), new CallerThreadDomain(), document, registry) {
			protected synchronized void removeParticipant(int participantId) {
				super.removeParticipant(participantId);
				removed.add(new Integer(participantId));
			}
		};
		logic.initPublisherConnection(connection);
		logic.getParticipantManager().addParticipant(1, null, participant);
		logic.start();
		logic.leave(1);
		
		// assertions
		assertEquals(1, removed.size());
		assertEquals(new Integer(1), removed.get(0));
				
		// verify
		connectionCtrl.verify();
		participantCtrl.verify();
		registryCtrl.verify();
	}

}
