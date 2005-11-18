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

import javax.swing.event.EventListenerList;

public abstract class AbstractPreferencesStore implements PreferencesStore {
	
	private final EventListenerList listeners = new EventListenerList();
	
	public void addPreferenceChangeListener(PreferenceChangeListener listener) {
		listeners.add(PreferenceChangeListener.class, listener);
	}

	public void removePreferenceChangeListener(PreferenceChangeListener listener) {
		listeners.remove(PreferenceChangeListener.class, listener);
	}
	
	protected void firePreferenceChanged(String key, String value) {
		PreferenceChangeListener[] listeners = (PreferenceChangeListener[]) 
				this.listeners.getListeners(PreferenceChangeListener.class);
		PreferenceChangeEvent evt = null;
		for (int i = 0; i < listeners.length; i++) {
			PreferenceChangeListener listener = listeners[i];
			if (evt == null) {
				evt = new PreferenceChangeEvent(this, key, value);
			}
			listener.preferenceChanged(evt);
		}
	}

}
