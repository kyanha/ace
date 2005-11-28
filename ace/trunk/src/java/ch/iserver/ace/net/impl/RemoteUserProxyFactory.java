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

package ch.iserver.ace.net.impl;

import ch.iserver.ace.net.impl.protocol.RequestFilter;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class RemoteUserProxyFactory {

	private RequestFilter filter;
	
	private static RemoteUserProxyFactory instance;
	
	private RemoteUserProxyFactory(RequestFilter filter) {
		this.filter = filter;
	}
	
	//TODO: since we no longer need the filter chain in RemoteUserProxyImpl,
	//the RemoteUserProxyFactory could be considered as obsolete i.e. could be removed
	public static void init(RequestFilter filter) {
		ParameterValidator.notNull("filter", filter);
		instance = new RemoteUserProxyFactory(filter);
	}
	
	public static RemoteUserProxyFactory getInstance() {
		if (instance == null) {
			throw new IllegalStateException("instance has not been initialized");
		}
		return instance;
	}
	
	public RemoteUserProxyExt createProxy(String id, MutableUserDetails details) {
		return new RemoteUserProxyImpl(id, details);
	}
	
}
