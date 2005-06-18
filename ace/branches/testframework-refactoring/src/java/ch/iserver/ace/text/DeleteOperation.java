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
 * TODO: comments
 */
public class DeleteOperation implements Operation {
	
	private static Logger LOG = Logger.getLogger(DeleteOperation.class);
	
	private String text;
	private int position;
	private boolean isUndo;
	private Operation original;
	  
	public DeleteOperation() { 
		setUndo(false);
	}

	public DeleteOperation(int position, String text) {
		setPosition(position);
		setText(text);
		setUndo(false);
	}
	
	public DeleteOperation(int position, String text, boolean isUndo) {
		setPosition(position);
		setText(text);
		setUndo(isUndo);
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		if (position < 0)
    		throw new IllegalArgumentException("position index must be >= 0");
		this.position = position;
	}
	
	public int getTextLength() {
		return text.length();
	}
	
	public String getText() {
		return text;
	}
	
	
	public void setText(String text) {
	    if (text == null)
    			throw new IllegalArgumentException("text may not be null");
		this.text = text;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see ch.iserver.ace.Operation#inverse()
	 */
	public Operation inverse() {
		return new InsertOperation(getPosition(), getText(), true);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see ch.iserver.ace.Operation#isUndo()
	 */
	public boolean isUndo() {
		return isUndo;
	}
	
	public void setUndo(boolean isUndo) {
		this.isUndo = isUndo;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see ch.iserver.ace.Operation#setOriginalOperation(ch.iserver.ace.Operation)
	 */
	public void setOriginalOperation(Operation op) {
		original = op;
		LOG.info("original operation: "+op);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see ch.iserver.ace.Operation#getOriginalOperation()
	 */
	public Operation getOriginalOperation() {
		return original;
	}
	
	public String toString() {
		return "Delete(" + position + ",'" + text + "',"+isUndo+")";
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj.getClass().equals(getClass())) {
			DeleteOperation op = (DeleteOperation) obj;
			return op.position == position && op.text.equals(text) && op.isUndo == isUndo;
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		int hashcode = position;
		hashcode += 13 * (new Boolean(isUndo)).hashCode();
		hashcode += 13 * text.hashCode();
		return hashcode;
	}
}
