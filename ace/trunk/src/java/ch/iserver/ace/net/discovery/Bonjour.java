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
package ch.iserver.ace.net.discovery;

import org.apache.log4j.Logger;

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.core.Discovery;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Bonjour is the default implementation for interface {@link ch.iserver.ace.net.core.Discovery}.
 * Class Bonjour itself is an application level class that uses the Bonjour library, also known 
 * as zero-configuration networking (cf. <a href="http://developer.apple.com/networking/bonjour/index.html">
 * http://developer.apple.com/networking/bonjour/index.html</a>).
 * <p>Bonjour, formerly known as Rendezvous, allows for three main technologies:
 * 	<ul>
 * 		<li>	Allocate IP addresses without a DHCP server.
 * 		<li>	Translate between names and addresses without a DNS server.
 * 		<li>	Locate or advertise services without using a directory server.
 * 	</ul> 
 * </p>
 */
public class Bonjour implements Discovery {
	
	private static Logger LOG = Logger.getLogger(Bonjour.class);
	private static Logger APP_LOG = Logger.getLogger("application");
	
	/**
	 * Service name separator.
	 */
	private static final String SERVICE_NAME_SEPARATOR = "._";
	
	
	//type values for resources and queries
	//constants are defined in nameser.h
	//see: http://www.opensource.apple.com/darwinsource/10.3.8/tcpdump-9/tcpdump/nameser.h
	
	/**
	 * Type value for host address.
	 */
	public static final int T_HOST_ADDRESS = 1;
	
	/**
	 * Type value for TXT record.
	 */
	public static final int T_TXT = 16;
	
	/**
	 * Static variable to hold the local service name. This
	 * is per default the user account name on the running host.
	 */
	private static String LOCAL_SERVICE_NAME;

	/**
	 * The local user's name and id.
	 */
	private String username, userid;
	
	/**
	 * The UserRegistration object.
	 */
	private UserRegistration registration;
	
	/**
	 * The PeerDiscovery object.
	 */
	private PeerDiscovery peerDiscovery;
	
	/**
	 * Creates a new Bonjour object.
	 * Note: the arguments may not be null.
	 * 
	 * @param registration	the UserRegistration implementation
	 * @param discovery		the PeerDiscovery implementation
	 * @throws IllegalArgumentException if a parameter is null
	 */
	public Bonjour(UserRegistration registration, PeerDiscovery discovery) {
		ParameterValidator.notNull("registration", registration);
		ParameterValidator.notNull("discovery", discovery);
		this.registration  = registration;
		this.peerDiscovery = discovery;
	}
	
	/**
	 * Extracts the service name from a full service domain name in the form 
	 * <servicename>.<protocol>.<domain>.
	 * 
	 * @param fullName	the full service domain name
	 * @return the service name extracted from the full name
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
	
	/**
	 * Sets the local service name.
	 * 
	 * @param name	the local service name to set
	 */
	public static void setLocalServiceName(String name) {
		LOCAL_SERVICE_NAME = name;
	}
	
	/**
	 * Gets the local service name.
	 * 
	 * @return	the local service name
	 */
	public static String getLocalServiceName() {
		//TODO: pass local service name directly to BrowseListenerImpl and then remove
		//this static variable
		return LOCAL_SERVICE_NAME;
	}
	
	/**
	 * Writes the exception to the application log file and
	 * notifies the upper layer that an unrecoverable Bonjour
	 * failure occured.
	 * 
	 * @param e	the exception that caused the call
	 */
	public static void writeErrorLog(Exception e) {
		APP_LOG.fatal("fatal discovery error ["+e.getMessage()+"]");
		NetworkServiceImpl.getInstance().getCallback().serviceFailure(FailureCodes.DNSSD_FAILURE, e.getMessage(), e);
	}
	
	/**************************************/
	/** methods from interface Discovery **/
	/**************************************/
	
	/**
	 * {@inheritDoc}
	 */
	public void execute() {
		LOG.debug("--> execute()");
		registration.register(username, userid);
		peerDiscovery.browse();
		LOG.debug("<-- execute()");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void abort() {
		registration.stop();
		peerDiscovery.stop();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setUserId(String uuid) {
		ParameterValidator.notNull("uuid", uuid);
		userid = uuid;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setUserDetails(UserDetails details) {
		ParameterValidator.notNull("details", details);
		username = details.getUsername();
		if (registration.isRegistered()) {
			registration.updateUserDetails(details);
		}
	}
}
