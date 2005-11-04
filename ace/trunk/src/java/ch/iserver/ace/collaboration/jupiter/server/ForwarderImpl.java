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

import java.util.Iterator;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
class ForwarderImpl implements Forwarder {
	
	final ServerLogic serverLogic;
	
	public ForwarderImpl(ServerLogic logic) {
		ParameterValidator.notNull("logic", logic);
		this.serverLogic = logic;
	}
	
	public ServerLogic getServerLogic() {
		return serverLogic;
	}
	
	public Iterator getProxies() {
		return getServerLogic().getParticipantProxies();
	}
		
	public void forward(int participantId, CaretUpdate update) {
		Iterator it = getProxies();
		while (it.hasNext()) {
			ParticipantProxy proxy = (ParticipantProxy) it.next();
			proxy.sendCaretUpdate(participantId, update);
		}
	}
	
	public void forward(int participantId, Operation op) {
		Iterator it = getProxies();
		while (it.hasNext()) {
			ParticipantProxy proxy = (ParticipantProxy) it.next();
			proxy.sendOperation(participantId, op);			
		}
	}
	
	public void forwardParticipantLeft(int participantId, int reason) {
		Iterator it = getProxies();
		while (it.hasNext()) {
			ParticipantProxy proxy = (ParticipantProxy) it.next();
			proxy.sendParticipantLeft(participantId, reason);
		}
	}
	
	public void forwardParticipantJoined(int participantId, RemoteUserProxy user) {
		Iterator it = getProxies();
		while (it.hasNext()) {
			ParticipantProxy proxy = (ParticipantProxy) it.next();
			proxy.sendParticipantJoined(participantId, user);
		}
	}
	
	public void close() {
		Iterator it = getProxies();
		while (it.hasNext()) {
			ParticipantProxy proxy = (ParticipantProxy) it.next();
			proxy.close();
		}
	}
	
}
