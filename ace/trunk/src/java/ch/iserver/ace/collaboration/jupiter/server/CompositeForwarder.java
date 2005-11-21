/*
 * $Id: ForwarderImpl.java 953 2005-11-04 15:10:56 +0100 (Fri, 04 Nov 2005) sim $
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

import java.util.Iterator;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.ParameterValidator;

/**
 * A Forwarder implementation that forwards requests to several forwarders.
 * The forwarders are retrieved from the {@link ServerLogic}.
 */
class CompositeForwarder implements Forwarder {
	
	/**
	 * The server logic holding the list of participants.
	 */
	private final ServerLogic serverLogic;
	
	/**
	 * Creates a new CompositeForwarder instance.
	 * 
	 * @param logic the server logic
	 */
	public CompositeForwarder(ServerLogic logic) {
		ParameterValidator.notNull("logic", logic);
		this.serverLogic = logic;
	}
	
	/**
	 * @return the server logic
	 */
	public ServerLogic getServerLogic() {
		return serverLogic;
	}
	
	/**
	 * @return gets an iterator over all the forwarders
	 */
	public Iterator getProxies() {
		return getServerLogic().getForwarders();
	}
		
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.Forwarder#sendCaretUpdate(int, ch.iserver.ace.CaretUpdate)
	 */
	public void sendCaretUpdate(int participantId, CaretUpdate update) {
		Iterator it = getProxies();
		while (it.hasNext()) {
			Forwarder proxy = (Forwarder) it.next();
			proxy.sendCaretUpdate(participantId, update);
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.Forwarder#sendOperation(int, ch.iserver.ace.Operation)
	 */
	public void sendOperation(int participantId, Operation op) {
		Iterator it = getProxies();
		while (it.hasNext()) {
			Forwarder proxy = (Forwarder) it.next();
			proxy.sendOperation(participantId, op);			
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.Forwarder#sendParticipantLeft(int, int)
	 */
	public void sendParticipantLeft(int participantId, int reason) {
		Iterator it = getProxies();
		while (it.hasNext()) {
			Forwarder proxy = (Forwarder) it.next();
			proxy.sendParticipantLeft(participantId, reason);
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.Forwarder#sendParticipantJoined(int, ch.iserver.ace.net.RemoteUserProxy)
	 */
	public void sendParticipantJoined(int participantId, RemoteUserProxy user) {
		Iterator it = getProxies();
		while (it.hasNext()) {
			Forwarder proxy = (Forwarder) it.next();
			proxy.sendParticipantJoined(participantId, user);
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.Forwarder#close()
	 */
	public void close() {
		Iterator it = getProxies();
		while (it.hasNext()) {
			Forwarder proxy = (Forwarder) it.next();
			proxy.close();
		}
	}
	
}
