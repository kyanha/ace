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

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;


public class JDKPreferencesStore extends AbstractPreferencesStore 
		implements PreferenceChangeListener {
	
	private Preferences preferences;
	
	public JDKPreferencesStore(String path) {
		this.preferences = Preferences.userRoot().node(path);
		this.preferences.addPreferenceChangeListener(this);
	}
	
	public String get(String key, String def) {
		return preferences.get(key, def);
	}
	
	public void put(String key, String value) {
		preferences.put(key, value);
	}
	
	public void preferenceChange(PreferenceChangeEvent evt) {
		firePreferenceChanged(evt.getKey(), evt.getNewValue());
	}

}
