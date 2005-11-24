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

package ch.iserver.ace.net.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Fragment;
import ch.iserver.ace.net.RemoteUserProxy;

/**
 *
 */
public class PortableDocumentImpl implements PortableDocumentExt {

	private List fragments;
	private Map selections; 	//id to caretupdate
	private Map proxies;		//id to remoteuserproxy
	private String id;
	private String publisherId;
	
	public PortableDocumentImpl() {
		fragments = Collections.synchronizedList(new ArrayList());
		selections = Collections.synchronizedMap(new LinkedHashMap());
		proxies = Collections.synchronizedMap(new LinkedHashMap());
	}
	
	/************************************************/
	/** methods from interface PortableDocumentExt **/
	/************************************************/
	
	public void addFragment(Fragment fragment) {
		fragments.add(fragment);
	}
	
	public void addParticipant(int id, RemoteUserProxyExt proxy) {
		proxies.put(new Integer(id), proxy);
	}
	
	public void setSelection(int participantId, CaretUpdate selection) {
		selections.put(new Integer(participantId), selection);
	}
	
	public void setDocumentId(String id) {
		this.id = id;
	}
	
	public String getDocumentId() {
		return id;
	}
	
	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}
	
	public String getPublisherId() {
		return publisherId;
	}
	
	
	/*********************************************/
	/** methods from interface PortableDocument **/
	/*********************************************/
	
	/**
	 * @see ch.iserver.ace.net.PortableDocument#getParticipantIds()
	 */
	public int[] getParticipantIds() {
		int[] ids = new int[proxies.size()];
		Iterator iter = proxies.keySet().iterator();
		int cnt = 0;
		while (iter.hasNext()) {
			Integer id = (Integer) iter.next();
			ids[cnt++] = id.intValue();
		}
		return ids;
	}

	/**
	 * @see ch.iserver.ace.net.PortableDocument#getUserProxy(int)
	 */
	public RemoteUserProxy getUserProxy(int participantId) {
		return (RemoteUserProxy) proxies.get(new Integer(participantId));
	}

	/**
	 * @see ch.iserver.ace.net.PortableDocument#getSelection(int)
	 */
	public CaretUpdate getSelection(int participantId) {
		return (CaretUpdate) selections.get(new Integer(participantId));
	}

	/**
	 * @see ch.iserver.ace.net.PortableDocument#getFragments()
	 */
	public Iterator getFragments() {
		return fragments.iterator();
	}

}
