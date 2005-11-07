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

import ch.iserver.ace.collaboration.Invitation;
import ch.iserver.ace.collaboration.RemoteDocument;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.collaboration.Session;
import ch.iserver.ace.collaboration.SessionCallback;
import ch.iserver.ace.net.InvitationProxy;
import ch.iserver.ace.net.SessionConnection;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Default implementation of the Invitation interface. This class wraps a
 * InvitationProxy from the network layer and delegates most method calls
 * to it.
 */
class InvitationImpl implements Invitation {
	
	/**
	 * The wrapped InvitationProxy from the network layer.
	 */
	private final InvitationProxy proxy;
	
	/**
	 * The SessionConnectionDecorator to use.
	 */
	private final SessionConnectionDecorator decorator;
	
	/**
	 * 
	 */
	private final UserRegistry registry;
	
	/**
	 * The RemoteUser that invited the local user.
	 */
	private final RemoteUser inviter;
	
	/**
	 * The RemoteDocument for which the user is invited.
	 */
	private final RemoteDocument document;
	
	/**
	 * Creates a new InvitationImpl object delegating most of the work
	 * to the passed in InvitationProxy.
	 * 
	 * @param proxy the InvitationProxy wrapped by this instance
	 * @param decorator the SessionConnectionDecorator
	 * @param inviter the inviter
	 * @param document the RemoteDocument to which the user is invited
	 */
	InvitationImpl(InvitationProxy proxy, 
					SessionConnectionDecorator decorator, 
					UserRegistry registry, 
					RemoteDocument document) {
		ParameterValidator.notNull("proxy", proxy);
		ParameterValidator.notNull("decorator", decorator);
		ParameterValidator.notNull("registry", registry);
		ParameterValidator.notNull("document", document);
		this.proxy = proxy;
		this.decorator = decorator;
		this.registry = registry;
		this.inviter = registry.getUser(proxy.getInviter().getId());
		this.document = document;
	}
	
	/**
	 * @return the wrapped InvitationProxy instance
	 */
	private InvitationProxy getProxy() {
		return proxy;
	}
	
	private UserRegistry getUserRegistry() {
		return registry;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.Invitation#getInviter()
	 */
	public RemoteUser getInviter() {
		return inviter;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.Invitation#getDocument()
	 */
	public RemoteDocument getDocument() {
		return document;
	}

	/**
	 * @see ch.iserver.ace.collaboration.Invitation#accept(ch.iserver.ace.collaboration.SessionCallback)
	 */
	public Session accept(SessionCallback callback) {
		SessionImpl session = new SessionImpl(callback);
		session.setConnectionDecorator(decorator);
		session.setUserRegistry(getUserRegistry());
		SessionConnection connection = getProxy().accept(session);
		session.setConnection(connection);
		return session;
	}

	/**
	 * @see ch.iserver.ace.collaboration.Invitation#reject()
	 */
	public void reject() {
		getProxy().reject();
	}

}
