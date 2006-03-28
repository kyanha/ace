/*
 * $Id:SessionConnectionImpl.java 2413 2005-12-09 13:20:12Z zbinl $
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

import ch.iserver.ace.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.net.SessionConnection;

/**
 * SessionConnectionImpl represents a session connection from a particpant to the publisher
 * of a shared document.
 * 
 * @see ch.iserver.ace.net.protocol.AbstractConnection
 */
public class SessionConnectionImpl extends AbstractConnection implements SessionConnection {

	private int participantId;
	private String docId, username, userid;
	private Serializer serializer;
	
	/**
	 * Constructor.
	 *
	 * @param docId		the document id
	 * @param channel	the channel
	 * @param listener	the reply listener
	 * @param serializer	the serializer
	 * @param username	the user name
	 * @param userId		the user id
	 */
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
	
	/**
	 * Sets the participant id.
	 * 
	 * @param id the participant id
	 */
	public void setParticipantId(int id) {
		LOG.debug("setParticipantId("+id+")");
		this.participantId = id;
	}
	
	/**
	 * Gets the document id.
	 * 
	 * @return the document id
	 */
	public String getDocumentId() {
		return docId;
	}
	
	/*****************************************************/
	/** methods from abstract class AbstractConnection  **/
	/*****************************************************/
	
	/**
	 * {@inheritDoc}
	 */
	public void cleanup() {
		LOG.debug("--> cleanup()");
		serializer = null;
		setReplyListener(null);
		Channel channel = getChannel();
		if (channel != null) {
			channel.setRequestHandler(null);
		}
		//TODO: check if a thread is waiting in method AbstractConnection.send(),
		//if so interrupt it, because setChannel, setState and send are synchronized -> deadlock 
		//-> check with raess
		Thread t = getSendingThread();
		if (t != null) {
			LOG.debug("interrupt sending thread [" + t.getName() + "]");
			t.interrupt();
		}
		setChannel(null);
		setState(STATE_CLOSED);
		LOG.debug("<-- cleanup()");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void recover() throws RecoveryException {
		throw new RecoveryException();
	}
	
	/***********************************************/
	/** methods from interface SessionConnection  **/
	/***********************************************/
	
	/**
	 * {@inheritDoc}
	 */
	public int getParticipantId() {
		return participantId;
	}

	public boolean isAlive() {
		return getState() != STATE_CLOSED && (getState() == STATE_ACTIVE || getState() == STATE_INITIALIZED);
	}

	/**
	 * {@inheritDoc}
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

	/**
	 * {@inheritDoc}
	 */
	public void sendRequest(Request request) {
		LOG.info("--> sendRequest(siteid="+request.getSiteId()+", "+ request.getTimestamp() 
				+", "+ request.getOperation().getClass() +")");
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

	/**
	 * {@inheritDoc}
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

	/**
	 * {@inheritDoc}
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
	
	/**
	 * Sends the <code>data</code> to the peer.
	 * 
	 * @param data the data to send
	 */
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
	 * Executes the session cleanup.
	 */
	public void executeCleanup() {
		SessionCleanup sessionCleanup = new SessionCleanup(docId, userid);
		sessionCleanup.execute();
	}

}
