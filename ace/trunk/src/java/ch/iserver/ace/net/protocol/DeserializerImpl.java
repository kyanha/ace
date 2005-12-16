/*
 * $Id:DeserializerImpl.java 1095 2005-11-09 13:56:51Z zbinl $
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

/**
 * Defaul implementation of interface <code>Deserializer</code>.
 * 
 * @see ch.iserver.ace.net.protocol.Deserializer
 */
public class DeserializerImpl implements Deserializer {

	/**
	 * the singleton instance
	 */
	private static DeserializerImpl instance;
	
	/**
	 * the SAX parser factory
	 */
	private static SAXParserFactory factory;
	
	/**
	 * Private constructor.
	 */
	private DeserializerImpl() {
		factory = SAXParserFactory.newInstance();
	}
	
	/**
	 * Gets the singleton instance.
	 * 
	 * @return the DeserializerImpl instance
	 */
	public static DeserializerImpl getInstance() {
		if (instance == null) {
			instance = new DeserializerImpl();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void deserialize(byte[] data, ParserHandler handler) throws DeserializeException {
		try {
			ByteArrayInputStream input = new ByteArrayInputStream(data);
			//could add xml validating using protocol.xsd
			//factory.setValidating(true)
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse( input, handler );
		} catch (Exception e) {
			e.printStackTrace();
			throw new DeserializeException(e.getMessage());
		}
	}

}
