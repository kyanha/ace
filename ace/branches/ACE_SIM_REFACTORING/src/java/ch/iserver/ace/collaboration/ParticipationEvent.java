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

package ch.iserver.ace.collaboration;

import java.util.EventObject;


/**
 * Event object for participation related events. The participation event
 * provides access to the Participant as well as to the concerned session.
 */
public class ParticipationEvent extends EventObject {
	
	/**
	 * Code specifying that the user joined the session.
	 */
	public static final int JOINED = 1;
	
	/**
	 * Code specifying that the user left the session.
	 */
	public static final int LEFT = 2;
	
	/**
	 * Code specifying that the user was kicked from the session.
	 */
	public static final int KICKED = 3;
	
	/**
	 * The session to which a participant joined or leaved.
	 */
	private final Session session;
	
	/**
	 * The participant that joined or leaved.
	 */
	private final Participant participant;
	
	/**
	 * The code that specifies what exactly happened. This must be either of
	 * <code>LEAVED</code>, <code>JOINED</code>, or <code>KICKED</code>.
	 */
	private final int code;
	
	public ParticipationEvent(Session source, Participant participant, int reason) {
		super(source);
		if (source == null) {
			throw new IllegalArgumentException("source cannot be null");
		}
		if (participant == null) {
			throw new IllegalArgumentException("participant cannot be null");
		}
		if (reason < 1 || reason > 3) {
			throw new IllegalArgumentException("reason must be one of JOINED, LEFT, or KICKED");
		}
		this.session = source;
		this.participant = participant;
		this.code = reason;
	}
		
	public Session getSession() {
		return session;
	}
	
	public Participant getParticipant() {
		return participant;
	}
	
	public int getCode() {
		return code;
	}
	
}
