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

package ch.iserver.ace.collaboration;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.algorithm.jupiter.JupiterRequest;
import ch.iserver.ace.algorithm.jupiter.JupiterVectorTime;
import ch.iserver.ace.collaboration.jupiter.CollaborationServiceImpl;
import ch.iserver.ace.collaboration.jupiter.ParticipantImpl;
import ch.iserver.ace.collaboration.jupiter.RemoteUserProxyStub;
import ch.iserver.ace.collaboration.jupiter.RemoteUserStub;
import ch.iserver.ace.collaboration.jupiter.server.ServerDocumentImpl;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.text.InsertOperation;

/**
 *
 */
public class CollaborationServiceTest extends TestCase {
	
	public void testPublish() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(PublishedSessionCallback.class);
		PublishedSessionCallback callback = (PublishedSessionCallback) callbackCtrl.getMock();
		MockControl connectionCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection connection = (ParticipantConnection) connectionCtrl.getMock();
		
		NetworkServiceStub networkService = new NetworkServiceStub();
		
		// define mock behavior
		connection.setParticipantId(1);
		connection.getUser();
		connectionCtrl.setDefaultReturnValue(new RemoteUserProxyStub("X"));
		connection.sendDocument(null);
		connectionCtrl.setMatcher(MockControl.ALWAYS_MATCHER);
		connection.sendParticipantJoined(2, new RemoteUserProxyStub("Z"));
		connection.close();
		
		callback.participantJoined(new ParticipantImpl(1, new RemoteUserStub("X")));
		callback.receiveOperation(new ParticipantImpl(1, new RemoteUserStub("X")), new InsertOperation(0, "XYZ"));
		callback.participantJoined(new ParticipantImpl(2, new RemoteUserStub("Z")));
		
		// replay
		callbackCtrl.replay();
		connectionCtrl.replay();
		
		// test
		CollaborationServiceImpl service = new CollaborationServiceImpl(networkService);

		DocumentModel document = new DocumentModel("", 0, 0, new DocumentDetails("collab.txt"));
		PublishedSession session = service.publish(callback, document);
		
		assertEquals(1, networkService.getDocumentServers().size());
		DocumentServerStub server = (DocumentServerStub) networkService.getDocumentServers().get(0);
		
		DocumentServerLogic logic = server.getLogic();
		ParticipantPort port1 = logic.join(connection);
		port1.receiveRequest(new JupiterRequest(1, new JupiterVectorTime(0, 0), new InsertOperation(0, "XYZ")));
		
		ParticipantConnectionStub connectionStub = new ParticipantConnectionStub(new RemoteUserProxyStub("Z"));
		ServerDocumentImpl expectedDocument = new ServerDocumentImpl();
		expectedDocument.participantJoined(0, null);
		expectedDocument.insertString(0, 0, document.getContent());
		expectedDocument.updateCaret(0, document.getDot(), document.getMark());
		expectedDocument.participantJoined(1, null);
		expectedDocument.insertString(1, 0, "XYZ");
		connectionStub.setExpectedDocument(expectedDocument);
		logic.join(connectionStub);
		
		Thread.sleep(2000);

		session.leave();

		Thread.sleep(2000);

		// verify
		callbackCtrl.verify();
		connectionCtrl.verify();
		
		// assertions
		assertNotNull(networkService.getTimestampFactory());
		assertEquals(service, networkService.getCallback());
		assertEquals(1, server.getShutdownCnt());
	}
	
}
