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

package ch.iserver.ace.net;

import java.util.ArrayList;
import java.util.Iterator;

import ch.iserver.ace.CaretUpdate;

/**
 *
 */
public class PortableDocumentStub implements PortableDocument {

	/**
	 * @see ch.iserver.ace.net.PortableDocument#getParticipantIds()
	 */
	public int[] getParticipantIds() {
		return new int[0];
	}

	/**
	 * @see ch.iserver.ace.net.PortableDocument#getUserProxy(int)
	 */
	public RemoteUserProxy getUserProxy(int participantId) {
		return null;
	}

	/**
	 * @see ch.iserver.ace.net.PortableDocument#getSelection(int)
	 */
	public CaretUpdate getSelection(int participantId) {
		return null;
	}

	/**
	 * @see ch.iserver.ace.net.PortableDocument#getFragments()
	 */
	public Iterator getFragments() {
		return new ArrayList().iterator();
	}

}
