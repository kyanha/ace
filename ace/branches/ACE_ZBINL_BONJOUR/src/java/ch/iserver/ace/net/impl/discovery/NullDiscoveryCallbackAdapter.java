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
package ch.iserver.ace.net.impl.discovery;

import org.apache.log4j.Logger;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.impl.DiscoveryCallback;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Null object pattern.
 *
 */
class NullDiscoveryCallbackAdapter extends DiscoveryCallbackAdapter {
	
	private static Logger LOG = Logger.getLogger(NullDiscoveryCallbackAdapter.class);
	
	private static NullDiscoveryCallbackAdapter instance;
	
	private NullDiscoveryCallbackAdapter() {}
	
	public static NullDiscoveryCallbackAdapter getInstance() {
		if (instance == null) {
			instance = new NullDiscoveryCallbackAdapter();
		}
		return instance;
	}

	public void userDiscovered(RemoteUserProxyExt user) {
		ParameterValidator.notNull("user", user);
		LOG.warn("called for "+user.getUserDetails().getUsername());
	}

	public void userDiscarded(String id) {
		ParameterValidator.notNull("id", id);
		LOG.warn("called for [id="+id+"]");
	}

	public void userDetailsChanged(String id, UserDetails details) {
		ParameterValidator.notNull("userId", id);
		ParameterValidator.notNull("details", details);
		LOG.warn("called for [id="+id+", username="+details.getUsername()+"]");
		
	}

}
