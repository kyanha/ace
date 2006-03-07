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

package ch.iserver.ace.net.protocol;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import ch.iserver.ace.net.core.NetworkProperties;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.core.PublishedDocument;

/**
 * Default implementation for interface {@link ch.iserver.ace.net.protocol.Serializer}.
 */
public class SerializerImpl implements Serializer, ProtocolConstants {

	private Logger LOG = Logger.getLogger(SerializerImpl.class);
	
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
	
	private TransformerHandler createHandler() {
		TransformerHandler handler = null;
		try {
			handler = factory.newTransformerHandler();
			Transformer serializer = handler.getTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, 
					NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
			serializer.setOutputProperty(OutputKeys.INDENT,"no");
		} catch (TransformerConfigurationException tce) {
			LOG.error("transformer could not be configured.");
		} 
		return handler;
	}
	
	/**
	 * @see ch.iserver.ace.net.protocol.Serializer#createQuery(int)
	 */
	public byte[] createQuery(int type) throws SerializeException {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] createRequest(int type, Object data) throws SerializeException {
		try {
			TransformerHandler handler = createHandler();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			handler.setResult(result);
			handler.startDocument();
			AttributesImpl attrs = new AttributesImpl();
			handler.startElement("", "", TAG_ACE, attrs);
			String userid = NetworkServiceImpl.getInstance().getUserId();
			if (type == JOIN) {
				String docId = (String) data;
				createRequest(handler, TAG_JOIN, userid, docId);
			} else if (type == INVITE) {
				String docId = (String) data;
				createRequest(handler, TAG_INVITE, userid, docId);
			} else {
				LOG.error("createRequest(): unknown notification type ["+type+"]");
			}
			handler.endElement("", "", TAG_ACE);
			handler.endDocument();
			output.flush();
			return output.toByteArray();
		} catch (Exception e) {
			LOG.error("could not serialize ["+e.getMessage()+"]");
			throw new SerializeException(e.getMessage());
		}
	}
	
	private void createRequest(TransformerHandler handler, String type, String userid, String docId) throws SAXException {
		AttributesImpl attrs = new AttributesImpl();
		handler.startElement("", "", TAG_REQUEST, attrs);
		attrs.addAttribute("", "", USER_ID, "", userid);	
		handler.startElement("", "", type, attrs);
		attrs = new AttributesImpl();
		attrs.addAttribute("", "", DOCUMENT_ID, "", docId);
		handler.startElement("", "", TAG_DOC, attrs);
		handler.endElement("", "", TAG_DOC);
		handler.endElement("", "", type);
		handler.endElement("", "", TAG_REQUEST);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] createResponse(int type, Object data1, Object data2) throws SerializeException {
		try {
			TransformerHandler handler = createHandler();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			handler.setResult(result);
			handler.startDocument();
			AttributesImpl attrs = new AttributesImpl();
			handler.startElement("", "", TAG_ACE, attrs);
			handler.startElement("", "", TAG_RESPONSE, attrs);
			//TODO: find a smoother way to get userid
			String userid = NetworkServiceImpl.getInstance().getUserId();
			
			if (type == INVITE_REJECTED) {
				String docId = (String) data1;
				attrs.addAttribute("", "", DOC_ID, "", docId);
				attrs.addAttribute("", "", USER_ID, "", userid);
				handler.startElement("", "", TAG_INVITE_REJECTED, attrs);
				handler.endElement("", "", TAG_INVITE_REJECTED);
			} else if (type == JOIN_REJECTED) {
				String docid = (String) data1;
				attrs.addAttribute("", "", DOC_ID, "", docid);
				attrs.addAttribute("", "", USER_ID, "", userid);
				handler.startElement("", "", TAG_JOIN_REJECTED, attrs);
				attrs = new AttributesImpl();
				String code = (String) data2;
				attrs.addAttribute("", "", CODE, "", code);
				handler.startElement("", "", TAG_REASON, attrs);
				handler.endElement("", "", TAG_REASON);
				handler.endElement("", "", TAG_JOIN_REJECTED);
			}
			handler.endElement("", "", TAG_RESPONSE);
			handler.endElement("", "", TAG_ACE);
			handler.endDocument();
			output.flush();
			return output.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("could not serialize ["+e+", "+e.getMessage()+"]");
			throw new SerializeException(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public byte[] createNotification(int type, Object data) throws SerializeException {
		try {
			TransformerHandler handler = createHandler();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			handler.setResult(result);
			handler.startDocument();
			AttributesImpl attrs = new AttributesImpl();
			handler.startElement("", "", TAG_ACE, attrs);
			
			if (type == SEND_DOCUMENTS) {
				Map docs = (Map) data;
				generateSendDocumentsXML(handler, docs);
			} else if (type == PUBLISH) {
				PublishedDocument doc = (PublishedDocument)data;	
				generatePublishXML(handler, doc);
			} else if (type == CONCEAL) {
				PublishedDocument doc = (PublishedDocument)data;	
				generateConcealXML(handler, doc);
			} else if (type == DOCUMENT_DETAILS_CHANGED) {
				PublishedDocument doc = (PublishedDocument)data;
				generateDocumentDetailsChangedXML(handler, doc);
			} else {
				LOG.error("createNotification(): unknown notification type ["+type+"]");
			}
			handler.endElement("", "", TAG_ACE);
			handler.endDocument();
			output.flush();
			return output.toByteArray();
		} catch (Exception e) {
			LOG.error("could not serialize ["+e.getMessage()+"]");
			throw new SerializeException(e.getMessage());
		}
	}
	//TODO: write unit test
	private void generateDocumentDetailsChangedXML(TransformerHandler handler, PublishedDocument doc) throws Exception {
		AttributesImpl attrs = new AttributesImpl();
		handler.startElement("", "", TAG_NOTIFICATION, attrs);
		String userid = NetworkServiceImpl.getInstance().getUserId();
		attrs.addAttribute("", "", USER_ID, "", userid);	
		handler.startElement("", "", TAG_DOCUMENT_DETAILS_CHANGED, attrs);
		attrs = new AttributesImpl();
		attrs.addAttribute("", "", DOCUMENT_ID, "", doc.getId());
		attrs.addAttribute("", "", NAME, "", doc.getDocumentDetails().getTitle());
		handler.startElement("", "", TAG_DOC, attrs);
		handler.endElement("", "", TAG_DOC);
		handler.endElement("", "", TAG_DOCUMENT_DETAILS_CHANGED);
		handler.endElement("", "", TAG_NOTIFICATION);
	}

	private void generateSendDocumentsXML(TransformerHandler handler, Map docs) throws Exception {
		AttributesImpl attrs = new AttributesImpl();
		handler.startElement("", "", TAG_NOTIFICATION, attrs);
		String userid = NetworkServiceImpl.getInstance().getUserId();
		attrs.addAttribute("", "", USER_ID, "", userid);	
		handler.startElement("", "", TAG_PUBLISHED_DOCS, attrs);
		synchronized(docs) {
			Iterator docIter = docs.values().iterator();
			while (docIter.hasNext()) {
				PublishedDocument doc = (PublishedDocument)docIter.next();
				attrs = new AttributesImpl();
				attrs.addAttribute("", "", DOCUMENT_ID, "", doc.getId());
				attrs.addAttribute("", "", NAME, "", doc.getDocumentDetails().getTitle());
				handler.startElement("", "", TAG_DOC, attrs);
				handler.endElement("", "", TAG_DOC);
			}
		}
		handler.endElement("", "", TAG_PUBLISHED_DOCS);
		handler.endElement("", "", TAG_NOTIFICATION);
		handler.endDocument();
	}

	private void generatePublishXML(TransformerHandler handler, PublishedDocument doc) throws Exception {
		AttributesImpl attrs = new AttributesImpl();
		handler.startElement("", "", TAG_NOTIFICATION, attrs);
		String userid = NetworkServiceImpl.getInstance().getUserId();
		attrs.addAttribute("", "", USER_ID, "", userid);	
		handler.startElement("", "", TAG_PUBLISH, attrs);
		attrs = new AttributesImpl();
		attrs.addAttribute("", "", DOCUMENT_ID, "", doc.getId());
		attrs.addAttribute("", "", NAME, "", doc.getDocumentDetails().getTitle());
		handler.startElement("", "", TAG_DOC, attrs);
		handler.endElement("", "", TAG_DOC);
		handler.endElement("", "", TAG_PUBLISH);
		handler.endElement("", "", TAG_NOTIFICATION);
	}
	
	private void generateConcealXML(TransformerHandler handler, PublishedDocument doc) throws Exception {
		AttributesImpl attrs = new AttributesImpl();
		handler.startElement("", "", TAG_NOTIFICATION, attrs);
		String userid = NetworkServiceImpl.getInstance().getUserId();
		attrs.addAttribute("", "", USER_ID, "", userid);
		handler.startElement("", "", TAG_CONCEAL, attrs);
		attrs = new AttributesImpl();
		attrs.addAttribute("", "", DOCUMENT_ID, "", doc.getId());
		handler.startElement("", "", TAG_DOC, attrs);
		handler.endElement("", "", TAG_DOC);
		handler.endElement("", "", TAG_CONCEAL);
		handler.endElement("", "", TAG_NOTIFICATION);
	}

	/**
	 * {@inheritDoc}
	 */
	public byte[] createSessionMessage(int type, Object data1, Object data2) throws SerializeException {
		throw new UnsupportedOperationException();
	}
}
