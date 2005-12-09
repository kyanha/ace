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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.OutputDataStream;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.core.RemoteDocumentProxyExt;
import ch.iserver.ace.net.core.RemoteDocumentProxyFactory;
import ch.iserver.ace.net.core.RemoteDocumentProxyImpl;
import ch.iserver.ace.net.core.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

/**
 *
 */
public class SendDocumentsReceiveFilter extends AbstractRequestFilter {

	private static Logger LOG = Logger.getLogger(SendDocumentsReceiveFilter.class);
	
	private RemoteDocumentProxyFactory factory;
	
	public SendDocumentsReceiveFilter(RequestFilter successor) {
		super(successor);
		factory = RemoteDocumentProxyFactory.getInstance();
	}
	
	public void process(Request request) {
		if (request.getType() == ProtocolConstants.SEND_DOCUMENTS) {
			LOG.info("--> process()");
			List docs = (List) request.getPayload();
			if (docs.size() > 0) {
				Iterator iter = docs.iterator();
				List proxies = new ArrayList(docs.size());
				while (iter.hasNext()) {
					DocumentInfo doc = (DocumentInfo) iter.next();
					RemoteDocumentProxy proxy = createProxy(doc);
					proxies.add(proxy);
				}
				NetworkServiceCallback callback = NetworkServiceImpl.getInstance().getCallback();
				RemoteDocumentProxy[] proxyArr = (RemoteDocumentProxy[]) proxies.toArray(new RemoteDocumentProxy[0]);
				callback.documentDiscovered(proxyArr);
			} else {
				LOG.warn("no documents in notification");
			}
			try {
				//confirm reception of msg	
				OutputDataStream os = new OutputDataStream();
				os.setComplete();
				request.getMessage().sendRPY(os);
//				request.getMessage().sendNUL();
			} catch (Exception e) {
				LOG.error("could not send confirmation ["+e+", "+e.getMessage()+"]");
			}
			LOG.info("<-- process()");
		} else { //Forward
			super.process(request);
		}
	}

	private RemoteDocumentProxy createProxy(DocumentInfo doc) {
		String docId = doc.getDocId();
		String docName = doc.getName();
		String userId = doc.getUserId();

		RemoteUserProxyExt user = SessionManager.getInstance().getSession(userId).getUser();
		DocumentDetails details = new DocumentDetails(docName);
		RemoteDocumentProxyExt proxy = factory.createProxy(docId, details, user);
		user.addSharedDocument(proxy);
		return proxy;
	}	
}
