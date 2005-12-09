/*
 * $Id:PublishDocumentPrepareFilter.java 1205 2005-11-14 07:57:10Z zbinl $
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

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.ReplyListener;

import ch.iserver.ace.FailureCodes;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.core.RemoteUserProxyExt;
import ch.iserver.ace.net.discovery.DiscoveryManager;
import ch.iserver.ace.net.discovery.DiscoveryManagerFactory;

/**
 *
 */
public class PublishDocumentPrepareFilter extends AbstractRequestFilter {

	private Logger LOG = Logger.getLogger(PublishDocumentPrepareFilter.class);
	
	private Serializer serializer;
	private ReplyListener listener;
	
	public PublishDocumentPrepareFilter(RequestFilter successor, Serializer serializer, ReplyListener listener) {
		super(successor);
		this.serializer = serializer;
		this.listener = listener;
	}
	
	
	/**
	 * @see ch.iserver.ace.net.impl.protocol.RequestFilter#process(ch.iserver.ace.net.impl.protocol.Request)
	 */
	public void process(Request request) {
		if (request.getType() == ProtocolConstants.PUBLISH) {
			LOG.info("--> process()");
			DiscoveryManager discoveryManager = DiscoveryManagerFactory.getDiscoveryManager();
			if (discoveryManager.getSize() > 0) {
				Object doc = request.getPayload();
				try {
					byte[] data = serializer.createNotification(ProtocolConstants.PUBLISH, doc);
					LOG.debug("doc ["+(new String(data))+"]");
					
					
					RemoteUserProxyExt[] peers = discoveryManager.getPeersWithNoSession();
					SessionManager manager = SessionManager.getInstance();
					LOG.debug("initiate sessions with "+peers.length+" peers; "+manager.size()+" sessions already initiated.");
					for (int i = 0; i < peers.length; i++) {
						RemoteUserProxyExt next = peers[i];
						manager.createSession(next);
					}
					Map sessions = manager.getSessions();
					
					LOG.info("publish to "+sessions.size()+" users.");
					synchronized(sessions) {
						Iterator iter = sessions.values().iterator();
						while (iter.hasNext()) {
							RemoteUserSession session = (RemoteUserSession)iter.next();
							try {
								LOG.debug("getMainConnection()");
								MainConnection connection = session.getMainConnection();
								LOG.debug("send()");
								connection.send(data, session.getUser().getUserDetails().getUsername(), listener);
							} catch (ConnectionException ce) {
								LOG.warn("connection failure for session ["+session.getUser().getUserDetails().getUsername()+"] "+ce.getMessage());
								NetworkServiceImpl.getInstance().getCallback().serviceFailure(
										FailureCodes.REMOTE_USER_FAILURE, session.getUser().getUserDetails().getUsername(), ce);
								//TODO: remove userProxy and userSession?
								LOG.debug("continue with next user.");
							}
						}
					}
				} catch (Exception e) {
					LOG.error("caught exception [" + e + ", " + e.getMessage() + "]");
				}
			} else {
				LOG.debug("no discovered peers available for publish.");
			}
			LOG.info("<-- process()");
		} else { //Forward
			super.process(request);
		}
	}
}
