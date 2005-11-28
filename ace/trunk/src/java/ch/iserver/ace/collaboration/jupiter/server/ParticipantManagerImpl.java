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

package ch.iserver.ace.collaboration.jupiter.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.ParameterValidator;

/**
 * The default implementation of the ParticipantManager interface.
 */
public class ParticipantManagerImpl implements ParticipantManager {
	
	private static final Logger LOG = Logger.getLogger(ParticipantManager.class);
	
	/**
	 * The mapping from user id to participant id.
	 * 
	 * TODO: use LRU map instead
	 */
	private final Map userParticipantMapping = new HashMap();
	
	/**
	 * The current participant id. The next unknown user joining the
	 * session gets the id <code>currentParticipantId + 1</code>.
	 */
	private int currentParticipantId;
	
	/**
	 * The mapping from participant id to Forwarder object.
	 */
	private final Map forwarders = new HashMap();
	
	/**
	 * The mapping from participant id to ParticipantConnection.
	 */
	private final Map connections = new HashMap();
	
	/**
	 * The mapping from participant id to user id. 
	 */
	private final Map participants = new HashMap();
	
	/**
	 * The set of blacklisted users. Blacklisted users are no longer allowed
	 * to join the session.
	 */
	private final Set blacklist = new HashSet();
	
	/**
	 * The set of invited users. Invited users can join the session without
	 * prior callback to the application.
	 */
	private final Set invited = new HashSet();
	
	/**
	 * The set of currently joining users. If a join request is already in
	 * progress, a join should be rejected.
	 */
	private final Set joinSet = new HashSet();
	
	/**
	 * The CompositeForwarder which is used to manage forwarders.
	 */
	private CompositeForwarder compositeForwarder;
	
	/**
	 * Creates a new ParticipantManagerImpl instance that uses the given
	 * CompositeForwarder to manager forwarders.
	 * 
	 * @param forwarder the composite forwarder
	 */
	public ParticipantManagerImpl(CompositeForwarder forwarder) {
		ParameterValidator.notNull("forwarder", forwarder);
		this.compositeForwarder = forwarder;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantManager#getParticipantId(java.lang.String)
	 */
	public int getParticipantId(String userId) {
		Integer id = (Integer) userParticipantMapping.get(userId);
		if (id == null) {
			id = new Integer(nextParticipantId());
			userParticipantMapping.put(userId, id);
		}
		return id.intValue();
	}
	
	/**
	 * @return the next available participant id
	 */
	private synchronized int nextParticipantId() {
		return ++currentParticipantId;
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantManager#isBlackListed(java.lang.String)
	 */
	public boolean isBlackListed(String userId) {
		return blacklist.contains(userId);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantManager#isJoining(java.lang.String)
	 */
	public boolean isJoining(String userId) {
		return joinSet.contains(userId);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantManager#isInvited(java.lang.String)
	 */
	public boolean isInvited(String userId) {
		return invited.contains(userId);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantManager#isParticipant(java.lang.String)
	 */
	public boolean isParticipant(String userId) {
		return participants.containsValue(userId);
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantManager#addParticipant(int, ch.iserver.ace.collaboration.jupiter.server.Forwarder, ch.iserver.ace.net.ParticipantConnection)
	 */
	public void addParticipant(int participantId, Forwarder forwarder,
					ParticipantConnection connection) {
		LOG.info("addParticipant: " + participantId);
		Integer key = new Integer(participantId);
		RemoteUserProxy user = connection.getUser();
		if (user != null) {
			participants.put(new Integer(participantId), user.getId());
		} else if (participantId != 0) {
			LOG.warn("could not add to participants: " + participantId);
		}
		forwarders.put(key, forwarder);
		connections.put(key, connection);
		compositeForwarder.addForwarder(forwarder);
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantManager#removeParticipant(int)
	 */
	public void removeParticipant(int participantId) {
		LOG.info("removeParticipant: " + participantId);
		Integer key = new Integer(participantId);
		ParticipantConnection connection = getConnection(participantId);
		if (connection != null) {
			participants.remove(connection.getUser().getId());
		}
		Forwarder removed = (Forwarder) forwarders.remove(key);
		compositeForwarder.removeForwarder(removed);
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantManager#participantKicked(int)
	 */
	public void participantKicked(int participantId) {
		String user = getUser(participantId);
		if (user != null) {
			participants.remove(new Integer(participantId));
			blacklist.add(user);
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantManager#participantLeft(int)
	 */
	public void participantLeft(int participantId) {
		LOG.info("participantLeft: " + participantId);
		String user = getUser(participantId);
		if (user != null) {
			participants.remove(new Integer(participantId));
		}
	}
	
	/**
	 * Gets the user id of the given participant. Note, if there is no
	 * participant with the given id in the session, null is returned.
	 * 
	 * @param participantId the participant id
	 * @return the user id of that particular participant
	 */
	private String getUser(int participantId) {
		return (String) participants.get(new Integer(participantId));
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantManager#userInvited(String)
	 */
	public void userInvited(String userId) {
		ParameterValidator.notNull("userId", userId);
		blacklist.remove(userId);
		invited.add(userId);
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantManager#joinRequested(java.lang.String)
	 */
	public void joinRequested(String userId) {
		ParameterValidator.notNull("userId", userId);
		joinSet.add(userId);
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantManager#joinRequestAccepted(String)
	 */
	public void joinRequestAccepted(String userId) {
		ParameterValidator.notNull("userId", userId);
		joinSet.remove(userId);
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantManager#joinRequestRejected(String)
	 */
	public void joinRequestRejected(String userId) {
		ParameterValidator.notNull("userId", userId);
		joinSet.remove(userId);
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.ParticipantManager#getConnection(int)
	 */
	public ParticipantConnection getConnection(int participantId) {
		return (ParticipantConnection) connections.get(new Integer(participantId));
	}

}
