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

import ch.iserver.ace.test.jupiter.NWayTestCase;

/**
 *
 */
public class JupiterUndoScenarioTest extends NWayTestCase {

	/**
	 * Tests some basic undo puzzle.
	 */
	public void testUndo1() throws Exception {
		execute("/test/jupiter/undo-1.xml");
	}
	
	/**
	 * This method tests the correct execution of example 3 and 
	 * figure 3, respectively, in paper 'group99-final.pdf'.
	 */
	public void testUndo2() throws Exception {
		execute("/test/jupiter/undo-2.xml");
	}
	
	/**
	 * This method tests the correct execution of example 4 and 
	 * figure 4, respectively, in paper 'group99-final.pdf'.
	 */
	public void testUndo3() throws Exception {
		execute("/test/jupiter/undo-3.xml");
	}
	
	/**
	 * This is the same test as {@link #testUndo3()} but
	 * has a different order in which the operations arrive at the server.
	 */
	public void testUndo3a() throws Exception {
		execute("/test/jupiter/undo-3a.xml");
	}

	/**
	 * This is the same test as {@link #testUndo3()} but
	 * has a different order in which the operations arrive at the server.
	 */
	public void testUndo3b() throws Exception {
		execute("/test/jupiter/undo-3b.xml");
	}
		
	/**
	 * This is the same test as {@link testUndo3()} but
	 * makes 2 concurrent undo's instead of one.
	 * site 1: | del 0,b ; ins 0,a ; undo ; undo 	|
	 * site 2: | del 0,b							|
	 */
	public void testUndo4() throws Exception {
		execute("/test/jupiter/undo-4.xml");
	}
	
	/**
	 * This is the same test as {@link #testUndo4()} but
	 * has a different order in which the operations arrive at the server.
	 */
	public void testUndo4a() throws Exception {
		execute("/test/jupiter/undo-4a.xml");
	}

	/**
	 * This is the same test as {@link #testUndo4()} but
	 * has a different order in which the operations arrive at the server.
	 */
	public void testUndo4b() throws Exception {
		execute("/test/jupiter/undo-4b.xml");
	}

	/**
	 * This is the same test as {@link #testUndo4()} but
	 * has a different order in which the operations arrive at the server.
	 */
	public void testUndo4c() throws Exception {
		execute("/test/jupiter/undo-4c.xml");
	}

	/**
	 * This is the same test as {@link #testUndo4()} but
	 * has a different order in which the operations arrive at the server.
	 */
	public void testUndo4d() throws Exception {
		execute("/test/jupiter/undo-4d.xml");
	}

	/**
	 * This test checks if the scenario of figure 6 from the Ressel Undo paper
	 * is handled correctly by the Jupiter Undo implementation.
	 */
	public void testUndo5() throws Exception {
		execute("/test/jupiter/undo-5.xml");
	}

	/**
	 * Tests a basic undo-redo puzzle (this is equivalent to 
	 * test in JupiterCharUndoTest.test01).
	 */
	public void testUndoRedo1() throws Exception {
		execute("/test/jupiter/undo-redo-1.xml");
	}

	/**
	 * Tests a basic undo-redo puzzle (this is equivalent to
	 * test in JupiterCharUndoTest.test02).
	 */
	public void testUndoRedo2() throws Exception {
		execute("/test/jupiter/undo-redo-2.xml");
	}
		
}
