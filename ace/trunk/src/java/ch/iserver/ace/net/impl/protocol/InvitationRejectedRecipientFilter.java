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

import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.PublishedDocument;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

/**
 *
 */
public class InvitationRejectedRecipientFilter extends AbstractRequestFilter {

	private Logger LOG = Logger.getLogger(InvitationRejectedRecipientFilter.class);
	
	public InvitationRejectedRecipientFilter(RequestFilter successor) {
		super(successor);
	}
	
	public void process(Request request) {
		if (request.getType() == ProtocolConstants.INVITE_REJECTED) {
			LOG.info("--> process()");
			
			DocumentInfo info = (DocumentInfo) request.getPayload();
			String docId = info.getDocId();
			PublishedDocument doc = (PublishedDocument) NetworkServiceImpl.getInstance().getPublishedDocuments().get(docId);
			if (doc != null) {
				String userId = info.getUserId();
				doc.rejectInvitedUser(userId);
			} else {
				LOG.debug("received 'invitation rejected' from [" + info.getUserId() + "] " +
						"for already concealed document [" + docId + "]");
			}
			
			try {
				//confirm reception of msg				
				request.getMessage().sendNUL();
			} catch (Exception e) {
				LOG.error("could not send confirmation ["+e.getMessage()+"]");
			}
			LOG.info("<-- process()");
		} else { //Forward
			super.process(request);
		}
	}

}
