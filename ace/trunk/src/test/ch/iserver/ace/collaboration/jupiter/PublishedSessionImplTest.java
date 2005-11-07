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

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.RequestImpl;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.PublishedSessionCallback;
import ch.iserver.ace.collaboration.jupiter.server.PublisherPort;
import ch.iserver.ace.collaboration.jupiter.server.ServerLogic;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.text.InsertOperation;

/**
 *
 */
public class PublishedSessionImplTest extends TestCase {

	private MockControl callbackCtrl;
	
	private PublishedSessionCallback callback;
	
	protected void setUp() throws Exception {
		callbackCtrl = MockControl.createControl(PublishedSessionCallback.class);
		callback = (PublishedSessionCallback) callbackCtrl.getMock();
	}
	
	public void testSetDocumentDetails() throws Exception {
		MockControl logicCtrl = MockControl.createControl(ServerLogic.class);
		ServerLogic logic = (ServerLogic) logicCtrl.getMock();
		
		PublishedSessionImpl impl = new PublishedSessionImpl(callback);
		DocumentDetails details = new DocumentDetails("collab.txt");
		
		// define mock behavior
		logic.getPublisherPort();
		logicCtrl.setReturnValue(null);
		logic.setDocumentDetails(details);
		
		// replay
		logicCtrl.replay();
		callbackCtrl.replay();
		
		// test
		impl.setServerLogic(logic);
		impl.setDocumentDetails(details);
		
		// verify
		logicCtrl.verify();
		callbackCtrl.verify();
	}
	
	public void testKick() throws Exception {
		MockControl logicCtrl = MockControl.createControl(ServerLogic.class);
		ServerLogic logic = (ServerLogic) logicCtrl.getMock();

		MockControl portCtrl = MockControl.createControl(PublisherPort.class);
		PublisherPort port = (PublisherPort) portCtrl.getMock();
		
		PublishedSessionImpl impl = new PublishedSessionImpl(callback);
		Participant participant = new ParticipantImpl(1, new RemoteUserStub("1"));
		
		// define mock behavior
		logic.getPublisherPort();
		logicCtrl.setReturnValue(port);
		port.kick(1);
		
		// replay
		logicCtrl.replay();
		callbackCtrl.replay();
		portCtrl.replay();
		
		// test
		impl.setServerLogic(logic);
		impl.kick(participant);
		
		// verify
		logicCtrl.verify();
		callbackCtrl.verify();
		portCtrl.verify();
	}
	
	public void testLeave() throws Exception {
		MockControl logicCtrl = MockControl.createControl(ServerLogic.class);
		ServerLogic logic = (ServerLogic) logicCtrl.getMock();

		MockControl portCtrl = MockControl.createControl(PublisherPort.class);
		PublisherPort port = (PublisherPort) portCtrl.getMock();

		PublishedSessionImpl impl = new PublishedSessionImpl(callback);
		
		// define mock behavior
		logic.getPublisherPort();
		logicCtrl.setReturnValue(port);
		port.leave();
		
		// replay
		logicCtrl.replay();
		callbackCtrl.replay();
		portCtrl.replay();
		
		// test
		impl.setServerLogic(logic);
		impl.leave();
		
		// verify
		logicCtrl.verify();
		callbackCtrl.verify();
		portCtrl.verify();
	}
		
	public void testSendOperation() throws Exception {
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl logicCtrl = MockControl.createControl(ServerLogic.class);
		ServerLogic logic = (ServerLogic) logicCtrl.getMock();
		MockControl portCtrl = MockControl.createControl(PublisherPort.class);
		PublisherPort port = (PublisherPort) portCtrl.getMock();
		
		PublishedSessionImpl impl = new PublishedSessionImpl(callback, algorithm);
		Operation operation = new InsertOperation(0, "x");
		Request request = new RequestImpl(0, null, operation);
		
		// define mock behavior
		logic.getPublisherPort();
		logicCtrl.setReturnValue(port);
		algorithm.generateRequest(operation);
		algorithmCtrl.setReturnValue(request);
		port.receiveRequest(request);
		
		// replay
		algorithmCtrl.replay();
		logicCtrl.replay();
		callbackCtrl.replay();
		portCtrl.replay();
		
		// test
		impl.setServerLogic(logic);
		impl.lock();
		impl.sendOperation(operation);
		impl.unlock();
		
		// verify
		algorithmCtrl.verify();
		logicCtrl.verify();
		portCtrl.verify();
		callbackCtrl.verify();
	}

	public void testSendOperationNoLocking() throws Exception {
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl logicCtrl = MockControl.createControl(ServerLogic.class);
		ServerLogic logic = (ServerLogic) logicCtrl.getMock();
		MockControl portCtrl = MockControl.createControl(PublisherPort.class);
		PublisherPort port = (PublisherPort) portCtrl.getMock();
		
		PublishedSessionImpl impl = new PublishedSessionImpl(callback, algorithm);
		Operation operation = new InsertOperation(0, "x");
		
		// define mock behavior
		logic.getPublisherPort();
		logicCtrl.setReturnValue(port);
		
		// replay
		algorithmCtrl.replay();
		logicCtrl.replay();
		callbackCtrl.replay();
		portCtrl.replay();
		
		// test
		impl.setServerLogic(logic);
		try {
			impl.sendOperation(operation);
			fail("sending operations without locking must fail");
		} catch (IllegalMonitorStateException e) {
			// expected
		}
		
		// verify
		algorithmCtrl.verify();
		logicCtrl.verify();
		portCtrl.verify();
		callbackCtrl.verify();
	}
	
	public void testSendCaretUpdate() throws Exception {
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl logicCtrl = MockControl.createControl(ServerLogic.class);
		ServerLogic logic = (ServerLogic) logicCtrl.getMock();
		MockControl portCtrl = MockControl.createControl(PublisherPort.class);
		PublisherPort port = (PublisherPort) portCtrl.getMock();
		
		PublishedSessionImpl impl = new PublishedSessionImpl(callback, algorithm);
		CaretUpdate update = new CaretUpdate(1, 2);
		CaretUpdateMessage message = new CaretUpdateMessage(0, null, update);
		
		// define mock behavior
		logic.getPublisherPort();
		logicCtrl.setReturnValue(port);
		algorithm.generateCaretUpdateMessage(update);
		algorithmCtrl.setReturnValue(message);
		port.receiveCaretUpdate(message);
		
		// replay
		algorithmCtrl.replay();
		logicCtrl.replay();
		callbackCtrl.replay();
		portCtrl.replay();
		
		// test
		impl.setServerLogic(logic);
		impl.lock();
		impl.sendCaretUpdate(update);
		impl.unlock();
		
		// verify
		algorithmCtrl.verify();
		logicCtrl.verify();
		portCtrl.verify();
		callbackCtrl.verify();
	}

	public void testSendCaretUpdateNoLocking() throws Exception {
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl logicCtrl = MockControl.createControl(ServerLogic.class);
		ServerLogic logic = (ServerLogic) logicCtrl.getMock();
		MockControl portCtrl = MockControl.createControl(PublisherPort.class);
		PublisherPort port = (PublisherPort) portCtrl.getMock();
		
		PublishedSessionImpl impl = new PublishedSessionImpl(callback, algorithm);
		CaretUpdate update = new CaretUpdate(0, 1);
		
		// define mock behavior
		logic.getPublisherPort();
		logicCtrl.setReturnValue(port);
		
		// replay
		algorithmCtrl.replay();
		logicCtrl.replay();
		callbackCtrl.replay();
		portCtrl.replay();
		
		// test
		impl.setServerLogic(logic);
		try {
			impl.sendCaretUpdate(update);
			fail("sending operations without locking must fail");
		} catch (IllegalMonitorStateException e) {
			// expected
		}
		
		// verify
		algorithmCtrl.verify();
		logicCtrl.verify();
		portCtrl.verify();
		callbackCtrl.verify();
	}
	
	public void testSendJoinedLeft() throws Exception {
		MockControl registryCtrl = MockControl.createControl(UserRegistry.class);
		UserRegistry registry = (UserRegistry) registryCtrl.getMock();
		
		MockControl logicCtrl = MockControl.createControl(ServerLogic.class);
		ServerLogic logic = (ServerLogic) logicCtrl.getMock();
		
		PublishedSessionImpl impl = new PublishedSessionImpl(callback);
		impl.setUserRegistry(registry);
		RemoteUserProxy proxy1 = new RemoteUserProxyStub("X");
		RemoteUserProxy proxy2 = new RemoteUserProxyStub("Y");
		Participant participant1 = new ParticipantImpl(1, new RemoteUserStub("X"));
		Participant participant2 = new ParticipantImpl(2, new RemoteUserStub("Y"));

		// define mock behavior
		registry.addUser(new RemoteUserProxyStub("X"));
		registryCtrl.setReturnValue(new RemoteUserStub("X"));
		registry.addUser(new RemoteUserProxyStub("Y"));
		registryCtrl.setReturnValue(new RemoteUserStub("Y"));
		logic.getPublisherPort();
		logicCtrl.setReturnValue(null);
		callback.participantJoined(participant1);
		callback.participantJoined(participant2);
		callback.participantLeft(participant1, Participant.LEFT);
		callback.participantLeft(participant2, Participant.KICKED);
		
		// replay
		registryCtrl.replay();
		logicCtrl.replay();
		callbackCtrl.replay();
		
		// test
		impl.setServerLogic(logic);
		assertEquals(0, impl.getParticipants().size());
		impl.sendParticipantJoined(1, proxy1);
		assertEquals(1, impl.getParticipants().size());
		assertEquals(participant1, impl.getParticipant(1));
		impl.sendParticipantJoined(2, proxy2);
		assertEquals(2, impl.getParticipants().size());
		assertEquals(participant2, impl.getParticipant(2));
		impl.sendParticipantLeft(1, Participant.LEFT);
		assertEquals(1, impl.getParticipants().size());
		assertNull(impl.getParticipant(1));
		impl.sendParticipantLeft(2, Participant.KICKED);
		assertEquals(0, impl.getParticipants().size());
		assertNull(impl.getParticipant(0));
		
		// verify
		registryCtrl.verify();
		logicCtrl.verify();
		callbackCtrl.verify();
	}
	
	public void testSendDocument() throws Exception {
		PublishedSessionImpl impl = new PublishedSessionImpl(callback);
		try {
			impl.sendDocument(null);
		} catch (UnsupportedOperationException e) {
			// expected
		}
	}
	
	public void testSendKicked() throws Exception {
		PublishedSessionImpl impl = new PublishedSessionImpl(callback);
		try {
			impl.sendKicked();
		} catch (UnsupportedOperationException e) {
			// expected
		}
	}

}
