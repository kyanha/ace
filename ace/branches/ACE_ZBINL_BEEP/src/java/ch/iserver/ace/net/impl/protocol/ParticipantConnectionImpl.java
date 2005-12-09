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
import org.beepcore.beep.core.ReplyListener;

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

/**
 * This connection does not establish its channel until <code>joinAccepted(ParticipantPort)</code> is 
 * invoked.
 *
 */
public class ParticipantConnectionImpl extends AbstractConnection implements
		ParticipantConnection {

	private RemoteUserSession session;
	private RequestFilter filter;
	private boolean joinAccepted;
	private Serializer serializer;
	private int participantId = -1;
	private String docId;
	private boolean isKicked, hasLeft;
	private String username;
	private ParticipantPort port;
	private Channel incoming;
	
	public ParticipantConnectionImpl(String docId, RemoteUserSession session, ReplyListener listener, Serializer serializer, RequestFilter filter) {
		super(null);
		setState(STATE_INITIALIZED);
		this.docId = docId;
		this.session = session;
		joinAccepted = false;
		this.serializer = serializer;
		this.filter = filter;
		setReplyListener(listener);
		super.LOG = Logger.getLogger(ParticipantConnectionImpl.class);
		isKicked = false;
		setHasLeft(false);
		username = session.getUser().getUserDetails().getUsername();
	}
	
	public String getDocumentId() {
		return docId;
	}
		
	public int getParticipantId() {
		return participantId;
	}
	
	public ParticipantPort getParticipantPort() {
		return port;
	}
	
	public void setHasLeft(boolean value) {
		LOG.debug("setHasLeft(" + value + ")");
		hasLeft = value;
	}
	
	public boolean hasLeft() {
		return hasLeft;
	}

	/*****************************************************/
	/** methods from abstract class AbstractConnection  **/
	/*****************************************************/
	public void cleanup() {
		LOG.debug("--> cleanup()");
		session = null;
		serializer = null;
		port = null;
		setReplyListener(null);
		Channel channel = getChannel();
		if (channel != null) {
			//TODO: cannot cast anymore because of SingleThreadDomain
			//((ParticipantRequestHandler)channel.getRequestHandler()).cleanup();
			channel.setRequestHandler(null);
		}
		setChannel(null);
		if (incoming != null) {
			incoming.setRequestHandler(null);
			incoming = null;
		}
//		}
		setState(STATE_CLOSED);
		LOG.debug("<-- cleanup()");
	}
	
	/***************************************************/
	/** methods from interface ParticipantConnection  **/
	/***************************************************/
	public void setParticipantId(int participantId) {
		LOG.debug("setParticipantId("+participantId+")");
		this.participantId = participantId;
	}
	
	public void joinAccepted(ParticipantPort port) {
		LOG.info("--> joinAccepted()");
		joinAccepted = true;
		this.port = port;
		//initiate collaboration channel
		try {
			LOG.debug("initiate incoming and outgoing channel to peer");
			//channel for outgoing messages
			Channel outgoing = session.startChannel(RemoteUserSession.CHANNEL_SESSION, null, getDocumentId());
			setChannel(outgoing);
			
			//channel for incoming messages
			incoming = session.startChannel(RemoteUserSession.CHANNEL_SESSION, this, getDocumentId());
			LOG.debug("done.");
			
			setState(STATE_ACTIVE);
		} catch (ConnectionException ce) {
			NetworkServiceImpl.getInstance().getCallback().serviceFailure(
					FailureCodes.CHANNEL_FAILURE, getUser().getUserDetails().getUsername(), ce);
		}
		LOG.info("<-- joinAccepted()");
	}
	
	public void joinRejected(int code) {
		LOG.info("--> joinRejected(" + code + ")");
		DocumentInfo info = new DocumentInfo(docId, null, null);
		info.setData(Integer.toString(code));
		ch.iserver.ace.net.impl.protocol.Request request = 
			new RequestImpl(ProtocolConstants.JOIN_REJECTED, session.getUser().getId(), info);
		filter.process(request);
		executeCleanup();
		LOG.info("<-- joinRejected()");
	}

	public RemoteUserProxy getUser() {
		return session.getUser();
	}

	public void sendDocument(PortableDocument document) {
		if (joinAccepted) {
			LOG.info("--> sendDocument()");
			if (getState() == STATE_ACTIVE) {
				byte[] data = null;
				try {
					DocumentInfo info = new DocumentInfo(docId, getParticipantId());
					data = serializer.createResponse(ProtocolConstants.JOIN_DOCUMENT, info, document);
				} catch (SerializeException se) {
					LOG.error("could not serialize document ["+se.getMessage()+"]");
				}
				
				//send via incoming channel so that the channel is set correctly at the receiver site
				LOG.debug("use incoming channel to send document");
				Channel outgoing = getChannel();
				setChannel(incoming);
				sendToPeer(data);
				setChannel(outgoing);
				LOG.debug("set outgoing channel as default channel again");
				
			} else {
				LOG.warn("do not send Document, connection is in state " + getStateString());
			}
			LOG.info("<-- sendDocument()");
		} else {
			throw new IllegalStateException("cannot send document before join is accepted.");
		}
	}

	public void sendRequest(int participantId, Request request) {
		LOG.info("--> sendRequest("+participantId+", "+request+")");
		if (getState() == STATE_ACTIVE) {
			byte[] data = null;
			try {
				data = serializer.createSessionMessage(ProtocolConstants.REQUEST, request, Integer.toString(participantId));
			} catch (SerializeException se) {
				LOG.error("could not serialize message ["+se.getMessage()+"]");
			}
			sendToPeer(data);
		} else {
			LOG.warn("do not send Acknowledge, connection is in state " + getStateString());
		}
		LOG.info("<-- sendRequest()");
	}

	public void sendCaretUpdateMessage(int participantId, CaretUpdateMessage message) {
		LOG.info("--> sendCaretUpdateMessage("+participantId+", "+message+")");
		if (getState() == STATE_ACTIVE) {
			byte[] data = null;
			try {
				data = serializer.createSessionMessage(ProtocolConstants.CARET_UPDATE, message, Integer.toString(participantId));
			} catch (SerializeException se) {
				LOG.error("could not serialize message ["+se.getMessage()+"]");
			}
			sendToPeer(data);
		} else {
			LOG.warn("do not send CaretUpdateMessage, connection is in state " + getStateString());
		}
		LOG.info("<-- sendCaretUpdateMessage()");
	}
	
	public void sendAcknowledge(int siteId, Timestamp timestamp) {
		LOG.info("--> sendAcknowledge("+siteId+", "+timestamp+")");
		if (getState() == STATE_ACTIVE) {
			byte[] data = null;
			try {
				data = serializer.createSessionMessage(ProtocolConstants.ACKNOWLEDGE, timestamp, Integer.toString(siteId));
			} catch (SerializeException se) {
				LOG.error("could not serialize message ["+se.getMessage()+"]");
			}
			sendToPeer(data);
		} else {
			LOG.warn("do not send Acknowledge, connection is in state " + getStateString());
		}
		LOG.info("<-- sendAcknowledge()");
	}

	public void sendParticipantJoined(int participantId, RemoteUserProxy proxy) {
		LOG.info("--> sendParticipantJoined("+participantId+", "+proxy+")");
		if (getState() == STATE_ACTIVE) {
			byte[] data = null;
			try {
				data = serializer.createSessionMessage(ProtocolConstants.PARTICIPANT_JOINED, proxy, Integer.toString(participantId));
			} catch (SerializeException se) {
				LOG.error("could not serialize message ["+se.getMessage()+"]");
			}
			sendToPeer(data);
		} else {
			LOG.warn("do not send participantJoined, connection is in state " + getStateString());
		}
		LOG.info("--> sendParticipantJoined()");
	}

	public void sendParticipantLeft(int participantId, int reason) {
		LOG.info("--> sendParticipantLeft("+participantId+", "+reason+")");
		if (getState() == STATE_ACTIVE) {
			byte[] data = null;
			try {
				data = serializer.createSessionMessage(ProtocolConstants.PARTICIPANT_LEFT, Integer.toString(reason), 
						Integer.toString(participantId));
			} catch (SerializeException se) {
				LOG.error("could not serialize message ["+se.getMessage()+"]");
			}
			sendToPeer(data);
		} else {
			LOG.warn("do not send participantLeft, connection is in state " + getStateString());
		}
		LOG.info("--> sendParticipantLeft()");
	}

	public void sendKicked() {
		LOG.info("--> sendKicked()");
		isKicked = true;
		if (getState() == STATE_ACTIVE) {
			byte [] data = null;
			try {
				data = serializer.createNotification(ProtocolConstants.KICKED, docId);
			} catch (SerializeException se) {
				LOG.error("could not serialize message ["+se.getMessage()+"]");
			}
			sendToPeer(data);
		} else {
			LOG.warn("do not send kicked, connection is in state " + getStateString());
		}
		LOG.info("<-- sendKicked()");
	}
	
	public void close() {
		LOG.info("--> close("+getParticipantId()+", "+username+")");
		if (getState() == STATE_ACTIVE) {
			try {
				if (!isKicked && !hasLeft()) {
					sendSessionTerminated();
				}
				getChannel().close();
			} catch (BEEPException be) {
				LOG.warn("could not close channel ["+be.getMessage()+"]");
			}
			executeCleanup();
		} else {
			LOG.warn("do not close, connection is in state " + getStateString());
		}
		LOG.info("<-- close()");
	}
	
	private void sendSessionTerminated() {
		byte[] data = null;
		try {
			data = serializer.createSessionMessage(ProtocolConstants.SESSION_TERMINATED, null, null);
		} catch (SerializeException se) {
			LOG.error("could not serialize message ["+se.getMessage()+"]");
		}
		sendToPeer(data);
	}
	
	private void sendToPeer(byte[] data) {
		try {
			send(data, username, getReplyListener());
		} catch (ProtocolException pe) {
			LOG.error("protocol exception ["+pe.getMessage()+"]");
			executeCleanup();
			throw new NetworkException("could not send message to '" + username + "' ["+pe.getMessage()+"]");
		}
	}
	
	/**
	 * Clean up participant resources.
	 */
	private void executeCleanup() {
		if (docId != null && session != null) {
			ParticipantCleanup cleanup = new ParticipantCleanup(docId, session.getUser().getId());
			cleanup.execute();
		} else {
			LOG.warn("cannot cleanup, docId and/or session null [" + docId + "] [" + session + "]");
		}
	}
	
//	public boolean equals(Object obj) {
//		return false;
//	}
//	
//	public int hashCode() {
//		return -1;
//	}

}
