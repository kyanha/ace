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

import ch.iserver.ace.net.SessionConnectionCallback;
import ch.iserver.ace.net.impl.PortableDocumentExt;
import ch.iserver.ace.net.impl.RemoteDocumentProxyExt;
import ch.iserver.ace.net.impl.SessionConnectionImpl;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

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
		this.deserializer = deserializer;
		this.handler = handler;
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
				Request response = null;
				synchronized(this) {
					//deserializer and handler are shared by all SessionRequestHandler instances, thus synchronize
					deserializer.deserialize(rawData, handler);
					response = handler.getResult();
				}
				int type = response.getType();
				if (type == ProtocolConstants.JOIN_DOCUMENT) {
					//reception and processing of a joined document
					PortableDocumentExt doc = (PortableDocumentExt) response.getPayload();
					publisherId = doc.getPublisherId();
					docId = doc.getDocumentId();
					RemoteUserSession session = SessionManager.getInstance().getSession(publisherId);
					SessionConnectionImpl connection = null;
					if (!session.hasSessionConnection(docId)) { //upon join request
						LOG.debug("addSessionConnection()");
						connection = session.addSessionConnection(docId, message.getChannel());
						connection.setParticipantId(doc.getParticipantId());
						RemoteDocumentProxyExt proxy = session.getUser().getSharedDocument(docId);
						sessionCallback = proxy.joinAccepted(connection);
					} else { //upon invitation
						LOG.debug("turn SessionConnection ACTIVE");
						connection = session.getSessionConnection(docId);
						connection.setChannel(message.getChannel());
						connection.setState(AbstractConnection.STATE_ACTIVE);
						sessionCallback = connection.getSessionConnectionCallback();
					}
					sessionCallback.setDocument(doc);
				} else if (type == ProtocolConstants.KICKED) {
					String docId = ((DocumentInfo) response.getPayload()).getDocId();
					LOG.debug("user kicked for doc [" + docId + "]");
					sessionCallback.kicked();
				}	else if (type == ProtocolConstants.REQUEST) {
					//TODO:
//					sessionCallback.receiveRequest(participantId, request);
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
