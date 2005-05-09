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

import java.util.ArrayList;
import java.util.List;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.InclusionTransformation;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;

/**
 *
 */
public class Jupiter implements Algorithm {

    private List operationBuffer;
    private InclusionTransformation inclusion;
    private DocumentModel document;
    private Timestamp stateVector;
    
    /**
     * @param it the inclusion transformation to be used
     * @param document the inital document model
     * @param timestamp the inital time stamp (state vector)
     */
    public Jupiter(InclusionTransformation it, DocumentModel document, Timestamp timestamp) {
        this.inclusion = it;
        this.document = document;
        this.stateVector = timestamp;
        this.operationBuffer = new ArrayList();
    }
    
    public Jupiter() {
        operationBuffer = new ArrayList();
    }
    
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#generateRequest(ch.iserver.ace.Operation)
     */
    public Request generateRequest(Operation op) {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#receiveRequest(ch.iserver.ace.algorithm.Request)
     */
    public void receiveRequest(Request req) {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#init(ch.iserver.ace.DocumentModel, ch.iserver.ace.algorithm.Timestamp)
     */
    public void init(DocumentModel doc, Timestamp timestamp) {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#getDocument()
     */
    public DocumentModel getDocument() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#siteAdded(int)
     */
    public void siteAdded(int siteId) {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#siteRemoved(int)
     */
    public void siteRemoved(int siteId) {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#undo()
     */
    public Request undo() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.Algorithm#redo()
     */
    public Request redo() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void setInclusionTransformation(InclusionTransformation it) {
        this.inclusion = it;
    }
    
    public InclusionTransformation getInclusionTransformation() {
        return inclusion;
    }
}
