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
package ch.iserver.ace.text;

import java.security.InvalidParameterException;

import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.InclusionTransformation;

/**
 *
 */
public class GOTInclusionTransformation implements InclusionTransformation {

    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.InclusionTransformation#transform(ch.iserver.ace.Operation, ch.iserver.ace.Operation)
     */
    public Operation transform(Operation op1, Operation op2) {
        Operation transformedOp;
        if (op1 instanceof InsertOperation && op2 instanceof InsertOperation) {
            transformedOp = transform((InsertOperation)op1, (InsertOperation)op2);
        } else if (op1 instanceof InsertOperation && op2 instanceof DeleteOperation) {
            transformedOp = transform((InsertOperation)op1, (DeleteOperation)op2);   
        } else if (op1 instanceof DeleteOperation && op2 instanceof InsertOperation) {
            transformedOp = transform((DeleteOperation)op1, (InsertOperation)op2);
        } else if (op1 instanceof DeleteOperation && op2 instanceof DeleteOperation) {
            transformedOp = transform((DeleteOperation)op1, (DeleteOperation)op2);
        } else {
            throw new InvalidParameterException();
        }
        return transformedOp;
    }
    
    private Operation transform(InsertOperation ins1, InsertOperation ins2) {
        return null;
    }
    
    private Operation transform(InsertOperation ins, DeleteOperation del) {
        return null;
    }
    
    private Operation transform(DeleteOperation del, InsertOperation ins) {
        return null;
    }
    
    private Operation transform(DeleteOperation del, DeleteOperation del2) {
        return null;
    }
}
