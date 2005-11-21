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

package ch.iserver.ace.collaboration.jupiter.server.serializer;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.collaboration.jupiter.server.Forwarder;
import ch.iserver.ace.collaboration.jupiter.server.ServerLogic;
import ch.iserver.ace.collaboration.jupiter.server.SessionParticipant;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.ParticipantPortStub;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.PortableDocumentStub;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.RemoteUserProxyStub;

/**
 *
 */
public class JoinCommandTest extends TestCase {
	
	public void testExecute() throws Exception {
		MockControl forwarderCtrl = MockControl.createControl(Forwarder.class);
		Forwarder forwarder = (Forwarder) forwarderCtrl.getMock();
		MockControl logicCtrl = MockControl.createControl(ServerLogic.class);
		ServerLogic logic = (ServerLogic) logicCtrl.getMock();
		MockControl connectionCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection connection = (ParticipantConnection) connectionCtrl.getMock();
		
		// test fixture
		ParticipantPort port = new ParticipantPortStub(1);
		RemoteUserProxy user = new RemoteUserProxyStub("X");
		SessionParticipant participant = new SessionParticipant(port, null, connection, user);
		PortableDocument doc = new PortableDocumentStub();
		
		// define mock behavior
		logic.getDocument();
		logicCtrl.setReturnValue(doc);
		connection.sendDocument(doc);
		logic.addParticipant(participant);
		forwarder.sendParticipantJoined(1, user);
		
		// replay
		forwarderCtrl.replay();
		logicCtrl.replay();
		connectionCtrl.replay();
		
		// test
		JoinCommand command = new JoinCommand(participant, logic);
		command.execute(forwarder);
		
		// verify
		forwarderCtrl.verify();
		logicCtrl.verify();
		connectionCtrl.verify();
	}
	
}
