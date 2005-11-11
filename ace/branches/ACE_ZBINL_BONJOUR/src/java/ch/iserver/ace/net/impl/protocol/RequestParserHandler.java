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

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 */
public class RequestParserHandler extends ParserHandler {
	
	private Request result;
	private int type;
	
	public RequestParserHandler() {
	}

	public void startDocument() throws SAXException {
	}
	
	public void endDocument() throws SAXException {
		if (type == PUBLISHED_DOCUMENTS) {
			result = new RequestImpl(type, null);
		}
		
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {	
		if (qName.equals(QUERY)) {
			if (attributes.getValue(QUERY_TYPE).equals(QUERY_TYPE_PUBLISHED_DOCUMENTS)) {
				type = PUBLISHED_DOCUMENTS;
			}
		}

	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
	}
	
	
	
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.impl.protocol.ParserHandler#getResult()
	 */
	public Object getResult() {
		return result;
	}

	public int getType() {
		return type;
	}

}
