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
import org.beepcore.beep.core.ReplyListener;

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.core.RemoteUserProxyExt;

/**
 *
 */
public class SendDocumentsPrepareFilter extends AbstractRequestFilter {

private static Logger LOG = Logger.getLogger(SendDocumentsPrepareFilter.class);
	
	private Serializer serializer;
	private ReplyListener listener;
	
	public SendDocumentsPrepareFilter(RequestFilter successor, Serializer serializer, ReplyListener listener) {
		super(successor);
		this.serializer = serializer;
		this.listener = listener;
	}
	
	public void process(Request request) {
		if (request.getType() == ProtocolConstants.SEND_DOCUMENTS) {
			LOG.info("--> process()");
			processImpl(request);
			LOG.info("<-- process()");
		} else { //forward
			super.process(request);
		}
	}

	private void processImpl(Request request) {
		RemoteUserProxyExt user = (RemoteUserProxyExt)request.getPayload();
		try {			
			RemoteUserSession session = SessionManager.getInstance().getSession(user.getId());
			if (session == null) {
				session = SessionManager.getInstance().createSession(user);
			}
			MainConnection connection = session.getMainConnection();
			Map publishedDocs = NetworkServiceImpl.getInstance().getPublishedDocuments();
			byte[] message = serializer.createNotification(ProtocolConstants.SEND_DOCUMENTS, publishedDocs);	
			connection.send(message, user.getUserDetails().getUsername(), listener);
		} catch (ConnectionException ce) {
			LOG.error("connection failure for session ["+user.getUserDetails()+"] "+ce.getMessage());
			NetworkServiceImpl.getInstance().getCallback().serviceFailure(
					FailureCodes.REMOTE_USER_FAILURE, user.getUserDetails().getUsername(), ce);
			//TODO: remove userProxy and userSession?
		} catch (Exception e) {
			LOG.error("process problem ["+e.getMessage()+"]");
		}
	}
}
