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

package ch.iserver.ace.net.protocol;

import java.net.BindException;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.ProfileRegistry;
import org.beepcore.beep.transport.tcp.TCPSession;
import org.beepcore.beep.transport.tcp.TCPSessionCreator;

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.net.core.NetworkProperties;
import ch.iserver.ace.net.core.NetworkServiceImpl;

/**
 * <code>BEEPSessionListener</code> listens repeatedly for sessions 
 * initiated by other users. After a session has been accepted, 
 * {@link ch.iserver.ace.net.protocol.StartChannelListenerImpl#startChannel(Channel, String, String)}
 * will be called.
 *
 * @see org.beepcore.beep.transport.tcp.TCPSessionCreator
 */
public class BEEPSessionListener extends Thread {

	private static Logger LOG = Logger.getLogger(BEEPSessionListener.class);
	
	/**
	 * The profile registry
	 */
	private ProfileRegistry registry;
	
	/**
	 * flag to indicate if the thread should terminate
	 */
	private boolean terminate;
	
	/**
	 * Creates a new BEEPSessionListener.
	 * 
	 * @param registry	the profile registry
	 */
	public BEEPSessionListener(ProfileRegistry registry) {
		this.registry = registry;
		terminate = false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void run() {
		String exitStatus = "normal";
		try {
			int port = Integer.parseInt(NetworkProperties.get(NetworkProperties.KEY_PROTOCOL_PORT));
			while (!terminate) {
				//could use a retry strategy when port is already in use -> e.g. increment port by one, 
				//but problems with firewall configuration could arise, so this issue is left as is
				LOG.debug("start listening at port "+port+" again");
				try { 
					TCPSession session = TCPSessionCreator.listen(port, registry);
					LOG.debug("accepted session with ["+session.getSocket()+"]");
				} catch (BEEPException be) {
					LOG.warn("server stopped ["+be.getMessage()+"]");
					Throwable thrown = be.getCause();
					if (thrown instanceof BindException) {
						LOG.error("BindException, stop server.");
						terminate = true;
						exitStatus = be.getMessage();
						NetworkServiceImpl.getInstance().getCallback().serviceFailure(
								FailureCodes.ADDRESS_ALREADY_USED, Integer.toString(port), be);
					}
				} catch (Exception e) {
					LOG.error("server stopped, shutdown ["+e.getMessage()+"]");
					exitStatus = e.getMessage();
					terminate = true;
				}
			}
			
		} catch (Exception e) {
			exitStatus = e.getMessage();
		}
		LOG.info("terminate ["+exitStatus+"]");
	}
	
	/**
	 * Terminates this thread.
	 */
	public void terminate() {
		LOG.debug("--> terminate()");
		terminate = true;
		interrupt();
		LOG.debug("<-- terminate()");
	}
}
