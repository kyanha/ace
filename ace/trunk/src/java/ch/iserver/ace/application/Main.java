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
import ch.iserver.ace.application.preferences.PreferencesStore;



public class Main {
	
	private static final String[] CONTEXT_FILES = new String[] {
		"application-context.xml", "actions-context.xml" ,"collaboration-context.xml", "network-context.xml"
	};
	
	public static void main(String[] args) throws Exception {
		Launcher launcher = new Launcher();
		launcher.setContextFiles(CONTEXT_FILES);
		
		String preferencesPath = System.getProperty("ch.iserver.ace.preferences");
		if (preferencesPath == null) {
			launcher.setPreferencesPath("ch/iserver/ace");
		} else {
			launcher.setPreferencesPath(preferencesPath);
		}
		
		String classname = System.getProperty("ch.iserver.ace.customizer");
		Customizer customizer = null;
		if (classname != null) {
			customizer = loadCustomizer(classname);
			launcher.setCustomizer(customizer);
		}

		launcher.launch();
	}

	private static Customizer loadCustomizer(String classname) {
		try {
			Class clazz = Class.forName(classname);
			return (Customizer) clazz.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void customize(Customizer customizer, ApplicationController controller) {
		if (customizer != null) {
			customizer.init(controller);
		}
	}

	private static UserDetails getUserDetails(PreferencesStore preferences) {
		return new UserDetails(preferences.get(PreferencesStore.NICKNAME_KEY, 
				System.getProperty("user.name")));
	}
	
}
