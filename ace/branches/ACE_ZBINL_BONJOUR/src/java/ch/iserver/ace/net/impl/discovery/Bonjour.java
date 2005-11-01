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
import ch.iserver.ace.net.impl.Discovery;
import ch.iserver.ace.net.impl.DiscoveryCallback;

public class Bonjour implements Discovery {
	
	public static final String KEY_REGISTRATION_TYPE = "registration.type";
	public static final String KEY_TXT_VERSION = "txt.version";
	public static final String KEY_PROTOCOL_VERSION = "protocol.version";
	public static final String KEY_USER = "user.name";
	public static final String KEY_USERID = "user.id";

	private Properties props;
	private UserRegistrationImpl registration;
	private PeerDiscoveryImpl peerDiscovery;
	
	public Bonjour(Properties props) {
		this.props = props;
		registration  = new UserRegistrationImpl();
		peerDiscovery = new PeerDiscoveryImpl();
	}
	
	/**
	 * @inheritDoc
	 */
	public void execute() {
		registration.register(props);
		peerDiscovery.browse(props);
	}
	
	public void abort() {
		registration.stop();
		peerDiscovery.stop();
	}
	
	/**
	 * @inheritDoc
	 */
	public void setDiscoveryCallback(DiscoveryCallback callback) {
		peerDiscovery.setDiscoveryCallback(callback);
	}
	
	/**
	 * @inheritDoc
	 */
	public void setUserId(String uuid) {
		props.put(KEY_USERID, uuid);
	}
	
	/**
	 * @inheritDoc
	 */
	public void setUserDetails(UserDetails details) {
		props.put(KEY_USER, details.getUsername());
		if (registration.isRegistered()) {
			registration.update(details);
		}
	}
	

}
