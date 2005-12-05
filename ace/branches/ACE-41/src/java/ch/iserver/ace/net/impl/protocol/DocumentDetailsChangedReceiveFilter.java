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

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.RemoteDocumentProxyExt;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

/**
 *
 */
public class DocumentDetailsChangedReceiveFilter extends AbstractRequestFilter {

	private Logger LOG = Logger.getLogger(DocumentDetailsChangedReceiveFilter.class);
	
	public DocumentDetailsChangedReceiveFilter(RequestFilter successor) {
		super(successor);
	}
	
	public void process(Request request) {
		try {
			if (request.getType() == ProtocolConstants.DOCUMENT_DETAILS_CHANGED) {
				LOG.info("--> process()");
				DocumentInfo info = (DocumentInfo) request.getPayload();
				String userId = info.getUserId();
				RemoteUserSession session = SessionManager.getInstance().getSession(userId);
				if (session != null) {
					RemoteDocumentProxyExt proxy = session.getUser().getSharedDocument(info.getDocId());
					if (proxy != null) {
						DocumentDetails details = new DocumentDetails(info.getName());
						proxy.setDocumentDetails(details);
						NetworkServiceCallback callback = NetworkServiceImpl.getInstance().getCallback();
						callback.documentDetailsChanged(proxy);
					} else {
						LOG.warn("RemoteUserProxy for [" + userId + "] not found");
					}
				} else {
					LOG.warn("RemoteUserSession for [" + userId + "] not found");
				}
				
				try {
					//confirm reception of msg				
					request.getMessage().sendNUL();
				} catch (Exception e) {
					LOG.error("could not send Nul confirmation ["+e.getMessage()+"]");
				}
				LOG.info("<-- process()");
			} else { //Forward
				super.process(request);
			} 
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("exception processing request ["+e+", "+e.getMessage()+"]");
		}
	}
}
