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

/**
 * This connection does not establish its channel until <code>joinAccepted(ParticipantPort)</code> is 
 * invoked.
 *
 */
public class ParticipantConnectionImpl extends AbstractConnection implements
		ParticipantConnection {

	private RemoteUserSession session;
	private boolean joinAccepted;
	private Serializer serializer;
	private int participantId;
	private String docId;
	private PublishedDocument publishedDoc;
	
	public ParticipantConnectionImpl(String docId, RemoteUserSession session, ReplyListener listener, Serializer serializer) {
		super(null);
		this.docId = docId;
		this.session = session;
		joinAccepted = false;
		this.serializer = serializer;
		setReplyListener(listener);
		super.LOG = Logger.getLogger(ParticipantConnectionImpl.class);
	}
		
	public int getParticipantId() {
		return participantId;
	}
	
	public void setPublishedDocument(PublishedDocument doc) {
		this.publishedDoc = doc;
	}
	
	public PublishedDocument getPublishedDocument() {
		return publishedDoc;
	}

	/*****************************************************/
	/** methods from abstract class AbstractConnection  **/
	/*****************************************************/
	public void cleanup() {
		LOG.debug("--> cleanup()");
		session = null;
		publishedDoc = null;
		serializer = null;
		setReplyListener(null);
		Channel channel = getChannel();
		((ParticipantRequestHandler)channel.getRequestHandler()).cleanup();
		setChannel(null);
		LOG.debug("<-- cleanup()");
	}
	
	/***************************************************/
	/** methods from interface ParticipantConnection  **/
	/***************************************************/
	public void setParticipantId(int participantId) {
		this.participantId = participantId;
	}
	
	public void joinAccepted(ParticipantPort port) {
		LOG.info("--> joinAccepted()");
		joinAccepted = true;
		//intiate collaboration channel
		try {
			Channel channel = session.startChannel(RemoteUserSession.CHANNEL_COLLABORATION);
			((ParticipantRequestHandler) channel.getRequestHandler()).setParticipantPort(port);
			setChannel(channel);
		} catch (ConnectionException ce) {
			NetworkServiceImpl.getInstance().getCallback().serviceFailure(
					FailureCodes.CHANNEL_FAILURE, "cannot start channel for collaboration", ce);
		}
		LOG.info("<-- joinAccepted()");
	}
	
	public void joinRejected(int code) {
		//TODO: do not initiate collaboration channel, instead return via JoinRejectedFilter
		LOG.info("joinRejected()");
	}

	public RemoteUserProxy getUser() {
		return session.getUser();
	}

	public void sendDocument(PortableDocument document) {
		if (joinAccepted) {
			LOG.info("--> sendDocument()");
			try {
				byte [] data = serializer.createResponse(ProtocolConstants.JOIN_DOCUMENT, getPublishedDocument(), document);
				send(data, null, getReplyListener());
			} catch (SerializeException se) {
				LOG.error("could not serialize document ["+se.getMessage()+"]");
			} catch (ProtocolException pe) {
				LOG.error("protocol exception ["+pe.getMessage()+"]");
			}
			LOG.info("<-- sendDocument()");
		} else {
			throw new IllegalStateException("cannot send document before join has been accepted.");
		}
	}

	public void sendRequest(int participantId, Request request) {
		LOG.info("--> sendRequest("+participantId+", "+request+")");
		
	}

	public void sendCaretUpdateMessage(int participantId, CaretUpdateMessage message) {
		LOG.info("--> sendCaretUpdateMessage("+participantId+", "+message+")");
		
	}
	
	public void sendAcknowledge(int siteId, Timestamp timestamp) {
		LOG.info("--> sendAcknowledge("+siteId+", "+timestamp+")");
		
	}

	public void sendParticipantJoined(int participantId, RemoteUserProxy proxy) {
		LOG.info("--> sendParticipantJoined("+participantId+", "+proxy.getUserDetails().getUsername()+")");
		
	}

	public void sendParticipantLeft(int participantId, int reason) {
		LOG.info("--> sendParticipantLeft("+participantId+", "+reason+")");
		
	}

	public void sendKicked() {
		LOG.info("--> sendKicked()");
		
	}
	
	public void close() {
		LOG.info("--> close("+getParticipantId()+", "+session.getUser().getUserDetails().getUsername()+")");
		//TODO: consider if on session shutdown it is more appropriate to 
		//notify the participant on close() invocation or on DocumentServer.shutdown()
		//invocation
		try {
			getChannel().close();
		} catch (BEEPException be) {
			LOG.warn("could not close channel ["+be.getMessage()+"]");
		}
		
		//clean up participant resources
		ParticipantCleanup cleanup = new ParticipantCleanup(getPublishedDocument().getId(), session.getUser().getId());
		cleanup.execute();
		
		LOG.info("<-- close()");
	}
	
//	public boolean equals(Object obj) {
//		return false;
//	}
//	
//	public int hashCode() {
//		return -1;
//	}

}
