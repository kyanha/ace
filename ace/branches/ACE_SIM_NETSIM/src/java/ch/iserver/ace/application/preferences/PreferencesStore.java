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

/**
 * Interface implemented by preferences store providers. A PreferencesStore
 * is an object that stores and retrieves user related preferences. This
 * interface is used to abstract the actual implementation away from
 * the rest of the application.
 */
public interface PreferencesStore {

	/**
	 * Key used to store the user id.
	 */
	String USER_ID = "user.id";

	/**
	 * Key used to store the user's nickname.
	 */
	String NICKNAME_KEY = "user.nickname";
	
	/**
	 * Key used to store the font size.
	 */
	String FONTSIZE_KEY = "font.size";
	
	/**
	 * Key used to save the default character encoding. 
	 */
	String CHARSET_KEY = "text.charset";
	
	/**
	 * Registers a PreferenceChangeListener with the preferences store.
	 * 
	 * @param listener the listener to be registered
	 */
	void addPreferenceChangeListener(PreferenceChangeListener listener);

	/**
	 * Removes a PreferenceChangeListener from the list of registered
	 * listeners.
	 * 
	 * @param listener the listener to be removed
	 */
	void removePreferenceChangeListener(PreferenceChangeListener listener);

	/**
	 * Sets the value for the given key.
	 * 
	 * @param key the key of the preference
	 * @param value the new value of the preference
	 */
	void put(String key, String value);
	
	/**
	 * Gets the preference value for the given key. If the value is not
	 * defined then the given default value is used.
	 * 
	 * @param key the key of the preference
	 * @param defaultValue the default value
	 * @return the value of the given preference or the default value if
	 *         the preference is not defined
	 */
	String get(String key, String defaultValue);
	
}
