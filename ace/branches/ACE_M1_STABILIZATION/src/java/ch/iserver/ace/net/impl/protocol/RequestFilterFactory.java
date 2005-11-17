/*
 * $Id:RequestFilterFactory.java 1205 2005-11-14 07:57:10Z zbinl $
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

import org.beepcore.beep.lib.NullReplyListener;

import ch.iserver.ace.net.impl.discovery.DiscoveryManager;
import ch.iserver.ace.net.impl.discovery.DiscoveryManagerFactory;

/**
 *
 */
public class RequestFilterFactory {

	
	public static RequestFilter createClientChain() {
		RequestFilter filter = new FailureFilter(null);
		ResponseListener listener = ResponseListener.getInstance();
		Deserializer deserializer = DeserializerImpl.getInstance();
		listener.init(deserializer, createClientChainForResponses());
		Serializer serializer = SerializerImpl.getInstance();
		filter = new ConcealDocumentPrepareFilter(filter, serializer, listener);
		filter = new PublishDocumentPrepareFilter(filter, serializer, listener); 
//		filter = new DocumentDiscoveryPrepareFilter(filter, serializer, listener);
		//TODO: could write an own NullReplyListener with logging
		filter = new PublishedDocumentsPrepareFilter(filter, serializer, NullReplyListener.getListener());
		return filter;
	}
	
	private static RequestFilter createClientChainForResponses() {
		RequestFilter filter = new FailureFilter(null);
//		filter = new DocumentDiscoveryResponseFilter(filter);
		boolean doForward = false;
		filter = new LogFilter(filter, doForward);
		return filter;
	}
	
	public static RequestFilter createServerChain() {
		RequestFilter filter = new FailureFilter(null);
		filter = new ConcealDocumentReceiveFilter(filter);
		filter = new PublishDocumentReceiveFilter(filter);
//		filter = new PublishedDocumentsRequestFilter(filter);
		filter = new PublishedDocumentsReceiveFilter(filter);
		return filter;
	}
	
}
