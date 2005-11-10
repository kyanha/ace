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

package ch.iserver.ace.net.impl.protocol;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 */
public class DocumentParserHandler extends DefaultHandler implements ParserHandler, ProtocolConstants {
	
	private Map result;
	private boolean isReady;
	
	public DocumentParserHandler() {
		isReady = false;
	}

	public void startDocument() throws SAXException {
		result = new HashMap();
	}
	
	public void endDocument() throws SAXException {
		
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {	
		if (isReady) {
			String id = attributes.getValue(DOCUMENT_ID);
			String name = attributes.getValue(DOCUMENT_NAME);
			result.put(id, name);
		} else if (qName.equals(RESPONSE_PUBLISHED_DOCUMENTS)) {
			isReady = true;
		}

	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals(RESPONSE_PUBLISHED_DOCUMENTS)) {
			isReady = false;
		}
	}
	
	/**
	 * Returns a Map with document id's and document names.
	 */
	public Object getResult() {
		Map tmp = result;
		result = null;
		return tmp;
	}
}
