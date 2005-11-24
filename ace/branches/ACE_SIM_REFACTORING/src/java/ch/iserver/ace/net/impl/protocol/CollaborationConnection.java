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

/**
 * This connection does not establish its channel until <code>joinAccepted(ParticipantPort)</code> is 
 * invoked.
 *
 */
public class CollaborationConnection extends AbstractConnection implements
		ParticipantConnection {

	private RemoteUserSession session;
	private ParticipantPort port;
	private boolean joinAccepted;
	private Serializer serializer;
	private String docId;
	
	public CollaborationConnection(RemoteUserSession session, Channel channel, ReplyListener listener, Serializer serializer) {
		super(channel);
		this.session = session;
		joinAccepted = false;
		this.serializer = serializer;
		setReplyListener(listener);
		super.LOG = Logger.getLogger(CollaborationConnection.class);
	}
	
	public void setDocumentId(String id) {
		this.docId = id;
	}
	
	public String getDocumentId() {
		return docId;
	}

	/***************************************************/
	/** methods from interface ParticipantConnection  **/
	/***************************************************/
	public void setParticipantId(int participantId) {
		// TODO Auto-generated method stub
		
	}
	
	public void joinAccepted(ParticipantPort port) {
		joinAccepted = true;
		this.port = port;
		//intiate collaboration channel
		try {
			session.startChannel(this);
		} catch (ConnectionException ce) {
			NetworkServiceImpl.getInstance().getCallback().serviceFailure(
					FailureCodes.CHANNEL_FAILURE, "cannot start channel for collaboration", ce);
		}
	}
	
	public void joinRejected(int code) {
		//TODO: do not initiate collaboration channel, instead return via JoinRejectedFilter
		
	}

	public RemoteUserProxy getUser() {
		return session.getUser();
	}

	public void sendDocument(PortableDocument document) {
		if (joinAccepted) {
			try {
				byte [] data = serializer.createResponse(ProtocolConstants.JOIN_DOCUMENT, getDocumentId(), document);
				send(data, null, getReplyListener());
			} catch (SerializeException se) {
				LOG.error("could not serialize document ["+se.getMessage()+"]");
			} catch (ProtocolException pe) {
				LOG.error("protocol exception ["+pe.getMessage()+"]");
			}
		} else {
			throw new IllegalStateException("cannot send document before join has been accepted.");
		}
	}

	public void sendRequest(int participantId, Request request) {
		// TODO Auto-generated method stub
		
	}

	public void sendCaretUpdateMessage(int participantId, CaretUpdateMessage message) {
		// TODO Auto-generated method stub
		
	}
	
	public void sendAcknowledge(int siteId, Timestamp timestamp) {
		// TODO Auto-generated method stub
		
	}

	public void sendParticipantJoined(int participantId, RemoteUserProxy proxy) {
		// TODO Auto-generated method stub
		
	}

	public void sendParticipantLeft(int participantId, int reason) {
		// TODO Auto-generated method stub
		
	}

	public void sendKicked() {
		// TODO Auto-generated method stub
		
	}
	
	public void close() {
		//TODO: consider if on session shutdown it is more appropriate to 
		//notify the participant on close() invocation or on DocumentServer.shutdown()
		//invocation
		try {
			getChannel().close();
		} catch (BEEPException be) {
			LOG.warn("could not close channel ["+be.getMessage()+"]");
		}
	}
	
	public boolean equals(Object obj) {
		//TODO:!!!
		throw new UnsupportedOperationException("to be implemented");
	}
	
	public int hashCode() {
		//TODO: !!!
		throw new UnsupportedOperationException("to be implemented");
	}

}
