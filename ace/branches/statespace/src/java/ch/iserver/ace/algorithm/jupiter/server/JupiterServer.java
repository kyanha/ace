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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.text.GOTOInclusionTransformation;
import ch.iserver.ace.util.SynchronizedQueue;

/**
 * JupiterServer is used for the client management on the server. Clients can be
 * added and removed.
 * 
 */
public class JupiterServer {

	private static final Logger LOG = Logger.getLogger(JupiterServer.class);

	/**
	 * counter for site id assignment.
	 */
	private int siteIdCounter;

	/**
	 * the request serializer.
	 * 
	 * @see RequestSerializer
	 */
	private RequestSerializer serializer;

	/**
	 * a queue containing the request forwarders.
	 * 
	 * @see RequestForwarder
	 */
	private SynchronizedQueue requestForwardQueue;

	/**
	 * a collection with siteId to request forwarder mappings.
	 * 
	 * @see RequestForwarder
	 */
	private Map requestForwarders;

	/**
	 * Class constructor.
	 */
	public JupiterServer() {
		siteIdCounter = 0;
		requestForwardQueue = new SynchronizedQueue();
		requestForwarders = new HashMap();
		serializer = new RequestSerializer(requestForwardQueue);
		serializer.start();
	}

	/**
	 * Adds a new client to this Jupiter server instance.
	 * 
	 * @param net
	 *            the net service implementation
	 * @return the client proxy just created.
	 */
	public synchronized ClientProxy addClient(NetService net) {
		// create client proxy
		Jupiter algo = new Jupiter(new GOTOInclusionTransformation(),
				new OperationExtractDocumentModel(), ++siteIdCounter, false);
		ClientProxy client = new DefaultClientProxy(siteIdCounter, net, algo,
				requestForwardQueue);

		// add client proxy to request serializer
		SynchronizedQueue outgoingQueue = new SynchronizedQueue();
		serializer.addClientProxy(client, outgoingQueue);

		// create and start request forwarder
		RequestForwarder forwarder = new RequestForwarder(outgoingQueue, 
													   client);
		requestForwarders.put(new Integer(siteIdCounter), forwarder);
		forwarder.start();
		// LOG.debug("addClient #"+siteIdCounter);
		return client;
	}

	/**
	 * Removes a client given its siteId. The client is immediately stopped from
	 * forwarding any more requests. The client is removed not until all
	 * requests waiting at the server at the time of removal have been
	 * processed.
	 * 
	 * @param siteId
	 *            the siteId of the client to remove
	 */
	public void removeClient(int siteId) {
		Integer id = new Integer(siteId);
		DefaultClientProxy proxy = (DefaultClientProxy) serializer
				.getClientProxies().get(id);
		if (proxy != null) {
			proxy.closeNetServiceConnection();
			((RequestForwarder) requestForwarders.remove(id)).shutdown();
			serializer.removeClientProxy(siteId);
			LOG.debug("removeClient #" + siteId);
		}
	}

	/**
	 * Returns a map with all the request forwarders.
	 * 
	 * @return the request forwarders
	 * @see RequestForwarder
	 */
	Map getRequestForwarders() {
		return requestForwarders;
	}

	/**
	 * Originaly intended for test use. Returns the client count.
	 * 
	 * @return the number of clients
	 */
	int getClientCount() {
		return serializer.getClientProxies().size();
	}

	/**
	 * Originaly intended for test use. Returns to RequestSerializer.
	 * 
	 * @return the request serializer
	 * @see RequestSerializer
	 */
	RequestSerializer getRequestSerializer() {
		return serializer;
	}

}
