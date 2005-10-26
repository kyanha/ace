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

import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
class RequestDispatcherCommand extends AbstractDispatcherCommand {

	private final Request request;
	
	public RequestDispatcherCommand(ParticipantConnection connection, int participantId, Request request) {
		super(connection, participantId);
		ParameterValidator.notNull("request", request);
		this.request = request;
	}
	
	protected Request getRequest() {
		return request;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.DispatcherCommand#execute()
	 */
	public void execute() {
		getConnection().sendRequest(getParticipantId(), getRequest());
	}

}
