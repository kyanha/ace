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

package ch.iserver.ace.net.impl.protocol;

import org.apache.log4j.Logger;

import ch.iserver.ace.net.impl.RemoteDocumentProxyExt;

/**
 *
 */
public class SessionCleanup {

	private static Logger LOG = Logger.getLogger(SessionCleanup.class);
	
	private String docId, userId;
	
	public SessionCleanup(String docId, String userId) {
		this.docId = docId;
		this.userId = userId;
	}
	
	/**
	 * Executes all the cleanup work after a document was left by
	 * the participant. 
	 */
	public void execute() {
		LOG.debug("--> execute()");
		RemoteUserSession session = SessionManager.getInstance().getSession(userId);
		session.removeSessionConnection(docId);
		RemoteDocumentProxyExt doc = session.getUser().getSharedDocument(docId);
		doc.cleanupAfterLeave();
		LOG.debug("<-- execute()");
	}
	
}
