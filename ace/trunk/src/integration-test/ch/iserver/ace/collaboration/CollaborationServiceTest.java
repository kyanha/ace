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
import ch.iserver.ace.collaboration.jupiter.CollaborationServiceImpl;
import ch.iserver.ace.collaboration.jupiter.ParticipantImpl;
import ch.iserver.ace.collaboration.jupiter.UserRegistry;
import ch.iserver.ace.collaboration.jupiter.UserRegistryImpl;
import ch.iserver.ace.collaboration.jupiter.server.ServerDocumentImpl;
import ch.iserver.ace.collaboration.jupiter.server.ServerLogicImpl;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.RemoteUserProxyStub;
import ch.iserver.ace.util.CallerThreadDomain;
import ch.iserver.ace.util.ThreadDomain;

/**
 * Complex test cases that test the overall working of the collaboration 
 * service and its collaborators.
 */
public class CollaborationServiceTest extends TestCase {
	
	public void testPublish() throws Exception {
		MockControl callbackCtrl = MockControl.createStrictControl(PublishedSessionCallback.class);
		PublishedSessionCallback callback = (PublishedSessionCallback) callbackCtrl.getMock();
		
		NetworkServiceStub networkService = new NetworkServiceStub();
		
		// define mock behavior
		callback.getLock();
		callbackCtrl.setDefaultReturnValue(null);
		
		// replay
		callbackCtrl.replay();
		
		// test
		ThreadDomain threadDomain = new CallerThreadDomain();
		UserRegistry registry = new UserRegistryImpl();
		DocumentModel document = new DocumentModel("", 0, 0, new DocumentDetails("collabl.txt"));
		CollaborationServiceImpl service = new CollaborationServiceImpl(networkService);
		service.setUserRegistry(registry);
		service.setPublisherThreadDomain(threadDomain);
		PublishedSession session = service.publish(callback, document);
		assertEquals(0, session.getParticipants().size());
		
		// verify
		callbackCtrl.verify();
	}
	
	public void testPublishJoining() throws Exception {
		MockControl callbackCtrl = MockControl.createControl(PublishedSessionCallback.class);
		PublishedSessionCallback callback = (PublishedSessionCallback) callbackCtrl.getMock();
		
		NetworkServiceStub networkService = new NetworkServiceStub();
		
		// define mock behavior		
		callback.getLock();
		callbackCtrl.setDefaultReturnValue(null);
		callback.participantJoined(new ParticipantImpl(1, new RemoteUserStub("X")));
		callback.participantJoined(new ParticipantImpl(2, new RemoteUserStub("Y")));
		
		// replay
		callbackCtrl.replay();
		
		// create the needed objects
		ThreadDomain threadDomain = new CallerThreadDomain();
		UserRegistry registry = new UserRegistryImpl();		
		CollaborationServiceImpl service = new CollaborationServiceImpl(networkService) {
			public ThreadDomain createIncomingDomain() {
				return new CallerThreadDomain();
			}
		};
		service.setUserRegistry(registry);
		service.setPublisherThreadDomain(threadDomain);
		
		// discover a user
		service.userDiscovered(new RemoteUserProxyStub("X"));
		service.userDiscovered(new RemoteUserProxyStub("Y"));

		// create document to publish
		DocumentModel document = new DocumentModel("", 0, 0, new DocumentDetails("collab.txt"));
		
		// publish a document
		PublishedSession session = service.publish(callback, document);
		
		// check that publish went down all the way to the network layer
		assertEquals(1, networkService.getDocumentServers().size());
				
		// extract the server logic from the stub
		DocumentServerStub server = (DocumentServerStub) networkService.getDocumentServers().get(0);
		ServerLogicImpl logic = (ServerLogicImpl) server.getLogic();
		
		// configure the server logic
		logic.setAccessControlStrategy(new AcceptingAccessControlStrategy());
		
		// configure a new joining user
		MockControl connectionCtrl1 = MockControl.createStrictControl(ParticipantConnection.class);
		ParticipantConnection connection1 = (ParticipantConnection) connectionCtrl1.getMock();
		
		// create expected document
		ServerDocumentImpl expectedDocument = new ServerDocumentImpl();
		expectedDocument.participantJoined(0, null);
		expectedDocument.insertString(0, 0, document.getContent());
		expectedDocument.updateCaret(0, document.getDot(), document.getMark());
		
		// set up expectations
		connection1.getUser();
		connectionCtrl1.setDefaultReturnValue(new RemoteUserProxyStub("X"));
		connection1.setParticipantId(1);
		connection1.joinAccepted(null);
		connectionCtrl1.setMatcher(MockControl.ALWAYS_MATCHER);
		connection1.sendDocument(expectedDocument.toPortableDocument());
		connectionCtrl1.setMatcher(MockControl.ALWAYS_MATCHER);
		connection1.sendParticipantJoined(2, new RemoteUserProxyStub("Y"));
		connection1.close();
		
		// replay / test
		connectionCtrl1.replay();
		logic.join(connection1);
		
		// configure a new joining user
		MockControl connectionCtrl2 = MockControl.createStrictControl(ParticipantConnection.class);
		ParticipantConnection connection2 = (ParticipantConnection) connectionCtrl2.getMock();

		// create expected document
		expectedDocument = new ServerDocumentImpl();
		expectedDocument.participantJoined(0, null);
		expectedDocument.insertString(0, 0, document.getContent());
		expectedDocument.updateCaret(0, document.getDot(), document.getMark());
		expectedDocument.participantJoined(1, new RemoteUserProxyStub("X"));

		// set up expectations
		connection2.getUser();
		connectionCtrl2.setDefaultReturnValue(new RemoteUserProxyStub("Y"));
		connection2.setParticipantId(2);
		connection2.joinAccepted(null);
		connectionCtrl2.setMatcher(MockControl.ALWAYS_MATCHER);
		connection2.sendDocument(expectedDocument.toPortableDocument());
		connectionCtrl2.setMatcher(MockControl.ALWAYS_MATCHER);
		connection2.close();
		
		// replay / test
		connectionCtrl2.replay();
		logic.join(connection2);
		session.leave();
		
		// verify
		callbackCtrl.verify();
		connectionCtrl1.verify();
		connectionCtrl2.verify();
		
		// assertions
		assertNotNull(networkService.getTimestampFactory());
		assertEquals(service, networkService.getCallback());
		assertEquals(1, server.getShutdownCnt());
	}
	
}
