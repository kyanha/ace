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
import org.beepcore.beep.core.ReplyListener;

import ch.iserver.ace.net.impl.RemoteUserProxyExt;

/**
 *
 */
public class DocumentDiscoveryPrepareFilter extends AbstractRequestFilter {

	private Logger LOG = Logger.getLogger(DocumentDiscoveryPrepareFilter.class);
	
	private Serializer serializer;
	private SessionManager manager;
	private ReplyListener listener;
	
	public DocumentDiscoveryPrepareFilter(RequestFilter successor, Serializer serializer, ReplyListener listener) {
		super(successor);
		this.serializer = serializer;
		manager = SessionManager.getInstance();
		this.listener = listener;
	}
	
	public void process(Request request) {
		if (request.getType() == ProtocolConstants.PUBLISHED_DOCUMENTS) {
			RemoteUserProxyExt user = (RemoteUserProxyExt)request.getPayload(); 
			RemoteUserSession session = manager.createSession(user); 
			Connection connection = session.getConnection();
			byte[] query = null;
			try {
				query = serializer.createQuery(ProtocolConstants.PUBLISHED_DOCUMENTS);
				connection.send(query, new QueryInfo(user.getId(), ProtocolConstants.PUBLISHED_DOCUMENTS), listener);
			} catch (Exception e) {
				//TODO: handling
				LOG.error("could no serialize ["+e.getMessage()+"]");
			}
			
			
		} else {
			super.process(request);
		}
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
		
		public String toString() {
			return "QueryInfo("+id+", "+queryType+")";
		}
	}
	
}
