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

package ch.iserver.ace.application.preferences;

import java.util.EventObject;

/**
 * Event object used to notify PreferenceChangeListeners about a preference
 * change.
 */
public class PreferenceChangeEvent extends EventObject {
	
	/**
	 * The key that changed.
	 */
	private final String key;
	
	/**
	 * The new value.
	 */
	private final String value;
	
	/**
	 * Creates a new PreferenceChangeEvent with the given key and value.
	 * 
	 * @param source the source of the event
	 * @param key the key that changed
	 * @param value the new value
	 */
	public PreferenceChangeEvent(Object source, String key, String value) {
		super(source);
		this.key = key;
		this.value = value;
	}
	
	/**
	 * Gets the key that changed.
	 * 
	 * @return the key that changed
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Gets the value that changed.
	 * 
	 * @return the value that changed
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getClass().getName() + "[source=" + getSource()
				+ ",key=" + getKey() + ",value=" + getValue() + "]";
	}
}
