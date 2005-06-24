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
 * Test case for all different types of jupiter scenarios. It
 * uses the extended testframework suitable for the jupiter
 * algorithm (using a central server).
 */
public class JupiterScenarioTest extends NWayTestCase {

	/**
	 * Tests a basic dOPT puzzle.
	 */
	public void testDOptPuzzle() throws Exception {
		execute("/test/jupiter/dopt-puzzle-1.xml");
	}
	
	/**
	 * Tests some basic undo puzzle.
	 */
	public void testUndoPuzzle() throws Exception {
		execute("/test/jupiter/undo.xml");
	}

	/**
	 * Tests some basic redo puzzle.
	 */
	public void testRedoPuzzle() throws Exception {
		execute("/test/jupiter/redo.xml");
	}
	
	/**
	 * Tests some basic puzzle with 3 sites.
	 */
	public void test3Sites() throws Exception {
		execute("/test/jupiter/3-sites.xml");
	}
	
	/**
	 * Tests some basic puzzle with 4 sites.
	 */
	public void test4Sites() throws Exception {
		execute("/test/jupiter/4-sites.xml");
	}
	
}
