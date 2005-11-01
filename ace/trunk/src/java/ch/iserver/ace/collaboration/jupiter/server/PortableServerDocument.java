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

package ch.iserver.ace.collaboration.jupiter.server;

import java.util.Iterator;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.jupiter.RemoteUserImpl;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class PortableServerDocument implements PortableDocument {

	private final ch.iserver.ace.collaboration.PortableDocument document;
	
	private int[] participantIds;
	
	public PortableServerDocument(ch.iserver.ace.collaboration.PortableDocument document) {
		ParameterValidator.notNull("document", document);
		this.document = document;
	}
	
	protected ch.iserver.ace.collaboration.PortableDocument getWrappedDocument() {
		return document;
	}
	
	protected Participant getParticipant(int participantId) {
		return getWrappedDocument().getParticipant(participantId);
	}
	
	/**
	 * @see ch.iserver.ace.net.PortableDocument#getParticipantIds()
	 */
	public int[] getParticipantIds() {
		if (participantIds == null) {
			participantIds = new int[0];
		}
		return participantIds;
	}

	/**
	 * @see ch.iserver.ace.net.PortableDocument#getUserProxy(int)
	 */
	public RemoteUserProxy getUserProxy(int participantId) {
		Participant participant = getParticipant(participantId);
		if (participant != null) {
			RemoteUserImpl user = (RemoteUserImpl) participant.getUser();
			return user.getProxy();
		} else {
			return null;
		}
	}

	/**
	 * @see ch.iserver.ace.net.PortableDocument#getSelection(int)
	 */
	public CaretUpdate getSelection(int participantId) {
		return getWrappedDocument().getSelection(participantId);
	}

	/**
	 * @see ch.iserver.ace.net.PortableDocument#getFragments()
	 */
	public Iterator getFragments() {
		return getWrappedDocument().getFragments();
	}

}
