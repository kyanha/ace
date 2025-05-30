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
package ch.iserver.ace.algorithm.jupiter;

import junit.framework.TestCase;
import ch.iserver.ace.algorithm.TransformationException;
import ch.iserver.ace.text.DeleteOperation;
import ch.iserver.ace.text.InsertOperation;

public class JupiterTest extends TestCase {

	/**
	 * Test method for 'ch.iserver.ace.algorithm.jupiter.Jupiter.receiveAwarenessInformation(AwarenessInformation)'
	 * @throws TransformationException 
	 */
	public void testReceiveAwarenessInformation() throws TransformationException {
		Jupiter jupiter = new Jupiter(false);
		jupiter.generateRequest(new InsertOperation(0, "a"));
		jupiter.generateRequest(new InsertOperation(1, "b"));
		jupiter.generateRequest(new DeleteOperation(0, "a"));
		int[] indices = (int[]) jupiter.transformIndices(new JupiterVectorTime(0, 1), new int[] { 0, 1 });
		assertEquals(0, indices[0]);
		assertEquals(1, indices[1]);
	}

}
