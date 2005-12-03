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
import org.beepcore.beep.core.RequestHandler;

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.SessionConnectionCallback;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.PortableDocumentExt;
import ch.iserver.ace.net.impl.RemoteDocumentProxyExt;
import ch.iserver.ace.net.impl.SessionConnectionImpl;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Client side request handler for a collaborative session.
 */
public class SessionRequestHandler extends AbstractRequestHandler {

	private static Logger LOG = Logger.getLogger(SessionRequestHandler.class);
	
	private Deserializer deserializer;
	private ParserHandler handler;
	private String docId, publisherId;
	private SessionConnectionCallback sessionCallback;
	
	public SessionRequestHandler(Deserializer deserializer, ParserHandler handler) {
		ParameterValidator.notNull("deserializer", deserializer);
		ParameterValidator.notNull("parserHandler", handler);
		this.deserializer = deserializer;
		this.handler = handler;
	}
	
	public void receiveMSG(MessageMSG message) {
		LOG.info("--> recieveMSG()");
		
		String readInData = null;
		try {
			Request response = null;
			synchronized(this) {
				InputDataStream input = message.getDataStream();
				byte[] rawData = readData(input); //only one thread shall read data at a time
				readInData = (new String(rawData));
				LOG.debug("received "+rawData.length+" bytes. ["+readInData+"]");
				//deserializer and handler are shared by all SessionRequestHandler instances, thus synchronize
				deserializer.deserialize(rawData, handler);
				response = handler.getResult();
				readInData = null;
			}
			int type = response.getType();
			if (type == ProtocolConstants.JOIN_DOCUMENT) {
				//reception and processing of a joined document
				PortableDocumentExt doc = (PortableDocumentExt) response.getPayload();
				publisherId = doc.getPublisherId();
				docId = doc.getDocumentId();
				int participantId = doc.getParticipantId();
				RemoteUserSession session = SessionManager.getInstance().getSession(publisherId);
				SessionConnectionImpl connection = null;
				connection = session.addSessionConnection(docId, message.getChannel());
				connection.setParticipantId(participantId);
				RemoteDocumentProxyExt proxy = session.getUser().getSharedDocument(docId);
				sessionCallback = proxy.joinAccepted(connection); 
				sessionCallback.setParticipantId(participantId);
				sessionCallback.setDocument(doc);
			} else if (type == ProtocolConstants.KICKED) {
				String docId = ((DocumentInfo) response.getPayload()).getDocId();
				LOG.debug("user kicked for doc [" + docId + "]");
				sessionCallback.kicked();
				executeCleanup();
			} else if (type == ProtocolConstants.REQUEST) {
				ch.iserver.ace.algorithm.Request algoRequest = (ch.iserver.ace.algorithm.Request) response.getPayload();
				LOG.debug("receiveRequest("+algoRequest+")");
				String participantId = response.getUserId();
				sessionCallback.receiveRequest(Integer.parseInt(participantId), algoRequest);
			} else if (type == ProtocolConstants.CARET_UPDATE) {
				CaretUpdateMessage update = (CaretUpdateMessage) response.getPayload();
				LOG.debug("receivedCaretUpdate("+update+")");
				String participantId = response.getUserId();
				sessionCallback.receiveCaretUpdate(Integer.parseInt(participantId), update);
			} else if (type == ProtocolConstants.ACKNOWLEDGE) {
				Timestamp timestamp = (Timestamp) response.getPayload();
				String siteId = response.getUserId();
				LOG.debug("receiveAcknowledge("+siteId+", "+timestamp);
				sessionCallback.receiveAcknowledge(Integer.parseInt(siteId), timestamp);
			} else if (type == ProtocolConstants.PARTICIPANT_JOINED) {
				RemoteUserProxy proxy = (RemoteUserProxy) response.getPayload();
				String participantId = response.getUserId();
				LOG.debug("participantJoined("+participantId+", "+proxy.getUserDetails().getUsername()+")");
				sessionCallback.participantJoined(Integer.parseInt(participantId), proxy);
			} else if (type == ProtocolConstants.PARTICIPANT_LEFT) {
				String reason = (String) response.getPayload();
				String participantId = response.getUserId();
				LOG.debug("participantLeft("+participantId+", "+reason+")");
				sessionCallback.participantLeft(Integer.parseInt(participantId), Integer.parseInt(reason));
			} else if (type == ProtocolConstants.SESSION_TERMINATED) {
				LOG.debug("sessionTerminated()");
				sessionCallback.sessionTerminated();
				executeCleanup();
			}
			try {
				if (type != ProtocolConstants.KICKED && type != ProtocolConstants.SESSION_TERMINATED) { //on KICKED message, channel is already closed here
					message.sendNUL(); //confirm reception of msg
				}
			} catch (Exception e) {
				LOG.error("could not send confirmation ["+e.getMessage()+"]");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("could not process request ["+e+"]");
			NetworkServiceImpl.getInstance().getCallback().serviceFailure(FailureCodes.SESSION_FAILURE, readInData, e);
		}
		LOG.debug("<-- receiveMSG");
		
	}
	
	public void executeCleanup() {
		SessionCleanup sessionCleanup = new SessionCleanup(getDocumentId(), getPublisherId());
		sessionCleanup.execute();
		cleanup();
	}
	
	public void cleanup() {
		deserializer = null;
		handler = null;
		sessionCallback = null;
	}
	
	public String getDocumentId() {
		return docId;
	}
	
	public String getPublisherId() {
		return publisherId;
	}
	
	protected Logger getLogger() {
		return LOG;
	}
	
}
