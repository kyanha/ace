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
import ch.iserver.ace.algorithm.jupiter.Jupiter;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.PublishedSession;
import ch.iserver.ace.collaboration.PublishedSessionCallback;
import ch.iserver.ace.collaboration.jupiter.server.ServerLogic;
import ch.iserver.ace.net.ParticipantPort;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.PublisherConnection;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.InterruptedRuntimeException;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class PublishedSessionImpl extends AbstractSession implements PublishedSession, PublisherConnection {
	
	private ServerLogic logic;
	
	private ParticipantPort port;
	
	private final PublishedSessionCallback callback;
	
	public PublishedSessionImpl(PublishedSessionCallback callback) {
		super(new Jupiter(true));
		ParameterValidator.notNull("callback", callback);
		this.callback = callback;
	}
	
	public void setServerLogic(ServerLogic logic) {
		ParameterValidator.notNull("logic", logic);
		this.logic = logic;
		this.port = logic.getPublisherPort();

	}
	
	public ServerLogic getLogic() {
		return logic;
	}
	
	protected ParticipantPort getPort() {
		return port;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.PublishedSession#setDocumentDetails(ch.iserver.ace.DocumentDetails)
	 */
	public void setDocumentDetails(DocumentDetails details) {
		getLogic().setDocumentDetails(details);
	}

	/**
	 * @see ch.iserver.ace.collaboration.PublishedSession#kick(ch.iserver.ace.collaboration.Participant)
	 */
	public void kick(Participant participant) {
		getLogic().kick(participant);
	}

	/**
	 * @see ch.iserver.ace.collaboration.PublishedSession#conceal()
	 */
	public void conceal() {
		getLogic().shutdown();
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
		conceal();
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#sendOperation(ch.iserver.ace.Operation)
	 */
	public void sendOperation(Operation operation) {
		checkLockUsage();
		Request request = getAlgorithm().generateRequest(operation);
		getPort().receiveRequest(request);
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#sendCaretUpdate(ch.iserver.ace.CaretUpdate)
	 */
	public void sendCaretUpdate(CaretUpdate update) {
		checkLockUsage();
		CaretUpdateMessage message = getAlgorithm().generateCaretUpdateMessage(update);
		getPort().receiveCaretUpdate(message);
	}
					
		
	protected PublishedSessionCallback getCallback() {
		return callback;
	}
		
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.ParticipantConnection#setParticipantId(int)
	 */
	public void setParticipantId(int participantId) {
		// ignore, participant id can be retrieved form port...
	}
				
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.ParticipantConnection#getUser()
	 */
	public RemoteUserProxy getUser() {
		// TODO: is this a problem?
		return null;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.ParticipantConnection#close()
	 */
	public void close() {
		// ignore, PublishedSession is the owner			
	}
		
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.ParticipantConnection#sendCaretUpdate(int, ch.iserver.ace.algorithm.CaretUpdateMessage)
	 */
	public void sendCaretUpdate(int participantId, CaretUpdateMessage message) {
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
		
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.ParticipantConnection#sendRequest(int, ch.iserver.ace.algorithm.Request)
	 */
	public void sendRequest(int participantId, Request request) {
		try {
			lock();
			try {
				Operation op = getAlgorithm().receiveRequest(request);
				getCallback().receiveOperation(getParticipant(participantId), op);
			} finally {
				unlock();
			}
		} catch (InterruptedException e) {
			// TODO: interrupted runtime exception
			throw new InterruptedRuntimeException("interrupted reception", e);
		}
	}
		
	/**
	 * @see ch.iserver.ace.net.ParticipantConnection#sendDocument(ch.iserver.ace.net.PortableDocument)
	 */
	public void sendDocument(PortableDocument document) {
		throw new UnsupportedOperationException("sendDocument is not supported for PublisherConnection objects");	
	}
		
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.ParticipantConnection#sendParticipantJoined(ch.iserver.ace.collaboration.Participant)
	 */
	public void sendParticipantJoined(Participant participant) {
		addParticipant(participant);
		fireParticipantJoined(participant);			
	}
		
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.ParticipantConnection#sendParticipantLeft(int, int)
	 */
	public void sendParticipantLeft(int participantId, int reason) {
		Participant participant = getParticipant(participantId);
		removeParticipant(participant);
		fireParticipantLeft(participant, reason);
	}
		
	/**
	 * @see ch.iserver.ace.net.ParticipantConnection#sendKicked()
	 */
	public void sendKicked() {
		throw new UnsupportedOperationException("publisher cannot be kicked");
	}
		
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.PublisherConnection#retrieveDocument()
	 */
	public PortableDocument retrieveDocument() {
		return getCallback().getDocument();
	}
	
}
