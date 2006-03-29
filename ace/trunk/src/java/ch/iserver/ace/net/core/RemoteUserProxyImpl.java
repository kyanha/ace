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
package ch.iserver.ace.net.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.protocol.DiscoveryException;
import ch.iserver.ace.net.protocol.RemoteUserSession;
import ch.iserver.ace.net.protocol.SessionManager;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Default implementation of type {@link ch.iserver.ace.net.core.RemoteDocumentProxyExt}.
 * <p>This class embodies a remote user. </p>
 * 
 * @see RemoteUserProxyImpl
 */
public class RemoteUserProxyImpl implements RemoteUserProxyExt {
	
	private static Logger LOG = Logger.getLogger(RemoteUserProxyImpl.class);
	
	/**
	 * The remote user id.
	 */
	private String id;
	
	/**
	 * The remote user details.
	 */
	private MutableUserDetails details;
	
	/**
	 * A map with all shared documents by this remote user.
	 */
	private Map documents; //docId to RemoteDocumentProxy
	
	/**
	 * Flag to indicate whether a session with this remote user is established,
	 * i.e. a physical connection exists (see RemoteUserSession).
	 */
	private boolean isSessionEstablished;
	
	/**
	 * Flag to indicate  whether this user is DNSSD discovered.
	 */
	private boolean isDNSSDdiscovered;
	
	/**
	 * Creates a new RemoteUserProxyImpl instance.
	 * 
	 * @param id			the user id
	 * @param details	the user details
	 */
	public RemoteUserProxyImpl(String id, MutableUserDetails details) {
		ParameterValidator.notNull("id", id);
		ParameterValidator.notNull("details", details);
		this.id = id;
		this.details = details;
		this.documents = Collections.synchronizedMap(new LinkedHashMap());
		isSessionEstablished = false;
		isDNSSDdiscovered = true; //default
	}
	
	/********************************************/
	/** methods from interface RemoteUserProxy **/
	/********************************************/
	
	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	public UserDetails getUserDetails() {
		return details;
	}
		
	
	/***********************************************/
	/** methods from interface RemoteUserProxyExt **/
	/***********************************************/
	
	/**
	 * {@inheritDoc}
	 */
	public void discover() throws DiscoveryException {
		if (!isDNSSDdiscovered() && !isSessionEstablished()) {
			try {
				RemoteUserSession session = SessionManager.getInstance().createSession(this);
				boolean isDiscovery = true;
				session.startMainConnection(isDiscovery);
			} catch (Exception e) {
				throw new DiscoveryException(e);
			}
		} else {
			LOG.warn("not going to discover user, is not explicitly discovered or session already established");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setId(String id) {
		ParameterValidator.notNull("id", id);
		this.id = id;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean hasDocumentShared(String id) {
		return documents.containsKey(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Map getDocuments() { 
		return documents;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MutableUserDetails getMutableUserDetails() {
		return details;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setMutableUserDetails(MutableUserDetails details) {
		ParameterValidator.notNull("details", details);
		this.details = details;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addSharedDocument(RemoteDocumentProxyExt doc) {
		documents.put(doc.getId(), doc);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public RemoteDocumentProxyExt removeSharedDocument(String id) {
		RemoteDocumentProxyExt doc = (RemoteDocumentProxyExt) documents.remove(id);
		LOG.debug("remove shared document ["+doc+"]");
		return doc;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public RemoteDocumentProxyExt getSharedDocument(String id) {
		return (RemoteDocumentProxyExt) documents.get(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setSessionEstablished(boolean value) {
		isSessionEstablished = value;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSessionEstablished() {
		return isSessionEstablished;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setDNSSDdiscovered(boolean value) {
		this.isDNSSDdiscovered = value;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isDNSSDdiscovered() {
		return isDNSSDdiscovered;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "RemoteUserProxyImpl( "+id+", "+details+", "+documents+" )";
	}
	
	/**
	 * {@inheritDoc}
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
	 * {@inheritDoc}
	 */
	public int hashCode() {
		int hash = 13;
		hash += id.hashCode();
		hash += details.hashCode();
		hash += documents.hashCode();
		return hash;
	}
}
