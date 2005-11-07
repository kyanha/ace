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

import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.collaboration.Session;
import ch.iserver.ace.net.SessionConnection;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class SessionConnectionWrapper implements SessionConnection {
	
	private final SessionConnection target;
	
	private final SessionConnectionFailureHandler handler;
	
	public SessionConnectionWrapper(SessionConnection target, SessionConnectionFailureHandler handler) {
		ParameterValidator.notNull("target", target);
		ParameterValidator.notNull("handler", handler);
		this.target = target;
		this.handler = handler;
	}
	
	protected SessionConnectionFailureHandler getFailureHandler() {
		return handler;
	}
	
	/**
	 * @see ch.iserver.ace.net.SessionConnection#getParticipantId()
	 */
	public int getParticipantId() {
		return target.getParticipantId();
	}

	/**
	 * @see ch.iserver.ace.net.SessionConnection#isAlive()
	 */
	public boolean isAlive() {
		return target.isAlive();
	}

	/**
	 * @see ch.iserver.ace.net.SessionConnection#leave()
	 */
	public void leave() {
		try {
			target.leave();
		} catch (Exception e) {
			// TODO: correct exception type
			getFailureHandler().handleFailure(Session.LEAVE_FAILED, e);
		}
	}

	/**
	 * @see ch.iserver.ace.net.SessionConnection#sendRequest(ch.iserver.ace.algorithm.Request)
	 */
	public void sendRequest(Request request) {
		try {
			target.sendRequest(request);			
		} catch (Exception e) {
			// TODO: correct exception type
			getFailureHandler().handleFailure(Session.SEND_FAILED, e);
		}
	}

	/**
	 * @see ch.iserver.ace.net.SessionConnection#sendCaretUpdate(ch.iserver.ace.algorithm.CaretUpdateMessage)
	 */
	public void sendCaretUpdate(CaretUpdateMessage message) {
		try {
			target.sendCaretUpdate(message);
		} catch (Exception e) {
			// TODO: correct exception type
			getFailureHandler().handleFailure(Session.SEND_FAILED, e);
		}
	}

}
