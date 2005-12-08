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

import java.net.BindException;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.ProfileRegistry;
import org.beepcore.beep.transport.tcp.TCPSession;
import org.beepcore.beep.transport.tcp.TCPSessionCreator;

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.net.impl.NetworkProperties;
import ch.iserver.ace.net.impl.NetworkServiceImpl;

/**
 *
 */
public class BEEPSessionListener extends Thread {

	private static Logger LOG = Logger.getLogger(BEEPSessionListener.class);
	
	private ProfileRegistry registry;
	
	private boolean terminate;
	
	public BEEPSessionListener(ProfileRegistry registry) {
		this.registry = registry;
		terminate = false;
	}
	
	
	public void run() {
		String exitStatus = "normal";
		try {
			int port = Integer.parseInt(NetworkProperties.get(NetworkProperties.KEY_PROTOCOL_PORT));
			while (!terminate) {
				//TODO: error handling, e.g. when port is already in use -> retry strategy
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
	
	public void terminate() {
		terminate = true;
		interrupt();
	}
}
