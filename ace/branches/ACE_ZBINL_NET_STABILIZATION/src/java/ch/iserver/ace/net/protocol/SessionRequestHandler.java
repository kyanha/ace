/*
 * $Id:SessionRequestHandler.java 2413 2005-12-09 13:20:12Z zbinl $
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

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.InputDataStream;
import org.beepcore.beep.core.MessageMSG;
import org.beepcore.beep.core.OutputDataStream;
import org.beepcore.beep.core.RequestHandler;

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.SessionConnectionCallback;
import ch.iserver.ace.net.core.NetworkServiceExt;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.core.PortableDocumentExt;
import ch.iserver.ace.net.core.RemoteDocumentProxyExt;
import ch.iserver.ace.net.core.RemoteUserProxyExt;
import ch.iserver.ace.net.discovery.DiscoveryManager;
import ch.iserver.ace.net.discovery.DiscoveryManagerFactory;
import ch.iserver.ace.net.protocol.RequestImpl.DocumentInfo;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.StackTrace;

/**
 * SessionRequestHandler is a client side request handler for a 
 * collaborative session.
 * 
 * @see org.beepcore.beep.core.RequestHandler
 */
public class SessionRequestHandler implements RequestHandler {

	private static Logger LOG = Logger.getLogger(SessionRequestHandler.class);
	
	private Deserializer deserializer;
	private ParserHandler handler;
	private String docId, publisherId;
	private SessionConnectionCallback sessionCallback;
	private NetworkServiceExt service;
	private DocumentInfo info;
	
	/**
	 * Creates a new SessionRequestHandler.
	 * 
	 * @param deserializer
	 * @param handler
	 * @param service
	 * @param info
	 */
	public SessionRequestHandler(Deserializer deserializer, ParserHandler handler, NetworkServiceExt service, DocumentInfo info) {
		ParameterValidator.notNull("deserializer", deserializer);
		ParameterValidator.notNull("parserHandler", handler);
		this.deserializer = deserializer;
		this.handler = handler;
		this.service = service;
		this.info = info;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void receiveMSG(MessageMSG message) {
		LOG.info("--> recieveMSG()");
		
		String readInData = null;
		try {
			Request response = null;
			int type = ProtocolConstants.NO_TYPE;
			if (!service.isStopped()) {
				InputDataStream input = message.getDataStream();
				byte[] rawData = DataStreamHelper.read(input); //only one thread shall read data at a time
				readInData = (new String(rawData));
				LOG.debug("received "+rawData.length+" bytes. ["+readInData+"]");
				//deserializer and handler are shared by all SessionRequestHandler instances, thus synchronize
				deserializer.deserialize(rawData, handler);
				response = handler.getResult();
				type = response.getType();
				readInData = null;
			}
			
			try {
				OutputDataStream os = new OutputDataStream();
				os.setComplete();
				message.sendRPY(os);
			} catch (Exception e) {
				LOG.error("could not send confirmation ["+e.getMessage()+"]");
			}
			
			if (sessionCallback == null && type != ProtocolConstants.JOIN_DOCUMENT) {
				try {
					publisherId = info.getUserId();
					docId = info.getDocId();
					RemoteUserSession session = SessionManager.getInstance().getSession(publisherId);
					sessionCallback = session.getUser().getSharedDocument(docId).getSessionConnectionCallback();
					ParameterValidator.notNull("sessionConnectionCallback", sessionCallback);
				} catch (Exception e) {
					LOG.error("error in initializing the sessionCallback [" + e + ", " + e.getMessage() + "]");
				}
			}
			
			if (type == ProtocolConstants.JOIN_DOCUMENT) {	
				//reception and processing of a joined document
				PortableDocumentExt doc = (PortableDocumentExt) response.getPayload();
				addNewUsers(doc.getUsers());
				publisherId = doc.getPublisherId();
				docId = doc.getDocumentId();
				int participantId = doc.getParticipantId();
				RemoteUserSession session = SessionManager.getInstance().getSession(publisherId);
				if (session != null) {
					SessionConnectionImpl connection = session.addSessionConnection(docId, message.getChannel());
					connection.setParticipantId(participantId);
					RemoteDocumentProxyExt proxy = session.getUser().getSharedDocument(docId);
					sessionCallback = proxy.joinAccepted(connection, doc.getParticipantIdUserMapping()); 
					sessionCallback.setParticipantId(participantId);
					sessionCallback.setDocument(doc);
				} else {
					//it should never come here, and if so, it's a bug
					String msg = "could not find RemoteUserSession for [" + publisherId + "]";
					LOG.warn(msg);
					throw new IllegalStateException(msg);
				}
			} else if (type == ProtocolConstants.KICKED) {
				String docId = ((DocumentInfo) response.getPayload()).getDocId();
				LOG.debug("user kicked for doc [" + docId + "]");
				sessionCallback.kicked();
				executeCleanup();
			} else if (type == ProtocolConstants.REQUEST) {
				ch.iserver.ace.algorithm.Request algoRequest = (ch.iserver.ace.algorithm.Request) response.getPayload();
				LOG.debug("receiveRequest(siteid=" + algoRequest.getSiteId() + ", " + algoRequest.getTimestamp() 
						+ ", " + algoRequest.getOperation().getClass() + ")");
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
				RemoteUserProxyExt proxy = (RemoteUserProxyExt) response.getPayload();
				addNewUser(proxy);
				String participantId = response.getUserId();
				LOG.debug("participantJoined("+participantId+", "+proxy.getUserDetails().getUsername()+")");
				sessionCallback.participantJoined(Integer.parseInt(participantId), proxy);
			} else if (type == ProtocolConstants.PARTICIPANT_LEFT) {
				String reason = (String) response.getPayload();
				String participantIdStr = response.getUserId();
				int participantId = Integer.parseInt(participantIdStr);
				LOG.debug("participantLeft(" + participantId + ", " + reason + ")");
				
				RemoteUserSession session = SessionManager.getInstance().getSession(publisherId);
				RemoteDocumentProxyExt doc = session.getUser().getSharedDocument(docId); 
				RemoteUserProxyExt proxy = doc.getSessionParticipant(participantId);
				if (proxy != null && 
						DiscoveryManagerFactory.getDiscoveryManager().isUserAlive(proxy.getId())) {
					sessionCallback.participantLeft(participantId, Integer.parseInt(reason));
				} else {
					LOG.debug("participant [" + participantId + "] not alive");
				}
				doc.removeSessionParticipant(participantId);
			} else if (type == ProtocolConstants.SESSION_TERMINATED) {
				LOG.debug("sessionTerminated()");
				sessionCallback.sessionTerminated();
				executeCleanup();
			}
			
		} catch (Exception e) {
			String stackTrace = StackTrace.get(e);
			LOG.error("could not process request [" + stackTrace + "]");
			String name = DiscoveryManagerFactory.getDiscoveryManager().getUser(publisherId).getUserDetails().getUsername();
			name += (readInData != null) ? " [" + readInData + "]" : ""; 
			NetworkServiceImpl.getInstance().getCallback().serviceFailure(FailureCodes.SESSION_FAILURE, "'" + name + "'", e);
			//TODO: go with same behavior as when user gets kicked (local copy) -> then he must rejoin the session
			executeCleanup();
		}
		LOG.debug("<-- receiveMSG");
		
	}
	
	private void addNewUsers(List users) {
		LOG.debug("--> addNewUsers()");
		Iterator iter = users.iterator();
		while (iter.hasNext()) {
			RemoteUserProxyExt user = (RemoteUserProxyExt) iter.next();
			addNewUser(user);
		}
		LOG.debug("<-- addNewUsers()");
	}
	
	/**
	 * Adds a new user to the DiscoveryManager.
	 * 
	 * @param user
	 */
	private void addNewUser(RemoteUserProxyExt user) {
		DiscoveryManager discoveryManager = DiscoveryManagerFactory.getDiscoveryManager();
		if (discoveryManager.getUser(user.getId()) == null) {
			discoveryManager.addUser(user); //TODO: make shure the new user receives published documents
		}
	}

	/**
	 * Executes the SessionCleanup.
	 *
	 */
	public void executeCleanup() {
		SessionCleanup sessionCleanup = new SessionCleanup(getDocumentId(), getPublisherId());
		sessionCleanup.execute();
		cleanup();
	}
	
	/**
	 * Cleans up this SessionRequestHandler.
	 *
	 */
	public void cleanup() {
		deserializer = null;
		handler = null;
		sessionCallback = null;
	}
	
	/**
	 * Gets the document id.
	 * 
	 * @return the document id
	 */
	private String getDocumentId() {
		return docId;
	}
	
	/**
	 * Gets the publisher id.
	 * 
	 * @return the publisher (user) id
	 */
	private String getPublisherId() {
		return publisherId;
	}
	
	
}
