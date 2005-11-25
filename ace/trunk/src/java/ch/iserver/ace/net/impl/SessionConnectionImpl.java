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

package ch.iserver.ace.net.impl;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.ReplyListener;

import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.net.SessionConnection;
import ch.iserver.ace.net.impl.protocol.AbstractConnection;
import ch.iserver.ace.net.impl.protocol.ProtocolConstants;
import ch.iserver.ace.net.impl.protocol.RemoteUserSession;
import ch.iserver.ace.net.impl.protocol.Serializer;

/**
 *
 */
public class SessionConnectionImpl extends AbstractConnection implements SessionConnection {

	private int participantId;
	private String docId;
	private RemoteUserSession session;
	private Serializer serializer;
	
	public SessionConnectionImpl(String docId, RemoteUserSession session, Channel channel, ReplyListener listener, Serializer serializer) {
		super(channel);
		this.docId = docId;
		this.session = session;
		this.serializer = serializer;
		setReplyListener(listener);
		super.LOG = Logger.getLogger(SessionConnectionImpl.class);
	}
	
	public void setParticipantId(int id) {
		this.participantId = id;
	}
	
	public String getDocumentId() {
		return docId;
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
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.SessionConnection#leave()
	 */
	public void leave() {
		LOG.debug("--> leave()");
		try {
			byte[] data = serializer.createNotification(ProtocolConstants.LEAVE, this);
			send(data, null, getReplyListener());
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("exception processing leave ["+e+", "+e.getMessage()+"]");
		}
		LOG.debug("<-- leave()");
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.SessionConnection#sendRequest(ch.iserver.ace.algorithm.Request)
	 */
	public void sendRequest(Request request) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.SessionConnection#sendCaretUpdateMessage(ch.iserver.ace.algorithm.CaretUpdateMessage)
	 */
	public void sendCaretUpdateMessage(CaretUpdateMessage message) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.SessionConnection#sendAcknowledge(int, ch.iserver.ace.algorithm.Timestamp)
	 */
	public void sendAcknowledge(int siteId, Timestamp timestamp) {
		// TODO Auto-generated method stub

	}

}
