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

package ch.iserver.ace.test;

/**
 * Scenario exception thrown while verifying the final document state.
 */
public class VerificationException extends ScenarioException {
	/** the site of failed verification */
	private String siteId;
	/** the expected content */
	private String expected;
	/** the effective content */
	private String was;
	
	/**
	 * Creates a new verification exception.
	 * 
	 * @param siteId the site of failure
	 * @param expected the expected content
	 * @param was the actual content
	 */
	public VerificationException(String siteId, String expected, String was) {
		this.siteId = siteId;
		this.expected = expected;
		this.was = was;
	}

	/**
	 * Returns the site id.
	 * 
	 * @return the site id
	 */
	public String getSiteId() {
		return siteId;
	}
	
	/**
	 * 
	 * @return String
	 */
	public String getExpected() {
		return expected;
	}

	/**
	 * 
	 * @return String
	 */
	public String getWas() {
		return was;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return getClass().getName() + "[siteId=" + siteId 
				+ ",expected=" + expected 
				+ ",was=" + was
				+ "]";
	}
	
}
