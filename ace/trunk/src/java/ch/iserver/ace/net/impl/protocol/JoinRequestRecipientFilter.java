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

import java.util.Map;

import org.apache.log4j.Logger;

import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.PublishedDocument;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

/**
 * Processes a Join request from another user for a particular document.
 */
public class JoinRequestRecipientFilter extends AbstractRequestFilter {

	private static Logger LOG = Logger.getLogger(JoinRequestRecipientFilter.class);
	
	public JoinRequestRecipientFilter(RequestFilter successor) {
		super(successor);
	}
	
	public void process(Request request) {
		try {
			if (request.getType() == ProtocolConstants.JOIN) {
				LOG.info("--> process()");
				
				DocumentInfo info = (DocumentInfo) request.getPayload();
				Map publishedDocs = NetworkServiceImpl.getInstance().getPublishedDocuments();
				PublishedDocument documentToJoin = (PublishedDocument) publishedDocs.get(info.getDocId());
				RemoteUserSession session = SessionManager.getInstance().getSession(request.getUserId());
				
				ParticipantConnectionImpl connection = session.createParticipantConnection(info.getDocId());
				connection.setPublishedDocument(documentToJoin);
				documentToJoin.join(connection);
				
				try {
					//confirm reception of msg				
					request.getMessage().sendNUL();
				} catch (Exception e) {
					LOG.error("could not send confirmation ["+e.getMessage()+"]");
				}
				LOG.info("<-- process()");
			} else {
				super.process(request);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("exception processing request ["+e+", "+e.getMessage()+"]");
		}	
	}

}
