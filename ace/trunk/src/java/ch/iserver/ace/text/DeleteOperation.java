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
public class DeleteOperation implements Operation {
	private String text;
	private int position;
	
	public DeleteOperation() { }

	public DeleteOperation(int position, String text) {
		this.position = position;
		this.text = text;
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public int getTextLength() {
		return text.length();
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text length) {
		this.text = text;
	}
	
	public String toString() {
		return "Delete(" + position + "," + text + ")";
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj.getClass().equals(getClass())) {
			DeleteOperation op = (DeleteOperation) obj;
			return op.position == position && op.text.equals(text);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		int hashcode = position * 13;
		hashcode += 13 text.hashCode();
		return hashcode;
	}
	
}
