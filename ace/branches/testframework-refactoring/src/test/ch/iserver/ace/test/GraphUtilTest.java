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

package ch.iserver.ace.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * JUnit test case for GraphUtil class.
 */
public class GraphUtilTest extends TestCase {

	public void testSortTopological() {
		StartNode s1 = new StartNode("1", "", 0);
		StartNode s2 = new StartNode("2", "", 1);
		DoNode g1 = new DoNode("1", "", null);
		DoNode g2 = new DoNode("2", "", null);
		ReceptionNode r1 = new SimpleReceptionNode("1", "1");
		ReceptionNode r2 = new SimpleReceptionNode("2", "2");
		EndNode e1 = new EndNode("1", "");
		EndNode e2 = new EndNode("2", "");
		
		s1.setLocalSuccessor(g1);
		s2.setLocalSuccessor(g2);
		g1.setLocalSuccessor(r1);
		g1.addRemoteSuccessor(r2);
		g2.setLocalSuccessor(r2);
		g2.addRemoteSuccessor(r1);
		r1.setLocalSuccessor(e1);
		r2.setLocalSuccessor(e2);
		
		List nodes = new ArrayList();
		nodes.add(e1);
		nodes.add(e2);
		nodes.add(r1);
		nodes.add(g1);
		nodes.add(r2);
		nodes.add(s1);
		nodes.add(s2);
		nodes.add(g2);
		
		List sorted = GraphUtil.topologicalSort(nodes);
		assertEquals(8, sorted.size());
	}
	
}
