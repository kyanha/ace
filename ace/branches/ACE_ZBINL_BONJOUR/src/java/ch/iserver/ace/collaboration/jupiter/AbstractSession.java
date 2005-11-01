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

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.ParticipationEvent;
import ch.iserver.ace.collaboration.ParticipationListener;
import ch.iserver.ace.collaboration.Session;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.SemaphoreLock;

/**
 *
 */
public abstract class AbstractSession implements Session {

	private final EventListenerList listeners = new EventListenerList();
	private final Lock lock;
	private final AlgorithmWrapper algorithm;
	private final Set participants = new HashSet();
	private final Map participantMap = new HashMap();

	protected AbstractSession(Algorithm algorithm) {
		ParameterValidator.notNull("algorithm", algorithm);
		this.lock = new SemaphoreLock();
		this.algorithm = new AlgorithmWrapperImpl(algorithm);
	}
	
	protected AlgorithmWrapper getAlgorithm() {
		return algorithm;
	}
	
	protected Lock getLock() {
		return lock;
	}
	
	protected synchronized void checkLockUsage() {
		if (!lock.isOwner(Thread.currentThread())) {
			throw new IllegalMonitorStateException("Lock the Session before sending.");
		}
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#addParticipationListener(ch.iserver.ace.collaboration.ParticipationListener)
	 */
	public void addParticipationListener(ParticipationListener listener) {
		ParameterValidator.notNull("listener", listener);
		listeners.add(ParticipationListener.class, listener);
	}

	/**
	 * @see ch.iserver.ace.collaboration.Session#removeParticipationListener(ch.iserver.ace.collaboration.ParticipationListener)
	 */
	public void removeParticipationListener(ParticipationListener listener) {
		listeners.remove(ParticipationListener.class, listener);
	}

	protected void fireParticipantJoined(final Participant participant) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ParticipationListener[] lists = (ParticipationListener[]) listeners.getListeners(ParticipationListener.class);
				ParticipationEvent event = null;
				for (int i = 0; i < lists.length; i++) {
					ParticipationListener listener = lists[i];
					if (event == null) {
						event = new ParticipationEvent(AbstractSession.this, participant, ParticipationEvent.JOINED);
					}
					listener.userJoined(event);
				}
			}
		});
	}

	protected void fireParticipantLeft(final Participant participant, final int reason) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ParticipationListener[] lists = (ParticipationListener[]) listeners.getListeners(ParticipationListener.class);
				ParticipationEvent event = null;
				for (int i = 0; i < lists.length; i++) {
					ParticipationListener listener = lists[i];
					if (event == null) {
						event = new ParticipationEvent(AbstractSession.this, participant, reason);
					}
					listener.userLeft(event);
				}
			}
		});
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

	protected void addParticipant(Participant participant) {
		participants.add(participant);
		participantMap.put(new Integer(participant.getParticipantId()), participant);
	}

	protected void removeParticipant(Participant participant) {
		participants.remove(participant);
		participantMap.remove(new Integer(participant.getParticipantId()));
	}

}
