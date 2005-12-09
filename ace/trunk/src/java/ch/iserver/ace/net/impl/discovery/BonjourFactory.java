/*
 * $Id:BonjourFactory.java 1205 2005-11-14 07:57:10Z zbinl $
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

import ch.iserver.ace.net.core.Discovery;
import ch.iserver.ace.net.core.DiscoveryCallback;
import ch.iserver.ace.net.core.DiscoveryFactory;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.ResolveListener;

public class BonjourFactory extends DiscoveryFactory {
	
	private UserRegistration registration;
	private PeerDiscovery discovery;
	
	public void init(UserRegistration registration, PeerDiscovery discovery) {
		this.registration = registration;
		this.discovery = discovery;
	}
	public Discovery createDiscovery(DiscoveryCallback callback) {
		UserRegistration actualRegistration = (registration != null) ? registration : new UserRegistrationImpl();
		PeerDiscovery actualDiscovery = (discovery != null) ? discovery : createPeerDiscovery(callback);
		Bonjour b = new Bonjour(actualRegistration, actualDiscovery);
		Bonjour.setLocalServiceName(System.getProperty("user.name"));
		return b;
	}
	
	private PeerDiscovery createPeerDiscovery(DiscoveryCallback callback) {
		//TODO: load classes via spring framework?
		DiscoveryManagerFactory.init(callback);
		DiscoveryCallbackAdapter adapter = DiscoveryManagerFactory.getDiscoveryCallbackAdapter();
		AbstractQueryListener ipListener = new IPQueryListener(adapter);
		AbstractQueryListener txtListener = new TXTQueryListener(adapter);
		ResolveListener resolveListener = new ResolveListenerImpl(adapter, ipListener, txtListener);
		BrowseListener browseListener = new BrowseListenerImpl(adapter, resolveListener);
		PeerDiscovery discovery = new PeerDiscoveryImpl(browseListener);
		return discovery;
	}
}
