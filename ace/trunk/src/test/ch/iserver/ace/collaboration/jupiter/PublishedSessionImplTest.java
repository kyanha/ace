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
import ch.iserver.ace.collaboration.RemoteUserStub;
import ch.iserver.ace.collaboration.jupiter.server.PublisherPort;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.RemoteUserProxyStub;
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
		MockControl portCtrl = MockControl.createControl(PublisherPort.class);
		PublisherPort port = (PublisherPort) portCtrl.getMock();
		
		DocumentDetails details = new DocumentDetails("collab.txt");
		
		// define mock behavior
		port.setDocumentDetails(details);
		callback.getLock();
		callbackCtrl.setDefaultReturnValue(null);
		
		// replay
		portCtrl.replay();
		callbackCtrl.replay();
		
		// test
		PublishedSessionImpl impl = new PublishedSessionImpl(callback);
		impl.setPublisherPort(port);
		impl.setDocumentDetails(details);
		
		// verify
		portCtrl.verify();
		callbackCtrl.verify();
	}
	
	public void testKick() throws Exception {
		MockControl portCtrl = MockControl.createControl(PublisherPort.class);
		PublisherPort port = (PublisherPort) portCtrl.getMock();
		
		Participant participant = new ParticipantImpl(1, new RemoteUserStub("1"));
		
		// define mock behavior
		callback.getLock();
		callbackCtrl.setDefaultReturnValue(null);
		port.kick(1);
		
		// replay
		callbackCtrl.replay();
		portCtrl.replay();
		
		// test
		PublishedSessionImpl impl = new PublishedSessionImpl(callback);
		impl.setPublisherPort(port);
		impl.kick(participant);
		
		// verify
		callbackCtrl.verify();
		portCtrl.verify();
	}
	
	public void testLeave() throws Exception {
		MockControl portCtrl = MockControl.createControl(PublisherPort.class);
		PublisherPort port = (PublisherPort) portCtrl.getMock();
		
		// define mock behavior
		callback.getLock();
		callbackCtrl.setDefaultReturnValue(null);
		port.leave();
		
		// replay
		callbackCtrl.replay();
		portCtrl.replay();
		
		// test
		PublishedSessionImpl impl = new PublishedSessionImpl(callback);
		impl.setPublisherPort(port);
		impl.leave();
		
		// verify
		callbackCtrl.verify();
		portCtrl.verify();
	}
		
	public void testSendOperation() throws Exception {
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl portCtrl = MockControl.createControl(PublisherPort.class);
		PublisherPort port = (PublisherPort) portCtrl.getMock();
		MockControl acknowledgeCtrl = MockControl.createControl(AcknowledgeStrategy.class);
		AcknowledgeStrategy acknowledge = (AcknowledgeStrategy) acknowledgeCtrl.getMock();
		
		Operation operation = new InsertOperation(0, "x");
		Request request = new RequestImpl(0, null, operation);
		
		// define mock behavior
		callback.getLock();
		callbackCtrl.setDefaultReturnValue(null);
		algorithm.generateRequest(operation);
		algorithmCtrl.setReturnValue(request);
		port.receiveRequest(request);
		acknowledge.init(null);
		acknowledgeCtrl.setMatcher(MockControl.ALWAYS_MATCHER);
		acknowledge.reset();
		
		// replay
		algorithmCtrl.replay();
		callbackCtrl.replay();
		portCtrl.replay();
		acknowledgeCtrl.replay();
		
		// test
		PublishedSessionImpl impl = new PublishedSessionImpl(callback, algorithm);
		impl.setPublisherPort(port);
		impl.setAcknowledgeStrategy(acknowledge);
		impl.sendOperation(operation);
		
		// verify
		algorithmCtrl.verify();
		portCtrl.verify();
		callbackCtrl.verify();
		acknowledgeCtrl.verify();
	}
	
	public void testSendCaretUpdate() throws Exception {
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl portCtrl = MockControl.createControl(PublisherPort.class);
		PublisherPort port = (PublisherPort) portCtrl.getMock();
		MockControl acknowledgeCtrl = MockControl.createControl(AcknowledgeStrategy.class);
		AcknowledgeStrategy acknowledge = (AcknowledgeStrategy) acknowledgeCtrl.getMock();
		
		CaretUpdate update = new CaretUpdate(1, 2);
		CaretUpdateMessage message = new CaretUpdateMessage(0, null, update);
		
		// define mock behavior
		algorithm.generateCaretUpdateMessage(update);
		algorithmCtrl.setReturnValue(message);
		port.receiveCaretUpdate(message);
		callback.getLock();
		callbackCtrl.setDefaultReturnValue(null);
		acknowledge.init(null);
		acknowledgeCtrl.setMatcher(MockControl.ALWAYS_MATCHER);
		acknowledge.reset();
		
		// replay
		algorithmCtrl.replay();
		callbackCtrl.replay();
		portCtrl.replay();
		acknowledgeCtrl.replay();
		
		// test
		PublishedSessionImpl impl = new PublishedSessionImpl(callback, algorithm);
		impl.setPublisherPort(port);
		impl.setAcknowledgeStrategy(acknowledge);
		impl.sendCaretUpdate(update);
		
		// verify
		algorithmCtrl.verify();
		portCtrl.verify();
		callbackCtrl.verify();
		acknowledgeCtrl.verify();
	}
	
	public void testSendJoinedLeft() throws Exception {
		MockControl registryCtrl = MockControl.createControl(UserRegistry.class);
		UserRegistry registry = (UserRegistry) registryCtrl.getMock();
		
		RemoteUserProxy proxy1 = new RemoteUserProxyStub("X");
		RemoteUserProxy proxy2 = new RemoteUserProxyStub("Y");
		Participant participant1 = new ParticipantImpl(1, new RemoteUserStub("X"));
		Participant participant2 = new ParticipantImpl(2, new RemoteUserStub("Y"));

		// define mock behavior
		registry.getUser(new RemoteUserProxyStub("X"));
		registryCtrl.setReturnValue(new RemoteUserStub("X"));
		registry.getUser(new RemoteUserProxyStub("Y"));
		registryCtrl.setReturnValue(new RemoteUserStub("Y"));
		callback.getLock();
		callbackCtrl.setDefaultReturnValue(null);
		callback.participantJoined(participant1);
		callback.participantJoined(participant2);
		callback.participantLeft(participant1, Participant.LEFT);
		callback.participantLeft(participant2, Participant.KICKED);
		
		// replay
		registryCtrl.replay();
		callbackCtrl.replay();
		
		// test
		PublishedSessionImpl impl = new PublishedSessionImpl(callback);
		impl.setUserRegistry(registry);
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
