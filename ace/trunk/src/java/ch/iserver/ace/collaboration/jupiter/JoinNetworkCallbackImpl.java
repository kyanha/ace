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
import ch.iserver.ace.util.AopUtil;
import ch.iserver.ace.util.LoggingInterceptor;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Default implementation of the JoinNetworkCallback interface. It wraps
 * a JoinCallback from the application layer and passes the callbacks
 * (after some wrapping) back to the application layer.
 */
class JoinNetworkCallbackImpl implements JoinNetworkCallback {
	
	/**
	 * The JoinCallback to be notified about in the application layer.
	 */
	private final JoinCallback callback;
	
	/**
	 * 
	 */
	private final SessionFactory sessionFactory;
	
	/**
	 * Creates a new JoinNetworkCallbackImpl wrapping the given JoinCallback
	 * from the application layer.
	 * 
	 * @param joinCallback the JoinCallback passed in from the application
	 *                     layer
	 */
	JoinNetworkCallbackImpl(JoinCallback joinCallback, SessionFactory factory) {
		ParameterValidator.notNull("joinCallback", joinCallback);
		ParameterValidator.notNull("factory", factory);
		this.callback = joinCallback;
		this.sessionFactory = factory;
	}

	/**
	 * @return the JoinCallback wrapped by this instance
	 */
	private JoinCallback getCallback() {
		return callback;
	}
		
	private SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * @see ch.iserver.ace.net.JoinNetworkCallback#accepted(ch.iserver.ace.net.SessionConnection)
	 */
	public SessionConnectionCallback accepted(SessionConnection connection) {
		ParameterValidator.notNull("connection", connection);
		ConfigurableSession session = getSessionFactory().createSession();
		session.setConnection(connection);
		session.setSessionCallback(getCallback().accepted(session));
		return (SessionConnectionCallback) AopUtil.wrap(
					session, 
					SessionConnectionCallback.class, 
					new LoggingInterceptor(JoinNetworkCallbackImpl.class));
	}
	
	/**
	 * @see ch.iserver.ace.net.JoinNetworkCallback#rejected()
	 */
	public void rejected(int code) {
		getCallback().rejected(code);			
	}
	
}
