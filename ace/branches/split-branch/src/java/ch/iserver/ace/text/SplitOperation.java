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

import ch.iserver.ace.Operation;

/**
 *
 */
public class SplitOperation implements Operation {

	private Operation op1;
	private Operation op2;
	
	public SplitOperation(Operation op1, Operation op2) {
		this.op1 = op1;
		this.op2 = op2;
	}
	
	public Operation getFirst() {
		return op1;
	}
	
	public void setFirst(Operation op1) {
		this.op1 = op1;
	}
	
	public Operation getSecond() {
		return op2;
	}
	
	public void setSecond(Operation op2) {
		this.op2 = op2;
	}

	public String toString() {
		return "Split(" + op1 + ", " + op2 + ")";
	}
	
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
	
	public int hashCode() {
		int hashcode = op1.hashCode();
		hashcode += 17 * op2.hashCode();
		return hashcode;
	}
	
}
