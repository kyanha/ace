/*
 * $Id:ParticipantRequestHandler.java 2413 2005-12-09 13:20:12Z zbinl $
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
import org.beepcore.beep.core.InputDataStream;
import org.beepcore.beep.core.MessageMSG;
import org.beepcore.beep.core.OutputDataStream;
import org.beepcore.beep.core.RequestHandler;

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.algorithm.TimestampFactory;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.core.NetworkServiceExt;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.protocol.RequestImpl.DocumentInfo;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.StackTrace;

/**
 * The <code>ParticipantRequestHandler</code> is a server side 
 * request handler for a collaborative session.
 * 
 * @see org.beepcore.beep.core.RequestHandler
 */
public class ParticipantRequestHandler implements RequestHandler {

	private static Logger LOG = Logger.getLogger(ParticipantRequestHandler.class);
	
	/**
	 * the deserializer
	 */
	private Deserializer deserializer;
	
	/**
	 * the participant port (communication port to the upper layer)
	 */
	private ParticipantPort port;
	
	/**
	 * the participant connection this request handler belongs to
	 */
	private ParticipantConnectionImpl connection;
	
	/**
	 * the timestamp factory to create timestamp when
	 * deserializing requests
	 */
	private TimestampFactory factory;
	
	/**
	 * A reference to the NetworkServiceExt
	 */
	private NetworkServiceExt service;
	
	/**
	 * Creates a new ParticipantRequestHandler.
	 * 
	 * @param deserializer		the deserializer to deserialize messages
	 * @param factory			the timestamp factory
	 * @param service			the NetworkService reference
	 */
	public ParticipantRequestHandler(Deserializer deserializer, TimestampFactory factory, NetworkServiceExt service) {
		ParameterValidator.notNull("deserializer", deserializer);
		ParameterValidator.notNull("factory", factory);
		ParameterValidator.notNull("service", service);
		this.deserializer = deserializer;
		this.factory = factory;
		this.service = service;
	}
	
	/**
	 * Sets the participant port.
	 * 
	 * @param port the ParticipantPort to set
	 */
	public void setParticipantPort(ParticipantPort port) {
		ParameterValidator.notNull("ParticipantPort", port);
		this.port = port;
	}
	
	/**
	 * Sets the ParticipantConnection.
	 * 
	 * @param connection the ParticipantConnection to set
	 */
	public void setParticipantConnection(ParticipantConnectionImpl connection) {
		this.connection = connection;
	}
	
	/**
	 * Cleans up the ParticipantRequestHandler i.e. releases its resources.
	 */
	public void cleanup() {
		deserializer = null;
		connection = null;
		port = null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void receiveMSG(MessageMSG message) {
		LOG.info("--> recieveMSG()");

		String readInData = null;
		try {
			Request result = null;
			int type = ProtocolConstants.NO_TYPE;
			if (!service.isStopped()) {
				InputDataStream input = message.getDataStream();
				byte[] rawData = DataStreamHelper.read(input);
				readInData = new String(rawData);
				LOG.debug("received " + rawData.length + " bytes. ["+readInData+"]");
				CollaborationParserHandler newHandler = new CollaborationParserHandler();
				newHandler.setTimestampFactory(factory);
				deserializer.deserialize(rawData, newHandler);
				readInData = null;
				result = newHandler.getResult();
				type = result.getType();
			} else {
				LOG.debug("network service stopped, stop request processing.");
			}
			
			try {				
				OutputDataStream os = new OutputDataStream();
				os.setComplete();
				message.sendRPY(os);
			} catch (Exception e) {
				LOG.error("could not send confirmation ["+e.getMessage()+"]");
			}
			
			if (port == null) {
				LOG.error("ParticipantRequestHandler has no ParticipantPort, return without any further processing");
				return;
			}
			
			if (type == ProtocolConstants.LEAVE) {
				//notify ParticipantConnectionImpl in order that no sessionTerminated message is sent
				connection.setHasLeft(true);
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
		} catch (Exception e) {
			String stackTrace = StackTrace.get(e);
			LOG.error("could not process request [" + e + ", " + stackTrace + ", " + readInData + "]");
			NetworkServiceImpl.getInstance().getCallback().serviceFailure(FailureCodes.SESSION_FAILURE, "'" + readInData + "'", e);
		}
		LOG.debug("<-- receiveMSG");
		
	}
	


}
