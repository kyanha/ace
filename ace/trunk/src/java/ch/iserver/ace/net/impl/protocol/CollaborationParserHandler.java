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
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.algorithm.TimestampFactory;
import ch.iserver.ace.net.impl.MutableUserDetails;
import ch.iserver.ace.net.impl.NetworkProperties;
import ch.iserver.ace.net.impl.PortableDocumentExt;
import ch.iserver.ace.net.impl.PortableDocumentImpl;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.RemoteUserProxyFactory;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;
import ch.iserver.ace.text.DeleteOperation;
import ch.iserver.ace.text.InsertOperation;
import ch.iserver.ace.util.Base64;

/**
 *
 */
public class CollaborationParserHandler extends ParserHandler {

	private static Logger LOG = Logger.getLogger(CollaborationParserHandler.class);
	
	private Request result;
	private DocumentInfo info;
	private int resultType, currentType;
	private PortableDocumentExt document;
	private boolean participants, isTextEncoded;
	private int currParticipantId, totalOriginalCount;
	private StringBuffer buffer;
	private String participantId, siteId;
	private Timestamp timestamp;
	private Operation currOperation;
	private CaretUpdateMessage caretMsg;
	
	private TimestampFactory factory;
	
	private Stack operationStack;
	
	
	public CollaborationParserHandler() {
	}
	
	public void setTimestampFactory(TimestampFactory factory) {
		this.factory = factory;
	}
	
	public void startDocument() throws SAXException {
		result = null;
		resultType = -1;
		currentType = -1;
		isTextEncoded = false;
	}
	
	public void endDocument() throws SAXException {
		int type = getType();
		if (type == LEAVE || type == KICKED) {
			result = new RequestImpl(type, null, info);
			info = null;
		} else if (type == JOIN_DOCUMENT) {
			result = new RequestImpl(JOIN_DOCUMENT, null, document);
			document = null;
		} else if (type == REQUEST) {
			assert operationStack.isEmpty();
			ch.iserver.ace.algorithm.Request request = 
				new ch.iserver.ace.algorithm.RequestImpl(Integer.parseInt(siteId), timestamp, currOperation);
			result = new RequestImpl(REQUEST, participantId, request);
			operationStack = null;
			request = null;
		} else if (type == CARET_UPDATE) {
			result = new RequestImpl(CARET_UPDATE, participantId, caretMsg);
			caretMsg = null;
		}
		buffer = null;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (currentType == JOIN_DOCUMENT) {
			if (participants) {
				if (qName.equals(PARTICIPANT)) {
					String participantId = attributes.getValue(ID); 
					currParticipantId = Integer.parseInt(participantId);
				} else if (qName.equals(USER)) {
					handleUser(attributes);
				} else { //selection
					String dot = attributes.getValue(DOT);
					String mark = attributes.getValue(MARK);
					CaretUpdate selection = new CaretUpdate(Integer.parseInt(dot), Integer.parseInt(mark));
					document.setSelection(currParticipantId, selection);
				}
			} else if (qName.equals(PARTICIPANTS)) {
				participants = true;
			} else if (qName.equals(DATA)) {
				buffer = new StringBuffer();
			}
		} else if (qName.equals(TAG_JOIN_DOCUMENT)) {
			initJoinDocumentParsing(attributes);
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
		} else if (qName.equals(TAG_REQUEST)) {
			siteId = attributes.getValue(SITE_ID);
			participantId = attributes.getValue(PARTICIPANT_ID);
			resultType = REQUEST;
			currentType = resultType;
			totalOriginalCount = 0;
			operationStack = new Stack();
		} else if (qName.equals(TAG_INSERT)) {//ignore tag 'operation'
			String position = attributes.getValue(POSITION);
			String origin = attributes.getValue(ORIGIN);
			currentType = INSERT;
			try {
				InsertOperation insert = new InsertOperation();
				insert.setPosition(Integer.parseInt(position));
				insert.setOrigin(Integer.parseInt(origin));
				currOperation = insert;
			} catch (NumberFormatException nfe) {
				LOG.error("could not parse int's ["+position+", "+origin+"]");
			}
		} else if (qName.equals(TAG_DELETE)) {
			String position = attributes.getValue(POSITION);
			currentType = DELETE;
			try {
				DeleteOperation delete = new DeleteOperation();
				delete.setPosition(Integer.parseInt(position));
				currOperation = delete;
			} catch (NumberFormatException nfe) {
				LOG.error("could not parse position ["+position+"]");
			}
		} else if (qName.equals(TAG_SPLIT)) {
			
			currentType = SPLIT;
			
			LOG.warn("SPLIT not ready to be parsed!!!!");
			
			
		} else if (qName.equals(TAG_CARETUPDATE)) {
			siteId = attributes.getValue(SITE_ID);
			participantId = attributes.getValue(PARTICIPANT_ID);
			resultType = CARET_UPDATE;
			currentType = resultType;
		} else if (qName.equals(TEXT)) {
			String value = attributes.getValue(ENCODED);
			if (value != null) {
				isTextEncoded = true; 
			}
			buffer = new StringBuffer();
		} else if (qName.equals(TAG_ORIGINAL)) {
			operationStack.push(currOperation);
			totalOriginalCount++;
		} else if (qName.equals(TAG_TIMESTAMP)) {
			buffer = new StringBuffer();
		} else if (qName.equals(CARET)) {
			String dotStr = attributes.getValue(DOT);
			String markStr = attributes.getValue(MARK);
			int dot = Integer.parseInt(dotStr);
			int mark = Integer.parseInt(markStr);
			CaretUpdate update = new CaretUpdate(dot, mark);
			caretMsg = new CaretUpdateMessage(Integer.parseInt(siteId), timestamp, update);
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
		} else if (qName.equals(TEXT)) {
			String data = buffer.toString();
			if (isTextEncoded) {
				byte[] decoded = Base64.decode(data);
				try {
					data = new String(decoded, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
				} catch (Exception e) {}
			}
			if (currOperation instanceof InsertOperation) { //TODO: could use currentType maybe instead of instanceof
				((InsertOperation)currOperation).setText(data);
			} else if (currOperation instanceof DeleteOperation) {
				((DeleteOperation)currOperation).setText(data);
			}
			isTextEncoded = false;
		} else if (qName.equals(TAG_ORIGINAL)) {
			//totalOriginalCount > 1 means that original operations are available for the currOperation
			if (!operationStack.isEmpty() && totalOriginalCount > 1) {
				Operation operation = (Operation) operationStack.pop();
				operation.setOriginalOperation(currOperation);
				currOperation = operation;
			}
		} else if (qName.equals(TAG_TIMESTAMP)) {
			StringTokenizer tokens = new StringTokenizer(buffer.toString());
			int[] components = new int[tokens.countTokens()];
			int cnt = 0;
			while(tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				components[cnt++] = Integer.parseInt(token); 
			}
			timestamp = factory.createTimestamp(components);
		}
 	}

	private void initJoinDocumentParsing(Attributes attributes) {
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
	}

	private void handleUser(Attributes attributes) {
		String userId = attributes.getValue(ID);
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
	}
	
	
	public int getType() {
		return resultType;
	}
	
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.impl.protocol.ParserHandler#getResult()
	 */
	public Request getResult() {
		return result;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.impl.protocol.ParserHandler#setMetaData(java.lang.Object)
	 */
	public void setMetaData(Object metadata) {
		throw new UnsupportedOperationException("metadata not supported");
	}

}
