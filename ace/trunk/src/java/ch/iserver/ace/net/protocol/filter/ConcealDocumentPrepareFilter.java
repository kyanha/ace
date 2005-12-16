/*
 * $Id:ConcealDocumentPrepareFilter.java 1205 2005-11-14 07:57:10Z zbinl $
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
import ch.iserver.ace.net.core.PublishedDocument;
import ch.iserver.ace.net.protocol.ConnectionException;
import ch.iserver.ace.net.protocol.MainConnection;
import ch.iserver.ace.net.protocol.ParticipantConnectionImpl;
import ch.iserver.ace.net.protocol.ProtocolConstants;
import ch.iserver.ace.net.protocol.RemoteUserSession;
import ch.iserver.ace.net.protocol.Request;
import ch.iserver.ace.net.protocol.Serializer;
import ch.iserver.ace.net.protocol.SessionManager;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Request prepare filter for a 'conceal document' message.
 * 
 * @see ch.iserver.ace.net.protocol.filter.AbstractRequestFilter
 */
public class ConcealDocumentPrepareFilter extends AbstractRequestFilter {

	private Logger LOG = Logger.getLogger(ConcealDocumentPrepareFilter.class);
	
	private Serializer serializer;
	private ReplyListener listener;
	
	/**
	 * Constructor.
	 * 
	 * @param successor
	 * @param serializer
	 * @param listener
	 */
	public ConcealDocumentPrepareFilter(RequestFilter successor, Serializer serializer, ReplyListener listener) {
		super(successor);
		ParameterValidator.notNull("serializer", serializer);
		ParameterValidator.notNull("listener", listener);
		this.serializer = serializer;
		this.listener = listener;
	}

	/**
	 * {@inheritDoc}
	 */
	public void process(Request request) {
		if (request.getType() == ProtocolConstants.CONCEAL) {
			LOG.info("--> process()");
			PublishedDocument doc = (PublishedDocument)request.getPayload();
			boolean isNetworkServiceStopped = NetworkServiceImpl.getInstance().isStopped();
			try {
				byte[] data = serializer.createNotification(ProtocolConstants.CONCEAL, doc);
			
				//send data to each known remote user
				SessionManager manager = SessionManager.getInstance();
				Map sessions = manager.getSessions();
				LOG.info("conceal to "+sessions.size()+" users.");
				synchronized(sessions) {
					Iterator iter = sessions.values().iterator();
					while (iter.hasNext()) {
						RemoteUserSession session = (RemoteUserSession)iter.next();
						LOG.debug("conceal to " + session.getUser().getUserDetails().getUsername());
						if (isNetworkServiceStopped) { //only close participant connections here if network service stopped.
							//send sessionTerminated message first to the user (if a participant), then conceal document
							ParticipantConnectionImpl conn = session.getParticipantConnection(doc.getId());
							if (conn != null) {
								conn.close();
							}
						}
						try {
							MainConnection connection = session.getMainConnection();
							connection.send(data, session.getUser().getUserDetails().getUsername(), listener);
						} catch (ConnectionException ce) {
							LOG.warn("connection failure for session ["+session.getUser().getUserDetails()+"] "+ce.getMessage());
							NetworkServiceImpl.getInstance().getCallback().serviceFailure(
									FailureCodes.REMOTE_USER_FAILURE, session.getUser().getUserDetails().getUsername(), ce);
						}
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("caught exception [" + e + ", " + e.getMessage()+"]");
			}
			LOG.info("<-- process()");
		} else {
			super.process(request);
		}
	}
	
}
