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
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.algorithm.TransformationException;
import ch.iserver.ace.algorithm.jupiter.JupiterVectorTime;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.jupiter.AlgorithmWrapper;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.text.InsertOperation;

public class ParticipantPortImplTest extends TestCase {
			
	/** The participant port under test. */
	private ParticipantPort port;
	
	/** The mock control for the algorithm. */
	private MockControl algorithmCtrl;
	
	/** The algorithm mock. */
	private AlgorithmWrapper algorithm;

	/** The mock control for the logic. */
	private MockControl logicCtrl;
	
	/** The server logic mock. */
	private ServerLogic logic;
	
	/** The mock control for the forwarder. */
	private MockControl forwarderCtrl;
	
	/** The forwarder mock. */
	private Forwarder forwarder;

	/** The mock control for the failure handler. */
	private MockControl handlerCtrl;
	
	/** The failure handler mock. */
	private FailureHandler handler;
	
	public void setUp() {
		algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		
		logicCtrl = MockControl.createControl(ServerLogic.class);
		logicCtrl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
		logic = (ServerLogic) logicCtrl.getMock();
		
		forwarderCtrl = MockControl.createControl(Forwarder.class);
		forwarder = (Forwarder) forwarderCtrl.getMock();

		handlerCtrl = MockControl.createControl(FailureHandler.class);
		handler = (FailureHandler) handlerCtrl.getMock();
		
		port = new ParticipantPortImpl(logic, handler, 1, algorithm, forwarder);
	}
	
	public void testReceiveCaretUpdate() throws Exception {
		CaretUpdate update = new CaretUpdate(0, 1);
		CaretUpdateMessage message = new CaretUpdateMessage(1, new JupiterVectorTime(0, 0), update);
		
		// define mock behavior
		algorithm.receiveCaretUpdateMessage(message);
		algorithmCtrl.setReturnValue(update);
		forwarder.sendCaretUpdate(1, update);
		
		// replay
		algorithmCtrl.replay();
		logicCtrl.replay();
		forwarderCtrl.replay();
		
		// test
		port.receiveCaretUpdate(message);

		// replay
		algorithmCtrl.verify();
		logicCtrl.verify();
		forwarderCtrl.verify();
	}

	public void testReceiveCaretUpdateFails() throws Exception {
		CaretUpdate update = new CaretUpdate(0, 1);
		CaretUpdateMessage message = new CaretUpdateMessage(1, new JupiterVectorTime(0, 0), update);
		
		// define mock behavior
		algorithm.receiveCaretUpdateMessage(message);
		algorithmCtrl.setThrowable(new TransformationException("transformation failed"));
		handler.handleFailure(port.getParticipantId(), Participant.RECEPTION_FAILED);
		
		// replay
		algorithmCtrl.replay();
		handlerCtrl.replay();
		logicCtrl.replay();
		forwarderCtrl.replay();
		
		// test
		port.receiveCaretUpdate(message);

		// replay
		algorithmCtrl.verify();
		handlerCtrl.verify();
		logicCtrl.verify();
		forwarderCtrl.verify();
	}

	public void testReceiveRequest() throws Exception {
		Operation operation = new InsertOperation(0, "x");
		Request request = new RequestImpl(0, new JupiterVectorTime(0, 0), operation);
		
		// define mock behavior
		algorithm.receiveRequest(request);
		algorithmCtrl.setReturnValue(operation);
		forwarder.sendOperation(1, operation);
		
		// replay
		algorithmCtrl.replay();
		logicCtrl.replay();
		forwarderCtrl.replay();
		
		// test
		port.receiveRequest(request);

		// replay
		algorithmCtrl.verify();
		logicCtrl.verify();
		forwarderCtrl.verify();
	}
	
	public void testReceiveRequestFails() throws Exception {
		Operation operation = new InsertOperation(0, "x");
		Request request = new RequestImpl(0, new JupiterVectorTime(0, 0), operation);
		
		// define mock behavior
		algorithm.receiveRequest(request);
		algorithmCtrl.setThrowable(new TransformationException("transformation failed"));
		handler.handleFailure(port.getParticipantId(), Participant.RECEPTION_FAILED);
		
		// replay
		algorithmCtrl.replay();
		handlerCtrl.replay();
		logicCtrl.replay();
		forwarderCtrl.replay();
		
		// test
		port.receiveRequest(request);

		// replay
		algorithmCtrl.verify();
		handlerCtrl.verify();
		logicCtrl.verify();
		forwarderCtrl.verify();
	}

	public void testReceiveAcknowledge() throws Exception {
		Timestamp timestamp = new JupiterVectorTime(0, 0);
		
		// define mock behavior
		algorithm.acknowledge(1, timestamp);
		
		// replay
		algorithmCtrl.replay();
		logicCtrl.replay();
		forwarderCtrl.replay();
		
		// test
		port.receiveAcknowledge(1, timestamp);

		// replay
		algorithmCtrl.verify();
		logicCtrl.verify();
		forwarderCtrl.verify();
	}
	
	public void testLeave() throws Exception {
		// define mock behavior
		logic.leave(port.getParticipantId());
		
		// replay
		algorithmCtrl.replay();
		logicCtrl.replay();
		forwarderCtrl.replay();
		
		// test
		port.leave();

		// replay
		algorithmCtrl.verify();
		logicCtrl.verify();
		forwarderCtrl.verify();
	}

}
