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

import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.jupiter.JupiterRequest;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.util.BlockingQueue;
import ch.iserver.ace.util.LinkedBlockingQueue;
import junit.framework.TestCase;

public class ParticipantPortImplTest extends TestCase {
	
	/** The algorithm mock. */
	private Algorithm algorithm;
	
	/** The blocking queue for the commands. */
	private BlockingQueue queue;
	
	/** The participant port under test. */
	private ParticipantPort port;
	
	public void setUp() {
		MockControl control = MockControl.createNiceControl(Algorithm.class);
		algorithm = (Algorithm) control.getMock();
		queue = new LinkedBlockingQueue();
		port = new ParticipantPortImpl(null, 1, algorithm, queue);
	}
	
	/**
	 * Test method for 'ch.iserver.ace.collaboration.jupiter.server.ParticipantPortImpl.receiveCaretUpdate(CaretUpdateMessage)'
	 */
	public void testReceiveCaretUpdate() throws InterruptedException {
		CaretUpdateMessage message = new CaretUpdateMessage(0, null, null);
		port.receiveCaretUpdate(message);
		assertEquals(1, queue.size());
		CaretUpdateSerializerCommand command = (CaretUpdateSerializerCommand) queue.get();
		assertEquals(1, command.getParticipantId());
		assertSame(algorithm, command.getAlgorithm());
		assertSame(message, command.getMessage());
		
	}

	/**
	 * Test method for 'ch.iserver.ace.collaboration.jupiter.server.ParticipantPortImpl.receiveRequest(Request)'
	 */
	public void testReceiveRequest() throws InterruptedException {
		Request request = new JupiterRequest(0, null, null);
		port.receiveRequest(request);
		assertEquals(1, queue.size());
		RequestSerializerCommand command = (RequestSerializerCommand) queue.get();
		assertEquals(1, command.getParticipantId());
		assertSame(algorithm, command.getAlgorithm());
		assertSame(request, command.getRequest());
	}

}
