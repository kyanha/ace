/*
 * $Id:PublishDocumentReceiveFilter.java 1205 2005-11-14 07:57:10Z zbinl $
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

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.RemoteDocumentProxyImpl;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

/**
 *
 */
public class PublishDocumentReceiveFilter extends AbstractRequestFilter {

	private Logger LOG = Logger.getLogger(PublishDocumentReceiveFilter.class);
	
	public PublishDocumentReceiveFilter(RequestFilter successor) {
		super(successor);
	}

	public void process(Request request) {
		LOG.info("--> process("+request+")");
		if (request.getType() == ProtocolConstants.PUBLISH) {
			
			DocumentInfo info = (DocumentInfo) request.getPayload();
			String userId = info.getUserId();
			RemoteUserSession session = SessionManager.getInstance().getSession(userId);
			LOG.info("found session ["+session.getUser().getUserDetails().getUsername()+"] for request");
			RemoteUserProxyExt publisher = session.getUser();
			RemoteDocumentProxy doc = new RemoteDocumentProxyImpl(
					info.getDocId(), new DocumentDetails(info.getName()), publisher);
			publisher.addSharedDocument(doc);
			NetworkServiceCallback callback = NetworkServiceImpl.getInstance().getCallback();
			RemoteDocumentProxy[] docs = new RemoteDocumentProxy[]{ doc };
			callback.documentDiscovered(docs);

			try {
				//confirm reception of msg				
				request.getMessage().sendNUL();
			} catch (Exception e) {
				LOG.error("could not send Nul confirmation ["+e.getMessage()+"]");
			}
		} else {
			super.process(request);
		}
		LOG.info("<-- process()");
	}
	
}
