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

package ch.iserver.ace.collaboration.jupiter;

import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.PublishedSessionCallback;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class RequestCommand extends LockingCommand {

	private final Participant participant;
	
	private final Request request;
	
	public RequestCommand(Lock lock, AlgorithmWrapper algorithm, Participant participant, Request request) {
		super(lock, algorithm);
		ParameterValidator.notNull("participant", participant);
		ParameterValidator.notNull("request", request);
		this.participant = participant;
		this.request = request;
	}

	protected Participant getParticipant() {
		return participant;
	}
	
	protected Request getRequest() {
		return request;
	}
	
	protected void doWork(PublishedSessionCallback callback) {
		Operation op = getAlgorithm().receiveRequest(getRequest());
		callback.receiveOperation(getParticipant(), op);
	}
	
}
