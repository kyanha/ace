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

import ch.iserver.ace.collaboration.JoinCallback;
import ch.iserver.ace.net.JoinNetworkCallback;
import ch.iserver.ace.net.SessionConnection;
import ch.iserver.ace.net.SessionConnectionCallback;
import ch.iserver.ace.util.ParameterValidator;

/**
 * 
 *
 */
class JoinNetworkCallbackImpl implements JoinNetworkCallback {
	
	private final JoinCallback callback;
	
	public JoinNetworkCallbackImpl(JoinCallback joinCallback) {
		ParameterValidator.notNull("joinCallback", joinCallback);
		this.callback = joinCallback;
	}

	protected JoinCallback getCallback() {
		return callback;
	}

	public SessionConnectionCallback accepted(SessionConnection connection) {
		SessionImpl session = new SessionImpl();
		session.setConnection(connection);
		session.setSessionCallback(getCallback().accepted(session));
		return session;
	}
	
	public void rejected() {
		getCallback().rejected();			
	}
	
}
