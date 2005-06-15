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

package ch.iserver.ace.algorithm;

import java.util.List;

import ch.iserver.ace.Operation;

/**
 * A RedoOperation is simply used to indicate an redo request
 * issued by the user through the GUI.
 */
public class RedoOperation implements Operation {
	
	/**
	 * Class constructor.
	 */
	public RedoOperation() { }
	
	/*
	 *  (non-Javadoc)
	 * @see ch.iserver.ace.Operation#inverse()
	 */
	public Operation inverse() {
		throw new UnsupportedOperationException();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see ch.iserver.ace.Operation#isUndo()
	 */
	public boolean isUndo() {
		return false;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.Operation#addToHistory(ch.iserver.ace.Operation)
	 */
	public void addToHistory(Operation op) {
		throw new UnsupportedOperationException();

	}
	/* (non-Javadoc)
	 * @see ch.iserver.ace.Operation#getTransformationHistory()
	 */
	public List getTransformationHistory() {
		return null;
	}
	public String toString() {
		return "Redo()";
	}
}
