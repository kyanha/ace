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

import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.PublishedSessionCallback;
import ch.iserver.ace.util.BlockingQueue;
import ch.iserver.ace.util.LinkedBlockingQueue;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.Worker;

/**
 *
 */
public class PublisherConnectionImpl implements PublisherConnection {
	
	private final PublishedSessionCallback callback;
	
	private final BlockingQueue queue;
	
	private final AlgorithmWrapper algorithm;
	
	private final Lock lock;
	
	private final Worker queueWorker;
	
	public PublisherConnectionImpl(PublishedSessionCallback callback, Lock lock, AlgorithmWrapper algorithm) {
		ParameterValidator.notNull("callback", callback);
		ParameterValidator.notNull("lock", lock);
		ParameterValidator.notNull("algorithm", algorithm);
		this.callback = callback;
		this.lock = lock;
		this.algorithm = algorithm;
		this.queue = new LinkedBlockingQueue();
		this.queueWorker = new CallbackWorker(callback, queue);
		this.queueWorker.start();
	}
	
	protected PublishedSessionCallback getCallback() {
		return callback;
	}
	
	protected BlockingQueue getCallbackQueue() {
		return queue;
	}
	
	protected AlgorithmWrapper getAlgorithm() {
		return algorithm;
	}
	
	protected Lock getLock() {
		return lock;
	}
	
	protected Worker getQueueWorker() {
		return queueWorker;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.PublisherConnection#close()
	 */
	public void close() {
		queueWorker.kill();
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.PublisherConnection#receiveRequest(ch.iserver.ace.collaboration.Participant, ch.iserver.ace.algorithm.Request)
	 */
	public void receiveRequest(Participant participant, Request request) {
		Command command = new RequestCommand(getLock(), getAlgorithm(), participant, request);
		getCallbackQueue().add(command);
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.PublisherConnection#receiveCaretUpdateMessage(ch.iserver.ace.collaboration.Participant, ch.iserver.ace.algorithm.CaretUpdateMessage)
	 */
	public void receiveCaretUpdateMessage(Participant participant,
					CaretUpdateMessage message) {
		Command command = new CaretUpdateCommand(getLock(), getAlgorithm(), participant, message);
		getCallbackQueue().add(command);
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.PublisherConnection#participantJoined(ch.iserver.ace.collaboration.Participant)
	 */
	public void participantJoined(Participant participant) {
		getCallback().participantJoined(participant);
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.PublisherConnection#participantLeft(ch.iserver.ace.collaboration.Participant, int)
	 */
	public void participantLeft(Participant participant, int reason) {
		getCallback().participantLeft(participant, reason);
	}

}
