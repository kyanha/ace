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
		filter = new InviteRequestSenderFilter(filter, serializer, listener);
		filter = new JoinRequestSenderFilter(filter, serializer, listener);
		filter = new ConcealDocumentPrepareFilter(filter, serializer, listener);
		filter = new DocumentDetailsChangedPrepareFilter(filter, serializer, listener);
		filter = new PublishDocumentPrepareFilter(filter, serializer, listener); 
		filter = new SendDocumentsPrepareFilter(filter, serializer, listener);
		filter = new LogFilter(filter, true);
		return filter;
	}
	
	private static RequestFilter createClientChainForResponses() {
		RequestFilter filter = new LogFilter(null, false);
		return filter;
	}
	
	public static RequestFilter createServerChain() {
		RequestFilter filter = new FailureFilter(null);
		filter = new InviteRequestRecipientFilter(filter);
		filter = new JoinRequestRecipientFilter(filter);
		filter = new ConcealDocumentReceiveFilter(filter);
		filter = new DocumentDetailsChangedReceiveFilter(filter);
		filter = new PublishDocumentReceiveFilter(filter);
		filter = new SendDocumentsReceiveFilter(filter);
		filter = new LogFilter(filter, true);
		return filter;
	}
	
}
