/*
 * $Id:SerializerImpl.java 1095 2005-11-09 13:56:51Z zbinl $
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

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.xml.sax.helpers.AttributesImpl;

import ch.iserver.ace.net.impl.NetworkConstants;
import ch.iserver.ace.net.impl.PublishedDocument;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class SerializerImpl implements Serializer, ProtocolConstants {

	private Logger LOG = Logger.getLogger(SerializerImpl.class);
	
//	private TransformerHandler handler;
	private SAXTransformerFactory factory;
	
	private static SerializerImpl instance;
	
	private SerializerImpl() {
		factory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
	}
	
	public static SerializerImpl getInstance() {
		if (instance == null) {
			instance = new SerializerImpl();
		}
		return instance;
	}
	
//	private void init() {
//		TransformerFactory factory = TransformerFactory.newInstance();
//		if (factory instanceof SAXTransformerFactory) {
//			try {
//				SAXTransformerFactory sax = (SAXTransformerFactory)factory;
//				handler = sax.newTransformerHandler();
//				Transformer serializer = handler.getTransformer();
//				serializer.setOutputProperty(OutputKeys.ENCODING, NetworkConstants.DEFAULT_ENCODING);;
//				serializer.setOutputProperty(OutputKeys.INDENT,"no");
//			} catch (TransformerConfigurationException tce) {
//				//TODO: handling
//				LOG.error("transformer could not be configured.");
//			}
//		} else {
//			LOG.error("unexpected TransformerFactory");
//			throw new IllegalStateException("unexpected TransformerFactory");
//		}
//	}
	
	private TransformerHandler createHandler() {
		TransformerHandler handler = null;
		try {
			handler = factory.newTransformerHandler();
			Transformer serializer = handler.getTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, NetworkConstants.DEFAULT_ENCODING);;
			serializer.setOutputProperty(OutputKeys.INDENT,"no");
		} catch (TransformerConfigurationException tce) {
			//TODO: handling
			LOG.error("transformer could not be configured.");
		} 
		return handler;
	}
	
	/**
	 * @see ch.iserver.ace.net.impl.protocol.Serializer#createQuery(int)
	 */
	public byte[] createQuery(int type) throws SerializeException {
		ParameterValidator.inRange("queryType", type, 0, NAMES.length-1);
		try {
			TransformerHandler handler = createHandler();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			AttributesImpl attrs = new AttributesImpl();
			handler.setResult(result);
			handler.startDocument();
			handler.startElement("", "", "ace", attrs);
			handler.startElement("", "", "request", attrs);
			attrs.addAttribute("", "", "type", "", NAMES[type]);
			handler.startElement("", "", "query", attrs);
			handler.endElement("", "", "query");
			handler.endElement("", "", "request");
			handler.endElement("", "", "ace");
			handler.endDocument();
			output.flush();
			return output.toByteArray();
		} catch (Exception e) {
			LOG.error("could not serialize ["+e.getMessage()+"]");
			throw new SerializeException(e.getMessage());
		}
	}

	public byte[] createResponse(int type, Object data) throws SerializeException {
		try {
			TransformerHandler handler = createHandler();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			if (type == PUBLISHED_DOCUMENTS) {
				List docs = (List)data;
				handler.setResult(result);
				handler.startDocument();
				AttributesImpl attrs = new AttributesImpl();
				handler.startElement("", "", "ace", attrs);
				handler.startElement("", "", "response", attrs);
				handler.startElement("", "", RESPONSE_PUBLISHED_DOCUMENTS, attrs);
				Iterator docIter = docs.iterator();
				while (docIter.hasNext()) {
					PublishedDocument doc = (PublishedDocument)docIter.next();
					attrs = new AttributesImpl();
					attrs.addAttribute("", "", "id", "", doc.getId());
					attrs.addAttribute("", "", "name", "", doc.getDetails().getTitle());
					handler.startElement("", "", "doc", attrs);
					handler.endElement("", "", "doc");
				}
				handler.endElement("", "", RESPONSE_PUBLISHED_DOCUMENTS);
				handler.endElement("", "", "response");
				handler.endElement("", "", "ace");
				handler.endDocument();
			}
			output.flush();
			return output.toByteArray();
		} catch (Exception e) {
			LOG.error("could not serialize ["+e.getMessage()+"]");
			throw new SerializeException(e.getMessage());
		}
	}
}
