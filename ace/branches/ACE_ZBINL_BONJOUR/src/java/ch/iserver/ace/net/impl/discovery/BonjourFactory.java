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

import java.util.Properties;

import ch.iserver.ace.net.impl.Discovery;
import ch.iserver.ace.net.impl.DiscoveryFactory;

public class BonjourFactory extends DiscoveryFactory {

	public Discovery createDiscovery() {
		Properties props = loadConfig();
		Bonjour b = new Bonjour(props);
		return b;
	}
	
	/**
	 * Load the properties for Bonjour zeroconf.
	 * TODO: load properties from text config file
	 */
	private Properties loadConfig() {
		Properties props = new Properties();
		props.put(Bonjour.KEY_REGISTRATION_TYPE, "_ace._tcp");
		props.put(Bonjour.KEY_TXT_VERSION, "1");
		props.put(Bonjour.KEY_PROTOCOL_VERSION, "0.1");
		
		props.put(Discovery.KEY_DISCOVERY_PORT, new Integer(4123));
		return props;
	}

}
