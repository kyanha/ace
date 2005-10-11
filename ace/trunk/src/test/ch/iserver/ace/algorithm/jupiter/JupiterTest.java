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

import ch.iserver.ace.algorithm.CursorUpdate;
import ch.iserver.ace.test.TestDocumentModel;
import ch.iserver.ace.text.DeleteOperation;
import ch.iserver.ace.text.GOTOInclusionTransformation;
import ch.iserver.ace.text.InsertOperation;
import junit.framework.TestCase;

public class JupiterTest extends TestCase {

	/**
	 * Test method for 'ch.iserver.ace.algorithm.jupiter.Jupiter.receiveAwarenessInformation(AwarenessInformation)'
	 */
	public void testReceiveAwarenessInformation() {
		Jupiter jupiter = new Jupiter(new GOTOInclusionTransformation(), new TestDocumentModel(""), 0, false);
		jupiter.generateRequest(new InsertOperation(0, "a"));
		jupiter.generateRequest(new InsertOperation(1, "b"));
		jupiter.generateRequest(new DeleteOperation(0, "a"));
		CursorUpdate update = (CursorUpdate) jupiter.receiveAwarenessInformation(new CursorUpdate(new JupiterVectorTime(0, 1), 0, 1));
		assertEquals(0, update.getDot());
		assertEquals(1, update.getMark());
	}

}
