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

package ch.iserver.ace.net.core;

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
	public static final String KEY_PROFILE_URI = "profile.uri";
	

	/************************************/
	/** General properties			 **/
	/************************************/
	public static final String KEY_DEFAULT_ENCODING = "default.encoding";
	
	
	/****************************************/
	/** Keys for retry strategy properties **/
	/****************************************/
	public static final String KEY_INITIAL_WAITINGTIME = "initial.waitingtime";
	public static final String KEY_SUBSEQUENT_WAITINGTIME = "subsequent.waitingtime";
	public static final String KEY_NUMBER_OF_RETRIES = "number.retries";
	
	static Properties properties;
	
	public static String get(String key) {
		if (properties == null) {
			init();
		}
		return (String)properties.get(key);
	}
	
	public static String get(String key, String defaultValue) {
		if (properties == null) {
			init();
		}
		return properties.getProperty(key, defaultValue);
	}
	
	/**
	 * Loads the properties.
	 */
	private static void init() {
		if (properties == null) {
			properties = new Properties();
			try {
				properties.load(NetworkProperties.class.getResourceAsStream("net.properties"));
			} catch (IOException e) {
	    			throw new ApplicationError(e);
			}
		}
	}
	
	public static void main(String args[]) {
		System.out.println(NetworkProperties.get(KEY_DEFAULT_ENCODING, "was not found"));
	}
}
