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

import org.easymock.MockControl;

import junit.framework.TestCase;

public class CountingAcknowledgeStrategyTest extends TestCase {
	
	private MockControl actionCtrl;
	
	private AcknowledgeAction action;
	
	private AcknowledgeStrategy strategy;
	
	public void setUp() {
		actionCtrl = MockControl.createControl(AcknowledgeAction.class);
		action = (AcknowledgeAction) actionCtrl.getMock();
		strategy = new CountingAcknowledgeStrategy(2);
		strategy.init(action);
	}
	
	public void tearDown() {
		actionCtrl.verify();
	}
	
	public void testBelowThreshold() throws Exception {		
		// define mock behavior
		
		// replay
		actionCtrl.replay();
		
		// test
		strategy.messageReceived();
	}
	
	public void testThreshold() throws Exception {
		// define mock behavior
		action.execute();
		
		// replay
		actionCtrl.replay();
		
		// test
		strategy.messageReceived();
		strategy.messageReceived();
	}
	
	public void testAboveThreshold() throws Exception {
		final int COUNT = 10;
		
		// define mock behavior
		for (int i = 0; i < COUNT / 2; i++) {
			action.execute();
		}
		
		// replay
		actionCtrl.replay();
		
		// test
		for (int i = 0; i < COUNT; i++) {
			strategy.messageReceived();
		}
	}
	
}
