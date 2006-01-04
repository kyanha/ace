/*
 * $Id:RequestParserHandler.java 1205 2005-11-14 07:57:10Z zbinl $
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

package ch.iserver.ace.net.protocol;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import ch.iserver.ace.net.core.MutableUserDetails;
import ch.iserver.ace.net.core.RemoteUserProxyExt;
import ch.iserver.ace.net.core.RemoteUserProxyFactory;
import ch.iserver.ace.net.protocol.RequestImpl.DocumentInfo;

/**
 * RequestParserHandler is used by the MainRequestHandler to parse
 * incoming message. The result from using RequestParserHandler is always
 * an instance of <code>Request</code> (call to {@link #getType()}.
 */
public class RequestParserHandler extends ParserHandler {
	
	private static Logger LOG = Logger.getLogger(RequestParserHandler.class);
	
	private Request result;
	private int requestType;
	private String userId;
	private DocumentInfo info;
	private List requestPayload;
	private RemoteUserProxyExt user;
	
	public RequestParserHandler() {
	}

	public void startDocument() throws SAXException {
		requestType = -1;
		userId = null;
		result = null;
	}
	
	public void endDocument() throws SAXException {
		int type = getType();
		if (type == USER_DISCARDED) {
			result = new RequestImpl(type, userId, null);
		} else if (type == PUBLISH 
				|| type == CONCEAL 
				|| type == DOCUMENT_DETAILS_CHANGED 
				|| type == JOIN
				|| type == INVITE
				|| type == INVITE_REJECTED
				|| type == JOIN_REJECTED
				|| type == CHANNEL_SESSION) {
			result = new RequestImpl(type, userId, info);
			info = null;
		} else if (type == SEND_DOCUMENTS) {
			result = new RequestImpl(SEND_DOCUMENTS, userId, requestPayload);
			requestPayload = null;
		} else if (type == CHANNEL_MAIN) {
			result = new RequestImpl(CHANNEL_MAIN, userId, user);
			user = null;
		}
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (requestType == SEND_DOCUMENTS) {
			String id = attributes.getValue(DOCUMENT_ID);
			String name = attributes.getValue(NAME);
			//TODO: userid not needed in documentinfo since we have it in the request
			DocumentInfo doc = new DocumentInfo(id, name, userId);
			requestPayload.add(doc);
		} else if (requestType == PUBLISH) {
			if (qName.equals(TAG_DOC)) {
				String id = attributes.getValue(DOCUMENT_ID);
				String name = attributes.getValue(NAME);
				info = new DocumentInfo(id, name, userId);
			} else {
				LOG.warn("unkown tag in <"+TAG_PUBLISH+"> tag.");
			}
		} else if (requestType == CONCEAL) {
			if (qName.equals(TAG_DOC)) {
				String id = attributes.getValue(DOCUMENT_ID);
				info = new DocumentInfo(id, null, userId);
			} else {
				LOG.warn("unkown tag in <"+TAG_CONCEAL+"> tag.");
			}
		} else if (requestType == DOCUMENT_DETAILS_CHANGED) {
			if (qName.equals(TAG_DOC)) {
				String id = attributes.getValue(DOCUMENT_ID);
				String name = attributes.getValue(NAME);
				info = new DocumentInfo(id, name, userId);
			} else {
				LOG.warn("unkown tag in <"+TAG_DOCUMENT_DETAILS_CHANGED+"> tag.");
			}
		} else if (requestType == JOIN) {
			if (qName.equals(TAG_DOC)) {
				String id = attributes.getValue(DOCUMENT_ID);
				info = new DocumentInfo(id, null, userId);
			} else {
				LOG.warn("unkown tag in <"+TAG_JOIN+"> tag.");
			}
		} else if (requestType == INVITE) {
			String id = attributes.getValue(DOCUMENT_ID);
			info = new DocumentInfo(id, null, userId);
		} else if (requestType == JOIN_REJECTED) {
			String code = attributes.getValue(CODE);
			info.setData(code);
		} else if (qName.equals(TAG_PUBLISHED_DOCS)) {
			userId = attributes.getValue(USER_ID);
			requestType = SEND_DOCUMENTS;
			requestPayload = new ArrayList();
		} else if (qName.equals(TAG_PUBLISH)) {
			userId = attributes.getValue(USER_ID);
			requestType = PUBLISH;
		} else if (qName.equals(TAG_CONCEAL)) {
			userId = attributes.getValue(USER_ID);
			requestType = CONCEAL;
		} else if (qName.equals(TAG_DOCUMENT_DETAILS_CHANGED)) {
			userId = attributes.getValue(USER_ID);
			requestType = DOCUMENT_DETAILS_CHANGED;
		} else if (qName.equals(TAG_JOIN)) {
			userId = attributes.getValue(USER_ID);
			requestType = JOIN;
		} else if (qName.equals(TAG_INVITE)) {
			userId = attributes.getValue(USER_ID);
			requestType = INVITE;
		} else if (qName.equals(TAG_CHANNEL)) {
			String type = attributes.getValue(TYPE);
			if (type.equals(RemoteUserSession.CHANNEL_MAIN)) {
				requestType = CHANNEL_MAIN;
			} else if (type.equals(RemoteUserSession.CHANNEL_SESSION)) {
				requestType = CHANNEL_SESSION;
				userId = attributes.getValue(USER_ID);
				String docId = attributes.getValue(DOC_ID);
				info = new DocumentInfo(docId, null, userId);
			} else {
				LOG.warn("unkown channel type ["+type+"], use '" + RemoteUserSession.CHANNEL_MAIN + " as default.");
				requestType = CHANNEL_MAIN;
			}
		} else if (qName.equals(TAG_JOIN_REJECTED)) {
			requestType = JOIN_REJECTED;
			String docId = attributes.getValue(DOC_ID);
			userId = attributes.getValue(USER_ID);
			info = new DocumentInfo(docId, null, userId);
		} else if (qName.equals(TAG_INVITE_REJECTED)) {
			requestType = INVITE_REJECTED;
			String docId = attributes.getValue(DOC_ID);
			userId = attributes.getValue(USER_ID);
			info = new DocumentInfo(docId, null, userId);
		} else if (qName.equals(USER)) { //explicit user discovery
			userId = attributes.getValue(ID);
			String username = attributes.getValue(NAME);
			String address = attributes.getValue(ADDRESS);
			String port = attributes.getValue(PORT);
			try {
				MutableUserDetails details = new MutableUserDetails(username, InetAddress.getByName(address), Integer.parseInt(port));
				user = RemoteUserProxyFactory.getInstance().createProxy(userId, details);
			} catch (Exception e) {
				LOG.error("could not create RemoteUserProxy [" + e + "]");
			}
		} else if (qName.equals(TAG_USER_DISCARDED)) {
			requestType = USER_DISCARDED;
			userId = attributes.getValue(ID);
		}
	}
	
	/**
	 * @see ch.iserver.ace.net.impl.protocol.ParserHandler#getResult()
	 */
	public Request getResult() {
		LOG.debug("getResult("+result+")");
		if (result == null) {
			throw new IllegalStateException("result may not be null after parsing.");
		}
		return result;
	}

	public int getType() {
		return requestType;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setMetaData(Object metadata) {
		throw new UnsupportedOperationException("setMetaData() to be implemented in RequestParserHandler");
		
	}
}
