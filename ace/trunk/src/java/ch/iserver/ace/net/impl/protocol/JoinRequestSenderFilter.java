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
import org.beepcore.beep.core.ReplyListener;

/**
 *
 */
public class JoinRequestSenderFilter extends AbstractRequestFilter {

	private static Logger LOG = Logger.getLogger(JoinRequestSenderFilter.class);
	
	private Serializer serializer;
	private ReplyListener listener;
	
	public JoinRequestSenderFilter(RequestFilter successor, Serializer serializer, ReplyListener listener) {
		super(successor);
		this.serializer = serializer;
		this.listener = listener;
	}
	
	public void process(Request request) {
		try {
			if (request.getType() == ProtocolConstants.JOIN) {
				LOG.info("--> process()");
				
				synchronized(this) { //synchronize precautionary
					byte[] data = serializer.createRequest(ProtocolConstants.JOIN, request.getPayload());
					RemoteUserSession session = SessionManager.getInstance().getSession(request.getUserId());
					MainConnection connection = session.getMainConnection();
					connection.send(data, session.getUser().getUserDetails().getUsername(), listener);
				}
				
				LOG.info("<-- process()");
			} else {
				super.process(request);
			}
		} catch (Exception e) {
			LOG.error("exception processing request ["+e+", "+e.getMessage()+"]");
		}
	}

}
