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

import ch.iserver.ace.algorithm.Timestamp;

/**
 * This class models the vector time for the Jupiter control algorithm. 
 */
public class JupiterVectorTime implements Timestamp {

    /**
     * Counter for the number of local operations. 
     */
    private int localOperationCnt;
    
    /**
     * Counter for the number of remote requests.
     */
    private int remoteRequestCnt;
    
    /**
     * Create a new JupiterVectorTime.
     * 
     * @param localCnt 	the local operation count.
     * @param remoteCnt	the remote request count.
     */
    public JupiterVectorTime(int localCnt, int remoteCnt) {
        assert localCnt >= 0 : "local operation count must be >= 0";
        assert remoteCnt >= 0 : "remote request count must be >= 0";
        localOperationCnt = localCnt;
        remoteRequestCnt = remoteCnt;
    }
    
    /**
     * @return Returns the clientMsgs.
     */
    public int getLocalOperationCount() {
        return localOperationCnt;
    }
    /**
     * @return Returns the serverMsgs.
     */
    public int getRemoteRequestCount() {
        return remoteRequestCnt;
    }
    
    /**
     * Increment the local operation counter.
     * 
     * @return	the counter after increment.
     */
    public int incrementLocalOperationCount() {
        return ++localOperationCnt;
    }
    
    /**
     * Increment the remote request counter.
     * 
     * @return 	the counter after increment.
     */
    public int incrementRemoteRequestCount() {
        return ++remoteRequestCnt;
    }
    
}
