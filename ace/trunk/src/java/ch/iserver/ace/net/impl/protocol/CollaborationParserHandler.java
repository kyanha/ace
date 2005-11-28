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

import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

/**
 *
 */
public class CollaborationParserHandler extends ParserHandler {

	private static Logger LOG = Logger.getLogger(CollaborationParserHandler.class);
	
	private Request result;
	private int requestType;
	private String userId;
	private DocumentInfo info;
	private List requestPayload;
	
	public CollaborationParserHandler() {
	}
	
	public void startDocument() throws SAXException {
		requestType = -1;
	}
	
	public void endDocument() throws SAXException {
		int type = getType();
		if (type == LEAVE || type == KICKED) {
			result = new RequestImpl(type, null, info);
		}
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals(TAG_LEAVE)) {
			requestType = LEAVE;
			String docId = attributes.getValue(DOC_ID);
			String participantId = attributes.getValue(PARTICIPANT_ID);
			info = new DocumentInfo(docId, Integer.parseInt(participantId));
		} else if (qName.equals(TAG_KICKED)) {
			requestType = KICKED;
			String docId = attributes.getValue(DOC_ID);
			info = new DocumentInfo(docId, -1);
		}
	}
	
	
	public int getType() {
		return requestType;
	}
	
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.impl.protocol.ParserHandler#getResult()
	 */
	public Request getResult() {
		LOG.debug("getResult("+result+")");
		return result;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.impl.protocol.ParserHandler#setMetaData(java.lang.Object)
	 */
	public void setMetaData(Object metadata) {
		throw new UnsupportedOperationException("metadata not supported");
	}

}
