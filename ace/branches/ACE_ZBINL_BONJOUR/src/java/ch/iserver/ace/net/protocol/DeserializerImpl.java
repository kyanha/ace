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

package ch.iserver.ace.net.protocol;

import java.io.ByteArrayInputStream;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 *
 */
public class DeserializerImpl implements Deserializer {

	private DocumentParserHandler docHandler;
	
	public DeserializerImpl(DocumentParserHandler handler) {
		this.docHandler = handler;
	}
	
	/**
	 * Returns a Map with document id's and document names.
	 * 
	 * @param data
	 * @return a map with id's and names
	 * @throws DeserializeException 
	 */
	public Map deserializeDocuments(byte[] data) throws DeserializeException {
		try {
			ByteArrayInputStream input = new ByteArrayInputStream(data);
			SAXParserFactory factory;
			factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse( input, docHandler );
			return (Map)docHandler.getResult();
		} catch (Exception e) {
			throw new DeserializeException(e);
		}
		
	}

}
