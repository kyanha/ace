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

import java.net.InetAddress;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.net.impl.MutableUserDetails;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.PortableDocumentExt;
import ch.iserver.ace.net.impl.PortableDocumentImpl;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.RemoteUserProxyFactory;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

/**
 *
 */
public class CollaborationParserHandler extends ParserHandler {

	private static Logger LOG = Logger.getLogger(CollaborationParserHandler.class);
	
	private Request result;
	private DocumentInfo info;
	private int resultType, currentType;
	private PortableDocumentExt document;
	private boolean participants, documentData;
	private int currParticipantId;
	private StringBuffer buffer;
	
	public CollaborationParserHandler() {
	}
	
	public void startDocument() throws SAXException {
		result = null;
		resultType = -1;
		currentType = -1;
	}
	
	public void endDocument() throws SAXException {
		int type = getType();
		if (type == LEAVE || type == KICKED) {
			result = new RequestImpl(type, null, info);
		} else if (type == JOIN_DOCUMENT) {
			result = new RequestImpl(JOIN_DOCUMENT, null, document);
		}
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (currentType == JOIN_DOCUMENT) {
			if (participants) {
				if (qName.equals(PARTICIPANT)) {
					String participantId = attributes.getValue(ID); 
					currParticipantId = Integer.parseInt(participantId);
				} else if (qName.equals(USER)) {
					String userId = attributes.getValue(ID);
//					if (userId.equals(NetworkServiceImpl.getInstance().getUserId())) {
//						document.setParticpantId(currParticipantId);
//					}
					String userName = attributes.getValue(NAME);
					String userAddress= attributes.getValue(ADDRESS);
					String userPort = attributes.getValue(PORT);
					InetAddress address = null;
					try {
						address = InetAddress.getByName(userAddress);
					} catch (Exception e) {
						LOG.error("could not parse address ["+userAddress+"]");
					}
					String explicitDiscovery = attributes.getValue(EXPLICIT_DISCOVERY);
					
					MutableUserDetails details = new MutableUserDetails(userName, 
							address, Integer.parseInt(userPort));
					RemoteUserProxyExt proxy = RemoteUserProxyFactory.getInstance().createProxy(userId, details);
					proxy.setExplicityDiscovered(Boolean.getBoolean(explicitDiscovery));
					document.addParticipant(currParticipantId, proxy);
				} else { //selection
					String dot = attributes.getValue(DOT);
					String mark = attributes.getValue(MARK);
					CaretUpdate selection = new CaretUpdate(Integer.parseInt(dot), Integer.parseInt(mark));
					document.setSelection(currParticipantId, selection);
				}
			} else if (qName.equals(PARTICIPANTS)) {
				participants = true;
			} else if (qName.equals(DATA)) {
				documentData = true;
				buffer = new StringBuffer();
			}
		} else if (qName.equals(TAG_JOIN_DOCUMENT)) {
			resultType = JOIN_DOCUMENT;
			currentType = resultType;
			document = new PortableDocumentImpl();
			String docid = attributes.getValue(DOCUMENT_ID);
			document.setDocumentId(docid);
			String userid = attributes.getValue(USER_ID);
			document.setPublisherId(userid);
			String participantId = attributes.getValue(PARTICIPANT_ID);
			int myParticipantId = Integer.parseInt(participantId);
			document.setParticpantId(myParticipantId);
			participants = false;
			documentData = false;
		} else if (qName.equals(TAG_LEAVE)) {
			resultType = LEAVE;
			currentType = resultType;
			String docId = attributes.getValue(DOC_ID);
			String participantId = attributes.getValue(PARTICIPANT_ID);
			info = new DocumentInfo(docId, Integer.parseInt(participantId));
		} else if (qName.equals(TAG_KICKED)) {
			resultType = KICKED;
			currentType = resultType;
			String docId = attributes.getValue(DOC_ID);
			info = new DocumentInfo(docId, -1);
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (buffer != null) {
			buffer.append(ch, start, length);
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals(TAG_JOIN_DOCUMENT)) {
			currentType = -1;
		} else if (qName.equals(TAG_LEAVE)) {
			currentType = -1;
		} else if (qName.equals(TAG_KICKED)) {
			currentType = -1;
		} else if (qName.equals(PARTICIPANTS)) {
			participants = false;
		} else if (qName.equals(DATA)) {
			TLVHandler.parse(buffer.toString(), document);
			documentData = false;
		}
	}
	
	public int getType() {
		return resultType;
	}
	
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.impl.protocol.ParserHandler#getResult()
	 */
	public Request getResult() {
		LOG.debug("getResult("+result+")");
		return result;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.impl.protocol.ParserHandler#setMetaData(java.lang.Object)
	 */
	public void setMetaData(Object metadata) {
		throw new UnsupportedOperationException("metadata not supported");
	}

}
