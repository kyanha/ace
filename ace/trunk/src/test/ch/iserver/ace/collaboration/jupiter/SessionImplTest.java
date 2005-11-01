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

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.net.SessionConnection;

/**
 *
 */
public class SessionImplTest extends TestCase {
	
	public void testSendOperation() throws Exception {
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl connectionCtrl = MockControl.createControl(SessionConnection.class);
		SessionConnection connection = (SessionConnection) connectionCtrl.getMock();
		
		SessionImpl impl = new SessionImpl(algorithm);
		impl.setConnection(connection);
		
		// define mock behavior
		algorithm.generateRequest(null);
		algorithmCtrl.setReturnValue(null);
		connection.sendRequest(null);
		
		// replay
		algorithmCtrl.replay();
		connectionCtrl.replay();
		
		impl.lock();
		impl.sendOperation(null);
		impl.unlock();
		
		// verify
		algorithmCtrl.verify();
		connectionCtrl.verify();
	}

	public void testSendOperationNoLocking() throws Exception {
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl connectionCtrl = MockControl.createControl(SessionConnection.class);
		SessionConnection connection = (SessionConnection) connectionCtrl.getMock();
		
		SessionImpl impl = new SessionImpl(algorithm);
		impl.setConnection(connection);
		
		// replay
		algorithmCtrl.replay();
		connectionCtrl.replay();
		
		try {
			impl.sendOperation(null);
			fail("sending operations without locking must fail");
		} catch (IllegalMonitorStateException e) {
			// expected
		}
		
		// verify
		algorithmCtrl.verify();
		connectionCtrl.verify();
	}
	
	public void testSendCaretUpdate() throws Exception {
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl connectionCtrl = MockControl.createControl(SessionConnection.class);
		SessionConnection connection = (SessionConnection) connectionCtrl.getMock();
		
		SessionImpl impl = new SessionImpl(algorithm);
		impl.setConnection(connection);
		
		// define mock behavior
		algorithm.generateCaretUpdateMessage(null);
		algorithmCtrl.setReturnValue(null);
		connection.sendCaretUpdate(null);
		
		// replay
		algorithmCtrl.replay();
		connectionCtrl.replay();
		
		impl.lock();
		impl.sendCaretUpdate(null);
		impl.unlock();
		
		// verify
		algorithmCtrl.verify();
		connectionCtrl.verify();
	}

	public void testSendCaretUpdateNoLocking() throws Exception {
		MockControl algorithmCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper algorithm = (AlgorithmWrapper) algorithmCtrl.getMock();
		MockControl connectionCtrl = MockControl.createControl(SessionConnection.class);
		SessionConnection connection = (SessionConnection) connectionCtrl.getMock();
		
		SessionImpl impl = new SessionImpl(algorithm);
		impl.setConnection(connection);
		
		// replay
		algorithmCtrl.replay();
		connectionCtrl.replay();
		
		try {
			impl.sendCaretUpdate(null);
			fail("sending caret updates without locking must fail");
		} catch (IllegalMonitorStateException e) {
			// expected
		}
		
		// verify
		algorithmCtrl.verify();
		connectionCtrl.verify();
	}

}
