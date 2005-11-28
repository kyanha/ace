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

import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.net.impl.MutableUserDetails;
import ch.iserver.ace.net.impl.NetworkConstants;
import ch.iserver.ace.net.impl.NetworkProperties;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.PortableDocumentExt;
import ch.iserver.ace.net.impl.PortableDocumentImpl;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.RemoteUserProxyFactory;
import ch.iserver.ace.net.impl.RemoteUserProxyImpl;

/**
 *
 */
public class ResponseParserHandler extends ParserHandler {
	
	private static Logger LOG = Logger.getLogger(ResponseParserHandler.class);
	
	private Request response;
	private int responseType, currentType;
	private QueryInfo info;
	private PortableDocumentExt document;
	private boolean participants, documentData;
	private int currParticipantId;
	private StringBuffer buffer;
	
	public ResponseParserHandler() {
	}

	public void startDocument() throws SAXException {
		response = null;
		responseType = -1;
		currentType = -1;
		info = (info == null) ? new QueryInfo("", -1) : info;
	}
	
	public void endDocument() throws SAXException {
		if (getType() == JOIN_DOCUMENT) {
			response = new RequestImpl(getType(), null, document);
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
					if (userId.equals(NetworkServiceImpl.getInstance().getUserId())) {
						document.setParticpantId(currParticipantId);
					}
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
			responseType = JOIN_DOCUMENT;
			currentType = responseType;
			document = new PortableDocumentImpl();
			String docid = attributes.getValue(DOCUMENT_ID);
			document.setDocumentId(docid);
			String userid = attributes.getValue(USER_ID);
			document.setPublisherId(userid);
			participants = false;
			documentData = false;
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals(TAG_JOIN_DOCUMENT)) {
			currentType = -1;
		} else if (qName.equals(PARTICIPANTS)) {
			participants = false;
		} else if (qName.equals(DATA)) {
			TLVHandler.parse(buffer.toString(), document);
			documentData = false;
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (buffer != null) {
			buffer.append(ch, start, length);
		}
	}
	
	/**
	 * 
	 */
	public Request getResult() {
		LOG.debug("getResult("+response+")");
		return response;
	}
	
	public int getType() {
		return responseType;
	}

	public void setMetaData(Object metadata) {
		if (metadata instanceof QueryInfo) {
			info = (QueryInfo) metadata;
		} else {
			LOG.warn("unknown metadata type ["+metadata+"]");
		}
	}
}
