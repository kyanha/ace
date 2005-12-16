/*
 * $Id:StartChannelListenerImpl.java 1205 2005-11-14 07:57:10Z zbinl $
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
import org.beepcore.beep.core.CloseChannelException;
import org.beepcore.beep.core.RequestHandler;
import org.beepcore.beep.core.Session;
import org.beepcore.beep.core.StartChannelException;
import org.beepcore.beep.core.StartChannelListener;

/**
 * BEEP Core specific class to receive notifications when a Channel is started or closed.
 * 
 * @see org.beepcore.beep.core.StartChannelListener
 */
public class StartChannelListenerImpl implements StartChannelListener {

	private static Logger LOG = Logger.getLogger(StartChannelListenerImpl.class);
	
	private DefaultRequestHandlerFactory factory;
	
	/**
	 * Creates a new StartChannelListenerImpl.
	 * 
	 * @param factory
	 */
	public StartChannelListenerImpl(DefaultRequestHandlerFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * @see org.beepcore.beep.core.StartChannelListener#advertiseProfile(org.beepcore.beep.core.Session)
	 */
	public boolean advertiseProfile(Session session) throws BEEPException {
		//always advertise the profile
		return true;
	}

	/**
	 * @see org.beepcore.beep.core.StartChannelListener#startChannel(org.beepcore.beep.core.Channel, java.lang.String, java.lang.String)
	 */
	public void startChannel(Channel channel, String encoding, String data)
			throws StartChannelException {
		LOG.debug("--> acceptChannel("+channel+", type="+data+")");
		RequestHandler requestHandler = factory.createHandler();
		channel.setRequestHandler(requestHandler);
		LOG.debug("<-- acceptChannel()");
	}

	/**
	 * @see org.beepcore.beep.core.StartChannelListener#closeChannel(org.beepcore.beep.core.Channel)
	 */
	public void closeChannel(Channel channel) throws CloseChannelException {
		LOG.debug("--> closeChannel("+channel+")");
		//--> cleanup is now initiated in SessionRequestHandler
		channel.setRequestHandler(null);
		LOG.debug("<-- closeChannel()");
	}
}
