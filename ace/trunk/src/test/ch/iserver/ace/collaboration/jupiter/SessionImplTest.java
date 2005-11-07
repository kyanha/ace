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
import ch.iserver.ace.collaboration.SessionCallback;
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
		impl.setConnection(connection);
		
		// define mock behavior
		connection.leave();
		
		// replay
		connectionCtrl.replay();
		
		// test
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
		impl.setConnection(connection);
		
		// define mock behavior
		algorithm.generateRequest(null);
		algorithmCtrl.setReturnValue(null);
		connection.sendRequest(null);
		
		// replay
		algorithmCtrl.replay();
		connectionCtrl.replay();
		
		impl.lock();
		impl.sendOperation(null);
		impl.unlock();
		
		// verify
		algorithmCtrl.verify();
		connectionCtrl.verify();
	}

	public void testSendOperationNoLocking() throws Exception {
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl connectionCtrl = MockControl.createControl(SessionConnection.class);
		SessionConnection connection = (SessionConnection) connectionCtrl.getMock();
		
		SessionImpl impl = new SessionImpl(algorithm);
		impl.setThreadDomain(new CallerThreadDomain());
		impl.setConnection(connection);
		
		// replay
		algorithmCtrl.replay();
		connectionCtrl.replay();
		
		try {
			impl.sendOperation(null);
			fail("sending operations without locking must fail");
		} catch (IllegalMonitorStateException e) {
			// expected
		}
		
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
		impl.setConnection(connection);
		
		// define mock behavior
		algorithm.generateCaretUpdateMessage(null);
		algorithmCtrl.setReturnValue(null);
		connection.sendCaretUpdate(null);
		
		// replay
		algorithmCtrl.replay();
		connectionCtrl.replay();
		
		impl.lock();
		impl.sendCaretUpdate(null);
		impl.unlock();
		
		// verify
		algorithmCtrl.verify();
		connectionCtrl.verify();
	}

	public void testSendCaretUpdateNoLocking() throws Exception {
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl connectionCtrl = MockControl.createControl(SessionConnection.class);
		SessionConnection connection = (SessionConnection) connectionCtrl.getMock();
		
		SessionImpl impl = new SessionImpl(algorithm);
		impl.setThreadDomain(new CallerThreadDomain());
		impl.setConnection(connection);
		
		// replay
		algorithmCtrl.replay();
		connectionCtrl.replay();
		
		try {
			impl.sendCaretUpdate(null);
			fail("sending caret updates without locking must fail");
		} catch (IllegalMonitorStateException e) {
			// expected
		}
		
		// verify
		algorithmCtrl.verify();
		connectionCtrl.verify();
	}
	
	public void testKicked() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(SessionCallback.class);
		SessionCallback callback = (SessionCallback) callbackCtrl.getMock();
		
		SessionImpl impl = new SessionImpl();
		impl.setSessionCallback(callback);
		
		// define mock behavior
		callback.kicked();
		
		// replay
		callbackCtrl.replay();
		
		// test
		impl.kicked();
		
		// verify
		callbackCtrl.verify();
	}

	public void testSessionTerminated() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(SessionCallback.class);
		SessionCallback callback = (SessionCallback) callbackCtrl.getMock();
		
		SessionImpl impl = new SessionImpl();
		impl.setSessionCallback(callback);
		
		// define mock behavior
		callback.sessionTerminated();
		
		// replay
		callbackCtrl.replay();
		
		// test
		impl.sessionTerminated();
		
		// verify
		callbackCtrl.verify();
	}

	public void testReceiveRequest() throws Exception {
		MockControl lockCtrl = MockControl.createControl(Lock.class);
		Lock lock = (Lock) lockCtrl.getMock();
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl callbackCtrl = MockControl.createControl(SessionCallback.class);
		SessionCallback callback = (SessionCallback) callbackCtrl.getMock();
		
		Operation operation = new InsertOperation(0, "x");
		Request request = new RequestImpl(0, null, operation);
		Participant participant = new ParticipantImpl(1, new RemoteUserStub("1"));
		SessionImpl impl = new SessionImpl(algorithm, lock);
		impl.setSessionCallback(callback);
		impl.addParticipant(participant);
		
		// define mock behavior
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
		MockControl callbackCtrl = MockControl.createControl(SessionCallback.class);
		SessionCallback callback = (SessionCallback) callbackCtrl.getMock();
		
		CaretUpdate update = new CaretUpdate(0, 1);
		CaretUpdateMessage message = new CaretUpdateMessage(0, null, update);
		Participant participant = new ParticipantImpl(1, new RemoteUserStub("1"));
		SessionImpl impl = new SessionImpl(algorithm, lock);
		impl.setSessionCallback(callback);
		impl.addParticipant(participant);
		
		// define mock behavior
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
		impl.receiveCaretUpdate(1, message);
		
		// verify
		lockCtrl.verify();
		algorithmCtrl.verify();
		callbackCtrl.verify();		
	}	

	public void testJoinLeave() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(SessionCallback.class);
		SessionCallback callback = (SessionCallback) callbackCtrl.getMock();
		
		MockControl registryCtrl = MockControl.createControl(UserRegistry.class);
		UserRegistry registry = (UserRegistry) registryCtrl.getMock();
		
		SessionImpl impl = new SessionImpl();
		impl.setSessionCallback(callback);
		impl.setUserRegistry(registry);
		
		// define mock behavior
		callback.participantJoined(new ParticipantImpl(1, new RemoteUserStub("1")));
		callback.participantLeft(new ParticipantImpl(1, new RemoteUserStub("1")), Participant.LEFT);
		
		registry.addUser(new RemoteUserProxyStub("1"));
		registryCtrl.setDefaultReturnValue(new RemoteUserStub("1"));
		
		// replay
		callbackCtrl.replay();
		registryCtrl.replay();
		
		// test
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
