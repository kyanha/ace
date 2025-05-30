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

package ch.iserver.ace.net.protocol;

import org.apache.log4j.Logger;

/**
 *
 */
public class ParticipantCleanup {

	private static Logger LOG = Logger.getLogger(ParticipantCleanup.class);
	
	private String docId, userId;
	
	public ParticipantCleanup(String docId, String userId) {
		this.docId = docId;
		this.userId = userId;
	}
	
	public void execute() {
		LOG.debug("--> execute()");
		RemoteUserSession session = SessionManager.getInstance().getSession(userId);
		session.removeParticipantConnection(docId);
		LOG.debug("<-- execute()");
	}
	
}
