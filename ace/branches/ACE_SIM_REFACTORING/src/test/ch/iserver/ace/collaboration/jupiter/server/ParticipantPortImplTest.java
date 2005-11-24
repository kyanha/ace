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

import ch.iserver.ace.collaboration.jupiter.AlgorithmWrapper;
import ch.iserver.ace.net.ParticipantPort;

public class ParticipantPortImplTest extends TestCase {
	
	/** The algorithm mock. */
	private AlgorithmWrapper algorithm;
		
	/** The participant port under test. */
	private ParticipantPort port;
	
	/** The mock control for the logic. */
	private MockControl logicCtrl;
	
	/** The server logic mock. */
	private ServerLogic logic;
	
	public void setUp() {
		MockControl control = MockControl.createNiceControl(AlgorithmWrapper.class);
		algorithm = (AlgorithmWrapper) control.getMock();
		
		logicCtrl = MockControl.createControl(ServerLogic.class);
		logicCtrl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
		logic = (ServerLogic) logicCtrl.getMock();
		
		port = new ParticipantPortImpl(logic, 1, algorithm, null);
	}
	
	public void testReceiveCaretUpdate() throws InterruptedException {

	}

	public void testReceiveRequest() throws InterruptedException {

	}

}
