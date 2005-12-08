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

package ch.iserver.ace.application;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.application.preferences.PreferenceChangeEvent;
import ch.iserver.ace.application.preferences.PreferenceChangeListener;
import ch.iserver.ace.application.preferences.PreferencesStore;
import ch.iserver.ace.collaboration.CollaborationService;
import ch.iserver.ace.util.CompareUtil;
import ch.iserver.ace.util.ParameterValidator;

/**
 * PreferenceChangeListener responsible to update the user details on the
 * collaboration service whenever user related information changes in
 * the preferences.
 */
public class UserDetailsUpdater implements PreferenceChangeListener {
	
	private final CollaborationService service;

	private String nickname;
	
	public UserDetailsUpdater(CollaborationService service, String nickname) {
		ParameterValidator.notNull("service", service);
		this.service = service;
		this.nickname = nickname;
	}
	
	/**
	 * @see ch.iserver.ace.application.preferences.PreferenceChangeListener#preferenceChanged(ch.iserver.ace.application.preferences.PreferenceChangeEvent)
	 */
	public void preferenceChanged(PreferenceChangeEvent event) {
		if (PreferencesStore.NICKNAME_KEY.equals(event.getKey())) {
			if (!CompareUtil.nullSafeEquals(nickname, event.getValue())) {
				service.setUserDetails(new UserDetails(event.getValue()));
				this.nickname = event.getValue();
			}
		}
	}

}
