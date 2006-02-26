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

package org.beepcore.beep.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
class MessageAssembler implements RequestHandler {

	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * RequestHandler to which the assembled messages will be forwarded.
	 */
	private RequestHandler handler;
	
	private int msgCount = 1;
	
	/**
	 * The message which is being assembled
	 */
	private MessageMSG currMsg;
	
	public MessageAssembler(RequestHandler handler) {
		this.handler = handler;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public synchronized void receiveMSG(MessageMSG message) { //TODO: synchronized is not the most performant solution
		log.debug("--> receiveMsg(" + message + ", " + message.getMessageType() + ", " + msgCount + ")");
		InputDataStream stream = message.getDataStream();
		if (currMsg == null) {
			currMsg = new MessageMSGImpl( (ChannelImpl) message.getChannel(), message.getMsgno(), new InputDataStream());
		}
		
		if (stream.availableSegment()) {
        		log.debug("--> add data");
			currMsg.getDataStream().add(stream.getNextSegment());
            	log.debug("<-- add data");
		}
		
		if (stream.isComplete() && !stream.availableSegment()) { //TODO: improve solution here
			log.debug("message complete, pass to actual handler [" + currMsg.getDataStream().available() + " bytes]");
			if (!currMsg.getDataStream().isComplete()) {
				currMsg.getDataStream().setComplete();
			}
			//pass complete message to actual application level request handler
			handler.receiveMSG(currMsg);
			currMsg = null;
		}
		log.debug("<-- receiveMsg(" + msgCount + ")");
		++msgCount;
	}
	
	public RequestHandler getRequestHandler() {
		return handler;
	}
	

}
