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

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.algorithm.TransformationException;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.collaboration.JoinRequest;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.PublishedSession;
import ch.iserver.ace.collaboration.PublishedSessionCallback;
import ch.iserver.ace.collaboration.jupiter.server.PublisherPort;
import ch.iserver.ace.collaboration.jupiter.server.ServerLogic;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.SemaphoreLock;

/**
 *
 */
public class PublishedSessionImpl extends AbstractSession 
		implements PublishedSession, PublisherConnection {
	
	private ServerLogic logic;
	
	private PublisherPort port;
	
	private final PublishedSessionCallback callback;
	
	public PublishedSessionImpl(PublishedSessionCallback callback) {
		this(callback, new AlgorithmWrapperImpl(new Jupiter(true)));
	}
	
	public PublishedSessionImpl(PublishedSessionCallback callback, AlgorithmWrapper wrapper) {
		super(wrapper, new SemaphoreLock("client-lock"));
		ParameterValidator.notNull("callback", callback);
		this.callback = callback;
	}
		
	public void setServerLogic(ServerLogic logic) {
		ParameterValidator.notNull("logic", logic);
		this.logic = logic;
	}
	
	public void setPublisherPort(PublisherPort port) {
		ParameterValidator.notNull("port", port);
		this.port = port;
	}
	
	public ServerLogic getLogic() {
		return logic;
	}
	
	protected PublisherPort getPort() {
		return port;
	}
	
	protected PublishedSessionCallback getCallback() {
		return callback;
	}
	
	protected AcknowledgeAction createAcknowledgeAction() {
		return new AcknowledgeAction() {
			public void execute() {
				lock();
				try {
					Timestamp timestamp = getAlgorithm().getTimestamp();
					int siteId = getAlgorithm().getSiteId();
					getPort().receiveAcknowledge(siteId, timestamp);
				} finally {
					unlock();
				}
			}
		};
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.PublishedSession#setDocumentDetails(ch.iserver.ace.DocumentDetails)
	 */
	public void setDocumentDetails(DocumentDetails details) {
		getPort().setDocumentDetails(details);
	}

	/**
	 * @see ch.iserver.ace.collaboration.PublishedSession#kick(ch.iserver.ace.collaboration.Participant)
	 */
	public void kick(Participant participant) {
		getPort().kick(participant.getParticipantId());
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#getParticipantId()
	 */
	public int getParticipantId() {
		return getPort().getParticipantId();
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#leave()
	 */
	public void leave() {
		try {
			getPort().leave();
		} finally {
			destroy();
		}
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#sendOperation(ch.iserver.ace.Operation)
	 */
	public void sendOperation(Operation operation) {
		checkLockUsage();
		resetAcknowledgeTimer();
		Request request = getAlgorithm().generateRequest(operation);
		getPort().receiveRequest(request);
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#sendCaretUpdate(ch.iserver.ace.CaretUpdate)
	 */
	public void sendCaretUpdate(CaretUpdate update) {
		checkLockUsage();
		resetAcknowledgeTimer();
		CaretUpdateMessage message = getAlgorithm().generateCaretUpdateMessage(update);
		getPort().receiveCaretUpdate(message);
	}
		
	// --> start ParticipantConnection implementation <--
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.PublisherConnection#sendJoinRequest(ch.iserver.ace.collaboration.JoinRequest)
	 */
	public void sendJoinRequest(JoinRequest request) {
		getCallback().joinRequest(request);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.PublisherConnection#sessionFailed(int, java.lang.Exception)
	 */
	public void sessionFailed(int reason, Exception e) {
		getCallback().sessionFailed(reason, e);
	}
	
	/**
	 * @see ch.iserver.ace.net.ParticipantConnection#setParticipantId(int)
	 */
	public void setParticipantId(int participantId) {
		// ignore, participant id can be retrieved form port...
	}
				
	/**
	 * @see ch.iserver.ace.net.ParticipantConnection#getUser()
	 */
	public RemoteUserProxy getUser() {
		return null;
	}

	/**
	 * @see ch.iserver.ace.net.ParticipantConnection#close()
	 */
	public void close() {
		// ignore, PublishedSession is the owner			
	}
		
	/**
	 * @see ch.iserver.ace.net.ParticipantConnection#sendCaretUpdateMessage(int, ch.iserver.ace.algorithm.CaretUpdateMessage)
	 */
	public void sendCaretUpdateMessage(int participantId, CaretUpdateMessage message) {
		lock();
		try {
			Participant participant = getParticipant(participantId);
			CaretUpdate update = getAlgorithm().receiveCaretUpdateMessage(message);
			getCallback().receiveCaretUpdate(participant, update);
		} catch (TransformationException e) {
			getCallback().sessionFailed(TRANSFORMATION_FAILED, e);
			leave();
		} finally {
			unlock();
		}
	}
		
	/**
	 * @see ch.iserver.ace.net.ParticipantConnection#sendRequest(int, ch.iserver.ace.algorithm.Request)
	 */
	public void sendRequest(int participantId, Request request) {
		lock();
		try {
			Participant participant = getParticipant(participantId);
			Operation op = getAlgorithm().receiveRequest(request);
			getCallback().receiveOperation(participant, op);
		} catch (TransformationException e) {
			getCallback().sessionFailed(TRANSFORMATION_FAILED, e);
			leave();
		} finally {
			unlock();
		}
	}
	
	/**
	 * @see ch.iserver.ace.net.ParticipantConnection#sendAcknowledge(int, ch.iserver.ace.algorithm.Timestamp)
	 */
	public void sendAcknowledge(int siteId, Timestamp timestamp) {
		lock();
		try {
			getAlgorithm().acknowledge(siteId, timestamp);
		} catch (TransformationException e) {
			getCallback().sessionFailed(TRANSFORMATION_FAILED, e);
			leave();
		} finally {
			unlock();
		}
	}

	/**
	 * @see ch.iserver.ace.net.ParticipantConnection#sendParticipantJoined(int, ch.iserver.ace.net.RemoteUserProxy)
	 */
	public void sendParticipantJoined(int participantId, RemoteUserProxy proxy) {
		Participant participant = createParticipant(participantId, proxy);
		addParticipant(participant);
		getCallback().participantJoined(participant);
	}
			
	/**
	 * @see ch.iserver.ace.net.ParticipantConnection#sendParticipantLeft(int, int)
	 */
	public void sendParticipantLeft(int participantId, int reason) {
		Participant participant = getParticipant(participantId);
		removeParticipant(participant);
		getCallback().participantLeft(participant, reason);
	}

	/**
	 * @see ch.iserver.ace.net.ParticipantConnection#sendDocument(ch.iserver.ace.net.PortableDocument)
	 */
	public void sendDocument(PortableDocument document) {
		throw new UnsupportedOperationException("sendDocument is not supported for PublisherConnection objects");	
	}
		
	/**
	 * @see ch.iserver.ace.net.ParticipantConnection#sendKicked()
	 */
	public void sendKicked() {
		throw new UnsupportedOperationException("publisher cannot be kicked");
	}
	
	/**
	 * @see ch.iserver.ace.net.ParticipantConnection#joinAccepted(ch.iserver.ace.net.ParticipantPort)
	 */
	public void joinAccepted(ParticipantPort port) {
		throw new UnsupportedOperationException("publisher is always part of session");
	}
	
	/**
	 * @see ch.iserver.ace.net.ParticipantConnection#joinRejected(int)
	 */
	public void joinRejected(int code) {
		throw new UnsupportedOperationException("publisher is never rejected");
	}
			
}
