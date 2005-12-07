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
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

/**
 *
 */
public class JoinRejectedRecipientFilter extends AbstractRequestFilter {

	private static Logger LOG = Logger.getLogger(JoinRejectedRecipientFilter.class);
	
	public JoinRejectedRecipientFilter(RequestFilter successor) {
		super(successor);
	}
	
	public void process(Request request) {
		try {
			if (request.getType() == ProtocolConstants.JOIN_REJECTED) {
				LOG.info("--> process()");
				
				DocumentInfo info = (DocumentInfo) request.getPayload();
				RemoteUserSession session = SessionManager.getInstance().getSession(request.getUserId());
				RemoteDocumentProxyExt doc = session.getUser().getSharedDocument(info.getDocId());
				if (doc!= null) {
					String reason = info.getData();
					doc.joinRejected(Integer.parseInt(reason));
				} else {
					LOG.debug("received joinRejected for concealed document");
				}
				
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
		} finally {
			
		}
	}

}
