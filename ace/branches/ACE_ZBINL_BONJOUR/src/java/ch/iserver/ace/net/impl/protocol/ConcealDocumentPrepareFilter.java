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
import org.beepcore.beep.core.ReplyListener;

import ch.iserver.ace.net.impl.PublishedDocument;

/**
 *
 */
public class ConcealDocumentPrepareFilter extends AbstractRequestFilter {

	private Logger LOG = Logger.getLogger(ConcealDocumentPrepareFilter.class);
	
	private Serializer serializer;
	private ReplyListener listener;
	
	public ConcealDocumentPrepareFilter(RequestFilter successor, Serializer serializer, ReplyListener listener) {
		super(successor);
		this.serializer = serializer;
		this.listener = listener;
		//TODO: how about a NullReplyListener for notifications where no answer is expected?
	}

	public void process(Request request) {
		if (request.getType() == ProtocolConstants.CONCEAL) {
			PublishedDocument doc = (PublishedDocument)request.getPayload();
			try {
				byte[] data = serializer.createNotification(ProtocolConstants.CONCEAL, doc);
			
				//send data to each known remote user
				SessionManager manager = SessionManager.getInstance();
				Iterator iter = manager.getSessions().values().iterator();
				while (iter.hasNext()) {
					RemoteUserSession session = (RemoteUserSession)iter.next();
					Connection connection = session.getConnection();
					connection.send(data, doc.toString(), listener);
				}
				
			} catch (Exception e) {
				LOG.error(e);
			}
			
		} else {
			super.process(request);
		}
	}
	
}
