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
 * The RequestEngine is the front-end component for the queue handler
 * and algorithm.
 * 
 * @see ch.iserver.ace.algorithm.QueueHandler
 * @see ch.iserver.ace.algorithm.Algorithm
 */
public interface RequestEngine {

	/**
	 * Generates a request from a given operation.
	 * 
	 * @param op the generated operation passed from the GUI
	 */
    public void generateRequest(Operation op);
    
    /**
     * Receive a request sent over the network.
     * 
     * @param req the request to be received
     */
    public void receiveRequest(Request req);
}
