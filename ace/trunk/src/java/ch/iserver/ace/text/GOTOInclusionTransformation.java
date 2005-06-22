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


import org.apache.log4j.Logger;

import java.security.InvalidParameterException;

import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.InclusionTransformation;

/**
 * Implementation of the GOTO operational transformation functions. The pseudo code
 * can be found in the paper "Achieving Convergence, Causality-preservation, 
 * and Intention-preservation in Real-time cooperative Editing Systems" by 
 * Chengzheng Sun, Xiaohua Jia, Yanchun Zhang, Yun Yang, and David Chen.
 */
public class GOTOInclusionTransformation implements InclusionTransformation {
	
	private static Logger LOG = Logger.getLogger(GOTOInclusionTransformation.class);
	
	/**
	 * This flag is used when two insert operations are to be transformed against and 
	 * both have the same position and origin index. If this flag is true, then the
	 * first insert operation is privileged and remains unchanged.
	 */
	private boolean isTransformOpPrivileged;

	/**
	 * {@inheritDoc}
	 */
    public Operation transform(Operation op1, Operation op2) {
        Operation transformedOp;
        if (op1 instanceof InsertOperation && op2 instanceof InsertOperation) {
        		transformedOp = transform((InsertOperation)op1, (InsertOperation)op2);
        		LOG.info("\ttransform("+op1+", "+isTransformOpPrivileged+", "+op2+") = "+transformedOp);
        } else if (op1 instanceof InsertOperation && op2 instanceof DeleteOperation) {
        		transformedOp = transform((InsertOperation)op1, (DeleteOperation)op2);
        		LOG.info("\ttransform("+op1+", "+op2+") = "+transformedOp);
        } else if (op1 instanceof DeleteOperation && op2 instanceof InsertOperation) {
        		transformedOp = transform((DeleteOperation)op1, (InsertOperation)op2);
        		LOG.info("\ttransform("+op1+", "+op2+") = "+transformedOp);
        } else if (op1 instanceof DeleteOperation && op2 instanceof DeleteOperation) {
        		transformedOp = transform((DeleteOperation)op1, (DeleteOperation)op2);
        		LOG.info("\ttransform("+op1+", "+op2+") = "+transformedOp);
        } else {
            throw new InvalidParameterException();
        }
        return transformedOp;
    }
    
    private Operation transform(InsertOperation insA, InsertOperation insB) {
		InsertOperation transformedOperation = null;
		int posA = insA.getPosition();
		int lenA = insA.getTextLength();
		int posB = insB.getPosition();
		int lenB = insB.getTextLength();
		if (posA < posB || posA == posB && insA.getOrigin() < insB.getOrigin()
				|| posA == posB && insA.getOrigin() == insB.getOrigin()
				&& isTransformOpPrivileged) {
			/* an alternative to isTransformOpPrivileged could be: 		*/
			/* && insA.getText().charAt(0) < insB.getText().charAt(0))  */
			
			/*
			 * Operation A starts before operation B.
			 * (B):       "ABCD"
			 * (A):      "12"
			 * (A'):     "12"
			 */
			transformedOperation = new InsertOperation(posA, insA.getText(),
					insA.getOrigin());
			transformedOperation.setUndo(insA.isUndo());
			//    		transformedOperation = insA;
			transformedOperation.setOriginalOperation(insA);
		} else {
			/*
			 * Operation A starts in or behind operation B. Index of operation A' must
			 * be increased by the length of the text of operation B.
			 * (B):      "ABCD"       |     "ABCD"
			 * (A):        "12"       |          "12"
			 * (A'):     "    12"     |          "12"
			 */
			transformedOperation = new InsertOperation(posA + lenB, insA
					.getText(), insA.getOrigin());
			transformedOperation.setUndo(insA.isUndo());
			transformedOperation.setOriginalOperation(insA);
		}
		return transformedOperation;
	}

	private Operation transform(InsertOperation insA, DeleteOperation delB) {
		InsertOperation transformedOperation = null;
		int posA = insA.getPosition();
		int lenA = insA.getTextLength();
		int posB = delB.getPosition();
		int lenB = delB.getTextLength();

		if (posA <= posB) {
			/*
			 * Operation A starts before or at the same position like operation B.
			 * (B):      "ABCD"     |      "ABCD"
			 * (A):      "12"       |     "12"
			 * (A'):     "12"       |     "12"
			 */
			transformedOperation = new InsertOperation(posA, insA.getText(),
					insA.getOrigin());
			transformedOperation.setUndo(insA.isUndo());
			transformedOperation.setOriginalOperation(insA);
		} else if (posA > (posB + lenB)) {
			/*
			 * Operation A starts after operation B. Index of operation A' must
			 * be reduced by the length of the text of operation B.
			 * (B):      "ABCD"
			 * (A):             "12"
			 * (A'):        "12"
			 */
			transformedOperation = new InsertOperation(posA - lenB, insA
					.getText(), insA.getOrigin());
			transformedOperation.setUndo(insA.isUndo());
			transformedOperation.setOriginalOperation(insA);
		} else {
			/*
			 * Operation A starts in operation B. Index of A' must be the index of
			 * operation B.
			 * (B):      "ABCD"
			 * (A):        "12"
			 * (A'):     "12"
			 */
			transformedOperation = new InsertOperation(posB, insA.getText(),
					insA.getOrigin());
			transformedOperation.setUndo(insA.isUndo());
			transformedOperation.setOriginalOperation(insA);
		}
		return transformedOperation;
	}

	private Operation transform(DeleteOperation delA, InsertOperation insB) {
		Operation transformedOperation = null;
		int posA = delA.getPosition();
		int lenA = delA.getTextLength();
		int posB = insB.getPosition();
		int lenB = insB.getTextLength();

		if (posB >= (posA + lenA)) {
			/*
			 * Operation A is completly before operation B.
			 * (B):          "ABCD"
			 * (A):      "12"
			 * (A'):     "12"
			 */
			transformedOperation = new DeleteOperation(posA, delA.getText());
			((DeleteOperation) transformedOperation).setUndo(delA.isUndo());
			transformedOperation.setOriginalOperation(delA);
		} else if (posA >= posB) {
			/*
			 * Operation A starts before or at the same position like operation B.
			 * (B):      "ABCD"       |     "ABCD"
			 * (A):      "12"         |       "12"
			 * (A'):         "12"     |           "12"
			 */
			transformedOperation = new DeleteOperation(posA + lenB, delA
					.getText());
			((DeleteOperation) transformedOperation).setUndo(delA.isUndo());
			transformedOperation.setOriginalOperation(delA);
		} else {
			/*
			 * Operation B (insert) is in the range of operation A (delete). Operation A'
			 * must be splitted up into two delete operations.
			 * (B):       "ABCD"
			 * (A):      "123456"
			 * (A'):     "1"  "23456"
			 */
			DeleteOperation del1 = new DeleteOperation(posA, delA.getText()
					.substring(0, posB - posA));
			del1.setUndo(delA.isUndo());
			DeleteOperation del2 = new DeleteOperation(posA + lenB
					+ (posB - posA), delA.getText()
					.substring(posB - posA, lenA));
			del2.setUndo(delA.isUndo());
			transformedOperation = new SplitOperation(del1, del2);
			//TODO: how to add the original operation? assume now that it is not necessesary since
			//these operations won't be transformed further.
		}
		return transformedOperation;
	}

	private Operation transform(DeleteOperation delA, DeleteOperation delB) {
		DeleteOperation transformedOperation;
		int posA = delA.getPosition();
		int lenA = delA.getTextLength();
		int posB = delB.getPosition();
		int lenB = delB.getTextLength();

		if (posB >= (posA + lenA)) {
			/*
			 * Operation A is completly before operation B.
			 * (B):          "ABCD"
			 * (A):      "12"
			 * (A'):     "12"
			 */
			transformedOperation = new DeleteOperation(posA, delA.getText());
			transformedOperation.setUndo(delA.isUndo());
			transformedOperation.setOriginalOperation(delA);
		} else if (posA >= (posB + lenB)) {
			/*
			 * Operation A starts at the end or after operation B. Index of operation A'
			 * must be reduced by the length of the text of operation B.
			 * (B):      "ABCD"
			 * (A):             "12"
			 * (A'):        "12"
			 */
			transformedOperation = new DeleteOperation(posA - lenB, delA
					.getText());
			transformedOperation.setUndo(delA.isUndo());
			transformedOperation.setOriginalOperation(delA);
		} else {
			/*
			 * Operation A and operation B are overlapping.
			 */
			if ((posB <= posA) && ((posA + lenA) <= (posB + lenB))) {
				/*
				 * Operation B starts before or at the same position like operation A
				 * and ends after or at the same position like operation A.
				 * (B):      "ABCD"     |     "ABCD
				 * (A):       "12"      |     "12"
				 * (A'):     ""         |     ""
				 */
				//TODO? information gets lost. -> why not create a noop-operation?
				transformedOperation = new DeleteOperation(posA, "");
				transformedOperation.setUndo(delA.isUndo());
				transformedOperation.setOriginalOperation(delA);
			} else if ((posB <= posA) && ((posA + lenA) > (posB + lenB))) {
				/*
				 * Operation B starts before or at the same position like operation A
				 * and ends before operation A.
				 * (B):      "ABCD"
				 * (A):        "12345"
				 * (A'):     "345"
				 */
				transformedOperation = new DeleteOperation(posB, delA.getText()
						.substring(posB + lenB - posA, lenA));
				transformedOperation.setUndo(delA.isUndo());
				transformedOperation.setOriginalOperation(delA);
			} else if ((posB > posA) && ((posB + lenB) >= (posA + lenA))) {
				/*
				 * Operation B starts after operation A and ends after or at the
				 * same position like operation A.
				 * (B):        "ABCD"
				 * (A):      "12345"
				 * (A'):     "12"
				 */
				transformedOperation = new DeleteOperation(posA, delA.getText()
						.substring(0, posB - posA));
				transformedOperation.setUndo(delA.isUndo());
				transformedOperation.setOriginalOperation(delA);
			} else {
				/*
				 * Operation B is fully in operation A.
				 * (B):       "ABCD"
				 * (A):      "123456"
				 * (A'):     "16"
				 */
				transformedOperation = new DeleteOperation(posA, delA.getText()
						.substring(0, posB - posA)
						+ delA.getText().substring(posB + lenB - posA, lenA));
				transformedOperation.setUndo(delA.isUndo());
				transformedOperation.setOriginalOperation(delA);
			}
		}
		return transformedOperation;
	}

	/**
	 * Marks the first operation in {@link GOTOInclusionTransformation#transform(Operation, Operation)}
	 * as privileged. This flag is used when two insert operations are to be transformed against and 
	 * both have the same position and origin index. If this flag is true, then the
	 * first insert operation is privileged and remains unchanged.
	 * 
	 * @param value 	the privilege value for the first operation
	 */
	public void setTransformOpPrivileged(boolean value) {
		isTransformOpPrivileged = value;
	}
}
