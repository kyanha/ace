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

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.iserver.ace.application.action.ToggleFullScreenEditingAction;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.application.editor.DummyEditor;
import ch.iserver.ace.application.preferences.PreferencesStore;
import ch.iserver.ace.collaboration.CollaborationService;
import ch.iserver.ace.util.UUID;



public class Main {
	
	private static final String[] CONTEXT_FILES = new String[] {
		"application-context.xml", "actions-context.xml" ,"collaboration-context.xml", "network-context.xml"
	};
	
	public static void main(String[] args) {		
		ApplicationContext context = new ClassPathXmlApplicationContext(CONTEXT_FILES);

		// get application factory
		ApplicationFactory applicationFactory = (ApplicationFactory)context.getBean("appFactory");

		// create frame
		PersistentFrame frame = (PersistentFrame)context.getBean("persistentMainFrame");
		frame.setMenuBar(applicationFactory.createMenuBar());
		frame.setToolBar(applicationFactory.createToolBar());
		PersistentContentPane pane = (PersistentContentPane)applicationFactory.createPersistentContentPane();
		frame.setContentPane(pane);
		frame.setStatusBar(applicationFactory.createStatusBar());
		frame.setVisible(true);
		
		// TODO: define persistentPane in spring
		//((DummyEditor)context.getBean("dummyEditor")).setPersistentContentPane(pane);
		((ToggleFullScreenEditingAction)context.getBean("toggleFullScreenEditingAction")).setPersistentContentPane(pane);
		
		// get application controller
		ApplicationController controller = (ApplicationController) context.getBean("applicationController");
		
		// customizing for operating system specific stuff
		String classname = System.getProperty("ch.iserver.ace.customizer");
		if (classname != null) {
			customize(classname, controller);
		}
		
		// get the preferences store
		PreferencesStore preferencesStore = (PreferencesStore) context.getBean("preferencesStore");
		
		// get collaboration service
		CollaborationService collaborationService = (CollaborationService) context.getBean("collaborationService");
		String id = preferencesStore.get(PreferencesStore.USER_ID, UUID.nextUUID());
		preferencesStore.put(PreferencesStore.USER_ID, id);
		collaborationService.setUserId(id);
		UserDetails details = getUserDetails(preferencesStore);
		collaborationService.setUserDetails(details);

		// preference listeners
		preferencesStore.addPreferenceChangeListener(
						new UserDetailsUpdater(collaborationService, details.getUsername()));
		
		// register listeners & start
		collaborationService.addUserListener((UserViewController)context.getBean("userViewController"));
		collaborationService.addDocumentListener((BrowseViewController)context.getBean("browseViewController"));
		collaborationService.start();
	}
	
	private static void customize(String classname, ApplicationController controller) {
		try {
			Class clazz = Class.forName(classname);
			Customizer customizer = (Customizer) clazz.newInstance();
			customizer.init(controller);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private static UserDetails getUserDetails(PreferencesStore preferences) {
		return new UserDetails(preferences.get(PreferencesStore.NICKNAME_KEY, 
				System.getProperty("user.name")));
	}
	
}
