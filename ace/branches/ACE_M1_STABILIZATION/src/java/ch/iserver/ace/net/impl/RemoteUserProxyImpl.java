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
import java.util.HashMap;
import java.util.Map;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.util.ParameterValidator;

public class RemoteUserProxyImpl implements RemoteUserProxyExt {
	
	private String id;
	private MutableUserDetails details;
	private Map documents;
	private boolean isSessionEstablished;
	
	public RemoteUserProxyImpl(String id, MutableUserDetails details) {
		ParameterValidator.notNull("id", id);
		ParameterValidator.notNull("details", details);
		this.id = id;
		this.details = details;
		this.documents = new HashMap();
		isSessionEstablished = false;
	}

	public String getId() {
		return id;
	}

	public UserDetails getUserDetails() {
		return details;
	}
	
	public MutableUserDetails getMutableUserDetails() {
		return details;
	}

	public Collection getSharedDocuments() {
		return documents.values();
	}

	public void invite(DocumentServerLogic logic) {
		throw new UnsupportedOperationException();
	}

	public void setMutableUserDetails(MutableUserDetails details) {
		ParameterValidator.notNull("details", details);
		this.details = details;
	}
	
	public void addSharedDocument(RemoteDocumentProxy doc) {
		documents.put(doc.getId(), doc);
	}
	
	public RemoteDocumentProxy removeSharedDocument(String id) {
		RemoteDocumentProxy doc = (RemoteDocumentProxy) documents.remove(id);
		return doc;
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
				this.getSharedDocuments().equals(proxy.getSharedDocuments());
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

	public void setSessionEstablished(boolean value) {
		isSessionEstablished = value;
	}

	public boolean isSessionEstablished() {
		return isSessionEstablished;
	}
}
