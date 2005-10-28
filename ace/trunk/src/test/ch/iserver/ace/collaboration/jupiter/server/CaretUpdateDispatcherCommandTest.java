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

import org.easymock.MockControl;

import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.net.ParticipantConnection;
import junit.framework.TestCase;

public class CaretUpdateDispatcherCommandTest extends TestCase {

	/**
	 * Test method for 'ch.iserver.ace.collaboration.jupiter.server.CaretUpdateDispatcherCommand.execute()'
	 */
	public void testExecute() {
		MockControl connectionCtrl = MockControl.createControl(ParticipantConnection.class);
		ParticipantConnection connection = (ParticipantConnection) connectionCtrl.getMock();
		CaretUpdateMessage message = new CaretUpdateMessage(0, null, null);
		CaretUpdateDispatcherCommand cmd = new CaretUpdateDispatcherCommand(
						connection, 1, message);
		
		// define mock behavior
		connection.sendCaretUpdate(1, message);
		
		// replay
		connectionCtrl.replay();
		
		// test
		cmd.execute();
		
		// verify
		connectionCtrl.verify();
	}

}
