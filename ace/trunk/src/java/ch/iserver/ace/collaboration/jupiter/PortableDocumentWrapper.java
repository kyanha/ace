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

package ch.iserver.ace.collaboration.jupiter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.collaboration.PortableDocument;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class PortableDocumentWrapper implements PortableDocument {
	
	private final ch.iserver.ace.net.PortableDocument document;
	
	private Set participants;
	
	public PortableDocumentWrapper(ch.iserver.ace.net.PortableDocument doc) {
		ParameterValidator.notNull("doc", doc);
		this.document = doc;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.PortableDocument#getParticipants()
	 */
	public Set getParticipants() {
		if (participants == null) {
			participants = new HashSet();
			int[] ids = document.getParticipantIds();
			for (int i = 0; i < ids.length; i++) {
				int id = ids[i];
				RemoteUser user = new RemoteUserImpl(document.getUserProxy(id));
				participants.add(new ParticipantImpl(id, user));
			}
		}
		return participants;
	}

	/**
	 * @see ch.iserver.ace.collaboration.PortableDocument#getSelection(int)
	 */
	public CaretUpdate getSelection(int participantId) {
		return document.getSelection(participantId);
	}

	/**
	 * @see ch.iserver.ace.collaboration.PortableDocument#getFragments()
	 */
	public Iterator getFragments() {
		return document.getFragments();
	}

}
