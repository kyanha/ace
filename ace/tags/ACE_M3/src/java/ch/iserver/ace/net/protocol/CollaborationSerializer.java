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

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.xml.sax.helpers.AttributesImpl;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.ServerInfo;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Operation;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.algorithm.text.DeleteOperation;
import ch.iserver.ace.algorithm.text.InsertOperation;
import ch.iserver.ace.algorithm.text.NoOperation;
import ch.iserver.ace.algorithm.text.SplitOperation;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.core.MutableUserDetails;
import ch.iserver.ace.net.core.NetworkProperties;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.core.RemoteUserProxyExt;
import ch.iserver.ace.net.protocol.RequestImpl.DocumentInfo;
import ch.iserver.ace.util.Base64;

/**
 * Serializes XML messages directly used in a collaborative session.
 * 
 */
public class CollaborationSerializer implements Serializer, ProtocolConstants {

	private Logger LOG = Logger.getLogger(CollaborationSerializer.class);
	
	private SAXTransformerFactory factory;
	
	
	public CollaborationSerializer() {
		factory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
	}
	
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.impl.protocol.Serializer#createQuery(int)
	 */
	public byte[] createQuery(int type) throws SerializeException {
		return null;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.impl.protocol.Serializer#createRequest(int, java.lang.Object)
	 */
	public byte[] createRequest(int type, Object data)
			throws SerializeException {
		return null;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.impl.protocol.Serializer#createResponse(int, java.lang.Object, java.lang.Object)
	 */
	public byte[] createResponse(int type, Object data1, Object data2)
			throws SerializeException {
		try {
			TransformerHandler handler = createHandler();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			handler.setResult(result);
			handler.startDocument();
			AttributesImpl attrs = new AttributesImpl();
			handler.startElement("", "", TAG_ACE, attrs);
			handler.startElement("", "", TAG_RESPONSE, attrs);
			String userid = NetworkServiceImpl.getInstance().getUserId();
			
			if (type == JOIN_DOCUMENT) {
				DocumentInfo publishedDoc = (DocumentInfo) data1;
				attrs.addAttribute("", "", ID, "", publishedDoc.getDocId());
				attrs.addAttribute("", "", USER_ID, "", userid);
				attrs.addAttribute("", "", PARTICIPANT_ID, "", Integer.toString(publishedDoc.getParticipantId()));
				handler.startElement("", "", TAG_JOIN_DOCUMENT, attrs);
				PortableDocument doc = (PortableDocument) data2;
				createParticipantTag(handler, doc);
				attrs = new AttributesImpl();
				handler.startElement("", "", DATA, attrs);
				char[] data = TLVHandler.create(doc);
				handler.characters(data, 0, data.length);
				handler.endElement("", "", DATA);
				handler.endElement("", "", TAG_JOIN_DOCUMENT);
			} else if (type == JOIN_REJECTED) {
				throw new IllegalStateException("JOIN_REJECTED must be serialized with instance of SerializerImpl");
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
	
	private void createParticipantTag(TransformerHandler handler, PortableDocument doc) throws Exception	 {
		AttributesImpl attrs = new AttributesImpl();
		handler.startElement("", "", PARTICIPANTS, attrs);
		int[] ids = doc.getParticipantIds();
		for (int i = 0; i < ids.length; i++) {
			attrs = new AttributesImpl();
			int id = ids[i];
			attrs.addAttribute("", "", ID, "", Integer.toString(id));
			handler.startElement("", "", PARTICIPANT, attrs);
			attrs = new AttributesImpl();
			String userid, name, address, port, isDNSSDdiscoveredStr;
			boolean isDNSSDdiscovered;
			if (id == ParticipantConnection.PUBLISHER_ID) {
				NetworkServiceImpl service = NetworkServiceImpl.getInstance();
				userid = service.getUserId();
				name = service.getUserDetails().getUsername();
				ServerInfo info = service.getServerInfo();
				address = info.getAddress().getHostAddress();
				port = Integer.toString(info.getPort());
				isDNSSDdiscovered = true; //will be ignored at the receiver site
				isDNSSDdiscoveredStr = Boolean.toString(isDNSSDdiscovered);
			} else {
				RemoteUserProxyExt proxy = (RemoteUserProxyExt) doc.getUserProxy(id);
				userid = proxy.getId();
				MutableUserDetails details = proxy.getMutableUserDetails();
				name = details.getUsername();
				address = details.getAddress().getHostAddress();
				port = Integer.toString(details.getPort());
				isDNSSDdiscovered = proxy.isDNSSDdiscovered();
				isDNSSDdiscoveredStr = Boolean.toString(isDNSSDdiscovered);
			}
			attrs.addAttribute("", "", ID, "", userid);
			attrs.addAttribute("", "", NAME, "", name);
			attrs.addAttribute("", "", ADDRESS, "", address);
			attrs.addAttribute("", "", PORT, "", port);
			attrs.addAttribute("", "", DNSSD_DISCOVERY, "", isDNSSDdiscoveredStr);
			handler.startElement("", "", USER, attrs);
			if (!isDNSSDdiscovered) {
				//TODO: add published documents of current user in the for loop
			}
			handler.endElement("", "", USER);
			attrs = new AttributesImpl();
			CaretUpdate selection = doc.getSelection(id);
			String mark = Integer.toString(selection.getMark());
			attrs.addAttribute("", "", MARK, "", mark);
			String dot = Integer.toString(selection.getDot());
			attrs.addAttribute("", "", DOT, "", dot);
			handler.startElement("", "", SELECTION, attrs);
			handler.endElement("", "", SELECTION);
			handler.endElement("", "", PARTICIPANT);
		}
		handler.endElement("", "", PARTICIPANTS);
		
	}
	

	/**
	 * @see ch.iserver.ace.net.protocol.Serializer#createNotification(int, java.lang.Object)
	 */
	public byte[] createNotification(int type, Object data)
			throws SerializeException {
		try {
			TransformerHandler handler = createHandler();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			handler.setResult(result);
			handler.startDocument();
			AttributesImpl attrs = new AttributesImpl();
			handler.startElement("", "", TAG_ACE, attrs);
			handler.startElement("", "", TAG_NOTIFICATION, attrs);
			if (type == LEAVE) {
				SessionConnectionImpl conn = (SessionConnectionImpl) data;
				attrs.addAttribute("", "", DOC_ID, "", conn.getDocumentId());
				attrs.addAttribute("", "", PARTICIPANT_ID, "", Integer.toString(conn.getParticipantId()));
				handler.startElement("", "", TAG_LEAVE, attrs);
				handler.endElement("", "", TAG_LEAVE);
			} else if (type == KICKED) {
				String docId = (String) data;
				attrs.addAttribute("", "", DOC_ID, "", docId);
				handler.startElement("", "", TAG_KICKED, attrs);
				handler.endElement("", "", TAG_KICKED);
			} else {
				LOG.error("unknown notification type ["+type+"]");
			}
			handler.endElement("", "", TAG_NOTIFICATION);
			handler.endElement("", "", TAG_ACE);
			handler.endDocument();
			output.flush();
			return output.toByteArray();
		} catch (Exception e) {
			LOG.error("could not serialize ["+e.getMessage()+"]");
			throw new SerializeException(e.getMessage());
		}
	}
	
	public byte[] createSessionMessage(int type, Object data, Object data2) throws SerializeException {
		try {
			TransformerHandler handler = createHandler();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			handler.setResult(result);
			handler.startDocument();
			AttributesImpl attrs = new AttributesImpl();
			handler.startElement("", "", TAG_ACE, attrs);
			handler.startElement("", "", TAG_SESSION, attrs);
			
			if (type == REQUEST) {
				Request algoRequest = (Request) data;
				int siteId = algoRequest.getSiteId();
				attrs.addAttribute("", "", SITE_ID, "", Integer.toString(siteId));
				String participantId = (String) data2;
				attrs.addAttribute("", "", PARTICIPANT_ID, "", participantId);
				handler.startElement("", "", TAG_REQUEST, attrs);
				attrs = new AttributesImpl();
				handler.startElement("", "", TAG_OPERATION, attrs);
				Operation operation = algoRequest.getOperation();
				addOperation(handler, operation);
				handler.endElement("", "", TAG_OPERATION);
				attrs = new AttributesImpl();
				handler.startElement("", "", TAG_TIMESTAMP, attrs);
				int[] components = algoRequest.getTimestamp().getComponents();
				char[] chars = getComponentsAsString(components).toCharArray();
				handler.characters(chars, 0, chars.length);
				handler.endElement("", "", TAG_TIMESTAMP);
				handler.endElement("", "", TAG_REQUEST);
			} else if (type == CARET_UPDATE) {
				CaretUpdateMessage caretMsg = (CaretUpdateMessage) data;
				int siteId = caretMsg.getSiteId();
				attrs.addAttribute("", "", SITE_ID, "", Integer.toString(siteId));
				String participantId = (String) data2;
				attrs.addAttribute("", "", PARTICIPANT_ID, "", participantId);
				handler.startElement("", "", TAG_CARETUPDATE, attrs);
				attrs = new AttributesImpl();
				handler.startElement("", "", TAG_TIMESTAMP, attrs);
				int[] components = caretMsg.getTimestamp().getComponents();
				char[] chars = getComponentsAsString(components).toCharArray();
				handler.characters(chars, 0, chars.length);
				handler.endElement("", "", TAG_TIMESTAMP);
				int dot = caretMsg.getUpdate().getDot();
				attrs.addAttribute("", "", DOT, "", Integer.toString(dot));
				int mark = caretMsg.getUpdate().getMark();
				attrs.addAttribute("", "", MARK, "", Integer.toString(mark));
				handler.startElement("", "", CARET, attrs);
				handler.endElement("", "", CARET);
				handler.endElement("", "", TAG_CARETUPDATE);
			} else if (type == ACKNOWLEDGE) { 
				String siteId = (String) data2;
				Timestamp timestamp = (Timestamp) data;
				attrs.addAttribute("", "", SITE_ID, "", siteId);
				handler.startElement("", "", TAG_ACKNOWLEDGE, attrs);
				attrs = new AttributesImpl();
				handler.startElement("", "", TAG_TIMESTAMP, attrs);
				int[] components = timestamp.getComponents();
				char[] chars = getComponentsAsString(components).toCharArray();
				handler.characters(chars, 0, chars.length);
				handler.endElement("", "", TAG_TIMESTAMP);
				handler.endElement("", "", TAG_ACKNOWLEDGE);
			} else if (type == PARTICIPANT_JOINED) {  
				String participantId = (String) data2;
				attrs.addAttribute("", "", ID, "", participantId);
				handler.startElement("", "", TAG_PARTICIPANT_JOINED, attrs);
				RemoteUserProxyExt proxy = (RemoteUserProxyExt) data;
				attrs = new AttributesImpl();
				attrs.addAttribute("", "", ID, "", proxy.getId());
				MutableUserDetails details = proxy.getMutableUserDetails();
				attrs.addAttribute("", "", NAME, "", details.getUsername());
				attrs.addAttribute("", "", ADDRESS, "", details.getAddress().getHostAddress());
				attrs.addAttribute("", "", PORT, "", Integer.toString(details.getPort()));
				attrs.addAttribute("", "", DNSSD_DISCOVERY, "", Boolean.toString(proxy.isDNSSDdiscovered()));
				handler.startElement("", "", USER, attrs);
				if (proxy.isDNSSDdiscovered()) {
					//TODO: add published documents of this user
				}
				handler.endElement("", "", USER);
				handler.endElement("", "", TAG_PARTICIPANT_JOINED);
					
			} else if (type == PARTICIPANT_LEFT) { 
				String reason = (String) data;
				String participantId = (String) data2;
				attrs.addAttribute("", "", ID, "", participantId);
				handler.startElement("", "", TAG_PARTICIPANT_LEFT, attrs);
				attrs = new AttributesImpl();
				attrs.addAttribute("", "", CODE, "", reason);
				handler.startElement("", "", TAG_REASON, attrs);
				handler.endElement("", "", TAG_REASON);
				handler.endElement("", "", TAG_PARTICIPANT_LEFT);
			} else if (type == SESSION_TERMINATED) {
				handler.startElement("", "", TAG_SESSION_TERMINATED, attrs);
				handler.endElement("", "", TAG_SESSION_TERMINATED);
			} else {
				LOG.error("unknown notification type ["+type+"]");
			}
			
			handler.endElement("", "", TAG_SESSION);
			handler.endElement("", "", TAG_ACE);
			handler.endDocument();
			output.flush();
			return output.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("could not serialize ["+e.getMessage()+"]");
			throw new SerializeException(e.getMessage());
		}
	}
	
	private String getComponentsAsString(int[] components) {
		String result = "";
		for (int i = 0; i < components.length; i++) {
			result += Integer.toString(components[i]) + " ";
		}
		return result;
	}
	
	private void addOperation(TransformerHandler handler, Operation operation) throws Exception {
		AttributesImpl attrs = new AttributesImpl();
		if (operation instanceof InsertOperation) {
			InsertOperation insert = (InsertOperation) operation;
			attrs.addAttribute("", "", POSITION, "", Integer.toString(insert.getPosition()));
			attrs.addAttribute("", "", ORIGIN, "", Integer.toString(insert.getOrigin()));
			handler.startElement("", "", TAG_INSERT, attrs);
			attrs = new AttributesImpl();
			String text = insert.getText();
			if (text.indexOf('<') >= 0) { //test if text possibly contains XML data, this check should be enhanced
				attrs.addAttribute("", "", ENCODED, "", "Base64");
				text = Base64.encodeBytes(text.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING)));
			}
			handler.startElement("", "", TEXT, attrs);
			handler.characters(text.toCharArray(), 0, text.length());
			handler.endElement("", "", TEXT);
			attrs = new AttributesImpl();
			handler.endElement("", "", TAG_INSERT);
		} else if (operation instanceof DeleteOperation) {
			DeleteOperation delete = (DeleteOperation) operation;
			attrs.addAttribute("", "", POSITION, "", Integer.toString(delete.getPosition()));
			handler.startElement("", "", TAG_DELETE, attrs);
			attrs = new AttributesImpl();
			String text = delete.getText();
			if (text.indexOf('<') >= 0) { //test if text possibly contains XML data, this check should be enhanced
				attrs.addAttribute("", "", ENCODED, "", "Base64");
				text = Base64.encodeBytes(text.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING)));
			}
			handler.startElement("", "", TEXT, attrs);
			handler.characters(text.toCharArray(), 0, text.length());
			handler.endElement("", "", TEXT);
			handler.endElement("", "", TAG_DELETE);
		} else if (operation instanceof SplitOperation) {
			SplitOperation split = (SplitOperation) operation;
			handler.startElement("", "", TAG_SPLIT, attrs);
			handler.startElement("", "", "first", attrs);
			addOperation(handler, split.getFirst());
			handler.endElement("", "", "first");
			handler.startElement("", "", "second", attrs);
			addOperation(handler, split.getSecond());
			handler.endElement("", "", "second");
			handler.endElement("", "", TAG_SPLIT);
		} else if (operation instanceof NoOperation) {
			handler.startElement("", "", TAG_NOOP, attrs);
			handler.endElement("", "", TAG_NOOP);
		} else {
			LOG.error("unknown operation type ["+operation+"]");
		}
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
	
}
