/*
 * $Id:TXTRecordProxy.java 1205 2005-11-14 07:57:10Z zbinl $
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
package ch.iserver.ace.net.discovery;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import ch.iserver.ace.net.core.NetworkProperties;
import ch.iserver.ace.util.ParameterValidator;

import com.apple.dnssd.TXTRecord;

/**
 * TXTRecordProxy is used to construct the data that is stored in the TXT record
 * for each user. TXTRecordProxy therefore writes and reads the TXT record.
 * This currently includes the following information:
 * <ul>
 * 	<li>user name
 * 	<li>user id
 *  <li>TXT protocol version
 *  <li>ACE communication protocol version
 * </ul>
 * 
 * The protocol version information is necessary for the case when the 
 * protocol changes. Errors from incompatible protocols could then be
 * avoided or appropriately handled. Therefore if the TXT or communication protocol
 * changes, the respective version number should also be changed.
 *
 */
class TXTRecordProxy {

	//TXT record keys
	
	/**
	 * TXT record key for the TXT version
	 */
	static final String TXT_VERSION = "txtvers";
	
	/**
	 * TXT record key for user name
	 */
	static final String TXT_USER = "name";
	
	/**
	 * TXT record key for user id
	 */
	static final String TXT_USERID = "userid";
	
	/**
	 * TXT record key for protocol version
	 */
	static final String TXT_PROTOCOL_VERSION = "version";
	
	private static Logger LOG = Logger.getLogger(TXTRecordProxy.class);
	
	/**
	 * Creates a new TXT record for the given user.
	 * 
	 * @param username	the user name
	 * @param userid		the user id
	 * @return the user specific TXTRecord
	 */
	public static TXTRecord create(String username, String userid) {
		LOG.debug("create("+username+", "+userid+")");
		TXTRecord r = new TXTRecord();
		r.set(TXT_VERSION, NetworkProperties.get(NetworkProperties.KEY_TXT_VERSION));
		r.set(TXT_USER, username);
		r.set(TXT_USERID, userid);
		r.set(TXT_PROTOCOL_VERSION, NetworkProperties.get(NetworkProperties.KEY_PROTOCOL_VERSION));
		return r;
	}
	
	/**
	 * Gets the value of the <code>key</code> in the <code>txt</code> TXTRecord.
	 * 
	 * @param key	the key in the TXT record
	 * @param txt	the TXT record to get the value from
	 * @return	the value or null if no value was found
	 */
	public static String get(final String key, final TXTRecord txt) {
		ParameterValidator.notNull("key", key);
		ParameterValidator.notNull("txtrecord", txt);
		byte[] data = txt.getValue(key);
		String result = null;
		if (data != null) {
			try {
				result = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
			} catch (UnsupportedEncodingException uee) {
				//should not happen, since  every implementation of the Java platform 
				//is required to support the default charset
				LOG.error(uee);
			}
		} else {
			LOG.warn("value in TXT record for ["+key+"] not found.");
		}
		return result;
	}
	
	/**
	 * Sets the <code>key-value</code> pair in the passed in TXT record. 
	 * 
	 * @param key		the key
	 * @param value		the value
	 * @param txt		the TXT record to add the key-value pair to
	 */
	public static void set(final String key, final String value, TXTRecord txt) {
		ParameterValidator.notNull("key", key);
		ParameterValidator.notNull("value", value);
		ParameterValidator.notNull("txtrecord", txt);
		try {
			txt.set(key, value.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING)));
		} catch (UnsupportedEncodingException uee) {
			//should not happen, since  every implementation of the Java platform 
			//is required to support the default charset
			LOG.error(uee);
		}
	}
	
}
