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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.ParticipationEvent;
import ch.iserver.ace.collaboration.jupiter.ParticipantImpl;
import ch.iserver.ace.collaboration.jupiter.RemoteUserImpl;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.RemoteUserProxy;
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
	
	private final HashMap ports = new HashMap();
	
	private final HashMap proxies = new HashMap();
	
	private final HashMap connections = new HashMap();
	
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
	
	public void dispose() throws InterruptedException {
		serializer.kill();
		dispatcher.kill();
	}
	
	private synchronized int nextParticipantId() {
		return ++nextParticipantId;
	}
	
	protected BlockingQueue getSerializerQueue() {
		return serializerQueue;
	}
	
	protected synchronized void addParticipant(ParticipantPort port, ParticipantProxy proxy, ParticipantConnection connection) {
		Integer key = new Integer(port.getParticipantId());
		ports.put(key, port);
		proxies.put(key, proxy);
		connections.put(key, connection);
	}
	
	public synchronized Iterator getParticipantProxies() {
		Map clone = (Map) proxies.clone();
		return clone.values().iterator();
	}
	
	protected synchronized Map getParticipantConnections() {
		return (Map) connections.clone();
	}
	
	private synchronized ParticipantConnection getParticipantConnection(int id) {
		return (ParticipantConnection) connections.get(new Integer(id));
	}
	
	/**
	 * @see ch.iserver.ace.net.DocumentServerLogic#join(ch.iserver.ace.net.ParticipantConnection)
	 */
	public synchronized ParticipantPort join(ParticipantConnection connection) {
		System.err.println("join " + connection);
		Algorithm algorithm = new Jupiter(false);
		int participantId = nextParticipantId();
		connection.setParticipantId(participantId);
		ParticipantPort port = new ParticipantPortImpl(participantId, algorithm, getSerializerQueue());
		ParticipantProxy proxy = new ParticipantProxyImpl(participantId, dispatcherQueue, algorithm, connection);
		notifyOthersAboutJoin(createParticipant(participantId, connection.getUser()));
		addParticipant(port, proxy, connection);
		return port;
	}
	
	protected Participant createParticipant(int participantId, RemoteUserProxy user) {
		return new ParticipantImpl(participantId, new RemoteUserImpl(user));
	}
	
	protected void notifyOthersAboutJoin(Participant participant) {
		Map map = getParticipantConnections();
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			Integer key = (Integer) it.next();
			ParticipantConnection connection = (ParticipantConnection) map.get(key);
			if (key.intValue() != participant.getParticipantId()) {
				connection.sendParticipantJoined(participant);
			}
		}
	}

	protected synchronized void notifyOthersAboutLeave(int participantId) {
		Iterator it = connections.keySet().iterator();
		while (it.hasNext()) {
			Integer key = (Integer) it.next();
			ParticipantConnection connection = (ParticipantConnection) connections.get(key);
			if (key.intValue() != participantId) {
				connection.sendParticipantLeft(participantId, ParticipationEvent.LEFT);
			}
		}
	}

	/**
	 * @see ch.iserver.ace.net.DocumentServerLogic#leave(int)
	 */
	public synchronized void leave(int participantId) {
		System.err.println("leave " + participantId);
		Integer key = new Integer(participantId);
		ParticipantConnection connection = getParticipantConnection(participantId);
		connection.close();
		notifyOthersAboutLeave(participantId);
		connections.remove(key);
		proxies.remove(key);
		ports.remove(key);
	}

}
