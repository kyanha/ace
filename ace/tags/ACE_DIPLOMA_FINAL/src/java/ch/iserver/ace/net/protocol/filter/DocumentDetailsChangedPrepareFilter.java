/*
 * $Id:DocumentDetailsChangedPrepareFilter.java 2413 2005-12-09 13:20:12Z zbinl $
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

package ch.iserver.ace.net.protocol.filter;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.ReplyListener;

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.protocol.ConnectionException;
import ch.iserver.ace.net.protocol.MainConnection;
import ch.iserver.ace.net.protocol.ProtocolConstants;
import ch.iserver.ace.net.protocol.RemoteUserSession;
import ch.iserver.ace.net.protocol.Request;
import ch.iserver.ace.net.protocol.Serializer;
import ch.iserver.ace.net.protocol.SessionManager;

/**
 * Request prepare filter for a 'document details changed' message.
 * 
 * @see ch.iserver.ace.net.protocol.filter.AbstractRequestFilter
 */
public class DocumentDetailsChangedPrepareFilter extends AbstractRequestFilter {

	private Logger LOG = Logger.getLogger(DocumentDetailsChangedPrepareFilter.class);
	
	private Serializer serializer;
	private ReplyListener listener;
	
	/**
	 * Constructor. 
	 * 
	 * @param successor
	 * @param serializer
	 * @param listener
	 */
	public DocumentDetailsChangedPrepareFilter(RequestFilter successor, Serializer serializer, ReplyListener listener) {
		super(successor);
		this.serializer = serializer;
		this.listener = listener;
	}

	/**
	 * {@inheritDoc}
	 */
	public void process(Request request) {
		//TODO: consider a refactoring of DocumentDetailsChangedPrepareFilter and PublishDocumentPrepareFilter,
		//it's almost the same logic.
		if (request.getType() == ProtocolConstants.DOCUMENT_DETAILS_CHANGED) {
			LOG.info("--> process()");
			Object doc = request.getPayload();
			try {
				byte[] data = serializer.createNotification(ProtocolConstants.DOCUMENT_DETAILS_CHANGED, doc);
				
				//send data to each remote user with whom we have a session, e.g. who already knows about the doc
				SessionManager manager = SessionManager.getInstance();
				Map sessions = manager.getSessions();
				LOG.info("send 'documentDetailsChanged' notification to "+sessions.size()+" users.");
				synchronized(sessions) {
					Iterator iter = sessions.values().iterator();
					while (iter.hasNext()) {
						RemoteUserSession session = (RemoteUserSession)iter.next();
						try {
							MainConnection connection = session.getMainConnection();
							connection.send(data, session.getUser().getUserDetails().getUsername(), listener);
						} catch (ConnectionException ce) {
							LOG.warn("connection failure for session ["+session.getUser().getUserDetails()+"] "+ce.getMessage());
							NetworkServiceImpl.getInstance().getCallback().serviceFailure(
									FailureCodes.REMOTE_USER_FAILURE, session.getUser().getUserDetails().getUsername(), ce);
							//TODO: remove userProxy and userSession?
							LOG.debug("continue with next user.");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("caught exception ["+e.getMessage()+"]");
			}
			LOG.info("<-- process()");
		} else { //Forward
			super.process(request);
		}
	}
}
