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

import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.jupiter.JupiterRequest;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.PublishedSessionCallback;
import ch.iserver.ace.util.Lock;

public class RequestCommandTest extends TestCase {

	public void testExecute() throws Exception {
		// mock objects
		MockControl lockCtrl = MockControl.createControl(Lock.class);
		Lock lock = (Lock) lockCtrl.getMock();
		MockControl callbackCtrl = MockControl.createControl(PublishedSessionCallback.class);
		PublishedSessionCallback callback = (PublishedSessionCallback) callbackCtrl.getMock();
		MockControl wrapperCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper wrapper = (AlgorithmWrapper) wrapperCtrl.getMock();
		MockControl participantCtrl = MockControl.createControl(Participant.class);
		Participant participant = (Participant) participantCtrl.getMock();
		
		// setup test object
		Request request = new JupiterRequest(0, null, null);
		RequestCommand cmd = new RequestCommand(lock, wrapper, participant, request);
		
		// define mock behavior
		lock.lock();
		wrapper.receiveRequest(request);
		wrapperCtrl.setReturnValue(null);
		callback.receiveOperation(participant, null);
		lock.unlock();
		
		// replay
		lockCtrl.replay();
		callbackCtrl.replay();
		wrapperCtrl.replay();
		participantCtrl.replay();
		
		// test
		cmd.execute(callback);
		
		// verify
		lockCtrl.verify();
		callbackCtrl.verify();
		wrapperCtrl.verify();
		participantCtrl.verify();
	}
	
	public void testDoWorkFails() throws Exception {
		// mock objects
		MockControl lockCtrl = MockControl.createControl(Lock.class);
		Lock lock = (Lock) lockCtrl.getMock();
		MockControl wrapperCtrl = MockControl.createControl(AlgorithmWrapper.class);
		AlgorithmWrapper wrapper = (AlgorithmWrapper) wrapperCtrl.getMock();
		MockControl participantCtrl = MockControl.createControl(Participant.class);
		Participant participant = (Participant) participantCtrl.getMock();
		
		// setup test object
		Request request = new JupiterRequest(0, null, null);
		RequestCommand cmd = new RequestCommand(lock, wrapper, participant, request);

		// define mock behavior
		lock.lock();
		wrapper.receiveRequest(request);
		wrapperCtrl.setThrowable(new RuntimeException());
		lock.unlock();
		
		// replay
		lockCtrl.replay();
		wrapperCtrl.replay();
		participantCtrl.replay();
		
		// test
		try {
			cmd.execute(null);
			fail("expected RuntimeException not thrown");
		} catch (RuntimeException e) {
			// expected
		}
		
		// verify
		lockCtrl.verify();
		wrapperCtrl.verify();
		participantCtrl.verify();		
	}

}
