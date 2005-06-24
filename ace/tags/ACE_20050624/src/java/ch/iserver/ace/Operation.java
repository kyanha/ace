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

package ch.iserver.ace;

import java.io.Serializable;

/**
 * This interface must be implemented by all operations. An
 * operation is application dependent and therefore this interface
 * does not contain any specific methods at all.
 */
public interface Operation extends Serializable {
	
	/**
	 * Returns the inverse of this operation.
	 * 
	 * @return the inverse of this operation
	 */
	public Operation inverse();

	/**
	 * Returns true iff this operation is an undo request.
	 * 
	 * @return returns true iff this operation is an undo request
	 */
	public boolean isUndo();
	
	/**
	 * 
	 * @param op
	 */
	public void setOriginalOperation(Operation op);
	
	/**
	 * Returns the original operation if this operation
	 * was transformed. Otherwise null is returned.
	 * 
	 * @return the original operation
	 */
	public Operation getOriginalOperation();
	
}
