/*
 * $Id:ConcealDocumentReceiveFilter.java 1205 2005-11-14 07:57:10Z zbinl $
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

import org.apache.log4j.Logger;
import org.beepcore.beep.core.OutputDataStream;

import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.protocol.ProtocolConstants;
import ch.iserver.ace.net.protocol.RemoteUserSession;
import ch.iserver.ace.net.protocol.Request;
import ch.iserver.ace.net.protocol.SessionManager;
import ch.iserver.ace.net.protocol.RequestImpl.DocumentInfo;

/**
 * Request receive filter for a 'conceal document' message.
 * 
 * @see ch.iserver.ace.net.protocol.filter.AbstractRequestFilter
 */
public class ConcealDocumentReceiveFilter extends AbstractRequestFilter {

	private Logger LOG = Logger.getLogger(ConcealDocumentReceiveFilter.class);
	
	/**
	 * Constructor.
	 * 
	 * @param successor the successor
	 */
	public ConcealDocumentReceiveFilter(RequestFilter successor) {
		super(successor);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void process(Request request) {
		if (request.getType() == ProtocolConstants.CONCEAL) {
			LOG.info("--> process()");
			DocumentInfo info = (DocumentInfo) request.getPayload();
			String userId = info.getUserId();
			RemoteUserSession session = SessionManager.getInstance().getSession(userId);
			RemoteDocumentProxy doc = session.getUser().removeSharedDocument(info.getDocId());
			if (doc != null) {
				NetworkServiceCallback callback = NetworkServiceImpl.getInstance().getCallback();
				RemoteDocumentProxy[] docs = new RemoteDocumentProxy[] { doc };
				LOG.debug("--> notifiy upper layer");
				callback.documentDiscarded(docs);
				LOG.debug("<-- ok.");
			} else {
				LOG.warn("document to be concealed not found [" + info.getDocId() + "]");
			}
			
			try {
				//confirm reception of msg
				OutputDataStream os = new OutputDataStream();
				os.setComplete();
				request.getMessage().sendRPY(os);
			} catch (Exception e) {
				LOG.error("could not send confirmation ["+e.getMessage()+"]");
			}
			LOG.info("<-- process()");
		} else { //Forward
			super.process(request);
		}
	}

}
