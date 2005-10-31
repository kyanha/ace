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
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.collaboration.SessionCallback;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.net.SessionConnection;
import ch.iserver.ace.net.SessionConnectionCallback;
import ch.iserver.ace.util.InterruptedRuntimeException;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class SessionImpl extends AbstractSession implements SessionConnectionCallback {
	
	private SessionCallback callback = NullSessionCallback.getInstance();
	
	private SessionConnection connection;
	
	public SessionImpl() {
		this(new Jupiter(true), null);
	}
	
	public SessionImpl(SessionCallback callback) {
		this(new Jupiter(true), callback);
	}
	
	protected SessionImpl(Algorithm algorithm, SessionCallback callback) {
		super(algorithm);
		setSessionCallback(callback);
	}
	
	public void setSessionCallback(SessionCallback callback) {
		ParameterValidator.notNull("callback", callback);
		this.callback = callback;
	}
	
	protected SessionCallback getCallback() {
		return callback;
	}
	
	protected void setConnection(SessionConnection connection) {
		ParameterValidator.notNull("connection", connection);
		this.connection = connection;
	}
	
	protected SessionConnection getConnection() {
		return connection;
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

	public void kicked() {
		getCallback().kicked();
	}
	
	public void sessionTerminated() {
		getCallback().sessionTerminated();
	}
	
	public void receiveCaretUpdate(int participantId, CaretUpdateMessage message) {
		try {
			lock();
			try {
				CaretUpdate update = getAlgorithm().receiveCaretUpdateMessage(message);
				getCallback().receiveCaretUpdate(getParticipant(participantId), update);
			} finally {
				unlock();
			}
		} catch (InterruptedException e) {
			// TODO: interrupted runtime exception
			throw new InterruptedRuntimeException("interrupted reception", e);
		}
	}
	
	public void receiveRequest(int participantId, Request request) {
		try {
			lock();
			try {
				Operation operation = getAlgorithm().receiveRequest(request);
				getCallback().receiveOperation(getParticipant(participantId), operation);
			} finally {
				unlock();
			}
		} catch (InterruptedException e) {
			// TODO: interrupted runtime exception
			throw new InterruptedRuntimeException("interrupted reception", e);
		}
	}
	
	public void setDocument(PortableDocument document) {
		getCallback().setDocument(document);		
	}
	
	public void userJoined(int participantId, RemoteUserProxy proxy) {
		RemoteUser user = new RemoteUserImpl(proxy);
		Participant participant = new ParticipantImpl(participantId, user);
		addParticipant(participant);
		fireParticipantJoined(participant);
	}
	
	public void userLeaved(int participantId, int reason) {
		Participant participant = getParticipant(participantId);
		removeParticipant(participant);
		fireParticipantLeft(participant, reason);
	}
	
}
