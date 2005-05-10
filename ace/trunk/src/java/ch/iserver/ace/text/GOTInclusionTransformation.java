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
    
    private InsertOperation transform(InsertOperation insA, InsertOperation insB) {
    	InsertOperation transformedOperation = null;
    	int posA = insA.getPosition();
    	int lenA = insA.getTextLength();
    	int posB = insB.getPosition();
    	int lenB = insB.getTextLength();
    	
    	if(posA < posB) {
    		// Case 1:   operation A is before operation B. [///A///] [\\\B\\\]
    		transformedOperation = insA;
    	} else {
    		// Case 2:   operation A is in or behind operation B. [\\\B\\\] [///A///]   OR   [\\\B\\[XX]//A///]
    		transformedOperation = new InsertOperation(posA + lenB, insA.getText());
    	}
        return transformedOperation;
    }
    
    private InsertOperation transform(InsertOperation insA, DeleteOperation delB) {
    	InsertOperation transformedOperation = null;
    	int posA = insA.getPosition();
    	int lenA = insA.getTextLength();
    	int posB = delB.getPosition();
    	int lenB = delB.getTextLength();
    	
    	if(posA <= posB) {
    		// Case 1:   operation A is before or in operation B. [///A///] [\\\B\\\]   OR   [XXXABXXX]
    		transformedOperation = insA;
    	} else if(posA > (posB + lenB)) {
    		// Case 2:   operation A is after operation B. [\\\B\\\] [///A///]
    		transformedOperation = new InsertOperation(posA - lenB, insA.getText());
    	} else {
    		// Case 3:   operation a is in operation B. [\\\B\\[XX]//A///]
    		transformedOperation = new InsertOperation(posB, insA.getText());
    	}
        return transformedOperation;
    }
    
    private DeleteOperation transform(DeleteOperation delA, InsertOperation insB) {
    	DeleteOperation transformedOperation = null;
    	int posA = delA.getPosition();
    	int lenA = delA.getTextLength();
    	int posB = insB.getPosition();
    	int lenB = insB.getTextLength();
    	
    	if(posB >= (posA + lenA)) {
    		// Case 1:   start of operation B is after the end of operation A. [///A///] [\\\B\\\]
    		transformedOperation = delA;
    	} else if(posA >= posB) {
    		// Case 2:   start of operation A is in or behind start of operation B. [\\\B\\\] [///A///]   OR   [\\\B\\[XX]//A///]   OR   [XXXABXXX]
    		transformedOperation = new DeleteOperation(posA + lenB, delA.getText());
    	} else {
    		// insert position is in deleting range -> we need 2 delete operations.
    		// 1st op: new DeleteOperation(posA, delA.subString(posA, posB-posA))
    		// 2nd op: new DeleteOperation(posB + lenB, delA.subString(lenA - (posB - posA)), -1)
    	}
        return transformedOperation;
    }
    
    private DeleteOperation transform(DeleteOperation delA, DeleteOperation delB) {
    	DeleteOperation transformedOperation;
    	int posA = delA.getPosition();
    	int lenA = delA.getTextLength();
    	int posB = delB.getPosition();
    	int lenB = delB.getTextLength();
    	
    	if(posB >= (posA + lenA)) {
    		transformedOperation = delA;
    	} else if(posA >= (posB + lenB)) {
    		transformedOperation = new DeleteOperation(posA + lenB, delA.getText());
    	} else {
    		if((posB <= posA) && ((posA + lenA) <= (posB - lenB))) {
    			transformedOperation = new DeleteOperation(0, "");
    		} else if((posB <= posA) && ((posA + lenA) > (posB + lenB))) {
    			transformedOperation = new DeleteOperation(0, "");
    		} else if((posB > posA) && ((posB + lenB) >= (posA + lenA))) {
    			transformedOperation = new DeleteOperation(0, "");
    		} else {
    			transformedOperation = new DeleteOperation(0, "");
    		}
    	}
        return transformedOperation;
    }
}
