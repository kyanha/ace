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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.RemoteUserProxyStub;
import ch.iserver.ace.text.InsertOperation;

public class CompositeForwarderTest extends TestCase {
	
	private static final int PROXY_COUNT = 4;
		
	private CompositeForwarderImpl forwarder;
	
	private MockControl defaultForwarderCtrl;
	
	private Forwarder defaultForwarder;
	
	private MockControl handlerCtrl;
	
	private FailureHandler handler;
	
	private List controls;
	
	private List proxies;
	
	public void setUp() {
		defaultForwarderCtrl = MockControl.createStrictControl(Forwarder.class);
		defaultForwarder = (Forwarder) defaultForwarderCtrl.getMock();
		
		handlerCtrl = MockControl.createStrictControl(FailureHandler.class);
		handler = (FailureHandler) handlerCtrl.getMock();
		
		forwarder = new CompositeForwarderImpl(defaultForwarder, handler);
		
		controls = new ArrayList();
		proxies = new ArrayList();
		
		for (int i = 0; i < PROXY_COUNT; i++) {
			MockControl control = MockControl.createStrictControl(Forwarder.class);
			controls.add(control);
			Forwarder proxy = (Forwarder) control.getMock();
			forwarder.addForwarder(proxy);
			proxies.add(proxy);
		}
	}
	
	private void replayProxyControls() {
		Iterator it = controls.iterator();
		while (it.hasNext()) {
			MockControl control = (MockControl) it.next();
			control.replay();
		}
	}
	
	private void verifyProxyControls() {
		Iterator it = controls.iterator();
		while (it.hasNext()) {
			MockControl control = (MockControl) it.next();
			control.verify();
		}
	}
	
	public void testSendCaretUpdate() {
		// test fixture
		CaretUpdate update = new CaretUpdate(1, 3);

		// define mock behavior
		defaultForwarder.sendCaretUpdate(1, update);
		
		for (int i = 0; i < PROXY_COUNT; i++) {
			((Forwarder) proxies.get(i)).sendCaretUpdate(1, update);
		}
		
		// replay
		defaultForwarderCtrl.replay();
		handlerCtrl.replay();
		replayProxyControls();
		
		// test forwarding of CaretUpdate
		forwarder.addParticipant(1);
		forwarder.sendCaretUpdate(1, update);
		
		// verify
		defaultForwarderCtrl.verify();
		handlerCtrl.verify();
		verifyProxyControls();
	}
	
	public void testSendCaretUpdateDefaultFails() {
		// test fixture
		CaretUpdate update = new CaretUpdate(1, 3);

		// define mock behavior		
		defaultForwarder.sendCaretUpdate(1, update);
		defaultForwarderCtrl.setThrowable(new RuntimeException("failing default forwarder"));
		handler.handleFailure(1, Participant.DOCUMENT_MODIFICATION_FAILED);
		
		// replay
		defaultForwarderCtrl.replay();
		handlerCtrl.replay();
		replayProxyControls();
		
		// test forwarding of CaretUpdate
		forwarder.addParticipant(1);
		forwarder.sendCaretUpdate(1, update);

		// test that further messages from that participant are ignored
		forwarder.removeParticipant(1);
		forwarder.sendCaretUpdate(1, update);

		// verify
		defaultForwarderCtrl.verify();
		handlerCtrl.verify();
		verifyProxyControls();
	}

	public void testSendOperation() {
		// test fixture
		Operation operation = new InsertOperation(0, "foo");

		// define mock behavior
		defaultForwarder.sendOperation(1, operation);
		
		for (int i = 0; i < PROXY_COUNT; i++) {
			((Forwarder) proxies.get(i)).sendOperation(1, operation);
		}
		
		// replay
		defaultForwarderCtrl.replay();
		handlerCtrl.replay();
		replayProxyControls();
		
		// test forwarding of CaretUpdate
		forwarder.addParticipant(1);
		forwarder.sendOperation(1, operation);
		
		// verify
		defaultForwarderCtrl.verify();
		handlerCtrl.verify();
		verifyProxyControls();
	}
	
	public void testSendOperationFails() {
		// test fixture
		Operation operation = new InsertOperation(0, "foo");

		// define mock behavior
		defaultForwarder.sendOperation(1, operation);
		defaultForwarderCtrl.setThrowable(new RuntimeException("failing default forwarder"));
		handler.handleFailure(1, Participant.DOCUMENT_MODIFICATION_FAILED);
		
		// replay
		defaultForwarderCtrl.replay();
		handlerCtrl.replay();
		replayProxyControls();
		
		// test
		forwarder.addParticipant(1);
		forwarder.sendOperation(1, operation);
		
		// test that further messages from that participant are ignored
		forwarder.removeParticipant(1);		
		forwarder.sendOperation(1, operation);
		
		// verify
		defaultForwarderCtrl.verify();
		handlerCtrl.verify();
		verifyProxyControls();
	}

	
	public void testSendParticipantJoined() throws Exception {
		RemoteUserProxy user = new RemoteUserProxyStub("X");

		// define mock behavior
		for (int i = 0; i < PROXY_COUNT; i++) {
			((Forwarder) proxies.get(i)).sendParticipantJoined(1, user);
		}
		
		// replay
		defaultForwarderCtrl.replay();
		handlerCtrl.replay();
		replayProxyControls();
		
		// test
		forwarder.sendParticipantJoined(1, user);

		// verify
		defaultForwarderCtrl.verify();
		handlerCtrl.verify();
		verifyProxyControls();
	}
	
	public void testSendParticipantLeft() throws Exception {
		// define mock behavior
		for (int i = 0; i < PROXY_COUNT; i++) {
			((Forwarder) proxies.get(i)).sendParticipantLeft(1, Participant.LEFT);
		}
		
		// replay
		defaultForwarderCtrl.replay();
		handlerCtrl.replay();
		replayProxyControls();
		
		// test
		forwarder.sendParticipantLeft(1, Participant.LEFT);

		// verify
		defaultForwarderCtrl.verify();
		handlerCtrl.verify();
		verifyProxyControls();
	}
	
	public void testJoinLeave() throws Exception {
		// define mock behavior
		for (int i = 0; i < PROXY_COUNT; i++) {
			Forwarder fwd = (Forwarder) proxies.get(i);
			fwd.sendParticipantJoined(1, new RemoteUserProxyStub("X"));
			fwd.sendParticipantLeft(1, Participant.LEFT);
			fwd.sendParticipantJoined(1, new RemoteUserProxyStub("X"));
			fwd.sendParticipantLeft(1, Participant.LEFT);
		}
		
		// replay
		defaultForwarderCtrl.replay();
		handlerCtrl.replay();
		replayProxyControls();
		
		// test
		forwarder.sendParticipantJoined(1, new RemoteUserProxyStub("X"));
		assertTrue(forwarder.acceptParticipant(1));
		forwarder.sendParticipantLeft(1, Participant.LEFT);
		assertFalse(forwarder.acceptParticipant(1));
		forwarder.sendParticipantJoined(1, new RemoteUserProxyStub("X"));
		assertTrue(forwarder.acceptParticipant(1));
		forwarder.sendParticipantLeft(1, Participant.LEFT);
		assertFalse(forwarder.acceptParticipant(1));

		// verify
		defaultForwarderCtrl.verify();
		handlerCtrl.verify();
		verifyProxyControls();
	}

	public void testSendClose() throws Exception {
		// define mock behavior
		defaultForwarder.close();
		for (int i = 0; i < PROXY_COUNT; i++) {
			((Forwarder) proxies.get(i)).close();
		}
		
		// replay
		defaultForwarderCtrl.replay();
		handlerCtrl.replay();
		replayProxyControls();
		
		// test
		forwarder.close();

		// verify
		defaultForwarderCtrl.verify();
		handlerCtrl.verify();
		verifyProxyControls();
	}

}
