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

import ch.iserver.ace.Operation;

/**
 * 
 */
public class DefaultRequestEngine implements RequestEngine {

	/**
	 * The queue handler which intermediates between the request engine and the
	 * algorithm.
	 */
	private QueueHandler queueHandler;

	private AlgorithmCommandQueue queue;
	
	/**
	 * 
	 * @param algo
	 *            the algorithm implementation
	 */
	public DefaultRequestEngine(Algorithm algo, AlgorithmCommandQueue queue) {
		if (algo == null) {
			throw new IllegalArgumentException("algo cannot be null");
		}
		if (queue == null) {
			throw new IllegalArgumentException("queue cannot be null");
		}
		this.queue = queue;
	}

	/**
	 * {@inheritDoc}
	 */
	public void generateRequest(Operation op) {
		if (op == null) {
			throw new IllegalArgumentException("op cannot be null");
		}
		queue.addCommand(new DoCommand(op));
	}
	
	public void undo() {
		queue.addCommand(new UndoCommand());
	}
	
	public void redo() {
		queue.addCommand(new RedoCommand());
	}

	/**
	 * {@inheritDoc}
	 */
	public void receiveRequest(Request req) {
		if (req == null) {
			throw new IllegalArgumentException("req cannot be null");
		}
		queue.addCommand(new ReceiveRequestCommand(req));
	}

	/**
	 * @return Returns the queueHandler.
	 */
	public QueueHandler getQueueHandler() {
		return queueHandler;
	}

	/**
	 * @param queueHandler
	 *            The queueHandler to set.
	 */
	public void setQueueHandler(QueueHandler queueHandler) {
		this.queueHandler = queueHandler;
	}
	
}
