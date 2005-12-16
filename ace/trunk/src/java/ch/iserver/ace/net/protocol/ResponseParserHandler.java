/*
 * $Id:DocumentParserHandler.java 1095 2005-11-09 13:56:51Z zbinl $
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

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * ResponseParserHandler is used to parse responses received from a 
 * Channel communication.
 */
public class ResponseParserHandler extends ParserHandler {
	
	private static Logger LOG = Logger.getLogger(ResponseParserHandler.class);
	
	private Request response;
	private int responseType;
	private QueryInfo info;
	private String userId;
	
	public ResponseParserHandler() {
	}

	public void startDocument() throws SAXException {
		response = null;
		responseType = -1;
		info = (info == null) ? new QueryInfo("", -1) : info;
	}
	
	public void endDocument() throws SAXException {
		if (responseType == USER_DISCOVERY) {
			response = new RequestImpl(USER_DISCOVERY, userId, info.getPayload());
			info = null;
		}
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {	
		if (qName.equals(USER)) {
			responseType = USER_DISCOVERY;
			userId = attributes.getValue(ID);
			String name = attributes.getValue(NAME);
			info.setPayload(name);
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Request getResult() {
		LOG.debug("getResult("+response+")");
		return response;
	}
	
	public int getType() {
		return responseType;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setMetaData(Object metadata) {
		if (metadata instanceof QueryInfo) {
			info = (QueryInfo) metadata;
		} else {
			LOG.warn("unknown metadata type ["+metadata+"]");
		}
	}
}
