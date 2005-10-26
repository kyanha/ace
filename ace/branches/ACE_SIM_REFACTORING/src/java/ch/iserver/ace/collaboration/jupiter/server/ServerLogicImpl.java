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

package ch.iserver.ace.collaboration.jupiter.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.util.BlockingQueue;
import ch.iserver.ace.util.LinkedBlockingQueue;
import ch.iserver.ace.util.Lock;

/**
 *
 */
class ServerLogicImpl implements ServerLogic, DocumentServerLogic {
	
	private int nextParticipantId;
	
	private final Forwarder forwarder;
	
	private final Serializer serializer;
	
	private final BlockingQueue serializerQueue;
	
	private final Map ports = new HashMap();
	
	private final Map proxies = new HashMap();
	
	private final Map connections = new HashMap();
	
	private final BlockingQueue dispatcherQueue;
	
	private final Dispatcher dispatcher;
	
	public ServerLogicImpl(Lock lock) {
		this.nextParticipantId = 0;
		this.forwarder = new ForwarderImpl(this);
		
		this.serializerQueue = new LinkedBlockingQueue();
		this.serializer = new Serializer(serializerQueue, lock, forwarder);
		
		this.dispatcherQueue = new LinkedBlockingQueue();
		this.dispatcher = new Dispatcher(dispatcherQueue);
	}
	
	public void start() {
		serializer.start();
		dispatcher.start();
	}
	
	private synchronized int nextParticipantId() {
		return ++nextParticipantId;
	}
	
	protected BlockingQueue getSerializerQueue() {
		return serializerQueue;
	}
	
	protected void addParticipant(ParticipantPort port, ParticipantProxy proxy, ParticipantConnection connection) {
		Integer key = new Integer(port.getParticipantId());
		ports.put(key, port);
		proxies.put(key, proxy);
		connections.put(key, connection);
	}
	
	public Iterator getParticipantProxies() {
		Collection result;
		synchronized (proxies) {
			Map clone = new HashMap(proxies);
			result = clone.values();
		}
		return result.iterator();
	}
	
	/**
	 * @see ch.iserver.ace.net.DocumentServerLogic#join(ch.iserver.ace.net.ParticipantConnection)
	 */
	public ParticipantPort join(ParticipantConnection connection) {
		Algorithm algorithm = new Jupiter(false);
		int participantId = nextParticipantId();
		ParticipantPort port = new ParticipantPortImpl(participantId, algorithm, getSerializerQueue());
		ParticipantProxy proxy = new ParticipantProxyImpl(participantId, dispatcherQueue, algorithm, connection);
		addParticipant(port, proxy, connection);
		return port;
	}

	/**
	 * @see ch.iserver.ace.net.DocumentServerLogic#leave(int)
	 */
	public void leave(int participantId) {
		// TODO: implement leave
	}

}
