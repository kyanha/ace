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
public class GOTOInclusionTransformation implements InclusionTransformation {

    /* (non-Javadoc)
     * @see ch.iserver.ace.algorithm.InclusionTransformation#transform(ch.iserver.ace.Operation, ch.iserver.ace.Operation)
     */
    private int transformedCnt = 1;
     
    public Operation transform(Operation op1, Operation op2) {
    	transformedCnt++;
    	//System.out.println(transformedCnt++);
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
			/*
			* Operation A starts before operation B.
			* (B):       "ABCD"
			* (A):      "12"
			* (A'):     "12"
			*/
    		transformedOperation = insA;
    	} else {
			/*
			* Operation A starts in or behind operation B. Index of operation A' must
			* be increased by the length of the text of operation B.
			* (B):      "ABCD"       |     "ABCD"
			* (A):        "12"       |          "12"
			* (A'):     "    12"     |          "12"
			*/
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
			/*
			* Operation A starts before or at the same position like operation B.
			* (B):      "ABCD"     |      "ABCD"
			* (A):      "12"       |     "12"
			* (A'):     "12"       |     "12"
			*/
    		transformedOperation = insA;
    	} else if(posA > (posB + lenB)) {
			/*
			* Operation A starts after operation B. Index of operation A' must
			* be reduced by the length of the text of operation B.
			* (B):      "ABCD"
			* (A):             "12"
			* (A'):        "12"
			*/
    		transformedOperation = new InsertOperation(posA - lenB, insA.getText());
    	} else {
			/*
			* Operation A starts in operation B. Index of A' must be the index of
			* operation B.
			* (B):      "ABCD"
			* (A):        "12"
			* (A'):     "12"
			*/
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
			/*
			* Operation A is completly before operation B.
			* (B):          "ABCD"
			* (A):      "12"
			* (A'):     "12"
			*/
    		transformedOperation = delA;
    	} else if(posA >= posB) {
			/*
			* Operation A starts before or at the same position like operation B.
			* (B):      "ABCD"       |     "ABCD"
			* (A):      "12"         |       "12"
			* (A'):         "12"     |           "12"
			*/
    		transformedOperation = new DeleteOperation(posA + lenB, delA.getText());
    	} else {
			/*
			* !NOT YET IMPLEMENTED!
			* Operation B (insert) is in the range of operation A (delete). Operation A'
			* must be splitted up into two delete operations.
			* (B):       "ABCD"
			* (A):      "123456"
			* (A'):     "1"  "23456"
			*/
		throw new RuntimeException("transform(Delete,Insert): not yet implemented");
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
			/*
			* Operation A is completly before operation B.
			* (B):          "ABCD"
			* (A):      "12"
			* (A'):     "12"
			*/
    		transformedOperation = delA;
    	} else if(posA >= (posB + lenB)) {
			/*
			* Operation A starts at the end or after operation B. Index of operation A'
			* must be reduced by the length of the text of operation B.
			* (B):      "ABCD"
			* (A):             "12"
			* (A'):        "12"
			*/
    		transformedOperation = new DeleteOperation(posA - lenB, delA.getText());
    	} else {
    		/*
			* !NOT YET IMPLEMENTED!
			* Operation A and operation B are overlapping.
			*/
		throw new RuntimeException("transform(Delete,Delete): not yet implemented");
    		
//		if((posB <= posA) && ((posA + lenA) <= (posB - lenB))) {
//    			transformedOperation = new DeleteOperation(0, "");
//    		} else if((posB <= posA) && ((posA + lenA) > (posB + lenB))) {
//    			transformedOperation = new DeleteOperation(0, "");
//    		} else if((posB > posA) && ((posB + lenB) >= (posA + lenA))) {
//    			transformedOperation = new DeleteOperation(0, "");
//    		} else {
//    			transformedOperation = new DeleteOperation(0, "");
//    		}
    	}
        return transformedOperation;
    }
}
