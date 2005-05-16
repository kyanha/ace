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

import junit.framework.TestCase;
import ch.iserver.ace.util.SynchronizedQueue;

/**
 *
 */
public class RequestSerializerTest extends TestCase {

	private static final int CLIENT_SITE_ID = 1;
	private RequestSerializer serializer;
	private SynchronizedQueue requestForwardQueue;
	private SynchronizedQueue outgoingQueue;
	private ClientProxy proxy;
	
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		requestForwardQueue = new SynchronizedQueue();
		outgoingQueue = new SynchronizedQueue();
		serializer = new RequestSerializer(requestForwardQueue);
		proxy = new DefaultClientProxy(CLIENT_SITE_ID,null,null,requestForwardQueue);
	}
	public void testAddRemoveClientProxy() throws Exception {
		//add a client proxy
		serializer.addClientProxy(proxy,outgoingQueue);
		assertEquals(1, serializer.getClientProxies().size());
		assertEquals(1, serializer.getOutgoingQueues().size());
		
		//remove a client proxy
		ClientProxy p = serializer.removeClientProxy(CLIENT_SITE_ID);
		assertEquals(proxy, p);
		assertEquals(0, serializer.getClientProxies().size());
		assertEquals(0, serializer.getOutgoingQueues().size());
	}
	
}
