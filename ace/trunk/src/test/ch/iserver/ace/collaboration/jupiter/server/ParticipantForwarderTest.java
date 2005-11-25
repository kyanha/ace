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

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.RequestImpl;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.jupiter.AcknowledgeStrategy;
import ch.iserver.ace.collaboration.jupiter.AlgorithmWrapper;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.RemoteUserProxyStub;
import ch.iserver.ace.text.InsertOperation;


public class ParticipantForwarderTest extends TestCase {
	
	private MockControl algorithmCtrl;
	
	private AlgorithmWrapper algorithm;
	
	private MockControl connectionCtrl;
	
	private ParticipantConnection connection;
	
	public void setUp() {
		algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		connectionCtrl = MockControl.createControl(ParticipantConnection.class);
		connection = (ParticipantConnection) connectionCtrl.getMock();
	}
	
	public void tearDown() {
		algorithmCtrl.verify();
		connectionCtrl.verify();
	}
	
	public void testSendCaretUpdate() {
		MockControl acknowledgerCtrl = MockControl.createControl(AcknowledgeStrategy.class);
		AcknowledgeStrategy acknowledger = (AcknowledgeStrategy) acknowledgerCtrl.getMock();
		
		// test fixture
		CaretUpdate update = new CaretUpdate(0, 1);
		CaretUpdateMessage message = new CaretUpdateMessage(0, null, null);
		
		// define mock behavior
		algorithm.generateCaretUpdateMessage(update);
		algorithmCtrl.setReturnValue(message);
		connection.sendCaretUpdateMessage(2, message);
		acknowledger.init(null);
		acknowledgerCtrl.setMatcher(MockControl.ALWAYS_MATCHER);
		acknowledger.resetTimer();
		
		// replay
		acknowledgerCtrl.replay();
		algorithmCtrl.replay();
		connectionCtrl.replay();
		
		// test
		ParticipantForwarder proxy = new ParticipantForwarder(1, algorithm, connection);
		proxy.setAcknowledgeStrategy(acknowledger);
		proxy.sendCaretUpdate(2, update);
		
		// verify
		acknowledgerCtrl.verify();
	}

	public void testSendOperation() {
		MockControl acknowledgerCtrl = MockControl.createControl(AcknowledgeStrategy.class);
		AcknowledgeStrategy acknowledger = (AcknowledgeStrategy) acknowledgerCtrl.getMock();

		// test fixture
		Operation operation = new InsertOperation(0, "x");
		Request request = new RequestImpl(0, null, null);
		
		// define mock behavior
		algorithm.generateRequest(operation);
		algorithmCtrl.setReturnValue(request);
		connection.sendRequest(2, request);
		acknowledger.init(null);
		acknowledgerCtrl.setMatcher(MockControl.ALWAYS_MATCHER);
		acknowledger.resetTimer();
		
		// replay
		acknowledgerCtrl.replay();
		algorithmCtrl.replay();
		connectionCtrl.replay();
		
		// test
		ParticipantForwarder proxy = new ParticipantForwarder(1, algorithm, connection);
		proxy.setAcknowledgeStrategy(acknowledger);
		proxy.sendOperation(2, operation);
		
		// verify
		acknowledgerCtrl.verify();
	}

	public void testSendParticipantJoined() {
		// test fixture
		RemoteUserProxy user = new RemoteUserProxyStub("XYZ");
		
		// define mock behavior
		connection.sendParticipantJoined(2, user);
		
		// replay
		algorithmCtrl.replay();
		connectionCtrl.replay();
		
		// test
		ParticipantForwarder proxy = new ParticipantForwarder(1, algorithm, connection);
		proxy.sendParticipantJoined(2, user);
	}

	public void testSendParticipantLeft() {
		// define mock behavior
		connection.sendParticipantLeft(2, Participant.DISCONNECTED);
		
		// replay
		algorithmCtrl.replay();
		connectionCtrl.replay();
		
		// test
		ParticipantForwarder proxy = new ParticipantForwarder(1, algorithm, connection);
		proxy.sendParticipantLeft(2, Participant.DISCONNECTED);
	}

}
