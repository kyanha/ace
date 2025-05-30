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

import ch.iserver.ace.test.jupiter.TwoWayTestCase;


/**
 * Test class for testing the jupiter algorithm implementation
 * with the test framework.
 */
public class TwoWayScenarioTest extends TwoWayTestCase {
	
	public void testPuzzleOne() throws Exception {
		execute("/test/puzzle.xml");
	}
	
	public void testPuzzleTwo() throws Exception {
		execute("/test/dopt-puzzle-1.xml");
	}
	
	public void testPuzzleThree() throws Exception {
		execute("/test/ecscw03-fig3.xml");
	}
	
	public void testInsertInsert() throws Exception {
		execute("/test/insert-insert.xml");
	}
	
	public void testMultiStepPathDivergence() throws Exception {
		execute("/test/multistep-path-divergence.xml");
	}
	
	public void testMultiStepPathDivergenceInverse() throws Exception {
		execute("/test/multistep-path-divergence-inverse.xml");
	}
	
	public void testMultiStepPathDivergenceInverse_4_2() throws Exception {
		execute("/test/multistep-path-divergence-4-2.xml");
	}
	
	public void testPartialConcurrency() throws Exception {
		execute("/test/partial-concurrency.xml");
	}
	
	public void testDeleteDelete() throws Exception {
		execute("/test/delete-delete-1.xml");
	}
	
	public void testSplitOperation() throws Exception {
		execute("/test/splitoperation-1.xml");
	}
	
	public void testUndo() throws Exception {
		execute("/test/undo.xml");
	}
	
	public void testRedo() throws Exception {
		execute("/test/redo.xml");
	}
	
}
