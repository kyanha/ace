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

import org.beepcore.beep.core.ReplyListener;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.RemoteUserProxy;

/**
 *
 */
public class DocumentDiscoveryImpl implements DocumentDiscovery {

	private Serializer serializer;
	private SessionHandler handler;
	private ReplyListener listener;
	
	//TODO: singleton?
	public DocumentDiscoveryImpl(Serializer serializer, ReplyListener listener) {
		this.serializer = serializer;
		this.listener = listener;
		this.handler = SessionHandler.getInstance();
	}
	
	/**
	 * @see ch.iserver.ace.net.protocol.DocumentDiscovery#execute(ch.iserver.ace.net.RemoteUserProxy)
	 */
	public void execute(RemoteUserProxy proxy) {
		String userId = proxy.getId();
		RemoteUserSession session = handler.createSession(proxy); 
		Connection connection = session.getConnection();
		byte[] query = null;
		try {
			query = serializer.createQuery(Serializer.PUBLISHED_DOCUMENTS);
		} catch (SerializeException se) {
			//TODO: 
			se.printStackTrace();
		}
		try {
			connection.send(query, new QueryInfo(userId, Serializer.PUBLISHED_DOCUMENTS), listener);
		} catch (ProtocolException pe) {
			//TOOD: handling
			pe.printStackTrace();
		}
	}

	/**
	 * @inheritDoc
	 */
	public void setCallback(DocumentDiscoveryCallback callback) {
		//TODO: create NullDocumentDiscoveryCallback
		
	}
	
	static class QueryInfo {
		
		private String id;
		private int queryType;
		
		public QueryInfo(String id, int queryType) {
			this.id = id;
			this.queryType = queryType;
		}
		
		public String getId() {
			return id;
		}
		
		public int getQueryType() {
			return queryType;
		}
		
	}

}
