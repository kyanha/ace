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

import com.apple.dnssd.RegisterListener;
import com.apple.dnssd.ResolveListener;

/**
 * 
 *
 */
public interface UserRegistration extends RegisterListener, ResolveListener {
	
	/**
	 * Registers the user for dynamic discovery.
	 * 
	 * @param properties the properties used for a successful registration
	 */
	void register(Properties properties);
	
	/**
	 * Determines if the user has been successfully
	 * registered.
	 * 
	 * @return true iff the user is registered
	 */
	boolean isRegistered();
	
	/**
	 * Updates the user's details in the TXT record of this service.
	 * 
	 * @param details the updated UserDetails
	 */
	void updateUserDetails(UserDetails details);
	
	/**
	 * Stops the user's registration.
	 */
	void stop();

}
