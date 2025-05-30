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

import ch.iserver.ace.net.impl.DiscoveryCallback;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class DiscoveryManagerFactory {

	private static DiscoveryManagerImpl instance;
	
	public static void init(DiscoveryCallback callback) {
		if (instance == null) {
			ParameterValidator.notNull("callback", callback);
			instance = new DiscoveryManagerImpl(callback);
		}
	}
	
	public static DiscoveryManager getDiscoveryManager() {
		if (instance == null) {
			throw new IllegalStateException("init(callback) must be called first");
		}
		return instance;
	}
	
	public static DiscoveryCallbackAdapter getDiscoveryCallbackAdapter() {
		if (instance == null) {
			throw new IllegalStateException("init(callback) must be called first");
		}
		return instance;
	}
	
}
