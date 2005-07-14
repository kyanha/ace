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

import org.apache.log4j.Logger;

import ch.iserver.ace.util.SynchronizedQueue;

/**
 * The QueueHandler class processes operations and requests, respectively, from
 * the buffers and passes them to the algorithm. The returned requests from the
 * algorithm are inserted into a buffer for outgoing requests.
 */
public class QueueHandler extends Thread {

	private static final Logger LOG = Logger.getLogger(QueueHandler.class);


	/**
	 * The algorithm to pass the operations.
	 */
	private Algorithm algorithm;

	private AlgorithmCommandQueue queue;
	
	private RequestConsumer consumer;

	
	/**
	 * Creates a new QueueHandler.
	 * 
	 * @param algo
	 *            the algorithm to pass the operations
	 * @param localOpBuf
	 *            a buffer containing local operations
	 * @param remoteReqBuf
	 *            a buffer containing remote requests
	 * @param outReqBuf
	 *            a buffer for outgoing requests
	 * @param synchObj
	 *            a synchronization object to be used in conjunction with the
	 *            buffers.
	 * @see SynchronizedQueue
	 */
	public QueueHandler(Algorithm algo, AlgorithmCommandQueue queue, RequestConsumer consumer) {
		this.algorithm = algo;
		this.queue = queue;
		this.consumer = consumer;
	}

	/**
	 * The run method of the queue handler. Is responsible for processing the
	 * buffers, i.e. pass the operations and requests respectively to the
	 * algorithm and forward the generated requests to the outgoing queue.
	 */
	public void run() {
		try {
			while (true) {
				AlgorithmCommand command = queue.getCommand();
				command.execute(algorithm, consumer);
			}
		} catch (InterruptedException ie) {
			LOG.fatal(ie);
		} catch (Exception e) {
			LOG.fatal(e);
		}
	}

	/**
	 * @return the algorithm
	 */
	public Algorithm getAlgorithm() {
		return algorithm;
	}
	
}
