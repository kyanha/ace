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

package ch.iserver.ace.net.impl.protocol;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

/**
 *
 */
public class RequestParserHandler extends ParserHandler {
	
	private static Logger LOG = Logger.getLogger(RequestParserHandler.class);
	
	private Request result;
	private int requestType;
	private String userId;
	private DocumentInfo info;
	private List requestPayload;
	
	public RequestParserHandler() {
	}

	public void startDocument() throws SAXException {
		requestType = -1;
	}
	
	public void endDocument() throws SAXException {
		if (getType() == PUBLISHED_DOCUMENTS) {
			result = new RequestImpl(getType(), userId, null);
		} else if (getType() == PUBLISH 
				|| getType() == CONCEAL 
				|| getType() == DOCUMENT_DETAILS_CHANGED 
				|| getType() == JOIN) {
			result = new RequestImpl(getType(), userId, info);
		} else if (getType() == SEND_DOCUMENTS) {
			result = new RequestImpl(getType(), userId, requestPayload);
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
		} else if (qName.equals(TAG_QUERY)) { //TODO: remove tag query, is discarded
			if (attributes.getValue(QUERY_TYPE).equals(QUERY_TYPE_PUBLISHED_DOCUMENTS)) {
				requestType = PUBLISHED_DOCUMENTS;
			} else {
				LOG.warn("unkown query type "+attributes.getValue(QUERY_TYPE));
			}
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
		}

	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
	}
	
	
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.impl.protocol.ParserHandler#getResult()
	 */
	public Request getResult() {
		LOG.debug("getResult("+result+")");
		return result;
	}

	public int getType() {
		return requestType;
	}

	public void setMetaData(Object metadata) {
		throw new UnsupportedOperationException("setMetaData() to be implemented in RequestParserHandler");
		
	}
}
