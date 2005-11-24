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


import java.util.HashMap;
import java.util.Map;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.algorithm.TransformationException;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.ParticipantSessionCallback;
import ch.iserver.ace.collaboration.Session;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.SessionConnection;
import ch.iserver.ace.net.SessionConnectionCallback;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.SemaphoreLock;
import ch.iserver.ace.util.ThreadDomain;

/**
 * Default implementation of the Session interface. This class further implements
 * the SessionConnectionCallback interface and can thus be set as callback
 * on SessionConnections.
 */
public class SessionImpl extends AbstractSession 
		implements ConfigurableSession, SessionConnectionCallback, SessionConnectionFailureHandler {
	
	/**
	 * The SessionCallback from the application layer.
	 */
	private ParticipantSessionCallback callback = NullSessionCallback.getInstance();
	
	/**
	 * The SessionConnection from the network layer.
	 */
	private SessionConnection connection;

	/**
	 * 
	 */
	private ThreadDomain threadDomain;
	
	/**
	 * Creates a new SessionImpl.
	 */
	public SessionImpl() {
		this(new AlgorithmWrapperImpl(new Jupiter(true)));
	}
			
	/**
	 * Creates a new SessionImpl.
	 * 
	 * @param algorithm the algorithm for the session
	 */
	public SessionImpl(AlgorithmWrapper algorithm) {
		this(algorithm, (ParticipantSessionCallback) null);
	}
	
	/**
	 * Creates a new SessionImpl using the given algorithm and lock.
	 * 
	 * @param wrapper the algorithm for the Session
	 * @param lock the Lock to be used by the Session
	 */
	public SessionImpl(AlgorithmWrapper wrapper, Lock lock) {
		super(wrapper, lock);
	}
	
	/**
	 * Creates a new SessionImpl using the given algorithm and 
	 * ParticipantSessionCallback.
	 * 
	 * @param algorithm the algorithm to be used
	 * @param callback the callback for the session
	 */
	protected SessionImpl(AlgorithmWrapper algorithm, ParticipantSessionCallback callback) {
		super(algorithm, new SemaphoreLock("client-lock"));
		setSessionCallback(callback);
	}
	
	/**
	 * Sets the callback to be notified from the application layer.
	 * 
	 * @param callback the new callback
	 */
	public void setSessionCallback(ParticipantSessionCallback callback) {
		this.callback = callback == null ? NullSessionCallback.getInstance() : callback;
	}
	
	/**
	 * @return the callback to be notified from the application layer
	 */
	protected ParticipantSessionCallback getCallback() {
		return callback;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.ConfigurableSession#setConnection(ch.iserver.ace.net.SessionConnection)
	 */
	public void setConnection(SessionConnection connection) {
		ParameterValidator.notNull("connection", connection);
		this.connection = (SessionConnection) getThreadDomain().wrap(
				new SessionConnectionWrapper(connection, this), SessionConnection.class);
	}
	
	/**
	 * @return the connection to the network layer
	 */
	protected SessionConnection getConnection() {
		return connection;
	}
	
	/**
	 * @return the ThreadDomain used by the session to wrap the session 
	 *         connection
	 */
	public ThreadDomain getThreadDomain() {
		return threadDomain;
	}
	
	/**
	 * Sets the ThreadDomain used to wrap the SessionConnection.
	 * 
	 * @param threadDomain 
	 */
	public void setThreadDomain(ThreadDomain threadDomain) {
		this.threadDomain = threadDomain;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.AbstractSession#createAcknowledgeAction()
	 */
	protected AcknowledgeAction createAcknowledgeAction() {
		return new AcknowledgeAction() {
			public void execute() {
				lock();
				try {
					Timestamp timestamp = getAlgorithm().getTimestamp();
					int siteId = getAlgorithm().getSiteId();
					getConnection().sendAcknowledge(siteId, timestamp);
				} finally {
					unlock();
				}
			}
		};
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.Session#getParticipantId()
	 */
	public int getParticipantId() {
		return connection.getParticipantId();
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#leave()
	 */
	public void leave() {
		try {
			connection.leave();
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
		getConnection().sendRequest(request);
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#sendCaretUpdate(ch.iserver.ace.CaretUpdate)
	 */
	public void sendCaretUpdate(CaretUpdate update) {
		checkLockUsage();
		resetAcknowledgeTimer();
		CaretUpdateMessage message = getAlgorithm().generateCaretUpdateMessage(update);
		getConnection().sendCaretUpdateMessage(message);
	}

	// --> SessionConnectionCallback methods <--
	
	/**
	 * @see ch.iserver.ace.net.SessionConnectionCallback#kicked()
	 */
	public void kicked() {
		getCallback().kicked();
	}
	
	/**
	 * @see ch.iserver.ace.net.SessionConnectionCallback#sessionTerminated()
	 */
	public void sessionTerminated() {
		getCallback().sessionTerminated();
	}
	
	/**
	 * @see ch.iserver.ace.net.SessionConnectionCallback#receiveCaretUpdate(int, ch.iserver.ace.algorithm.CaretUpdateMessage)
	 */
	public void receiveCaretUpdate(int participantId, CaretUpdateMessage message) {
		lock();
		try {
			CaretUpdate update = getAlgorithm().receiveCaretUpdateMessage(message);
			getCallback().receiveCaretUpdate(getParticipant(participantId), update);
		} catch (TransformationException e) {
			getCallback().sessionFailed(Session.TRANSFORMATION_FAILED, e);
		} finally {
			unlock();
		}
	}
	
	/**
	 * @see ch.iserver.ace.net.SessionConnectionCallback#receiveRequest(int, ch.iserver.ace.algorithm.Request)
	 */
	public void receiveRequest(int participantId, Request request) {
		lock();
		try {
			Operation operation = getAlgorithm().receiveRequest(request);
			getCallback().receiveOperation(getParticipant(participantId), operation);
		} catch (TransformationException e) {
			getCallback().sessionFailed(Session.TRANSFORMATION_FAILED, e);
		} finally {
			unlock();
		}
	}
	
	/**
	 * @see ch.iserver.ace.net.SessionConnectionCallback#receiveAcknowledge(int, ch.iserver.ace.algorithm.Timestamp)
	 */
	public void receiveAcknowledge(int siteId, Timestamp timestamp) {
		lock();
		try {
			getAlgorithm().acknowledge(siteId, timestamp);
		} catch (TransformationException e) {
			getCallback().sessionFailed(Session.TRANSFORMATION_FAILED, e);
		} finally {
			unlock();
		}
	}
	
	/**
	 * @see ch.iserver.ace.net.SessionConnectionCallback#setDocument(ch.iserver.ace.net.PortableDocument)
	 */
	public void setDocument(PortableDocument document) {
		Map participants = new HashMap();
		int[] ids = document.getParticipantIds();
		for (int i = 0; i < ids.length; i++) {
			int id = ids[i];
			Participant participant = createParticipant(id, document.getUserProxy(id));
			participants.put(new Integer(id), participant);
			addParticipant(participant);
		}
		PortableDocumentWrapper wrapper = new PortableDocumentWrapper(document, participants);
		getCallback().setDocument(wrapper);
	}
	
	/**
	 * @see ch.iserver.ace.net.SessionConnectionCallback#participantJoined(int, ch.iserver.ace.net.RemoteUserProxy)
	 */
	public void participantJoined(int participantId, RemoteUserProxy proxy) {
		Participant participant = createParticipant(participantId, proxy);
		addParticipant(participant);
		getCallback().participantJoined(participant);
	}
	
	/**
	 * @see ch.iserver.ace.net.SessionConnectionCallback#participantLeft(int, int)
	 */
	public void participantLeft(int participantId, int reason) {
		Participant participant = getParticipant(participantId);
		removeParticipant(participant);
		getCallback().participantLeft(participant, reason);
	}
	
	// --> FailureHandler implementation <--
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.SessionConnectionFailureHandler#handleFailure(int, java.lang.Exception)
	 */
	public void handleFailure(int reason, Exception e) {
		getCallback().sessionFailed(reason, e);
	}
	
}
