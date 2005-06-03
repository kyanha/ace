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
        		System.out.print("\ttransform("+op1+", "+op2+") = ");
        		transformedOp = transform((InsertOperation)op1, (InsertOperation)op2);
             System.out.println(transformedOp);
        } else if (op1 instanceof InsertOperation && op2 instanceof DeleteOperation) {
        		System.out.print("\ttransform("+op1+", "+op2+") = ");
        		transformedOp = transform((InsertOperation)op1, (DeleteOperation)op2);
        		System.out.println(transformedOp);
        } else if (op1 instanceof DeleteOperation && op2 instanceof InsertOperation) {
        		System.out.print("\ttransform("+op1+", "+op2+") = ");  
        		transformedOp = transform((DeleteOperation)op1, (InsertOperation)op2);
        		System.out.println(transformedOp);
        } else if (op1 instanceof DeleteOperation && op2 instanceof DeleteOperation) {
        		System.out.print("\ttransform("+op1+", "+op2+") = ");
        		transformedOp = transform((DeleteOperation)op1, (DeleteOperation)op2);
        		System.out.println(transformedOp);
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
    	//TODO: the char comparison could/should be replaced later by a client/server flag
    	if (posA < posB || posA == posB && insA.getOrigin() < insB.getOrigin() || 
    			posA == posB && insA.getOrigin() == insB.getOrigin() && insA.getText().charAt(0) < insB.getText().charAt(0)) {
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
   			transformedOperation = new InsertOperation(posA + lenB, insA.getText(), insA.getOrigin());
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
    		transformedOperation = new InsertOperation(posA - lenB, insA.getText(), insA.getOrigin());
    	} else {
			/*
			* Operation A starts in operation B. Index of A' must be the index of
			* operation B.
			* (B):      "ABCD"
			* (A):        "12"
			* (A'):     "12"
			*/
    		transformedOperation = new InsertOperation(posB, insA.getText(), insA.getOrigin());
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
			* Operation A and operation B are overlapping.
			*/
			if((posB <= posA) && ((posA + lenA) <= (posB + lenB))) {
				/*
				* Operation B starts before or at the same position like operation A
				* and ends after or at the same position like operation A.
				* (B):      "ABCD"     |     "ABCD
				* (A):       "12"      |     "12"
				* (A'):     ""         |     ""
				*/
				//TODO? information gets lost.
				transformedOperation = new DeleteOperation(posA, "");
			} else if((posB <= posA) && ((posA + lenA) > (posB + lenB))) {
				/*
				* Operation B starts before or at the same position like operation A
				* and ends before operation A.
				* (B):      "ABCD"
				* (A):        "12345"
				* (A'):     "345"
				*/
				transformedOperation = new DeleteOperation(posB, delA.getText().substring(posB + lenB - posA, lenA));
			} else if((posB > posA) && ((posB + lenB) >= (posA + lenA))) {
				/*
				* Operation B starts after operation A and ends after or at the
				* same position like operation A.
				* (B):        "ABCD"
				* (A):      "12345"
				* (A'):     "12"
				*/
				transformedOperation = new DeleteOperation(posA, delA.getText().substring(0, posB - posA));
			} else {
				/*
				* Operation B is fully in operation A.
				* (B):       "ABCD"
				* (A):      "123456"
				* (A'):     "16"
				*/
				transformedOperation = new DeleteOperation(posA, delA.getText().substring(0, posB - posA) + delA.getText().substring(posB + lenB - posA, lenA));
			}
		}
        return transformedOperation;
    }
}
