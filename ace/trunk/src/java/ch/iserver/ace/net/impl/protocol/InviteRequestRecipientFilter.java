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

import ch.iserver.ace.net.InvitationProxy;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.impl.InvitationProxyFactory;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

/**
 *
 */
public class InviteRequestRecipientFilter extends AbstractRequestFilter {

	private static Logger LOG = Logger.getLogger(InviteRequestRecipientFilter.class);
	
	public InviteRequestRecipientFilter(RequestFilter successor) {
		super(successor);
	}

	public void process(Request request) {
		try {
			if (request.getType() == ProtocolConstants.INVITE) {
				LOG.info("--> process()");		
				NetworkServiceCallback callback = NetworkServiceImpl.getInstance().getCallback();
				DocumentInfo info = (DocumentInfo) request.getPayload();
				RemoteUserSession session = SessionManager.getInstance().getSession(info.getUserId());
				RemoteDocumentProxy proxy = session.getUser().getSharedDocument(info.getDocId());
				InvitationProxy invitation = InvitationProxyFactory.getInstance().createProxy(proxy, session);
				callback.invitationReceived(invitation);
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
