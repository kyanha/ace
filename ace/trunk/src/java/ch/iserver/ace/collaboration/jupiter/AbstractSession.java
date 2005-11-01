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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.Session;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.SemaphoreLock;

/**
 * Abstract base class for Session implementations. Implements the common methods
 * of both PublishedSessionImpl and SessionImpl.
 */
abstract class AbstractSession implements Session {
	
	/**
	 * The Lock used to guard the access to the Algorithm.
	 */
	private final Lock lock;
	
	/**
	 * The AlgorithmWrapper wrapping the Algorithm.
	 */
	private final AlgorithmWrapper algorithm;
	
	/**
	 * The current Set of Participant objects.
	 */
	private final Set participants = new HashSet();
	
	/**
	 * A mapping from participant id to Participant objects.
	 */
	private final Map participantMap = new HashMap();
	
	/**
	 * Creates a new AbstractSession that uses the given Algorithm.
	 * 
	 * @param algorithm the Algorithm used by the Session
	 */
	protected AbstractSession(AlgorithmWrapper algorithm) {
		ParameterValidator.notNull("algorithm", algorithm);
		this.lock = new SemaphoreLock();
		this.algorithm = algorithm;
	}
	
	/**
	 * @return the AlgorithmWrapper
	 */
	protected AlgorithmWrapper getAlgorithm() {
		return algorithm;
	}
	
	/**
	 * @return the Lock guarding the access to the Algorithm
	 */
	protected Lock getLock() {
		return lock;
	}
	
	/**
	 * Checks whether calls to the send methods are properly wrapped in 
	 * lock/unlock calls.
	 * 
	 * @throws IllegalMonitorStateException if the Session is not properly locked
	 *                       before sending operations and caret updates
	 */
	protected synchronized void checkLockUsage() {
		if (!lock.isOwner(Thread.currentThread())) {
			throw new IllegalMonitorStateException("Lock the Session before sending.");
		}
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#lock()
	 */
	public void lock() throws InterruptedException {
		lock.lock();
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#unlock()
	 */
	public void unlock() {
		lock.unlock();
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#getParticipants()
	 */
	public Set getParticipants() {
		return Collections.unmodifiableSet(participants);
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#getParticipant(int)
	 */
	public Participant getParticipant(int participantId) {
		return (Participant) participantMap.get(new Integer(participantId));
	}

	/**
	 * Adds a Participant to the current set of participants in this Session.
	 * 
	 * @param participant the Participant to be added
	 */
	protected void addParticipant(Participant participant) {
		participants.add(participant);
		participantMap.put(new Integer(participant.getParticipantId()), participant);
	}

	/**
	 * Removes a Participant from the current set of participants in the
	 * Session.
	 * 
	 * @param participant the Participant to be removed
	 */
	protected void removeParticipant(Participant participant) {
		participants.remove(participant);
		participantMap.remove(new Integer(participant.getParticipantId()));
	}

}
