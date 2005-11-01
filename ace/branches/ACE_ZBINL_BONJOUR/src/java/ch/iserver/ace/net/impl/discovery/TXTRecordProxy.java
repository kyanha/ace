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

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.log4j.Logger;

import ch.iserver.ace.net.impl.NetworkConstants;

import com.apple.dnssd.TXTRecord;

class TXTRecordProxy {

	//TXT record keys
	static final String TXT_VERSION = "txtvers";
	static final String TXT_USER = "name";
	static final String TXT_USERID = "userid";
	static final String TXT_PROTOCOL_VERSION = "version";
	
	private static Logger LOG = Logger.getLogger(TXTRecordProxy.class);
	
	public static TXTRecord create(final Properties props) {
		TXTRecord r = new TXTRecord();
		r.set(TXT_VERSION, (String)props.get(Bonjour.KEY_TXT_VERSION));
		r.set(TXT_USER, (String)props.get(Bonjour.KEY_USER));
		r.set(TXT_USERID, (String)props.get(Bonjour.KEY_USERID));
		r.set(TXT_PROTOCOL_VERSION, (String)props.get(Bonjour.KEY_PROTOCOL_VERSION));
		return r;
	}
	
	public static String get(final String key, final TXTRecord txt) {
		byte[] data = txt.getValue(key);
		String result = null;
		if (data != null) {
			try {
				result = new String(data, NetworkConstants.DEFAULT_CHARSET);
			} catch (UnsupportedEncodingException uee) {
				//should not happen, since  every implementation of the Java platform 
				//is required to support the default charset
				LOG.error(uee);
			}
		} else {
			LOG.warn("data in TXT record not found.");
		}
		return result;
	}
	
	public static void set(String key, String value, TXTRecord txt) {
		try {
			txt.set(key, value.getBytes(NetworkConstants.DEFAULT_CHARSET));
		} catch (UnsupportedEncodingException uee) {
			//should not happen, since  every implementation of the Java platform 
			//is required to support the default charset
			LOG.error(uee);
		}
	}
	
}
