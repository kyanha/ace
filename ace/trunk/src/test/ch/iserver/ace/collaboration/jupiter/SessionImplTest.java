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
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.RequestImpl;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.ParticipantSessionCallback;
import ch.iserver.ace.collaboration.RemoteUserStub;
import ch.iserver.ace.net.RemoteUserProxyStub;
import ch.iserver.ace.net.SessionConnection;
import ch.iserver.ace.text.InsertOperation;
import ch.iserver.ace.util.CallerThreadDomain;
import ch.iserver.ace.util.Lock;

/**
 *
 */
public class SessionImplTest extends TestCase {
	
	public void testLeave() throws Exception {
		MockControl connectionCtrl = MockControl.createControl(SessionConnection.class);
		SessionConnection connection = (SessionConnection) connectionCtrl.getMock();
		
		SessionImpl impl = new SessionImpl();
		impl.setThreadDomain(new CallerThreadDomain());
		
		// define mock behavior
		connection.getParticipantId();
		connectionCtrl.setDefaultReturnValue(1);
		connection.leave();
		
		// replay
		connectionCtrl.replay();
		
		// test
		impl.setConnection(connection);
		impl.leave();
		
		// verify
		connectionCtrl.verify();
	}
	
	public void testSendOperation() throws Exception {
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl connectionCtrl = MockControl.createControl(SessionConnection.class);
		SessionConnection connection = (SessionConnection) connectionCtrl.getMock();
		
		SessionImpl impl = new SessionImpl(algorithm);
		impl.setThreadDomain(new CallerThreadDomain());
		
		// define mock behavior
		algorithm.generateRequest(null);
		algorithmCtrl.setReturnValue(null);
		connection.getParticipantId();
		connectionCtrl.setDefaultReturnValue(1);
		connection.sendRequest(null);
		
		// replay
		algorithmCtrl.replay();
		connectionCtrl.replay();
		
		// test
		impl.setConnection(connection);
		impl.sendOperation(null);
		
		// verify
		algorithmCtrl.verify();
		connectionCtrl.verify();
	}
	
	public void testSendCaretUpdate() throws Exception {
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl connectionCtrl = MockControl.createControl(SessionConnection.class);
		SessionConnection connection = (SessionConnection) connectionCtrl.getMock();
		
		SessionImpl impl = new SessionImpl(algorithm);
		impl.setThreadDomain(new CallerThreadDomain());
		
		// define mock behavior
		algorithm.generateCaretUpdateMessage(null);
		algorithmCtrl.setReturnValue(null);
		connection.getParticipantId();
		connectionCtrl.setDefaultReturnValue(1);
		connection.sendCaretUpdateMessage(null);
		
		// replay
		algorithmCtrl.replay();
		connectionCtrl.replay();
		
		impl.setConnection(connection);
		impl.sendCaretUpdate(null);
		
		// verify
		algorithmCtrl.verify();
		connectionCtrl.verify();
	}
	
	public void testKicked() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(ParticipantSessionCallback.class);
		ParticipantSessionCallback callback = (ParticipantSessionCallback) callbackCtrl.getMock();
				
		// define mock behavior
		callback.getLock();
		callbackCtrl.setDefaultReturnValue(null);
		callback.kicked();
		
		// replay
		callbackCtrl.replay();
		
		// test
		SessionImpl impl = new SessionImpl();
		impl.setSessionCallback(callback);
		impl.kicked();
		
		// verify
		callbackCtrl.verify();
	}

	public void testSessionTerminated() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(ParticipantSessionCallback.class);
		ParticipantSessionCallback callback = (ParticipantSessionCallback) callbackCtrl.getMock();
				
		// define mock behavior
		callback.getLock();
		callbackCtrl.setDefaultReturnValue(null);
		callback.sessionTerminated();
		
		// replay
		callbackCtrl.replay();
		
		// test
		SessionImpl impl = new SessionImpl();
		impl.setSessionCallback(callback);
		impl.sessionTerminated();
		
		// verify
		callbackCtrl.verify();
	}

	public void testReceiveRequest() throws Exception {
		MockControl lockCtrl = MockControl.createControl(Lock.class);
		Lock lock = (Lock) lockCtrl.getMock();
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl callbackCtrl = MockControl.createControl(ParticipantSessionCallback.class);
		ParticipantSessionCallback callback = (ParticipantSessionCallback) callbackCtrl.getMock();
		
		Operation operation = new InsertOperation(0, "x");
		Request request = new RequestImpl(0, null, operation);
		Participant participant = new ParticipantImpl(1, new RemoteUserStub("1"));
		
		// define mock behavior
		callback.getLock();
		callbackCtrl.setDefaultReturnValue(lock);
		lock.lock();
		algorithm.receiveRequest(request);
		algorithmCtrl.setReturnValue(operation);
		callback.receiveOperation(participant, operation);
		lock.unlock();
		
		// replay
		lockCtrl.replay();
		algorithmCtrl.replay();
		callbackCtrl.replay();
		
		// test
		SessionImpl impl = new SessionImpl(algorithm);
		impl.setSessionCallback(callback);
		impl.addParticipant(participant);
		impl.receiveRequest(1, request);
		
		// verify
		lockCtrl.verify();
		algorithmCtrl.verify();
		callbackCtrl.verify();		
	}

	public void testReceiveCaretUpdateMessage() throws Exception {
		MockControl lockCtrl = MockControl.createControl(Lock.class);
		Lock lock = (Lock) lockCtrl.getMock();
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl callbackCtrl = MockControl.createControl(ParticipantSessionCallback.class);
		ParticipantSessionCallback callback = (ParticipantSessionCallback) callbackCtrl.getMock();
		
		CaretUpdate update = new CaretUpdate(0, 1);
		CaretUpdateMessage message = new CaretUpdateMessage(0, null, update);
		Participant participant = new ParticipantImpl(1, new RemoteUserStub("1"));
		
		// define mock behavior
		callback.getLock();
		callbackCtrl.setDefaultReturnValue(lock);
		lock.lock();
		algorithm.receiveCaretUpdateMessage(message);
		algorithmCtrl.setReturnValue(update);
		callback.receiveCaretUpdate(participant, update);
		lock.unlock();
		
		// replay
		lockCtrl.replay();
		algorithmCtrl.replay();
		callbackCtrl.replay();
		
		// test
		SessionImpl impl = new SessionImpl(algorithm);
		impl.setSessionCallback(callback);
		impl.addParticipant(participant);
		impl.receiveCaretUpdate(1, message);
		
		// verify
		lockCtrl.verify();
		algorithmCtrl.verify();
		callbackCtrl.verify();		
	}	

	public void testJoinLeave() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(ParticipantSessionCallback.class);
		ParticipantSessionCallback callback = (ParticipantSessionCallback) callbackCtrl.getMock();
		
		MockControl registryCtrl = MockControl.createControl(UserRegistry.class);
		UserRegistry registry = (UserRegistry) registryCtrl.getMock();
				
		// define mock behavior
		callback.getLock();
		callbackCtrl.setDefaultReturnValue(null);
		callback.participantJoined(new ParticipantImpl(1, new RemoteUserStub("1")));
		callback.participantLeft(new ParticipantImpl(1, new RemoteUserStub("1")), Participant.LEFT);
		
		registry.getUser(new RemoteUserProxyStub("1"));
		registryCtrl.setDefaultReturnValue(new RemoteUserStub("1"));
		
		// replay
		callbackCtrl.replay();
		registryCtrl.replay();
		
		// test
		SessionImpl impl = new SessionImpl();
		impl.setSessionCallback(callback);
		impl.setUserRegistry(registry);
		impl.participantJoined(1, new RemoteUserProxyStub("1"));
		assertEquals(1, impl.getParticipants().size());
		assertEquals(new ParticipantImpl(1, new RemoteUserStub("1")), impl.getParticipant(1));
		impl.participantLeft(1, Participant.LEFT);
		assertEquals(0, impl.getParticipants().size());
		
		// verify
		callbackCtrl.verify();
		registryCtrl.verify();
	}

}
