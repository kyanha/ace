/*
 * $Id:QueryListener.java 1095 2005-11-09 13:56:51Z zbinl $
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
import org.beepcore.beep.core.AbortChannelException;
import org.beepcore.beep.core.InputDataStream;
import org.beepcore.beep.core.Message;
import org.beepcore.beep.core.ReplyListener;
import org.beepcore.beep.transport.tcp.TCPSession;

import ch.iserver.ace.net.protocol.filter.RequestFilter;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Default implementation for interface <code>ReplyListener</code>.
 * ResponseListener listens for responses in a BEEP Core Channel 
 * communication.
 */
public class ResponseListener implements ReplyListener {

	private static Logger LOG = Logger.getLogger(ResponseListener.class);
	
	private static ResponseListener instance;
	
	private ResponseListener() {
		handler = new ResponseParserHandler();
	}
	
	public static ResponseListener getInstance() {
		if (instance == null) {
			instance = new ResponseListener();
		}
		return instance;
	}
	
	public void init(Deserializer deserializer, RequestFilter filter) {
		ParameterValidator.notNull("deserializer", deserializer);
		ParameterValidator.notNull("filter", filter);
		this.deserializer = deserializer;
		this.filter = filter;
	}
	
	/**
	 * 
	 * @see org.beepcore.beep.core.ReplyListener#receiveRPY(org.beepcore.beep.core.Message)
	 */
	public void receiveRPY(Message message) throws AbortChannelException {
		String result = "n/a";
		try {
			InputDataStream stream = message.getDataStream();
			byte[] data = DataStreamHelper.read(stream);
			result = new String(data);
		} catch (Exception e) {
			LOG.error("could not read stream [" + e + "]");
		}
		LOG.debug("receiveRPY(" + result + ")");
	}


	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.ReplyListener#receiveERR(org.beepcore.beep.core.Message)
	 */
	public void receiveERR(Message message) throws AbortChannelException {
		LOG.error("receiveERR("+message+")");
	}

	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.ReplyListener#receiveANS(org.beepcore.beep.core.Message)
	 */
	public void receiveANS(Message message) throws AbortChannelException {
		//should not receive any ANS message
		LOG.debug("receivedANS() -> not intended!");
	}

	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.ReplyListener#receiveNUL(org.beepcore.beep.core.Message)
	 */
	public void receiveNUL(Message message) throws AbortChannelException {
		Object appData = message.getChannel().getAppData();
		TCPSession session = (TCPSession) message.getChannel().getSession();
		LOG.debug("received confirmation from ["+appData+", "+session.getSocket()+"]");
	}
}
