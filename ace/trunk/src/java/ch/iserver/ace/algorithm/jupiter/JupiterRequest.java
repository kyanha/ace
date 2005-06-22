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
package ch.iserver.ace.algorithm.jupiter;

import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;

/**
 * This class models a Jupiter request.
 */
public class JupiterRequest implements Request {
    
    /**
     * The id from the originating site.
     */
    private int siteId;
    
    /**
     * The vector time of this request.
     */
    private JupiterVectorTime vectorTime;
    
    /**
     * The operation to be excecuted by the receiving site.
     */
    private Operation operation;
    
    
    /**
     * Class constructor.
     * 
     * @param siteId 	
     * 				the Id from the originating site.
     * @param vectorTime		
     * 				the vector time corresponding to the document state
     * 				when the request was created.
     * @param operation		
     * 				the operation to be executed.
     */
    public JupiterRequest(int siteId, JupiterVectorTime vectorTime, Operation operation) {
        this.siteId = siteId;
        this.vectorTime = vectorTime;
        this.operation = operation;
    }

    /**
     * Returns the site id.
     * 
     * @return the site id
     */
    public int getSiteId() {
        return siteId;
    }

	/**
	 * @param siteId The siteId to set.
	 */
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	
    /**
     * {@inheritDoc}
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * {@inheritDoc}
     */
    public Timestamp getTimestamp() {
        return vectorTime;
    }
   
    /**
     * Convenience method that returns the Jupiter vector time
     * of this request. Therefore, no cast is needed.
     * 
     * @return 	the Jupiter vector of this request.
     * @see JupiterVectorTime
     */
    public JupiterVectorTime getJupiterVectorTime() {
        return vectorTime;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
    		return "JupiterRequest(" + siteId + ", " + vectorTime + ", " + operation + ")";
    }

}
