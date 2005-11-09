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

import org.easymock.MockControl;

import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.RequestImpl;
import ch.iserver.ace.collaboration.Session;
import ch.iserver.ace.net.SessionConnection;
import junit.framework.TestCase;

/**
 *
 */
public class SessionConnectionWrapperTest extends TestCase {
	
	public void testLeave() throws Exception {
		MockControl targetCtrl = MockControl.createControl(SessionConnection.class);
		SessionConnection target = (SessionConnection) targetCtrl.getMock();
		MockControl handlerCtrl = MockControl.createControl(SessionConnectionFailureHandler.class);
		SessionConnectionFailureHandler handler = (SessionConnectionFailureHandler) handlerCtrl.getMock();
		
		// fixture
		RuntimeException e = new RuntimeException();
		
		// define mock behavior
		target.leave();
		targetCtrl.setThrowable(e);
		handler.handleFailure(Session.LEAVE_FAILED, e);
		
		// replay
		targetCtrl.replay();
		handlerCtrl.replay();
		
		// test
		SessionConnection connection = new SessionConnectionWrapper(target, handler);
		connection.leave();
		
		// verify
		targetCtrl.verify();
		handlerCtrl.verify();
	}
	
	public void testSendCaretUpdate() throws Exception {
		MockControl targetCtrl = MockControl.createControl(SessionConnection.class);
		SessionConnection target = (SessionConnection) targetCtrl.getMock();
		MockControl handlerCtrl = MockControl.createControl(SessionConnectionFailureHandler.class);
		SessionConnectionFailureHandler handler = (SessionConnectionFailureHandler) handlerCtrl.getMock();
		
		// fixture
		CaretUpdateMessage message = new CaretUpdateMessage(0, null, null);
		RuntimeException e = new RuntimeException();
		
		// define mock behavior
		target.sendCaretUpdateMessage(message);
		handler.handleFailure(Session.SEND_FAILED, e);
		
		// replay
		targetCtrl.replay();
		handlerCtrl.replay();
		
		// test
		SessionConnection connection = new SessionConnectionWrapper(target, handler);
		connection.sendCaretUpdateMessage(message);
	}
	
	public void testSendRequest() throws Exception {
		MockControl targetCtrl = MockControl.createControl(SessionConnection.class);
		SessionConnection target = (SessionConnection) targetCtrl.getMock();
		MockControl handlerCtrl = MockControl.createControl(SessionConnectionFailureHandler.class);
		SessionConnectionFailureHandler handler = (SessionConnectionFailureHandler) handlerCtrl.getMock();
		
		// fixture
		Request request = new RequestImpl(0, null, null);
		RuntimeException e = new RuntimeException();
		
		// define mock behavior
		target.sendRequest(request);
		targetCtrl.setThrowable(e);
		handler.handleFailure(Session.SEND_FAILED, e);
		
		// replay
		targetCtrl.replay();
		handlerCtrl.replay();
		
		// test
		SessionConnection connection = new SessionConnectionWrapper(target, handler);
		connection.sendRequest(request);
	}
	
	public void testGetParticipantId() throws Exception {
		MockControl targetCtrl = MockControl.createControl(SessionConnection.class);
		SessionConnection target = (SessionConnection) targetCtrl.getMock();
		
		// define mock behavior
		target.getParticipantId();
		targetCtrl.setReturnValue(2);
		
		// replay
		targetCtrl.replay();
		
		// test
		SessionConnection connection = new SessionConnectionWrapper(target, null);
		assertEquals(2, connection.getParticipantId());
	}
	
	public void testIsAlive() throws Exception {
		MockControl targetCtrl = MockControl.createControl(SessionConnection.class);
		SessionConnection target = (SessionConnection) targetCtrl.getMock();
		
		// define mock behavior
		target.isAlive();
		targetCtrl.setReturnValue(true);
		
		// replay
		targetCtrl.replay();
		
		// test
		SessionConnection connection = new SessionConnectionWrapper(target, null);
		assertTrue(connection.isAlive());
	}
	
}
