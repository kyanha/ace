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

import ch.iserver.ace.net.impl.DiscoveryCallback;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.QueryListener;
import com.apple.dnssd.ResolveListener;

/**
 * 
 *
 */
public interface PeerDiscovery extends BrowseListener, ResolveListener,
		QueryListener {

	/**
	 * Browses the local network for other services of the same type, i.e.
	 * other users.
	 * 
	 * @param properties the properties for the DNSSD call.
	 * @see com.apple.dnssd.DNSSD
	 */
	void browse(Properties properties);
	
	/**
	 * Sets the discovery callback adapter.
	 * 
	 * @param adapter the discovery callback adapter to set
	 * @see DiscoveryCallback
	 */
	void setDiscoveryCallbackAdapter(DiscoveryCallbackAdapter adapter);
	
	/**
	 * Stops the Bonjour peer discovery process.
	 */
	void stop();
}
