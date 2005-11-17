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

package ch.iserver.ace.net.impl.protocol;

import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.ProfileRegistry;
import org.beepcore.beep.core.RequestHandler;
import org.beepcore.beep.core.StartChannelListener;

import ch.iserver.ace.net.impl.discovery.DiscoveryManager;
import ch.iserver.ace.net.impl.discovery.DiscoveryManagerFactory;

/**
 *
 */
public class ProfileRegistryFactory {

	private static ProfileRegistry instance;
	
	public static ProfileRegistry getProfileRegistry() {
		if (instance == null) {
			Deserializer deserializer = DeserializerImpl.getInstance();
			RequestFilter filter = RequestFilterFactory.createServerChain();
			instance = new ProfileRegistry();
			DiscoveryManager manager = DiscoveryManagerFactory.getDiscoveryManager(null);
			RequestHandler handler = new RequestHandlerImpl(deserializer, filter, manager);
			StartChannelListener listener = new StartChannelListenerImpl(handler);
			DefaultProfile profile = new DefaultProfile(listener);
			StartChannelListener channelListener = null;
			try {
			channelListener = profile.init(ProtocolConstants.PROFILE_URI, null);
			} catch (BEEPException be) {}
			instance.addStartChannelListener(ProtocolConstants.PROFILE_URI, channelListener, null);
		}
		return instance;
	}
	
}
