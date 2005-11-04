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
import ch.iserver.ace.collaboration.jupiter.RemoteUserProxyStub;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.text.InsertOperation;

public class CompositeForwarderTest extends TestCase {
	
	private static final int PROXY_COUNT = 4;
		
	private MockControl control;
	
	private ServerLogic logic;
	
	private Forwarder forwarder;
	
	private List controls;
	
	private List proxies;
	
	public void setUp() {
		control = MockControl.createControl(ServerLogic.class);
		logic = (ServerLogic) control.getMock();
		forwarder = new CompositeForwarder(logic);
		
		controls = new ArrayList();
		proxies = new ArrayList();
		
		for (int i = 0; i < PROXY_COUNT; i++) {
			MockControl control = MockControl.createControl(Forwarder.class);
			controls.add(control);
			Forwarder proxy = (Forwarder) control.getMock();
			proxies.add(proxy);
		}
	}
	
	private Iterator getProxies() {
		return proxies.iterator();
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
		logic.getParticipantProxies();
		control.setReturnValue(getProxies());
		
		for (int i = 0; i < PROXY_COUNT; i++) {
			((Forwarder) proxies.get(i)).sendCaretUpdate(1, update);
		}
		
		// replay
		control.replay();
		replayProxyControls();
		
		// test forwarding of CaretUpdate
		forwarder.sendCaretUpdate(1, update);
		
		// verify
		control.verify();
		verifyProxyControls();
	}

	public void testSendOperation() {
		// test fixture
		Operation operation = new InsertOperation(0, "foo");

		// define mock behavior
		logic.getParticipantProxies();
		control.setReturnValue(getProxies());
		
		for (int i = 0; i < PROXY_COUNT; i++) {
			((Forwarder) proxies.get(i)).sendOperation(1, operation);
		}
		
		// replay
		control.replay();
		replayProxyControls();
		
		// test forwarding of CaretUpdate
		forwarder.sendOperation(1, operation);
		
		// verify
		control.verify();
		verifyProxyControls();
	}
	
	public void testSendParticipantJoined() throws Exception {
		RemoteUserProxy user = new RemoteUserProxyStub("X");

		// define mock behavior
		logic.getParticipantProxies();
		control.setReturnValue(getProxies());
		
		for (int i = 0; i < PROXY_COUNT; i++) {
			((Forwarder) proxies.get(i)).sendParticipantJoined(1, user);
		}
		
		// replay
		control.replay();
		replayProxyControls();
		
		// test
		forwarder.sendParticipantJoined(1, user);

		// verify
		control.verify();
		verifyProxyControls();
	}
	
	public void testSendParticipantLeft() throws Exception {
		// define mock behavior
		logic.getParticipantProxies();
		control.setReturnValue(getProxies());
		
		for (int i = 0; i < PROXY_COUNT; i++) {
			((Forwarder) proxies.get(i)).sendParticipantLeft(1, Participant.LEFT);
		}
		
		// replay
		control.replay();
		replayProxyControls();
		
		// test
		forwarder.sendParticipantLeft(1, Participant.LEFT);

		// verify
		control.verify();
		verifyProxyControls();
	}

}
