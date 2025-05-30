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

package ch.iserver.ace.net.protocol;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.ReplyListener;

import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.net.SessionConnection;

/**
 *
 */
public class SessionConnectionImpl extends AbstractConnection implements SessionConnection {

	private int participantId;
	private String docId, username, userid;
	private Serializer serializer;
//	private boolean hasLeft;
	
	public SessionConnectionImpl(String docId, Channel channel, ReplyListener listener, Serializer serializer, String username, String userId) {
		super(channel);
		setState((channel == null) ? STATE_INITIALIZED : STATE_ACTIVE);
		this.docId = docId;
		this.serializer = serializer;
		this.username = username;
		this.userid = userId;
		setReplyListener(listener);
		super.LOG = Logger.getLogger(SessionConnectionImpl.class);
	}
	
	public void setParticipantId(int id) {
		LOG.debug("setParticipantId("+id+")");
		this.participantId = id;
	}
	
	public String getDocumentId() {
		return docId;
	}
	
	/*****************************************************/
	/** methods from abstract class AbstractConnection  **/
	/*****************************************************/
	public void cleanup() {
		LOG.debug("--> cleanup()");
		serializer = null;
		setReplyListener(null);
		setChannel(null);
		setState(STATE_CLOSED);
		LOG.debug("<-- cleanup()");
	}
	
	/***********************************************/
	/** methods from interface SessionConnection  **/
	/***********************************************/
	
	/*
	 * @see ch.iserver.ace.net.SessionConnection#getParticipantId()
	 */
	public int getParticipantId() {
		return participantId;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.SessionConnection#isAlive()
	 */
	public boolean isAlive() {
		return getState() != STATE_CLOSED && (getState() == STATE_ACTIVE || getState() == STATE_INITIALIZED);
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.SessionConnection#leave()
	 */
	public void leave() {
		LOG.debug("--> leave()");
		if (getState() == STATE_ACTIVE) {
			try {
				byte[] data = serializer.createNotification(ProtocolConstants.LEAVE, this);
				sendToPeer(data);
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("exception processing leave ["+e+", "+e.getMessage()+"]");
			}
			setState(STATE_CLOSED);
			executeCleanup();
		} else {
			LOG.warn("not sending leave, state is " + getStateString());
		}
		LOG.debug("<-- leave()");
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.SessionConnection#sendRequest(ch.iserver.ace.algorithm.Request)
	 */
	public void sendRequest(Request request) {
		LOG.info("--> sendRequest("+request+")");
		if (getState() == STATE_ACTIVE) {
			byte[] data = null;
			try {
				data = serializer.createSessionMessage(ProtocolConstants.REQUEST, request, Integer.toString(participantId));
			} catch (SerializeException se) {
				LOG.error("could not serialize message ["+se.getMessage()+"]");
			}
			sendToPeer(data);
		} else {
			LOG.warn("not sending request, state is " + getStateString());
		}
		LOG.info("<-- sendRequest()");

	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.SessionConnection#sendCaretUpdateMessage(ch.iserver.ace.algorithm.CaretUpdateMessage)
	 */
	public void sendCaretUpdateMessage(CaretUpdateMessage message) {
		LOG.info("--> sendCaretUpdateMessage("+message+")");
		if (getState() == STATE_ACTIVE) {
			byte[] data = null;
			try {
				data = serializer.createSessionMessage(ProtocolConstants.CARET_UPDATE, message, Integer.toString(participantId));
			} catch (SerializeException se) {
				LOG.error("could not serialize message ["+se.getMessage()+"]");
			}
			sendToPeer(data);
		} else {
			LOG.warn("not sending caret update message, state is " + getStateString());
		}
		LOG.info("<-- sendCaretUpdateMessage()");
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.SessionConnection#sendAcknowledge(int, ch.iserver.ace.algorithm.Timestamp)
	 */
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
			LOG.warn("not sending acknowledge, state is " + getStateString());
		}	
		LOG.info("<-- sendAcknowledge()");
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
	
	public void executeCleanup() {
		SessionCleanup sessionCleanup = new SessionCleanup(docId, userid);
		sessionCleanup.execute();
		cleanup();
	}

}
