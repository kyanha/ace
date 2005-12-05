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
import ch.iserver.ace.net.impl.PublishedDocument;
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
	private boolean isKicked;
	
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
	}
		
	public int getParticipantId() {
		return participantId;
	}

	/*****************************************************/
	/** methods from abstract class AbstractConnection  **/
	/*****************************************************/
	public void cleanup() {
		LOG.debug("--> cleanup()");
		session = null;
		serializer = null;
		setReplyListener(null);
		Channel channel = getChannel();
		LOG.debug("channel: "+channel);
		if (channel != null) {
			((ParticipantRequestHandler)channel.getRequestHandler()).cleanup();
			setChannel(null);
		}
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
		//initiate collaboration channel
		try {
			Channel channel = session.startChannel(RemoteUserSession.CHANNEL_SESSION);
			((ParticipantRequestHandler) channel.getRequestHandler()).setParticipantPort(port);
			setChannel(channel);
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
			byte[] data = null;
			try {
				DocumentInfo info = new DocumentInfo(docId, getParticipantId());
				data = serializer.createResponse(ProtocolConstants.JOIN_DOCUMENT, info, document);
			} catch (SerializeException se) {
				LOG.error("could not serialize document ["+se.getMessage()+"]");
			}
			sendToPeer(data);
			LOG.info("<-- sendDocument()");
		} else {
			throw new IllegalStateException("cannot send document before join has been accepted.");
		}
	}

	public void sendRequest(int participantId, Request request) {
		LOG.info("--> sendRequest("+participantId+", "+request+")");
		
		byte[] data = null;
		try {
			data = serializer.createSessionMessage(ProtocolConstants.REQUEST, request, Integer.toString(participantId));
		} catch (SerializeException se) {
			LOG.error("could not serialize message ["+se.getMessage()+"]");
		}
		sendToPeer(data);
		
		LOG.info("<-- sendRequest()");
	}

	public void sendCaretUpdateMessage(int participantId, CaretUpdateMessage message) {
		LOG.info("--> sendCaretUpdateMessage("+participantId+", "+message+")");
		
		byte[] data = null;
		try {
			data = serializer.createSessionMessage(ProtocolConstants.CARET_UPDATE, message, Integer.toString(participantId));
		} catch (SerializeException se) {
			LOG.error("could not serialize message ["+se.getMessage()+"]");
		}
		sendToPeer(data);
		
		LOG.info("<-- sendCaretUpdateMessage()");
	}
	
	public void sendAcknowledge(int siteId, Timestamp timestamp) {
		LOG.info("--> sendAcknowledge("+siteId+", "+timestamp+")");
		
		byte[] data = null;
		try {
			data = serializer.createSessionMessage(ProtocolConstants.ACKNOWLEDGE, timestamp, Integer.toString(siteId));
		} catch (SerializeException se) {
			LOG.error("could not serialize message ["+se.getMessage()+"]");
		}
		sendToPeer(data);
			
		LOG.info("<-- sendAcknowledge()");
	}

	public void sendParticipantJoined(int participantId, RemoteUserProxy proxy) {
		LOG.info("--> sendParticipantJoined("+participantId+", "+proxy+")");
		
		byte[] data = null;
		try {
			data = serializer.createSessionMessage(ProtocolConstants.PARTICIPANT_JOINED, proxy, Integer.toString(participantId));
		} catch (SerializeException se) {
			LOG.error("could not serialize message ["+se.getMessage()+"]");
		}
		sendToPeer(data);
		
		LOG.info("--> sendParticipantJoined()");
	}

	public void sendParticipantLeft(int participantId, int reason) {
		LOG.info("--> sendParticipantLeft("+participantId+", "+reason+")");
		
		byte[] data = null;
		try {
			data = serializer.createSessionMessage(ProtocolConstants.PARTICIPANT_LEFT, Integer.toString(reason), 
					Integer.toString(participantId));
		} catch (SerializeException se) {
			LOG.error("could not serialize message ["+se.getMessage()+"]");
		}
		sendToPeer(data);
		
		LOG.info("--> sendParticipantLeft()");
	}

	public void sendKicked() {
		LOG.info("--> sendKicked()");
		isKicked = true;
		byte [] data = null;
		try {
			data = serializer.createNotification(ProtocolConstants.KICKED, docId);
		} catch (SerializeException se) {
			LOG.error("could not serialize message ["+se.getMessage()+"]");
		}
		sendToPeer(data);
		LOG.info("<-- sendKicked()");
	}
	
	public void close() {
		LOG.info("--> close("+getParticipantId()+", "+getUser().getUserDetails().getUsername()+")");
		//TODO: consider if on session shutdown it is more appropriate to 
		//notify the participant on close() invocation or on DocumentServer.shutdown()
		//invocation
		try {
			if (!isKicked) {
				sendSessionTerminated();
			}
			getChannel().close();
		} catch (BEEPException be) {
			LOG.warn("could not close channel ["+be.getMessage()+"]");
		}
		executeCleanup();
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
			send(data, session.getUser().getUserDetails().getUsername(), getReplyListener());
		} catch (ProtocolException pe) {
			//TODO: error handling?
			LOG.error("protocol exception ["+pe.getMessage()+"]");
			throw new NetworkException("could not send message to peer ["+pe.getMessage()+"]");
		}
	}
	
	private void executeCleanup() {
		//clean up participant resources
		ParticipantCleanup cleanup = new ParticipantCleanup(docId, session.getUser().getId());
		cleanup.execute();
	}
	
//	public boolean equals(Object obj) {
//		return false;
//	}
//	
//	public int hashCode() {
//		return -1;
//	}

}
