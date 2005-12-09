/*
 * $Id:Bonjour.java 1205 2005-11-14 07:57:10Z zbinl $
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

import ch.iserver.ace.ApplicationError;
import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.core.Discovery;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class Bonjour implements Discovery {
	
	private static Logger LOG = Logger.getLogger(Bonjour.class);
	private static Logger APP_LOG = Logger.getLogger("application");
	
	private static final String SERVICE_NAME_SEPARATOR = "._";
	
	//type values for resources and queries
	//constants defined as in nameser.h
	public static final int T_HOST_ADDRESS = 1;
	public static final int T_TXT = 16;
	
	private static String LOCAL_SERVICE_NAME;

	private String username, userid;
	private UserRegistration registration;
	private PeerDiscovery peerDiscovery;
	
	public Bonjour(UserRegistration registration, PeerDiscovery discovery) {
		ParameterValidator.notNull("registration", registration);
		ParameterValidator.notNull("discovery", discovery);
		this.registration  = registration;
		this.peerDiscovery = discovery;
	}
	
	/**
	 * @inheritDoc
	 */
	public void execute() {
		LOG.debug("--> execute()");
		registration.register(username, userid);
		peerDiscovery.browse();
		LOG.debug("<-- execute()");
	}
	
	public void abort() {
		registration.stop();
		peerDiscovery.stop();
	}
	
	/**
	 * @inheritDoc
	 */
	public void setUserId(String uuid) {
		ParameterValidator.notNull("uuid", uuid);
		userid = uuid;
	}
	
	/**
	 * @inheritDoc
	 */
	public void setUserDetails(UserDetails details) {
		ParameterValidator.notNull("details", details);
		username = details.getUsername();
		if (registration.isRegistered()) {
			registration.updateUserDetails(details);
		}
	}
	
	/**
	 * 
	 * @param fullName
	 * @return the service name
	 */
	public static String getServiceName(String fullName) {
		String result = "";
		if ( !(fullName == null || fullName.indexOf(SERVICE_NAME_SEPARATOR) == -1) ) {
			result = fullName.substring(0, fullName.indexOf(SERVICE_NAME_SEPARATOR));
		} else {
			LOG.warn("no service name found for ["+fullName+"]");
		}
		return result;
	}
	
	public static void setLocalServiceName(String name) {
		LOCAL_SERVICE_NAME = name;
	}
	
	public static String getLocalServiceName() {
		return LOCAL_SERVICE_NAME;
	}
	
	
	public static void writeErrorLog(Exception e) {
		APP_LOG.fatal("fatal discovery error ["+e.getMessage()+"]");
		NetworkServiceImpl.getInstance().getCallback().serviceFailure(FailureCodes.DNSSD_FAILURE, e.getMessage(), e);
	}
	

}
