/*
 * $Id:PublishedSessionCallbackImpl.java 1091 2005-11-09 13:29:05Z zbinl $
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

package ch.iserver.ace.application;

import ch.iserver.ace.Fragment;
import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.PortableDocument;
import ch.iserver.ace.collaboration.ParticipantSessionCallback;

import java.util.Iterator;



public class ParticipantSessionCallbackImpl extends SessionCallbackImpl implements ParticipantSessionCallback {

	public ParticipantSessionCallbackImpl() {
	}
	
	public synchronized void setDocument(PortableDocument doc) {
		// IMPORTANT: this method is called first from the collaboration layer. set participants here.
		System.out.println("setDocument");

		// add all participants
		Iterator pIter = doc.getParticipants().iterator();
		while(pIter.hasNext()) {
			Participant p = (Participant)pIter.next();
			participationColorManager.addParticipant(p);
		}
		
		// get document fragments
		Iterator fIter = doc.getFragments();
		while(fIter.hasNext()) {
			Fragment f = (Fragment)fIter.next();
			System.out.println(f.getText() + "(" + f.getParticipantId() + ")");
		}
	}
	
	public void sessionTerminated() {
		System.out.println("sessionTerminated");
	}
	
	public void kicked() {
		System.out.println("kicked");
	}
	
}