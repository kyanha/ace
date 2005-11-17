/*
 * $Id:BEEPServer.java 1205 2005-11-14 07:57:10Z zbinl $
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
import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.ProfileRegistry;
import org.beepcore.beep.core.Session;
import org.beepcore.beep.core.StartChannelListener;
import org.beepcore.beep.core.event.SessionEvent;
import org.beepcore.beep.core.event.SessionListener;
import org.beepcore.beep.core.event.SessionResetEvent;
import org.beepcore.beep.profile.Profile;
import org.beepcore.beep.transport.tcp.TCPSession;
import org.beepcore.beep.transport.tcp.TCPSessionCreator;

/**
 *
 */
public class BEEPSessionListener extends Thread implements SessionListener {

	private static Logger LOG = Logger.getLogger(BEEPSessionListener.class);
	
	private ProfileRegistry registry;
	private Profile profile;
	
	private boolean terminate;
	
	public BEEPSessionListener(Profile profile) {
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
				//TODO: error handling, e.g. when port is already in use -> retry strategy
				LOG.debug("start listening at port "+ProtocolConstants.LISTENING_PORT+" again");
				try {
					TCPSession session = TCPSessionCreator.listen(ProtocolConstants.LISTENING_PORT, registry);
					LOG.debug("accepted session with ["+session+"]");
					session.addSessionListener(this);
				} catch (BEEPException be) {
					LOG.warn("server stopped, restart ["+be.getMessage()+"]");
				} catch (Exception e) {
					LOG.error("server stopped, shutdown ["+e.getMessage()+"]");
					exitStatus = e.getMessage();
					terminate = true;
				}
			}
			
		} catch (Exception e) {
			exitStatus = e.getMessage();
		}
		LOG.debug("terminate ["+exitStatus+"]");
	}
	
	public void terminate() {
		terminate = true;
		interrupt();
	}


	public void greetingReceived(SessionEvent event) {
		LOG.debug("greeting received for session ["+((Session)event.getSource()).toString()+"]");
		
	}


	public void sessionClosed(SessionEvent arg0) {
		LOG.debug("sesion closed");
		
	}


	public void sessionReset(SessionResetEvent arg0) {
		LOG.debug("sesion reset");
		
	}
}
