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

import java.util.Iterator;
import java.util.Set;

import ch.iserver.ace.collaboration.Participant;

/**
 * A PortableDocument is a representation of a document in a portable way.
 * PortableDocuments can be serialized in an implementation independent
 * way and thus facilitate collaboration across application boundaries.
 */
public interface PortableDocument {
	
	/**
	 * Gets the set of participants. The type is Set&lt;Participant&gt;.
	 * 
	 * @return the set of participants
	 */
	Set getParticipants();
	
	/**
	 * Gets the caret of the specified <var>participant</var>.
	 * 
	 * @param participant the participant for which to retrieve the caret
	 * @return the Caret of the given Participant
	 */
	Caret getSelection(Participant participant);
	
	/**
	 * Gets an iterator over all the fragments of the document.
	 * 
	 * @return an iterator over all the fragments
	 */
	Iterator getFragments();
	
	/**
	 * A Fragment is continous part of text edited last by one particular
	 * participant, the owner of the fragment.
	 */
	interface Fragment {
		
		/**
		 * Gets the owner of this fragment, that is the participant that last
		 * edited this particular section of text.
		 * 
		 * @return the owner of this fragment
		 */
		Participant getOwner();
		
		/**
		 * Gets the textual content of the fragment.
		 * 
		 * @return the textual content
		 */
		String getText();
		
	}
	
	/**
	 * A Caret specifies the cursor position and selection of a participant.
	 */
	interface Caret {
		
		/**
		 * Gets the current position of the cursor.
		 * 
		 * @return the cursor position
		 */
		int getDot();
		
		/**
		 * Gets the position of the mark (other end of selection).
		 * 
		 * @return the mark position
		 */
		int getMark();
		
	}
	
}
