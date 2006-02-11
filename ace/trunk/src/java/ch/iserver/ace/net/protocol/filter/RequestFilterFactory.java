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

package ch.iserver.ace.net.protocol.filter;

import ch.iserver.ace.net.protocol.Deserializer;
import ch.iserver.ace.net.protocol.DeserializerImpl;
import ch.iserver.ace.net.protocol.ResponseListener;
import ch.iserver.ace.net.protocol.Serializer;
import ch.iserver.ace.net.protocol.SerializerImpl;


/**
 * RequestFilter chain Factory to create the client and server
 * request filter chains.
 */
public class RequestFilterFactory {

	private static RequestFilter clientChain;
	
	/**
	 * Creates the client request filter chain.
	 * 
	 * @return the client request filter chain
	 */
	public static RequestFilter createClientChain() {
		if (clientChain == null) {
			RequestFilter filter = new FailureFilter(null);
			ResponseListener listener = ResponseListener.getInstance();
			Deserializer deserializer = DeserializerImpl.getInstance();
			listener.init(deserializer, createClientChainForResponses());
			Serializer serializer = SerializerImpl.getInstance();
			filter = new InvitationRejectedSenderFilter(filter, serializer, listener);
			filter = new InviteRequestSenderFilter(filter, serializer, listener);
			filter = new JoinRejectedSenderFilter(filter, serializer, listener);
			filter = new JoinRequestSenderFilter(filter, serializer, listener);
			filter = new ConcealDocumentPrepareFilter(filter, serializer, listener);
			filter = new DocumentDetailsChangedPrepareFilter(filter, serializer, listener);
			filter = new PublishDocumentPrepareFilter(filter, serializer, listener); 
			filter = new SendDocumentsPrepareFilter(filter, serializer, listener);
			filter = new LogFilter(filter, true);
			clientChain = filter;
		}
		return clientChain;
	}
	
	/**
	 * Creates the request filter chain for responses
	 * @return
	 */
	private static RequestFilter createClientChainForResponses() {
		RequestFilter filter = new LogFilter(null, false);
		return filter;
	}
	
	/**
	 * Creates the server request filter chain.
	 * 
	 * @return the sever request filter chain
	 */
	public static RequestFilter createServerChain() {
		RequestFilter filter = new FailureFilter(null);
		filter = new UserDiscardedRecipientFilter(filter);
		filter = new InvitationRejectedRecipientFilter(filter);
		filter = new InviteRequestRecipientFilter(filter);
		filter = new JoinRejectedRecipientFilter(filter);
		filter = new JoinRequestRecipientFilter(filter, createClientChain());
		filter = new ConcealDocumentReceiveFilter(filter);
		filter = new DocumentDetailsChangedReceiveFilter(filter);
		filter = new PublishDocumentReceiveFilter(filter);
		filter = new SendDocumentsReceiveFilter(filter);
		filter = new LogFilter(filter, true);
		filter = new ShutdownFilter(filter);
		return filter;
	}
	
}
