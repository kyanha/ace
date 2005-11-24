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

package ch.iserver.ace.collaboration.jupiter;

import org.apache.log4j.Logger;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.collaboration.JoinRequest;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.PortableDocument;
import ch.iserver.ace.collaboration.ParticipantSessionCallback;

/**
 * Null object of a SessionCallback. Logs a warning if methods are called on
 * this object.
 */
final class NullSessionCallback implements ParticipantSessionCallback {
	
	private static final Logger LOG = Logger.getLogger(NullSessionCallback.class);
	
	private static ParticipantSessionCallback instance;
	
	private NullSessionCallback() {
		// hidden constructor
	}
	
	public static final synchronized ParticipantSessionCallback getInstance() {
		if (instance == null) {
			instance = new NullSessionCallback();
		}
		return instance;
	}
	
	public void joinRequest(JoinRequest request) {
		LOG.warn("SessionCallback not set on Session (joinRequest called)");
	}
	
	public void participantJoined(Participant participant) {
		LOG.warn("SessionCallback not set on Session (participantJoined called)");
	}
	
	public void participantLeft(Participant participant, int code) {
		LOG.warn("SessionCallback not set on Session (participantLeft called)");
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.ParticipantSessionCallback#setDocument(PortableDocument)
	 */
	public void setDocument(PortableDocument doc) {
		LOG.warn("SessionCallback not set on Session (setDocument called)");
	}

	/**
	 * @see ch.iserver.ace.collaboration.ParticipantSessionCallback#sessionTerminated()
	 */
	public void sessionTerminated() {
		LOG.warn("SessionCallback not set on Session (sessionTerminated called)");
	}

	/**
	 * @see ch.iserver.ace.collaboration.ParticipantSessionCallback#kicked()
	 */
	public void kicked() {
		LOG.warn("SessionCallback not set on Session (kicked called)");
	}

	/**
	 * @see ch.iserver.ace.collaboration.PublishedSessionCallback#receiveOperation(ch.iserver.ace.collaboration.Participant, ch.iserver.ace.Operation)
	 */
	public void receiveOperation(Participant participant, Operation operation) {
		LOG.warn("SessionCallback not set on Session (receiveOperation called)");
	}

	/**
	 * @see ch.iserver.ace.collaboration.PublishedSessionCallback#receiveCaretUpdate(ch.iserver.ace.collaboration.Participant, ch.iserver.ace.CaretUpdate)
	 */
	public void receiveCaretUpdate(Participant participant, CaretUpdate update) {
		LOG.warn("SessionCallback not set on Session (receiveCaretUpdate called)");
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.PublishedSessionCallback#sessionFailed(int, java.lang.Exception)
	 */
	public void sessionFailed(int reason, Exception e) {
		LOG.warn("SessionCallback not set on Session (sessionFailed called)");
	}

}