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

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;

/**
 *
 */
public class CollaborationDeserializer implements Deserializer {

	private Logger LOG = Logger.getLogger(CollaborationDeserializer.class);
	
	private static SAXParserFactory factory;
	
	public CollaborationDeserializer() {
		factory = SAXParserFactory.newInstance();
	}
	
	/**
	 * @see ch.iserver.ace.net.protocol.Deserializer#deserialize(byte[], ch.iserver.ace.net.protocol.ParserHandler)
	 */
	public void deserialize(byte[] data, ParserHandler handler)
			throws DeserializeException {
		try {
			ByteArrayInputStream input = new ByteArrayInputStream(data);
			//TODO: add xml validating, write xml schema
			//factory.setValidating(true)
			SAXParser saxParser = factory.newSAXParser();
//			LOG.debug("--> parse()");
			synchronized(this) {
				saxParser.parse( input, handler );
			}
//			LOG.debug("<-- parse()");
		} catch (Exception e) {
			e.printStackTrace();
			throw new DeserializeException(e.getMessage());
		}
	}

}
