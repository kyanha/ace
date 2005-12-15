/*
 * $Id:Connection.java 1095 2005-11-09 13:56:51Z zbinl $
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

import org.apache.log4j.Logger;
import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.transport.tcp.TCPSession;

/**
 * Wrapper/Decorator class around a <code>Channel</code>. The MainConnection
 * is the main connection between two peers. All basic communication except the
 * document editing goes over the main connection.
 * 
 * @see org.beepcore.beep.core.Channel
 */
public class MainConnection extends AbstractConnection {
	
	/**
	 * Constructor.
	 * 
	 * @param channel the channel belonging to this MainConnection
	 * @see Channel
	 */
	public MainConnection(Channel channel) {
		super(channel);
		setState((channel == null) ? STATE_INITIALIZED : STATE_ACTIVE);
		super.LOG = Logger.getLogger(MainConnection.class);
	}

	/**
	 * Closes the main connection.
	 *
	 */
	public void close() {
		LOG.debug("--> close()");
		try {
			//request handler != null means that no closeChannel event has been received for that channel
			if (getState() == STATE_ACTIVE && getChannel().getRequestHandler() != null) {
				//consider if there could be other states upon which a close() should be done
				getChannel().close();
			}
		} catch (BEEPException be) {
			LOG.warn("could not close channel ["+be.getMessage()+"]");
		}
		setChannel(null);
		setReplyListener(null);
		setState(STATE_CLOSED);
		LOG.debug("<-- close()");
	}
	
	public void cleanup() {
		LOG.debug("not used yet.");
	}
	
	public void recover() throws RecoveryException {
		throw new RecoveryException();
	}
	
	public String toString() {
		return "MainConnection( "+((TCPSession)getChannel().getSession()).getSocket()+" )";
	}
	
}
