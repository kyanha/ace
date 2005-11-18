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

import java.io.IOException;
import java.util.Properties;

import ch.iserver.ace.ApplicationError;

/**
 *
 */
public class NetworkProperties {

	/****************************/
	/** Protocol property keys **/
	/****************************/
	public static final String KEY_PROTOCOL_PORT = "protocol.port";
	public static final String KEY_PROTOCOL_VERSION = "protocol.version";
	
	
	/************************************/
	/** Bonjour zeroconf property keys **/
	/************************************/
	public static final String KEY_REGISTRATION_TYPE = "registration.type";
	public static final String KEY_TXT_VERSION = "txt.version";
	public static final String KEY_USER = "user.name";
	public static final String KEY_USERID = "user.id";
	
	Properties properties;
	
	public NetworkProperties() {}
	
	/**
	 * Loads the properties for Bonjour zeroconf.
	 */
	public void init() {
		if (properties == null) {
			properties = new Properties();
			try {
				properties.load(getClass().getResourceAsStream("net.properties"));
			} catch (IOException e) {
	    			throw new ApplicationError(e);
			}
		}
	}
	
	public String get(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
}
