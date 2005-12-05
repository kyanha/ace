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

import java.util.List;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Fragment;
import ch.iserver.ace.net.PortableDocument;

/**
 *
 */
public interface PortableDocumentExt extends PortableDocument {

	public int getParticipantId();
	
	public void setParticpantId(int id);
	
	public void addFragment(Fragment fragment);
	
	public void addParticipant(int id, RemoteUserProxyExt proxy);
	
	public void setSelection(int participantId, CaretUpdate selection);
	
	public void setDocumentId(String docId);
	
	public String getDocumentId();
	
	public void setPublisherId(String publisherId);
	
	public String getPublisherId();
	
	public List getUsers();
	
}
