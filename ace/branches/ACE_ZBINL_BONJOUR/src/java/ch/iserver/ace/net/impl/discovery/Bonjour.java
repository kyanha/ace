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

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.util.UUID;

public class Bonjour implements Discovery {
	
	public static final String REGISTRATION_TYPE_KEY = "regType";
	public static final String TXT_VERSION_KEY = "txtvers";
	public static final String PROTOCOL_VERSION_KEY = "version";
	public static final String USER_KEY = "name";
	public static final String USERID_KEY = "id";

	private Properties props;
	private DiscoveryCallback callback;
	private BonjourUserRegistration registration;
	private BonjourPeerDiscovery discovery;
	
	public Bonjour() {
		loadConfig();
		registration = new BonjourUserRegistration();
		discovery = new BonjourPeerDiscovery(callback);
	}
	
	/**
	 * Load the properties for Bonjour zeroconf.
	 * TODO: load properties from text config file
	 */
	private void loadConfig() {
		props.put(REGISTRATION_TYPE_KEY, "_ace._tcp");
		props.put(TXT_VERSION_KEY, "1");
		props.put(PROTOCOL_VERSION_KEY, "0.1");
		
		props.put(Discovery.DISCOVERY_PORT_KEY, new Integer(4123));
		//TODO: where is the best place to set the userid?
		props.put(USERID_KEY, UUID.nextUUID());
	}
	
	/**
	 * @inheritDoc
	 */
	public void execute() {
		registration.register(props);
		discovery.browse(props);
	}
	
	/**
	 * @inheritDoc
	 */
	public void setDiscoveryCallback(DiscoveryCallback callback) {
		this.callback = callback;
	}
	
	/**
	 * 
	 */
	public void setUserDetails(UserDetails details) {
		props.put(USER_KEY, details.getUsername());
	}
	
	
	
	

}
