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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class GraphUtil {

	private GraphUtil() { }
	
	public static List topologicalSort(Collection nodes) {
		List result = new ArrayList();
		
		Incount counter = new Incount();
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			node.accept(counter);
		}
		List stack = counter.getStartNodes();
		
		while (stack.size() > 0) {
			Node node = (Node) stack.remove(0);
			result.add(node);
			
			Iterator successors = node.getSuccessors().iterator();
			
			while (successors.hasNext()) {
				Node succ = (Node) successors.next();
				int count = counter.decrementIncount(succ);
				if (count == 0) {
					stack.add(succ);
				}
			}
		}
		
		int total = nodes.size();
		if (result.size() != total) {
			throw new ScenarioException("not a dag: " + result.size() + "," + total);
		}

		return result;
	}
	
	private static class Incount implements NodeVisitor {
		private Map map = new HashMap();
		private List start = new ArrayList();
		
		public void visit(StartNode node) {
			map.put(node, new Integer(0));
			start.add(node);
		}
		
		public void visit(GenerationNode node) {
			map.put(node, new Integer(1));
		}
		
		public void visit(ReceptionNode node) {
			map.put(node, new Integer(2));
		}
		
		public void visit(EndNode node) {
			map.put(node, new Integer(1));
		}
		
		public List getStartNodes() {
			return start;
		}
		
		public int getIncount(Node node) {
			return ((Integer) map.get(node)).intValue();
		}
		
		public int decrementIncount(Node node) {
			int incount = getIncount(node) - 1;
			map.put(node, new Integer(incount));
			return incount;
		}
	}
	
}
