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

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.application.action.ToggleFullScreenEditingAction;
import ch.iserver.ace.application.preferences.PreferencesStore;
import ch.iserver.ace.collaboration.CollaborationService;
import ch.iserver.ace.collaboration.InvitationCallback;
import ch.iserver.ace.util.UUID;



public class Main {
	
	private static final String[] CONTEXT_FILES = new String[] {
		"application-context.xml", "actions-context.xml" ,"collaboration-context.xml", "network-context.xml"
	};
	
	public static void main(String[] args) {

		// CREATE CUSTOMIZER
		// customizing for operating system specific stuff
		String classname = System.getProperty("ch.iserver.ace.customizer");
		Customizer customizer = null;
		if (classname != null) {
			customizer = loadCustomizer(classname);
		}

		String propertiesPath = System.getProperty("ch.iserver.ace.preferences");
		if (propertiesPath == null) {
			System.setProperty("ch.iserver.ace.preferences", "ch/iserver/ace");
		}

		// LOAD BEANS
		final ApplicationContext context = new ClassPathXmlApplicationContext(CONTEXT_FILES);

		// 1. application factory
		ApplicationFactory applicationFactory = (ApplicationFactory) context.getBean("appFactory");
		// 2. application controller
		ApplicationController controller = (ApplicationController) context.getBean("applicationController");		
		customize(customizer, controller);
		// 3. collaboration service
		CollaborationService collaborationService = (CollaborationService) context.getBean("collaborationService");
		// 4. preferences store
		PreferencesStore preferencesStore = (PreferencesStore) context.getBean("preferencesStore");



		// CREATE FRAME
		PersistentFrame frame = (PersistentFrame)context.getBean("persistentMainFrame");
		PersistentContentPane pane = (PersistentContentPane)context.getBean("persistentContentPane");
		frame.setMenuBar(applicationFactory.createMenuBar());
		frame.setToolBar(applicationFactory.createToolBar());
		frame.setContentPane(pane);
		frame.setStatusBar(applicationFactory.createStatusBar());
		frame.setTitle("ACE - a collaborative editor");
		frame.setVisible(true);
		

		
		// INIT
		// 1. preferences store
		String id = preferencesStore.get(PreferencesStore.USER_ID, UUID.nextUUID());
		preferencesStore.put(PreferencesStore.USER_ID, id);

		// 2. collaboration service
		collaborationService.setUserId(id);
		UserDetails details = getUserDetails(preferencesStore);
		collaborationService.setUserDetails(details);
		collaborationService.setInvitationCallback((InvitationCallback)context.getBean("invitationCallback"));

		// 3. preference listeners
		preferencesStore.addPreferenceChangeListener(
						new UserDetailsUpdater(collaborationService, details.getUsername()));
		
		// 4. register listeners & start
		collaborationService.addUserListener((UserViewController)context.getBean("userViewController"));
		collaborationService.addDocumentListener((BrowseViewController)context.getBean("browseViewController"));
		collaborationService.start();
		
		// 5. create empty document
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					((DocumentManager) context.getBean("documentManager")).newDocument();
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
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
