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
import ch.iserver.ace.algorithm.TransformationException;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.SessionCallback;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.SessionConnection;
import ch.iserver.ace.net.SessionConnectionCallback;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.SemaphoreLock;

/**
 * Default implementation of the Session interface. This class further implements
 * the SessionConnectionCallback interface and can thus be set as callback
 * on SessionConnections.
 */
public class SessionImpl extends AbstractSession implements SessionConnectionCallback, SessionConnectionFailureHandler {
	
	/**
	 * The SessionCallback from the application layer.
	 */
	private SessionCallback callback = NullSessionCallback.getInstance();
	
	/**
	 * The SessionConnection from the network layer.
	 */
	private SessionConnection connection;
	
	private SessionConnectionDecorator connectionDecorator;
	
	public SessionImpl() {
		this(new AlgorithmWrapperImpl(new Jupiter(true)));
	}
	
	public SessionImpl(SessionCallback callback) {
		this(new AlgorithmWrapperImpl(new Jupiter(true)), callback);
	}
		
	public SessionImpl(AlgorithmWrapper algorithm) {
		this(algorithm, (SessionCallback) null);
	}
	
	public SessionImpl(AlgorithmWrapper wrapper, Lock lock) {
		super(wrapper, lock);
	}
	
	protected SessionImpl(AlgorithmWrapper algorithm, SessionCallback callback) {
		super(algorithm, new SemaphoreLock("client-lock"));
		setSessionCallback(callback);
	}
	
	/**
	 * Sets the callback to be notified from the application layer.
	 * 
	 * @param callback the new callback
	 */
	public void setSessionCallback(SessionCallback callback) {
		this.callback = callback == null ? NullSessionCallback.getInstance() : callback;
	}
	
	/**
	 * @return the callback to be notified from the application layer
	 */
	protected SessionCallback getCallback() {
		return callback;
	}
	
	/**
	 * Sets the connection to the network layer.
	 * 
	 * @param connection the new connection
	 */
	protected void setConnection(SessionConnection connection) {
		ParameterValidator.notNull("connection", connection);
		this.connection = getConnectionDecorator().decorate(new SessionConnectionWrapper(connection, this));
	}
	
	/**
	 * @return the connection to the network layer
	 */
	protected SessionConnection getConnection() {
		return connection;
	}
	
	public SessionConnectionDecorator getConnectionDecorator() {
		return connectionDecorator;
	}
	
	public void setConnectionDecorator(SessionConnectionDecorator connectionDecorator) {
		this.connectionDecorator = connectionDecorator;
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
		connection.leave();
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#sendOperation(ch.iserver.ace.Operation)
	 */
	public void sendOperation(Operation operation) {
		checkLockUsage();
		Request request = getAlgorithm().generateRequest(operation);
		getConnection().sendRequest(request);
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#sendCaretUpdate(ch.iserver.ace.CaretUpdate)
	 */
	public void sendCaretUpdate(CaretUpdate update) {
		checkLockUsage();
		CaretUpdateMessage message = getAlgorithm().generateCaretUpdateMessage(update);
		getConnection().sendCaretUpdate(message);
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
			// TODO: handle receive failed
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
			// TODO: handle receive failed
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
			participants.put(new Integer(id), createParticipant(id, document.getUserProxy(id)));
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
