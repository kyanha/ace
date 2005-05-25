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
public class InsertOperation implements Operation {
	private String text;
	private int position;
	
	public InsertOperation() { }

	public InsertOperation(int position, String text) {
		setPosition(position);
		setText(text);
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
	    assert position >= 0 : "position index must be >= 0";
		this.position = position;
	}

	public String getText() {
		return text;
	}
	
	public int getTextLength() {
		return text.length();
	}

	public void setText(String text) {
	    assert text != null : "text may not be null";
		this.text = text;
	}
	
	public String toString() {
		return "Insert(" + position + "," + text + ")";
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj.getClass().equals(getClass())) {
			InsertOperation op = (InsertOperation) obj;
			return op.position == position && op.text.equals(text);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		int hashcode = position;
		hashcode += 13 * text.hashCode();
		return hashcode;
	}
	
}
