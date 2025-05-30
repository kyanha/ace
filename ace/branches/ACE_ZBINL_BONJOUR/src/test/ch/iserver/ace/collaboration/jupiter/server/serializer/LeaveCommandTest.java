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

import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.jupiter.server.Forwarder;

/**
 *
 */
public class LeaveCommandTest extends TestCase {
	
	public void testExecute() throws Exception {
		MockControl forwarderCtrl = MockControl.createControl(Forwarder.class);
		Forwarder forwarder = (Forwarder) forwarderCtrl.getMock();
						
		// define mock behavior
		forwarder.sendParticipantLeft(1, Participant.KICKED);
		
		// replay
		forwarderCtrl.replay();
		
		// test
		LeaveCommand command = new LeaveCommand(1, Participant.KICKED);
		command.execute(forwarder);
		
		// verify
		forwarderCtrl.verify();
	}
	
}
