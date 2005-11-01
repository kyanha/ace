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

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.jupiter.JupiterRequest;
import ch.iserver.ace.algorithm.jupiter.JupiterVectorTime;
import ch.iserver.ace.text.InsertOperation;

import junit.framework.TestCase;

/**
 * 
 *
 */
public class AlgorithmWrapperImplTest extends TestCase {
	
	private MockControl algorithmCtrl;
	
	private Algorithm algorithm;
	
	private AlgorithmWrapper wrapper;
	
	public void setUp() {
		algorithmCtrl = MockControl.createControl(Algorithm.class);
		algorithm = (Algorithm) algorithmCtrl.getMock();
		wrapper = new AlgorithmWrapperImpl(algorithm);
	}
	
	public void testReceiveRequest() throws Exception {
		Request request = new JupiterRequest(0, null, null);
		Operation op = new InsertOperation(0, "x");
		
		// define mock behavior
		algorithm.receiveRequest(request);
		algorithmCtrl.setReturnValue(op);
		
		// replay
		algorithmCtrl.replay();
		
		// test
		assertEquals(op, wrapper.receiveRequest(request));
	}
	
	public void testGenerateRequest() throws Exception {
		Request request = new JupiterRequest(0, null, null);
		Operation op = new InsertOperation(0, "x");
		
		// define mock behavior
		algorithm.generateRequest(op);
		algorithmCtrl.setReturnValue(request);
		
		// replay
		algorithmCtrl.replay();
		
		// test
		assertEquals(request, wrapper.generateRequest(op));
	}
	
	public void testReceiveCaretUpdateMessage() throws Exception {
		CaretUpdateMessage message = new CaretUpdateMessage(0, new JupiterVectorTime(0, 0), new CaretUpdate(1, 2));
		CaretUpdate update = new CaretUpdate(1, 1);
		
		// define mock behavior
		algorithm.transformIndices(new JupiterVectorTime(0, 0), new int[] { 1, 2 });
		algorithmCtrl.setMatcher(MockControl.ARRAY_MATCHER);
		algorithmCtrl.setReturnValue(new int[] { 1, 1 });
		
		// replay
		algorithmCtrl.replay();
		
		// test
		assertEquals(update, wrapper.receiveCaretUpdateMessage(message));
	}
	
	public void testGenerateCaretUpdateMessage() throws Exception {
		CaretUpdateMessage message = new CaretUpdateMessage(0, new JupiterVectorTime(1, 1), new CaretUpdate(1, 1));
		CaretUpdate update = new CaretUpdate(1, 1);
		
		// define mock behavior
		algorithm.getSiteId();
		algorithmCtrl.setReturnValue(0);
		algorithm.getTimestamp();
		algorithmCtrl.setReturnValue(new JupiterVectorTime(1, 1));
		
		// replay
		algorithmCtrl.replay();
		
		// test
		assertEquals(message, wrapper.generateCaretUpdateMessage(update));
	}
	
	public void tearDown() {
		algorithmCtrl.verify();
	}
	
}
