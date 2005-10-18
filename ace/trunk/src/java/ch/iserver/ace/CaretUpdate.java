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
 
package ch.iserver.ace;

/**
 * A CaretUpdate encapsulates the details of an update to the caret. The
 * caret consists of a dot (the position of the cursor) and a mark. If
 * both are at the same position, there is no selection. Otherwise the
 * selection starts at the mark and ends at the dot. 
 */
public class CaretUpdate  {

	/**
	 * The site id of the participant.
	 */
	private int siteId;
	
	/**
	 * Int array keeping dot and mark values.
	 */
	private int[] indices = new int[2];
	
	/**
	 * Creates a new CaretUpdate.
	 * 
	 * @param siteId the site id of the generating site
	 * @param dot the dot value
	 * @param mark the mark value
	 */
	public CaretUpdate(int siteId, int dot, int mark) {
		this.siteId = siteId;
		this.indices[0] = dot;
		this.indices[1] = mark;
	}
	
	/**
	 * @return the siteId of the generating site
	 */
	public int getSiteId() {
		return siteId;
	}
	
	/**
	 * @return the dot value
	 */
	public int getDot() {
		return indices[0];
	}
	
	/**
	 * @return the mark value
	 */
	public int getMark() {
		return indices[1];
	}
	
	/**
	 * @return array consisting of dot and mark (index 0 and 1 respecitvely)
	 */
	public int[] getIndices() {
		return indices;
	}

}
