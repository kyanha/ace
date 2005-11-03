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

import ch.iserver.ace.util.Lock;
import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

public class SerializerTest extends TestCase {

	/**
	 * Test method for 'ch.iserver.ace.collaboration.jupiter.server.Serializer.doWork()'
	 */
	public void testDoWork() throws InterruptedException {
		MockControl lockControl = MockControl.createControl(Lock.class);
		Lock lock = (Lock) lockControl.getMock();
		MockControl forwarderControl = MockControl.createControl(Forwarder.class);
		Forwarder forwarder = (Forwarder) forwarderControl.getMock();
		MockControl commandControl = MockControl.createControl(SerializerCommand.class);
		SerializerCommand command = (SerializerCommand) commandControl.getMock();
		BlockingQueue queue = new LinkedBlockingQueue();
		
		// define mock behavior
		lock.lock();
		command.execute(forwarder);
		lock.unlock();
		
		// replay
		lockControl.replay();
		commandControl.replay();
		
		// test
		Serializer serializer = new Serializer(queue, lock, forwarder);
		queue.add(command);
		serializer.doWork();
		
		// verify
		lockControl.verify();
		commandControl.verify();
	}

}
