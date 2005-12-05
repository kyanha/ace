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

import ch.iserver.ace.Operation;

/**
 * The SplitOperation contains two operations. It is used
 * when an operation needs to be split up under certain 
 * transformation conditions.
 * 
 * @see ch.iserver.ace.Operation
 */
public class SplitOperation implements Operation {

	private static final Logger LOG = Logger.getLogger(SplitOperation.class);
	
	/**
	 * The first operation.
	 */
	private Operation op1;
	
	/**
	 * The second operation.
	 */
	private Operation op2;
	
	/**
	 * this operation's original operation, i.e if an operation is transformed,
	 * a new operation is created and the old one passed to it as the original
	 * operation.
	 */
	private Operation original;

	/**
	 * Class constructor.
	 */
	public SplitOperation() {}
	
	/**
	 * Class constructor.
	 * 
	 * @param op1 	the first operation
	 * @param op2	the second operation
	 */
	public SplitOperation(Operation op1, Operation op2) {
		this.op1 = op1;
		this.op2 = op2;
	}
	
	/**
	 * Returns the first operation.
	 * 
	 * @return the first operation
	 */
	public Operation getFirst() {
		return op1;
	}
	
	/**
	 * Sets the first operation.
	 * 
	 * @param op1 the first operation
	 */
	public void setFirst(Operation op1) {
		this.op1 = op1;
	}
	
	/**
	 * Returns the second operation.
	 * 
	 * @return the second operation
	 */
	public Operation getSecond() {
		return op2;
	}
	
	/**
	 * Sets the second operation.
	 * 
	 * @param op2 the second operation
	 */
	public void setSecond(Operation op2) {
		this.op2 = op2;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setOriginalOperation(Operation op) {
		original = op;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Operation getOriginalOperation() {
		return original;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "Split(" + op1 + ", " + op2 + ")";
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj.getClass().equals(getClass())) {
			SplitOperation op = (SplitOperation) obj;
			return op.getFirst().equals(op1) && op.getSecond().equals(op2); 
		} else {
			return false;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		int hashcode = op1.hashCode();
		hashcode += 17 * op2.hashCode();
		return hashcode;
	}
	
}
