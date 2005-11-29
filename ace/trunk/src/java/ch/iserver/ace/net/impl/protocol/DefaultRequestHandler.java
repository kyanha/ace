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
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.InputDataStream;
import org.beepcore.beep.core.MessageMSG;
import org.beepcore.beep.core.RequestHandler;

import ch.iserver.ace.net.impl.NetworkProperties;

/**
 * Determines the correct RequestHandler a Channel, i.e. it sets
 * the correct request handler to a newly initiated channel.
 */
public class DefaultRequestHandler extends AbstractRequestHandler {

	private static Logger LOG = Logger.getLogger(DefaultRequestHandler.class);
	
	private static Object MUTEX = new Object();
	
	private RequestHandler mainHandler;
	private Deserializer deserializer;
	private ParserHandler handler;
	
	public DefaultRequestHandler(RequestHandler mainHandler, Deserializer deserializer, ParserHandler handler) {
		this.mainHandler = mainHandler;
		this.deserializer = deserializer;
		this.handler = handler;
	}
	
	
	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.RequestHandler#receiveMSG(org.beepcore.beep.core.MessageMSG)
	 */
	public void receiveMSG(MessageMSG message) {
		LOG.debug("--> receiveMSG()");
		try {
			InputDataStream input = message.getDataStream();
			byte[] rawData = readData(input);
			Request response = null;
			synchronized(MUTEX) {
				deserializer.deserialize(rawData, handler);
				response = handler.getResult();
			}
			RequestHandler handler;
			Channel channel = message.getChannel();
			int type = response.getType();
			if (type == ProtocolConstants.CHANNEL_MAIN) {
				handler = mainHandler;
			} else if (type == ProtocolConstants.CHANNEL_SESSION) {
				handler = SessionRequestHandlerFactory.getInstance().createHandler();
			} else {
				LOG.warn("unkown channel type, use main as default");
				handler = mainHandler;
			}
			channel.setRequestHandler(handler);
			
			try {
				//confirm reception of msg				
				message.sendNUL();
			} catch (Exception e) {
				LOG.error("could not send confirmation ["+e+", "+e.getMessage()+"]");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("could not process request ["+e+"]");
		}
		LOG.debug("<-- receiveMSG()");
	}


	public void cleanup() {
		// TODO Auto-generated method stub
		
	}


	protected Logger getLogger() {
		return LOG;
	}

}
