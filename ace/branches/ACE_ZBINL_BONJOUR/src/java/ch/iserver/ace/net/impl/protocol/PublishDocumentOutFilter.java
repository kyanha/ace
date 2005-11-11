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

import java.util.Iterator;

import org.apache.log4j.Logger;

import ch.iserver.ace.net.impl.PublishedDocument;

/**
 *
 */
public class PublishDocumentOutFilter extends AbstractRequestFilter {

	private Logger LOG = Logger.getLogger(PublishDocumentOutFilter.class);
	
	public PublishDocumentOutFilter(RequestFilter successor) {
		super(successor);
	}
	
	
	/**
	 * @see ch.iserver.ace.net.impl.protocol.RequestFilter#process(ch.iserver.ace.net.impl.protocol.Request)
	 */
	public void process(Request request) {
		if (request.getType() == ProtocolConstants.PUBLISH) {
			PublishedDocument doc = (PublishedDocument)request.getPayload();
			try {
				byte[] data = SerializerImpl.getInstance().createNotification(ProtocolConstants.PUBLISH, doc);
				
				//send data to each known remote user
				SessionManager manager = SessionManager.getInstance();
				Iterator iter = manager.getSessions().values().iterator();
				while (iter.hasNext()) {
					RemoteUserSession session = (RemoteUserSession)iter.next();
					Connection connection = session.getConnection();
					connection.send(data, null, null);
				}
				
			} catch (Exception e) {
				//TODO: handling
				LOG.error(e);
			}
			
			
			
			
			
		} else { //Forward
			super.process(request);
		}
	}

}
