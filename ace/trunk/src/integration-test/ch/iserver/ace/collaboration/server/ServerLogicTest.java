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

package ch.iserver.ace.collaboration.server;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Invocation;
import org.jmock.core.Stub;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.collaboration.AcceptingAccessControlStrategy;
import ch.iserver.ace.collaboration.JoinRequest;
import ch.iserver.ace.collaboration.jupiter.UserRegistry;
import ch.iserver.ace.collaboration.jupiter.UserRegistryImpl;
import ch.iserver.ace.collaboration.jupiter.server.ServerDocument;
import ch.iserver.ace.collaboration.jupiter.server.ServerDocumentImpl;
import ch.iserver.ace.collaboration.jupiter.server.ServerLogicImpl;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.RemoteUserProxyStub;
import ch.iserver.ace.util.CallerThreadDomain;
import ch.iserver.ace.util.SingleThreadDomain;

/**
 *
 */
public class ServerLogicTest extends MockObjectTestCase {
	
	public void testJoinShutdown() throws Exception {
		DocumentModel document = new DocumentModel("", 0, 0, new DocumentDetails("collab.txt"));
		UserRegistry registry = new UserRegistryImpl();
		
		ServerLogicImpl logic = new ServerLogicImpl(new SingleThreadDomain(), new CallerThreadDomain(), document, registry);
		
		Mock mock = mock(ParticipantConnection.class);
		ParticipantConnection connection = (ParticipantConnection) mock.proxy();
		
		mock.expects(once()).method("joinRejected").with(eq(JoinRequest.SHUTDOWN));		
		logic.join(connection);
	}
	
	public void testJoin() throws Exception {
		final int USERS = 5;
		
		DocumentModel doc = new DocumentModel("", 0, 0, new DocumentDetails("collab.txt"));
		UserRegistry registry = new UserRegistryImpl();
		Mock documentMock = mock(ServerDocument.class);
		final ServerDocument document = (ServerDocument) documentMock.proxy();
		ServerDocument[] documents = new ServerDocumentImpl[USERS];
		
		for (int i = 0; i < USERS; i++) {
			documents[i] = new ServerDocumentImpl();
		}
		
		for (int i = 0; i < USERS; i++) {
			documentMock.expects(once()).method("participantJoined").with(eq(i + 1), eq(new RemoteUserProxyStub("" + i)));
			documentMock.expects(once()).method("toPortableDocument").will(returnValue(documents[i]));
		}
		
		for (int i = 0; i < USERS; i++) {
			documentMock.expects(once()).method("participantLeft").with(eq(i + 1));
		}
		
		for (int i = 0; i < USERS; i++) {
			registry.addUser(new RemoteUserProxyStub("" + i));
		}
		
		ServerLogicImpl logic = new ServerLogicImpl(new SingleThreadDomain(), 
						new CallerThreadDomain(), doc, registry) {
			protected ServerDocument createServerDocument(DocumentModel doc) {
				return document;
			}
		};
		logic.setAccessControlStrategy(new AcceptingAccessControlStrategy());
		logic.start();
		
		Mock[] mock = new Mock[USERS];
		ParticipantConnection[] connection = new ParticipantConnection[USERS];
		PortRetriever[] retriever = new PortRetriever[USERS];
		
		for (int i = 0; i < USERS; i++) {
			mock[i] = mock(ParticipantConnection.class);
			connection[i] = (ParticipantConnection) mock[i].proxy();
		}
				
		for (int i = 0; i < USERS; i++) {
			mock[i].expects(atLeastOnce()).method("getUser").will(returnValue(new RemoteUserProxyStub("" + i)));
			mock[i].expects(once()).method("setParticipantId").with(eq(i + 1));
			retriever[i] = new PortRetriever();
			mock[i].expects(once()).method("joinAccepted").with(ANYTHING).will(retriever[i]);
			mock[i].expects(once()).method("sendDocument").with(eq(documents[i]));
			for (int j = 0; j < i; j++) {
				mock[j].expects(once()).method("sendParticipantJoined").with(eq(i + 1), eq(new RemoteUserProxyStub("" + i)));
			}
		}
				
		for (int i = 0; i < USERS; i++) {
			logic.join(connection[i]);
		}
		
		for (int i = 0; i < USERS; i++) {
			mock[i].expects(once()).method("close");
			for (int j = i + 1; j < USERS; j++) {
				mock[j].expects(once()).method("sendParticipantLeft").with(eq(i + 1), eq(1));
			}
		}
		
		for (int i = 0; i < USERS; i++) {
			retriever[i].getPort().leave();
		}
	}
	
	private static class PortRetriever implements Stub {
		private ParticipantPort port; 
		public ParticipantPort getPort() {
			return port;
		}
		public StringBuffer describeTo(StringBuffer buf) {
			buf.append("PortRetriever: ");
			buf.append(port);
			return buf;
		}
		public Object invoke(Invocation invocation) throws Throwable {
			port = (ParticipantPort) invocation.parameterValues.get(0);
			return null;
		}
	}
	
}
