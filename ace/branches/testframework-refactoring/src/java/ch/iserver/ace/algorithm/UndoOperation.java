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


import ch.iserver.ace.Operation;

/**
 * An UndoOperation is simply used to indicate an undo request
 * issued by the user through the GUI.
 */
public class UndoOperation implements Operation {
	
	/**
	 * Class constructor.
	 */
	public UndoOperation() { }
	
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
		return true;
	}

	/*
	 *  (non-Javadoc)
	 * @see ch.iserver.ace.Operation#setOriginalOperation(ch.iserver.ace.Operation)
	 */
	public void setOriginalOperation(Operation op) {
		throw new UnsupportedOperationException();
	}
	/*
	 *  (non-Javadoc)
	 * @see ch.iserver.ace.Operation#getOriginalOperation()
	 */
	public Operation getOriginalOperation() {
		return null;
	}
	
	public String toString() {
		return "Undo()";
	}
}