/*
 * $Id:DocumentDiscoveryCallbackImpl.java 1095 2005-11-09 13:56:51Z zbinl $
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
import java.util.Map;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.impl.RemoteDocumentProxyImpl;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class DocumentDiscoveryCallbackImpl implements DocumentDiscoveryCallback {

	private NetworkServiceCallback callback;
	
	public DocumentDiscoveryCallbackImpl(NetworkServiceCallback callback) {
		ParameterValidator.notNull("callback", callback);
		this.callback = callback;
	}
	
	/**
	 * @see ch.iserver.ace.net.impl.protocol.DocumentDiscoveryCallback#documentsDiscovered(java.lang.String, java.util.Map)
	 */
	public void documentsDiscovered(String id, Map docs) {
		RemoteDocumentProxy[] proxies = createProxies(id, docs);
		callback.documentDiscovered(proxies);
	}
	

	private RemoteDocumentProxy[] createProxies(String id, Map result) {
		RemoteUserProxyExt user = SessionManager.getInstance().getSession(id).getUser();
		Iterator keys = result.keySet().iterator();
		List docs = new ArrayList(result.size());
		while (keys.hasNext()) {
			String docId = (String)keys.next();
			String docName = (String)result.get(docId);
			DocumentDetails details = new DocumentDetails(docName);
			RemoteDocumentProxy newDoc = new RemoteDocumentProxyImpl(docId, details, user);
			docs.add(newDoc);
		}
		user.setSharedDocuments(docs);
		return (RemoteDocumentProxy[])docs.toArray(new RemoteDocumentProxy[0]);
	}

}
