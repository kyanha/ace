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

package ch.iserver.ace.net.impl.discovery.dnssd;

import java.util.Properties;

/**
 *
 */
public class RetryStrategyFactory {

	/**
	 * Creates a {@link RetryStrategy}.
	 * 
	 * @return a retry strategy
	 */
	public static RetryStrategy create() {
		Properties props = loadConfig();
		return new AdditiveWaitRetryStrategy(
				Integer.parseInt((String)props.get(AdditiveWaitRetryStrategy.KEY_NUMBER_OF_RETRIES)),
				Long.parseLong((String)props.get(AdditiveWaitRetryStrategy.KEY_INITIAL_WAITINGTIME)), 
				Long.parseLong((String)props.get(AdditiveWaitRetryStrategy.KEY_SUBSEQUENT_WAITINGTIME)));
	}
	
	/**
	 * Loads the properties for Bonjour zeroconf.
	 */
	//TODO: the properties are loaded a second time -> improve
	private static Properties loadConfig() {
	    Properties properties = new Properties();
	    properties.put(AdditiveWaitRetryStrategy.KEY_NUMBER_OF_RETRIES, "3");
	    properties.put(AdditiveWaitRetryStrategy.KEY_INITIAL_WAITINGTIME, "2000");
	    properties.put(AdditiveWaitRetryStrategy.KEY_SUBSEQUENT_WAITINGTIME, "4000");
//	    try {
//	        properties.load(getResourceAsStream("zeroconf.properties"));
//	    } catch (IOException e) {
//	    		throw new ApplicationError(e);
//	    }
		return properties;
	}
	
}
