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

import java.io.ByteArrayOutputStream;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.xml.sax.helpers.AttributesImpl;

import ch.iserver.ace.net.impl.NetworkConstants;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class SerializerImpl implements Serializer {

	
	
	/**
	 * @see ch.iserver.ace.net.protocol.Serializer#createQuery(int)
	 */
	public byte[] createQuery(int type) throws SerializeException {
		ParameterValidator.inRange("queryType", type, 0, NAMES.length-1);
		try {
			OutputFormat format = new OutputFormat();
			format.setEncoding(NetworkConstants.DEFAULT_ENCODING);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			XMLSerializer serializer = new XMLSerializer(output, format);
			serializer.asContentHandler();
			serializer.startDocument();
			serializer.startElement("ace", null);
			AttributesImpl attrs = new AttributesImpl();
			attrs.addAttribute("", "type", "", "", NAMES[type]);
			serializer.startElement("", "query", "", attrs);
			serializer.endElement("query");
			serializer.endElement("ace");
			serializer.endDocument();
			
			output.flush();
			return output.toByteArray();
		} catch (Exception e) {
			throw new SerializeException(e.getMessage());
		}
	}

}
