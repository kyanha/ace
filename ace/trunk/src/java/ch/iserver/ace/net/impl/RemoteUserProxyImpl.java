/*
 * $Id:RemoteUserProxyImpl.java 1205 2005-11-14 07:57:10Z zbinl $
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
package ch.iserver.ace.net.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.InputDataStream;
import org.beepcore.beep.core.MessageMSG;
import org.beepcore.beep.core.OutputDataStream;
import org.beepcore.beep.core.ProfileRegistry;
import org.beepcore.beep.core.RequestHandler;
import org.beepcore.beep.lib.Reply;
import org.beepcore.beep.transport.tcp.TCPSession;
import org.beepcore.beep.transport.tcp.TCPSessionCreator;

import ch.iserver.ace.ServerInfo;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.impl.protocol.DataStreamHelper;
import ch.iserver.ace.net.impl.protocol.DeserializerImpl;
import ch.iserver.ace.net.impl.protocol.DiscoveryException;
import ch.iserver.ace.net.impl.protocol.ProfileRegistryFactory;
import ch.iserver.ace.net.impl.protocol.ProtocolConstants;
import ch.iserver.ace.net.impl.protocol.RemoteUserSession;
import ch.iserver.ace.net.impl.protocol.Request;
import ch.iserver.ace.net.impl.protocol.ResponseParserHandler;
import ch.iserver.ace.net.impl.protocol.SessionManager;
import ch.iserver.ace.util.ParameterValidator;

public class RemoteUserProxyImpl implements RemoteUserProxyExt {
	
	private static Logger LOG = Logger.getLogger(RemoteUserProxyImpl.class);
	
	private String id;
	private MutableUserDetails details;
	private Map documents; //docId to RemoteDocumentProxy
	private boolean isSessionEstablished;
	private boolean isExplicitlyDiscovered;
	
	public RemoteUserProxyImpl(String id, MutableUserDetails details) {
		ParameterValidator.notNull("id", id);
		ParameterValidator.notNull("details", details);
		this.id = id;
		this.details = details;
		this.documents = Collections.synchronizedMap(new LinkedHashMap());
		isSessionEstablished = false;
		isExplicitlyDiscovered = false;
	}
	
	/********************************************/
	/** methods from interface RemoteUserProxy **/
	/********************************************/
	public String getId() {
		return id;
	}

	public UserDetails getUserDetails() {
		return details;
	}
	
	public Collection getSharedDocuments() {
		return documents.values();
	}
	
	
	/***********************************************/
	/** methods from interface RemoteUserProxyExt **/
	/***********************************************/
	
	public boolean hasDocumentShared(String id) {
		return documents.containsKey(id);
	}
	
	public Map getDocuments() { 
		return documents;
	}
	
	public MutableUserDetails getMutableUserDetails() {
		return details;
	}

	public void setMutableUserDetails(MutableUserDetails details) {
		ParameterValidator.notNull("details", details);
		this.details = details;
	}
	
	public void addSharedDocument(RemoteDocumentProxyExt doc) {
		documents.put(doc.getId(), doc);
	}
	
	public RemoteDocumentProxyExt removeSharedDocument(String id) {
		RemoteDocumentProxyExt doc = (RemoteDocumentProxyExt) documents.remove(id);
		LOG.debug("remove shared document ["+doc+"]");
		return doc;
	}
	
	public RemoteDocumentProxyExt getSharedDocument(String id) {
		return (RemoteDocumentProxyExt) documents.get(id);
	}
	
	public void setSessionEstablished(boolean value) {
		isSessionEstablished = value;
	}

	public boolean isSessionEstablished() {
		return isSessionEstablished;
	}
	
	public void setExplicityDiscovered(boolean value) {
		this.isExplicitlyDiscovered = value;
	}
	
	public boolean isExplicitlyDiscovered() {
		return isExplicitlyDiscovered;
	}
	
	public void discover() throws DiscoveryException {
		LOG.debug("--> discover()");
		if (isExplicitlyDiscovered() && !isSessionEstablished()) {
			try {
				/** pack into remoteusersession **/
				ProfileRegistry registry = ProfileRegistryFactory.getProfileRegistry();
				TCPSession session =  TCPSessionCreator.initiate(
					getMutableUserDetails().getAddress(), getMutableUserDetails().getPort(), registry);
				String uri = NetworkProperties.get(NetworkProperties.KEY_PROFILE_URI);
				RequestHandler requestHandler = ProfileRegistryFactory.getMainRequestHandler();
				Channel channel = session.startChannel(uri, requestHandler);
				/** pack **/
				
				NetworkServiceImpl service = NetworkServiceImpl.getInstance();
				String userid = service.getUserId();
				String username = service.getUserDetails().getUsername();
				ServerInfo info = service.getServerInfo();
				String address = info.getAddress().getHostAddress();
				String port = Integer.toString(info.getPort());
				String user = "<user id=\"" + userid + "\" name=\"" + username + "\" address=\"" + address + "\" port=\"" + port + "\"/>";
				String channelType = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						"<ace><channel type=\"" + RemoteUserSession.CHANNEL_MAIN + "\" discovery=\"true\">" + user + "</channel></ace>";
				byte[] data = channelType.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
				OutputDataStream output = DataStreamHelper.prepare(data);
				Reply reply = new Reply();
				channel.sendMSG(output, reply);
				
				//process response
				LOG.debug("--> getNextReply()");
				InputDataStream response = reply.getNextReply().getDataStream();
				LOG.debug("<-- getNextReply()");
				byte[] rawData = DataStreamHelper.read(response);
				ResponseParserHandler handler = new ResponseParserHandler();
				DeserializerImpl.getInstance().deserialize(rawData, handler);
				Request result = handler.getResult();
				if (result.getType() == ProtocolConstants.USER_DISCOVERY) {
					id = result.getUserId();
					String name = (String) result.getPayload();
					getMutableUserDetails().setUsername(name);
				} else { //only for debugging
					LOG.error("unknown response type parsed.");
				}
				SessionManager.getInstance().createSession(this, session, channel);
				LOG.debug("user discovery succeded.");
			} catch (Exception e) {
				throw new DiscoveryException(e);
			}
		} else {
			LOG.debug("not going to discover user, is not explicitly discovered or session already established");
		}
		LOG.debug("<-- discover()");
	}
	
	/**
	 * @inheritDoc
	 */
	public String toString() {
		return "RemoteUserProxyImpl( "+id+", "+details+", "+documents+" )";
	}
	
	/**
	 * @inheritDoc
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof RemoteUserProxyImpl) {
			RemoteUserProxyImpl proxy = (RemoteUserProxyImpl) obj;
			return this.getId().equals(proxy.getId()) && 
				this.getUserDetails().equals(proxy.getUserDetails()) && 
				this.getDocuments().equals(proxy.getDocuments());
		}
		return false;
	}
	
	/**
	 * @inheritDoc
	 */
	public int hashCode() {
		int hash = 13;
		hash += id.hashCode();
		hash += details.hashCode();
		hash += documents.hashCode();
		return hash;
	}
	
	private class DiscoveryHandler implements RequestHandler {
		
		public void receiveMSG(MessageMSG message) {
			
			
			
		}
	}
}
