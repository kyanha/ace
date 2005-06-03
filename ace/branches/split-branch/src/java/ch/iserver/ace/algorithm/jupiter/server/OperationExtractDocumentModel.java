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
package ch.iserver.ace.algorithm.jupiter.server;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;

/**
 * This is a dummy document model whose sole purpose is to extract
 * the last operation applied to the document.
 */
public class OperationExtractDocumentModel implements DocumentModel {

    private Operation operation;
    
    /* (non-Javadoc)
     * @see ch.iserver.ace.DocumentModel#apply(ch.iserver.ace.Operation)
     */
    public void apply(Operation operation) {
    		System.out.println("OEDM: "+operation);
        this.operation = operation;
    }
    
    public Operation getOperation() {
        return operation;
    }

}
