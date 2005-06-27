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
import ch.iserver.ace.util.SynchronizedQueue;

/**
 *
 */
public class DefaultRequestEngine implements RequestEngine {

	/**
	 * The buffer for incoming local operations.
	 */
	private SynchronizedQueue localOperationBuffer;

	/**
	 * The buffer for incoming remote requests.
	 */
	private SynchronizedQueue remoteRequestBuffer;

	/**
	 * The buffer for outgoing requests.
	 */
	private SynchronizedQueue outgoingRequestBuffer;

	/**
	 * The queue handler which intermediates between the request engine
	 * and the algorithm.
	 */
	private QueueHandler queueHandler;

	/**
	 * 
	 * @param algo the algorithm implementation
	 */
	public DefaultRequestEngine(Algorithm algo) {
		if (algo == null) {
			throw new IllegalArgumentException("algo may not be null");
		}
		Object synchObj = new Object();
		localOperationBuffer = new SynchronizedQueue(synchObj);
		remoteRequestBuffer = new SynchronizedQueue(synchObj);
		outgoingRequestBuffer = new SynchronizedQueue();
		queueHandler = new QueueHandler(algo, localOperationBuffer,
				remoteRequestBuffer, outgoingRequestBuffer, synchObj);
		queueHandler.start();
	}

	/**
	 * {@inheritDoc}
	 */
	public void generateRequest(Operation op) {
		if (op != null) {
			localOperationBuffer.add(op);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void receiveRequest(Request req) {
		if (req != null) {
			remoteRequestBuffer.add(req);
		}
	}

	/**
	 * @return Returns the localOperationBuffer.
	 */
	public SynchronizedQueue getLocalOperationBuffer() {
		return localOperationBuffer;
	}

	/**
	 * @param localOperationBuffer The localOperationBuffer to set.
	 */
	public void setLocalOperationBuffer(SynchronizedQueue localOperationBuffer) {
		this.localOperationBuffer = localOperationBuffer;
	}

	/**
	 * @return Returns the outgoingRequestBuffer.
	 */
	public SynchronizedQueue getOutgoingRequestBuffer() {
		return outgoingRequestBuffer;
	}

	/**
	 * @param outgoingRequestBuffer The outgoingRequestBuffer to set.
	 */
	public void setOutgoingRequestBuffer(SynchronizedQueue outgoingRequestBuffer) {
		this.outgoingRequestBuffer = outgoingRequestBuffer;
	}

	/**
	 * @return Returns the remoteRequestBuffer.
	 */
	public SynchronizedQueue getRemoteRequestBuffer() {
		return remoteRequestBuffer;
	}

	/**
	 * @param remoteRequestBuffer The remoteRequestBuffer to set.
	 */
	public void setRemoteRequestBuffer(SynchronizedQueue remoteRequestBuffer) {
		this.remoteRequestBuffer = remoteRequestBuffer;
	}

	/**
	 * @return Returns the queueHandler.
	 */
	public QueueHandler getQueueHandler() {
		return queueHandler;
	}

	/**
	 * @param queueHandler The queueHandler to set.
	 */
	public void setQueueHandler(QueueHandler queueHandler) {
		this.queueHandler = queueHandler;
	}
}
