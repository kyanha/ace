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

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.algorithm.TimestampFactory;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Server side request handler for a collaborative session.
 */
public class ParticipantRequestHandler extends AbstractRequestHandler {

	private static Logger LOG = Logger.getLogger(ParticipantRequestHandler.class);
	
	private Deserializer deserializer;
	private ParticipantPort port;
	private TimestampFactory factory;
	
	public ParticipantRequestHandler(Deserializer deserializer, TimestampFactory factory) {
		ParameterValidator.notNull("deserializer", deserializer);
		ParameterValidator.notNull("factory", factory);
		this.deserializer = deserializer;
		this.factory = factory;
	}
	
	public void setParticipantPort(ParticipantPort port) {
		ParameterValidator.notNull("ParticipantPort", port);
		this.port = port;
	}
	
	public void cleanup() {
		deserializer = null;
		port = null;
	}
	
	public void receiveMSG(MessageMSG message) {
		LOG.info("--> recieveMSG()");
		
		InputDataStream input = message.getDataStream();
		String readInData = null;
		try {
			byte[] rawData = DataStreamHelper.read(input);
			readInData = new String(rawData);
			LOG.debug("received "+rawData.length+" bytes. ["+readInData+"]");
			CollaborationParserHandler newHandler = new CollaborationParserHandler();
			newHandler.setTimestampFactory(factory);
			deserializer.deserialize(rawData, newHandler);
			readInData = null;
			Request result = newHandler.getResult();
			int type = result.getType();
			
			try {				
				message.sendNUL(); //confirm reception of msg
			} catch (Exception e) {
				LOG.error("could not send confirmation ["+e.getMessage()+"]");
			}
			
			if (type == ProtocolConstants.LEAVE) {
				port.leave();
				LOG.debug("participant ["+((DocumentInfo)result.getPayload()).getParticipantId()+"] left.");
				//cleanup of participant resources is done in ParticipantConnectionImpl.close()
			} else if (type == ProtocolConstants.REQUEST) {
				ch.iserver.ace.algorithm.Request algoRequest = (ch.iserver.ace.algorithm.Request) result.getPayload();
				LOG.debug("receiveRequest("+algoRequest+")");
				port.receiveRequest(algoRequest);
			} else if (type == ProtocolConstants.CARET_UPDATE) {
				CaretUpdateMessage updateMsg = (CaretUpdateMessage) result.getPayload();
				LOG.debug("receivedCaretUpdate("+updateMsg+")");
				port.receiveCaretUpdate(updateMsg);
			} else if (type == ProtocolConstants.ACKNOWLEDGE) {
				Timestamp timestamp = (Timestamp) result.getPayload();
				String siteId = result.getUserId();
				LOG.debug("receiveAcknowledge("+siteId+", "+timestamp);
				port.receiveAcknowledge(Integer.parseInt(siteId), timestamp);
			} 
			
//			try {				
//				message.sendNUL(); //confirm reception of msg
//			} catch (Exception e) {
//				LOG.error("could not send confirmation ["+e.getMessage()+"]");
//			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("could not process request ["+e+"]");
			NetworkServiceImpl.getInstance().getCallback().serviceFailure(FailureCodes.SESSION_FAILURE, "'" + readInData + "'", e);
		}
		LOG.debug("<-- receiveMSG");
		
	}
	
	protected Logger getLogger() {
		return LOG;
	}

}
