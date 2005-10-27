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

import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.jupiter.JupiterRequest;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.text.InsertOperation;
import ch.iserver.ace.util.BlockingQueue;

public class ParticipantProxyImplTest extends TestCase {

	/**
	 * Test method for 'ch.iserver.ace.collaboration.jupiter.server.ParticipantProxyImpl.sendCaretUpdate(int, CaretUpdate)'
	 */
	public void testSendCaretUpdate() {
		// TODO: fix testSendCaretUpdate method (compiler error)
		/*MockControl queueCtrl = MockControl.createControl(BlockingQueue.class);
		queueCtrl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
		BlockingQueue queue = (BlockingQueue) queueCtrl.getMock();
		MockControl algorithmCtrl = MockControl.createControl(Algorithm.class);
		Algorithm algorithm = (Algorithm) algorithmCtrl.getMock();
		MockControl connectionCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection connection = (ParticipantConnection) connectionCtrl.getMock();
		ParticipantProxy proxy = new ParticipantProxyImpl(1, queue, algorithm, connection);
		
		// define mock behavior
		CaretUpdate update = new CaretUpdate(1, 2);
		CaretUpdateMessage message = new CaretUpdateMessage(0, null, update);
		queue.add(new CaretUpdateDispatcherCommand(connection, 0, message));
		
		algorithm.generateCaretUpdateMessage(update);
		algorithmCtrl.setReturnValue(message);
		
		// replay
		queueCtrl.replay();
		algorithmCtrl.replay();
		
		// test
		proxy.sendCaretUpdate(0, update);
		
		// verify
		queueCtrl.verify();
		algorithmCtrl.verify();*/
	}

	/**
	 * Test method for 'ch.iserver.ace.collaboration.jupiter.server.ParticipantProxyImpl.sendOperation(int, Operation)'
	 */
	public void testSendOperation() {
		MockControl queueCtrl = MockControl.createControl(BlockingQueue.class);
		queueCtrl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
		BlockingQueue queue = (BlockingQueue) queueCtrl.getMock();
		MockControl algorithmCtrl = MockControl.createControl(Algorithm.class);
		Algorithm algorithm = (Algorithm) algorithmCtrl.getMock();
		MockControl connectionCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection connection = (ParticipantConnection) connectionCtrl.getMock();
		ParticipantProxy proxy = new ParticipantProxyImpl(1, queue, algorithm, connection);
		
		// define mock behavior
		Operation operation = new InsertOperation(0, "x");
		Request request = new JupiterRequest(0, null, operation);
		queue.add(new RequestDispatcherCommand(connection, 0, request));
		
		algorithm.generateRequest(operation);
		algorithmCtrl.setReturnValue(request);
		
		// replay
		queueCtrl.replay();
		algorithmCtrl.replay();
		
		// test
		proxy.sendOperation(0, operation);
		
		// verify
		queueCtrl.verify();
		algorithmCtrl.verify();
	}

}
