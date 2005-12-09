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

package ch.iserver.ace.net.protocol;

import java.util.Map;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.OutputDataStream;

import ch.iserver.ace.collaboration.JoinRequest;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.core.PublishedDocument;
import ch.iserver.ace.net.protocol.RequestImpl.DocumentInfo;

/**
 * Processes a Join request from another user for a particular document.
 */
public class JoinRequestRecipientFilter extends AbstractRequestFilter {

	private static Logger LOG = Logger.getLogger(JoinRequestRecipientFilter.class);
	
	private RequestFilter clientChain;
	
	public JoinRequestRecipientFilter(RequestFilter successor, RequestFilter clientChain) {
		super(successor);
		this.clientChain = clientChain;
	}
	
	public void process(Request request) {
		try {
			if (request.getType() == ProtocolConstants.JOIN) {
				LOG.info("--> process()");
				
				DocumentInfo info = (DocumentInfo) request.getPayload();
				Map publishedDocs = NetworkServiceImpl.getInstance().getPublishedDocuments();
				PublishedDocument documentToJoin = (PublishedDocument) publishedDocs.get(info.getDocId());
				if (documentToJoin != null && !documentToJoin.isShutdown()) {
					RemoteUserSession session = SessionManager.getInstance().getSession(request.getUserId());
					ParticipantConnectionImpl connection = session.addParticipantConnection(info.getDocId());
					String userId = request.getUserId();
					if (documentToJoin.isUserInvited(userId)) {
						documentToJoin.joinInvitedUser(userId, connection);
					} else {
						documentToJoin.join(connection);
					}
				} else {
					if (documentToJoin == null) {
						LOG.warn("join request for not available document [" + info.getDocId() + "] received");
					} else {
						LOG.warn("join request for shutdown document [" + info.getDocId() + "] received");
					}
					info.setData(Integer.toString(JoinRequest.SHUTDOWN));
					Request response = new RequestImpl(ProtocolConstants.JOIN_REJECTED, request.getUserId(), info);
					clientChain.process(response);
				}
				
				try {
					//confirm reception of msg	
					OutputDataStream os = new OutputDataStream();
					os.setComplete();
					request.getMessage().sendRPY(os);
//					request.getMessage().sendNUL();
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
