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

import java.util.ArrayList;
import java.util.List;

import ch.iserver.ace.Operation;

/**
 * TODO: comments
 */
public class InsertOperation implements Operation {
	private String text;
	private int position;
	private int origin;
	private boolean isUndo;
	private List transformationHistory;
	
	public InsertOperation() { }

	public InsertOperation(int position, String text) {
		setPosition(position);
		setText(text);
		origin = getPosition();
		setUndo(false);
		transformationHistory = new ArrayList();
	}
	
	public InsertOperation(int position, String text, boolean isUndo) {
		this(position, text);
		origin = getPosition();
		setUndo(isUndo);
	}
	
	public InsertOperation(int position, String text, int origin) {
		this(position, text);
		this.origin = origin;
		setUndo(false);
	}
	
	public InsertOperation(int position, String text, int origin, boolean isUndo) {
		this(position, text, origin);
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

	public String getText() {
		return text;
	}
	
	public int getTextLength() {
		return text.length();
	}
	
	public int getOrigin() {
		return origin;
	}
	
	public void setOrigin(int origin) {
	    if (origin < 0)
    			throw new IllegalArgumentException("origin index must be >= 0");
		this.origin = origin;
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
		//TODO: origin position gets lost.
		return new DeleteOperation(getPosition(), getText(), true);
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
	
	/* (non-Javadoc)
	 * @see ch.iserver.ace.Operation#addToHistory(ch.iserver.ace.Operation)
	 */
	public void addToHistory(Operation op) {
//		//append the history of op to this operations history.
//		List history = op.getTransformationHistory();
//		if (!history.isEmpty()) {
//			transformationHistory.addAll(new ArrayList(history));
//		}
		transformationHistory.add(op);
	}
	/* (non-Javadoc)
	 * @see ch.iserver.ace.Operation#getOriginOperation()
	 */
	public List getTransformationHistory() {
//		return (!transformationHistory.isEmpty()) ? (Operation)transformationHistory.get(0) : null;
		return transformationHistory;
	}
	
	public String toString() {
		return "Insert(" + position + ",'" + text + "',"+origin+","+isUndo+")";
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj.getClass().equals(getClass())) {
			InsertOperation op = (InsertOperation) obj;
			return op.position == position && op.text.equals(text) && op.origin == origin && op.isUndo == isUndo;
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		int hashcode = position;
		hashcode += 13 * origin;
		hashcode += 13 * (new Boolean(isUndo)).hashCode();
		hashcode += 13 * text.hashCode();
		return hashcode;
	}
	
}
