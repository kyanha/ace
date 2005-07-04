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

import org.apache.log4j.Logger;

import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Request;

/**
 * This interface represents a state space. A state space is used by the algorithm to ...
 */
public interface StateSpace {
	
	/**
	 * Inserts the operation of the request into the stace space and returns the transformed operation.
	 * 
	 * @return the transformed operation of the request.
	 */
	public Operation insertRequest(Request request);

	/**
	 * Inserts the operation into the state space and returns the request with the transformed operation.
	 * 
	 * @return the request with the transformed operation.
	 */
	public Request insertOperation(Operation op);

}
