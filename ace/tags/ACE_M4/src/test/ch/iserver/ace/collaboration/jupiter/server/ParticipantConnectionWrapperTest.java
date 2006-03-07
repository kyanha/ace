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

import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.net.ParticipantConnection;

/**
 *
 */
public class ParticipantConnectionWrapperTest extends TestCase {
	
	public void testSendCaretUpdateMessageFailure() throws Exception {
		MockControl targetCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection target = (ParticipantConnection) targetCtrl.getMock();
		
		MockControl handlerCtrl = MockControl.createControl(FailureHandler.class);
		FailureHandler handler = (FailureHandler) handlerCtrl.getMock();
				
		// define mock behavior
		target.setParticipantId(1);
		target.sendCaretUpdateMessage(0, null);
		targetCtrl.setThrowable(new RuntimeException());
		
		handler.handleFailure(1, Participant.DISCONNECTED);
		
		// replay
		targetCtrl.replay();
		handlerCtrl.replay();
		
		// test
		ParticipantConnectionWrapper wrapper = new ParticipantConnectionWrapper(target, handler);
		wrapper.setParticipantId(1);
		wrapper.sendCaretUpdateMessage(0, null);
		
		// verify
		targetCtrl.verify();
		handlerCtrl.verify();
	}

	public void testSendRequestFailure() throws Exception {
		MockControl targetCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection target = (ParticipantConnection) targetCtrl.getMock();
		
		MockControl handlerCtrl = MockControl.createControl(FailureHandler.class);
		FailureHandler handler = (FailureHandler) handlerCtrl.getMock();
				
		// define mock behavior
		target.setParticipantId(1);
		target.sendRequest(0, null);
		targetCtrl.setThrowable(new RuntimeException());
		
		handler.handleFailure(1, Participant.DISCONNECTED);
		
		// replay
		targetCtrl.replay();
		handlerCtrl.replay();
		
		// test
		ParticipantConnectionWrapper wrapper = new ParticipantConnectionWrapper(target, handler);
		wrapper.setParticipantId(1);
		wrapper.sendRequest(0, null);
		
		// verify
		targetCtrl.verify();
		handlerCtrl.verify();
	}

	public void testSendParticipantLeftFailure() throws Exception {
		MockControl targetCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection target = (ParticipantConnection) targetCtrl.getMock();
		
		MockControl handlerCtrl = MockControl.createControl(FailureHandler.class);
		FailureHandler handler = (FailureHandler) handlerCtrl.getMock();
				
		// define mock behavior
		target.setParticipantId(1);
		target.sendParticipantLeft(2, Participant.LEFT);
		targetCtrl.setThrowable(new RuntimeException());
		
		handler.handleFailure(1, Participant.DISCONNECTED);
		
		// replay
		targetCtrl.replay();
		handlerCtrl.replay();
		
		// test
		ParticipantConnectionWrapper wrapper = new ParticipantConnectionWrapper(target, handler);
		wrapper.setParticipantId(1);
		wrapper.sendParticipantLeft(2, Participant.LEFT);
		
		// verify
		targetCtrl.verify();
		handlerCtrl.verify();
	}

	public void testSendParticipantJoinedFailure() throws Exception {
		MockControl targetCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection target = (ParticipantConnection) targetCtrl.getMock();
		
		MockControl handlerCtrl = MockControl.createControl(FailureHandler.class);
		FailureHandler handler = (FailureHandler) handlerCtrl.getMock();
				
		// define mock behavior
		target.setParticipantId(1);
		target.sendParticipantJoined(2, null);
		targetCtrl.setThrowable(new RuntimeException());
		
		handler.handleFailure(1, Participant.DISCONNECTED);
		
		// replay
		targetCtrl.replay();
		handlerCtrl.replay();
		
		// test
		ParticipantConnectionWrapper wrapper = new ParticipantConnectionWrapper(target, handler);
		wrapper.setParticipantId(1);
		wrapper.sendParticipantJoined(2, null);
		
		// verify
		targetCtrl.verify();
		handlerCtrl.verify();
	}

	public void testSendKickedFailure() throws Exception {
		MockControl targetCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection target = (ParticipantConnection) targetCtrl.getMock();
		
		MockControl handlerCtrl = MockControl.createControl(FailureHandler.class);
		FailureHandler handler = (FailureHandler) handlerCtrl.getMock();
				
		// define mock behavior
		target.setParticipantId(1);
		target.sendKicked();
		targetCtrl.setThrowable(new RuntimeException());
		
		handler.handleFailure(1, Participant.DISCONNECTED);
		
		// replay
		targetCtrl.replay();
		handlerCtrl.replay();
		
		// test
		ParticipantConnectionWrapper wrapper = new ParticipantConnectionWrapper(target, handler);
		wrapper.setParticipantId(1);
		wrapper.sendKicked();
		
		// verify
		targetCtrl.verify();
		handlerCtrl.verify();
	}

	public void testSendDocumentFailure() throws Exception {
		MockControl targetCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection target = (ParticipantConnection) targetCtrl.getMock();
		
		MockControl handlerCtrl = MockControl.createControl(FailureHandler.class);
		FailureHandler handler = (FailureHandler) handlerCtrl.getMock();
				
		// define mock behavior
		target.setParticipantId(1);
		target.sendDocument(null);
		targetCtrl.setThrowable(new RuntimeException());
		
		handler.handleFailure(1, Participant.DISCONNECTED);
		
		// replay
		targetCtrl.replay();
		handlerCtrl.replay();
		
		// test
		ParticipantConnectionWrapper wrapper = new ParticipantConnectionWrapper(target, handler);
		wrapper.setParticipantId(1);
		wrapper.sendDocument(null);
		
		// verify
		targetCtrl.verify();
		handlerCtrl.verify();
	}

}
