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

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.TransformationException;

public class CaretUpdateSerializerCommandTest extends TestCase {

	public void testExecute() throws SerializerException, TransformationException {
		MockControl forwarderCtrl = MockControl.createControl(Forwarder.class);
		Forwarder forwarder = (Forwarder) forwarderCtrl.getMock();
		MockControl algorithmCtrl = MockControl.createControl(Algorithm.class);
		algorithmCtrl.setDefaultMatcher(MockControl.ARRAY_MATCHER);
		Algorithm algorithm = (Algorithm) algorithmCtrl.getMock();
		CaretUpdateMessage message = new CaretUpdateMessage(0, null, new CaretUpdate(1, 2));
		SerializerCommand command = new CaretUpdateSerializerCommand(1, algorithm, message);
		
		// define mock behavior
		forwarder.sendCaretUpdate(1, new CaretUpdate(1, 2));
		algorithm.transformIndices(null, new int[] { 1, 2 });
		algorithmCtrl.setReturnValue(new int[] { 1, 2 });
		
		// replay
		forwarderCtrl.replay();
		algorithmCtrl.replay();
		
		// test
		command.execute(forwarder);
		
		// verify
		forwarderCtrl.verify();
		algorithmCtrl.verify();
	}

}
