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

package ch.iserver.ace.collaboration.jupiter.server;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class CaretHandler implements DocumentListener {
	
	private int dot;
	
	private int mark;
	
	public CaretHandler(int dot, int mark) {
		this.dot = dot;
		this.mark = mark;
	}
	
	public int getDot() {
		return dot;
	}
	
	public void setDot(int dot) {
		this.dot = dot;
	}
	
	protected void setDot(int dot, int length) {
		this.dot = Math.max(Math.min(dot, length), -1);
	}
	
	public int getMark() {
		return mark;
	}
	
	public void setMark(int mark) {
		this.mark = mark;
	}
	
	protected void setMark(int mark, int length) {
		this.mark = Math.max(Math.min(mark, length), -1);
	}
	
	public void changedUpdate(DocumentEvent e) {
		// ignored: attribute changes do not modify positions
	}
	
	public void insertUpdate(DocumentEvent e) {
		int offset = e.getOffset();
		int length = e.getLength();
		int newDot = dot;
		if (newDot >= offset) {
			newDot += length;
		}
		int newMark = mark;
		if (newMark >= offset) {
			newMark += length;
		}		    
		setDot(newDot, e.getDocument().getLength());
		setMark(newMark, e.getDocument().getLength());
	}
	
	public void removeUpdate(DocumentEvent e) {
		int offs0 = e.getOffset();
		int offs1 = e.getOffset() + e.getLength();
		int newDot = dot;
		if (newDot >= offs1) {
			newDot -= (offs1 - offs0);
		} else if (newDot >= offs0) {
			newDot = offs0;
		}
		int newMark = mark;
		if (newMark >= offs1) {
			newMark -= (offs1 - offs0);
		} else if (newMark >= offs0) {
			newMark = offs0;
		}
		setDot(newDot, e.getDocument().getLength());
		setMark(newMark, e.getDocument().getLength());
	}
	
}
