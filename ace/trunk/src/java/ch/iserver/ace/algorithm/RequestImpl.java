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

import ch.iserver.ace.Operation;
import ch.iserver.ace.util.CompareUtil;

/**
 *
 */
public class RequestImpl implements Request {
	
	private final int siteId;
	
	private final Timestamp timestamp;
	
	private final Operation operation;
	
	public RequestImpl(int siteId, Timestamp timestamp, Operation operation) {
		this.siteId = siteId;
		this.timestamp = timestamp;
		this.operation = operation;
	}
	
	/**
	 * @see ch.iserver.ace.algorithm.Request#getSiteId()
	 */
	public int getSiteId() {
		return siteId;
	}

	/**
	 * @see ch.iserver.ace.algorithm.Request#getOperation()
	 */
	public Operation getOperation() {
		return operation;
	}

	/**
	 * @see ch.iserver.ace.algorithm.Request#getTimestamp()
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Request) {
			Request request = (Request) obj;
			return siteId == request.getSiteId()
			       && CompareUtil.nullSafeEquals(timestamp, request.getTimestamp())
			       && CompareUtil.nullSafeEquals(operation, request.getOperation());
		} else {
			return false;
		}
	}
		
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int hashCode = 13 * siteId;
		hashCode += (timestamp != null) ? 17 * timestamp.hashCode() : 0;
		hashCode += (operation != null) ? 29 * operation.hashCode() : 0;
		return hashCode;
	}

}
