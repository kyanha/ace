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

import org.easymock.MockControl;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.algorithm.jupiter.JupiterRequest;
import ch.iserver.ace.algorithm.jupiter.JupiterVectorTime;
import ch.iserver.ace.collaboration.jupiter.server.ServerLogicImpl;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.text.InsertOperation;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.SemaphoreLock;
import junit.framework.TestCase;

/**
 *
 */
public class ServerTest extends TestCase {
	
	public static final int PARTICIPANTS = 3;
	
	public void testBasics() throws Exception {
		Lock lock = new SemaphoreLock();
		MockControl[] controls = new MockControl[PARTICIPANTS];
		ParticipantConnection[] connections = new ParticipantConnection[PARTICIPANTS];
		ParticipantPort[] ports = new ParticipantPort[PARTICIPANTS];
		
		for (int i = 0; i < PARTICIPANTS; i++) {
			controls[i] = MockControl.createControl(ParticipantConnection.class);
			connections[i] = (ParticipantConnection) controls[i].getMock();
		}
		
		// define mock behavior
		connections[0].setParticipantId(0);
		
		for (int i = 0; i < PARTICIPANTS; i++) {
			connections[i].getUser();
			controls[i].setDefaultReturnValue(null);
		}
		
		for (int i = 1; i < PARTICIPANTS; i++) {
			connections[i].setParticipantId(i);
			connections[i].sendDocument(null);
			controls[i].setMatcher(MockControl.ALWAYS_MATCHER);
			for (int j = 0; j < i; j++) {
				connections[j].sendParticipantJoined(i, null);
				controls[j].setMatcher(MockControl.ALWAYS_MATCHER);
			}
			connections[i].sendRequest(0, new JupiterRequest(0, new JupiterVectorTime(0, 0), new InsertOperation(0, "x")));
		}
				
		// replay
		for (int i = 0; i < PARTICIPANTS; i++) {
			controls[i].replay();
		}

		// test
		DocumentModel document = new DocumentModel("", 0, 0, new DocumentDetails("collab.txt"));
		ServerLogicImpl server = new ServerLogicImpl(lock, connections[0], document);
		ports[0] = server.getPublisherPort();
		server.start();
		
		for (int i = 1; i < PARTICIPANTS; i++) {
			ports[i] = server.join(connections[i]);
		}
				
		// test
		ports[0].receiveRequest(new JupiterRequest(1, new JupiterVectorTime(0, 0), new InsertOperation(0, "x")));
		
		// sleeep
		Thread.sleep(2000);
		
		// verify
		for (int i = 0; i < PARTICIPANTS; i++) {
			controls[i].verify();
		}
	}
	
}
