/*
 * $Id:PublishDocumentPrepareFilter.java 1205 2005-11-14 07:57:10Z zbinl $
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
public class PublishDocumentPrepareFilter extends AbstractRequestFilter {

	private Logger LOG = Logger.getLogger(PublishDocumentPrepareFilter.class);
	
	private Serializer serializer;
	private ReplyListener listener;
	
	public PublishDocumentPrepareFilter(RequestFilter successor, Serializer serializer, ReplyListener listener) {
		super(successor);
		this.serializer = serializer;
		this.listener = listener;
	}
	
	
	/**
	 * @see ch.iserver.ace.net.impl.protocol.RequestFilter#process(ch.iserver.ace.net.impl.protocol.Request)
	 */
	public void process(Request request) {
		if (request.getType() == ProtocolConstants.PUBLISH) {
			if (SessionManager.getInstance().size() > 0) {
				Object doc = request.getPayload();
				try {
					byte[] data = serializer.createNotification(ProtocolConstants.PUBLISH, doc);
					
					//send data to each known remote user
					SessionManager manager = SessionManager.getInstance();
					Iterator iter = manager.getSessions().iterator();
					while (iter.hasNext()) {
						RemoteUserSession session = (RemoteUserSession)iter.next();
						ParticipantConnectionExt connection = session.getConnection();
						connection.send(data, doc.toString(), listener);
					}
					
				} catch (Exception e) {
					//TODO: handling
					LOG.error(e);
				}
			} 
		} else { //Forward
			super.process(request);
		}
	}

}
