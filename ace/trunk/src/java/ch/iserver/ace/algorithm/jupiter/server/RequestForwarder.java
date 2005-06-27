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
package ch.iserver.ace.algorithm.jupiter.server;

import org.apache.log4j.Logger;

import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.util.SynchronizedQueue;

/**
 * Forwards requests from a queue to a client proxy.
 */
public class RequestForwarder extends Thread {

	private static final Logger LOG = Logger.getLogger(RequestForwarder.class);

	private ClientProxy proxy;

	private SynchronizedQueue queue;

	private boolean shutdown;

	/**
	 * Creates a new RequestForwarder using the given SynchronizationQueue and
	 * the given ClientProxy.
	 * 
	 * @param queue
	 *            the queue to get the requests from.
	 * @param proxy
	 *            the proxy to pass the request to.
	 * @see SynchronizedQueue
	 * @see ClientProxy
	 */
	public RequestForwarder(SynchronizedQueue queue, ClientProxy proxy) {
		this.queue = queue;
		this.proxy = proxy;
		shutdown = false;
	}

	/**
	 * Runs this RequestForwarder's task of passing requests from the queue to
	 * the client proxy.
	 * 
	 * @see SynchronizedQueue
	 * @see ClientProxy
	 */
	public void run() {
		try {
			while (!shutdown && !isInterrupted()) {
				Request req = (Request) queue.get();
				proxy.sendRequest(req);
			}
		} catch (InterruptedException ie) {
			LOG.warn(ie);
		}
	}

	/**
	 * Shuts down this instance of RequestForwarder.
	 */
	public void shutdown() {
		interrupt();
		shutdown = true;
	}
}
