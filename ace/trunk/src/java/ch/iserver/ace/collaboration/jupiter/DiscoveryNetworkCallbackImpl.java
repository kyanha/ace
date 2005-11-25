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

import ch.iserver.ace.collaboration.DiscoveryCallback;
import ch.iserver.ace.collaboration.DiscoveryResult;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.net.DiscoveryNetworkCallback;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Default implementation of the DiscoveryNetworkCallback interface. This interface
 * is used in the case of explicit discovery requests.
 */
class DiscoveryNetworkCallbackImpl implements DiscoveryNetworkCallback {
	
	private final UserRegistry registry;
	
	private final DiscoveryCallback callback;
	
	DiscoveryNetworkCallbackImpl(DiscoveryCallback callback, UserRegistry registry) {
		ParameterValidator.notNull("callback", callback);
		ParameterValidator.notNull("registry", registry);
		this.callback = callback;
		this.registry = registry;
	}
	
	private DiscoveryCallback getCallback() {
		return callback;
	}
	
	private UserRegistry getUserRegistry() {
		return registry;
	}
	
	/**
	 * @see ch.iserver.ace.net.DiscoveryNetworkCallback#userDiscoveryFailed(int, java.lang.String)
	 */
	public void userDiscoveryFailed(int status, String message) {
		getCallback().discovered(new DiscoveryResult(status, message));
	}

	/**
	 * @see ch.iserver.ace.net.DiscoveryNetworkCallback#userDiscoverySucceeded(ch.iserver.ace.net.RemoteUserProxy)
	 */
	public void userDiscoverySucceeded(RemoteUserProxy proxy) {
		ParameterValidator.notNull("proxy", proxy);
		RemoteUser user = getUserRegistry().getUser(proxy);
		getCallback().discovered(new DiscoveryResult(user));
	}

}
