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

package ch.iserver.ace.algorithm;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 */
public class DefaultAlgorithmCommandQueue implements AlgorithmCommandQueue {

	private List queue = new LinkedList();
	
	private int nextLocalIndex;
	
	public DefaultAlgorithmCommandQueue() {
		nextLocalIndex = 0;
	}
	
	/**
	 * @see ch.iserver.ace.algorithm.AlgorithmCommandQueue#getCommand()
	 */
	public synchronized AlgorithmCommand getCommand() throws InterruptedException {
		while (queue.isEmpty()) {
			wait();
		}
		AlgorithmCommand cmd = (AlgorithmCommand) queue.remove(0);
		if (cmd.isLocal()) {
			nextLocalIndex--;
		}
		return cmd;
	}

	/**
	 * @see ch.iserver.ace.algorithm.AlgorithmCommandQueue#addCommand(ch.iserver.ace.algorithm.AlgorithmCommand)
	 */
	public synchronized void addCommand(AlgorithmCommand command) {
		if (command == null) {
			throw new IllegalArgumentException("command cannot be null");
		}
		if (command.isLocal()) {
			queue.add(nextLocalIndex, command);
			nextLocalIndex++;
		} else {
			queue.add(command);
		}
		if (queue.size() == 1) {
			notify();
		}
	}
	
	ListIterator iterator() {
		return queue.listIterator();
	}
	
	int getNextLocalIndex() {
		return nextLocalIndex;
	}

	/**
	 * @see ch.iserver.ace.algorithm.AlgorithmCommandQueue#size()
	 */
	public int size() {
		return queue.size();
	}

	/**
	 * @see ch.iserver.ace.algorithm.AlgorithmCommandQueue#isEmpty()
	 */
	public boolean isEmpty() {
		return queue.isEmpty();
	}

}
