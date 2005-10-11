/* $Id$
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
 
package ch.iserver.ace.algorithm;

public class CursorUpdate implements AwarenessInformation {

	private Timestamp timestamp;
	private int siteId;
	private int[] indices = new int[2];
	
	public CursorUpdate(Timestamp timestamp, int dot, int mark) {
		this.timestamp = timestamp;
		this.indices[0] = dot;
		this.indices[1] = mark;
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public int getSiteId() {
		return siteId;
	}
	
	public int getDot() {
		return indices[0];
	}
	
	public int getMark() {
		return indices[1];
	}

	public int[] getIndices() {
		return indices;
	}

}
