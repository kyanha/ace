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
import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.CloseChannelException;
import org.beepcore.beep.core.RequestHandler;
import org.beepcore.beep.core.Session;
import org.beepcore.beep.core.StartChannelException;
import org.beepcore.beep.core.StartChannelListener;

import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class StartChannelListenerImpl implements StartChannelListener {

	private static Logger LOG = Logger.getLogger(StartChannelListenerImpl.class);
	
	private RequestHandler handler;
	
	public StartChannelListenerImpl(RequestHandler handler) {
		ParameterValidator.notNull("handler", handler);
		this.handler = handler;
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
		//Called when the underlying BEEP framework receives  a "start" element.
		LOG.debug("startChannel("+channel+", "+encoding+", "+data+")");
		channel.setRequestHandler(handler);
	}

	/**
	 * @see org.beepcore.beep.core.StartChannelListener#closeChannel(org.beepcore.beep.core.Channel)
	 */
	public void closeChannel(Channel channel) throws CloseChannelException {
		//Called when the underlying BEEP framework receives  a "close" element.
		LOG.debug("closeChannel("+channel+")");
		channel.setRequestHandler(null);
	}
}
