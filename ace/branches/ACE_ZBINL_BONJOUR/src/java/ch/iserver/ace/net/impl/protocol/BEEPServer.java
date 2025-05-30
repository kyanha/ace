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

import org.apache.log4j.Logger;
import org.beepcore.beep.core.ProfileRegistry;
import org.beepcore.beep.core.StartChannelListener;
import org.beepcore.beep.profile.Profile;
import org.beepcore.beep.transport.tcp.TCPSessionCreator;

/**
 *
 */
public class BEEPServer extends Thread {

	private static Logger LOG = Logger.getLogger(BEEPServer.class);
	
	private ProfileRegistry registry;
	private Profile profile;
	
	private boolean terminate;
	
	public BEEPServer(Profile profile) {
		this.profile = profile;
		this.registry = new ProfileRegistry();
		terminate = false;
	}
	
	
	public void run() {
		String exitStatus = "normal";
		try {
			StartChannelListener listener = profile.init(ProtocolConstants.PROFILE_URI, null);
			registry.addStartChannelListener(ProtocolConstants.PROFILE_URI, listener, null);
			
			while (!terminate) {
				//TODO: error handling, e.g. when port is already in use
				TCPSessionCreator.listen(ProtocolConstants.LISTENING_PORT, registry);
			}
			
		} catch (Exception e) {
			exitStatus = e.getMessage();
		}
		LOG.debug("stopped ["+exitStatus+"]");
	}
	
	public void terminate() {
		terminate = true;
		interrupt();
	}
}
