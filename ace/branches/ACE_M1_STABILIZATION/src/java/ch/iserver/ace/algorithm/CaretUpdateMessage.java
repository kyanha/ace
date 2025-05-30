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

package ch.iserver.ace.algorithm;

import ch.iserver.ace.CaretUpdate;

/**
 * A CaretUpdateMessage is a message that carries a CaretUpdate as payload.
 * Site id and timestamp specify the sender and the time at the sender.
 */
public class CaretUpdateMessage {
	
	/**
	 * The site id of the sender.
	 */
	private final int siteId;
	
	/**
	 * The Timestamp at send time.
	 */
	private final Timestamp timestamp;
	
	/**
	 * The CaretUpdate payload information.
	 */
	private final CaretUpdate update;
	
	/**
	 * Creates a new CaretUpdateMessage. 
	 * 
	 * @param siteId the site id of the sender
	 * @param timestamp the timestamp at send time
	 * @param update the CaretUpdate information
	 */
	public CaretUpdateMessage(int siteId, Timestamp timestamp, CaretUpdate update) {
		this.siteId = siteId;
		this.timestamp = timestamp;
		this.update = update;
	}

	/**
	 * @return the site id of the sender
	 */
	public int getSiteId() {
		return siteId;
	}

	/**
	 * @return the timestamp at send time
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the CaretUpdate payload information
	 */
	public CaretUpdate getUpdate() {
		return update;
	}
	
	// --> java.lang.Object methods <--
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof CaretUpdateMessage) {
			CaretUpdateMessage msg = (CaretUpdateMessage) obj;
			return siteId == msg.siteId 
					&& nullSafeEquals(timestamp, msg.timestamp)
					&& nullSafeEquals(update, msg.update);
		} else {
			return false;
		}
	}
	
	private boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		} else if (o1 == null || o2 == null) {
			return false;
		} else {
			return o1.equals(o2);
		}
	}
	
	public int hashCode() {
		int hashCode = 7 * siteId;
		hashCode += timestamp == null ? 0 : 13 * timestamp.hashCode();
		hashCode += update == null ? 0 : 17 * update.hashCode();
		return hashCode;
	}
	
}
