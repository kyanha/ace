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
import org.beepcore.beep.core.InputDataStream;
import org.beepcore.beep.core.MessageMSG;

import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.TimestampFactory;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

/**
 * Server side request handler for a collaborative session.
 */
public class ParticipantRequestHandler extends AbstractRequestHandler {

	
	private static Logger LOG = Logger.getLogger(ParticipantRequestHandler.class);
	
	private Deserializer deserializer;
//	private ParserHandler handler;
	private ParticipantPort port;
	private TimestampFactory factory;
	
	public ParticipantRequestHandler(Deserializer deserializer, TimestampFactory factory) {
		LOG.debug("new ParticiantRequestHandler()");
		this.deserializer = deserializer;
//		this.handler = handler;
		this.factory = factory;
	}
	
	public void setParticipantPort(ParticipantPort port) {
		this.port = port;
	}
	
	public ParticipantPort getParticipantPort() {
		return port;
	}
	
	public void cleanup() {
		deserializer = null;
//		handler = null;
		port = null;
	}
	
	public void receiveMSG(MessageMSG message) {
		LOG.info("--> recieveMSG()");
		
		InputDataStream input = message.getDataStream();
		
		try {
			byte[] rawData = readData(input);
			LOG.debug("received "+rawData.length+" bytes. ["+(new String(rawData))+"]");
			if (rawData.length == PIGGYBACKED_MESSAGE_LENGTH) {
				handlePiggybackedMessage(message);
			} else {
				CollaborationParserHandler newHandler = new CollaborationParserHandler();
				newHandler.setTimestampFactory(factory);
				deserializer.deserialize(rawData, newHandler);
				Request result = newHandler.getResult();
				int type = result.getType();
				if (type == ProtocolConstants.LEAVE) {
					port.leave();
					LOG.debug("participant ["+((DocumentInfo)result.getPayload()).getParticipantId()+"] left.");
					//cleanup of participant resources is done in ParticipantConnectionImpl.close()
				} else if (type == ProtocolConstants.REQUEST) {
					ch.iserver.ace.algorithm.Request algoRequest = (ch.iserver.ace.algorithm.Request) result.getPayload();
					port.receiveRequest(algoRequest);
				} else if (type == ProtocolConstants.CARET_UPDATE) {
					CaretUpdateMessage updateMsg = (CaretUpdateMessage) result.getPayload();
					port.receiveCaretUpdate(updateMsg);
				} else if (type == ProtocolConstants.ACKNOWLEDGE) {
					
					throw new UnsupportedOperationException();
					
				} 
				
				try {				
					message.sendNUL(); //confirm reception of msg
				} catch (Exception e) {
					LOG.error("could not send confirmation ["+e.getMessage()+"]");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("could not process request ["+e+"]");
		}
		LOG.debug("<-- receiveMSG");
		
	}
	
	protected Logger getLogger() {
		return LOG;
	}

}
