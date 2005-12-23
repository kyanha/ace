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
import ch.iserver.ace.application.preferences.PreferencesStore;
import ch.iserver.ace.collaboration.CollaborationService;
import ch.iserver.ace.collaboration.InvitationHandler;
import ch.iserver.ace.collaboration.ServiceFailureHandler;
import ch.iserver.ace.util.UUID;

/**
 *
 */
public class Launcher {
	
	private Customizer customizer;
	
	private String preferencesPath;
	
	private String[] contextFiles;
	
	private ApplicationTerminator terminator;
	
	public Launcher() { }
	
	public void setContextFiles(String[] contextFiles) {
		this.contextFiles = contextFiles;
	}
	
	public void setCustomizer(Customizer customizer) {
		this.customizer = customizer;
	}
	
	public void setPreferencesPath(String preferencesPath) {
		this.preferencesPath = preferencesPath;
	}
	
	public void setApplicationTerminator(ApplicationTerminator terminator) {
		this.terminator = terminator;
	}
	
	public void launch() throws Exception {
		System.setProperty("ch.iserver.ace.preferences", preferencesPath);
		
		final ApplicationContext context = new ClassPathXmlApplicationContext(contextFiles);
		
		// 1. application factory
		ApplicationFactory applicationFactory = (ApplicationFactory) context.getBean("appFactory");
		// 2. application controller
		ApplicationController controller = (ApplicationController) context.getBean("applicationController");
		controller.setApplicationTerminator(terminator);
		if (customizer != null) {
			customizer.init(controller);
		}
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
		collaborationService.setInvitationHandler((InvitationHandler)context.getBean("invitationHandler"));
		collaborationService.setFailureHandler((ServiceFailureHandler)context.getBean("serviceFailureHandler"));

		// 3. preference listeners
		preferencesStore.addPreferenceChangeListener(
						new UserDetailsUpdater(collaborationService, details.getUsername()));
		
		// 4. register listeners & start
		collaborationService.addUserListener((UserViewController)context.getBean("userViewController"));
		collaborationService.addDocumentListener((BrowseViewController)context.getBean("browseViewController"));
		collaborationService.start();
		
		// 5. create empty document
		try {
			if (SwingUtilities.isEventDispatchThread()) {
				((DocumentManager) context.getBean("documentManager")).newDocument();
			} else {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						((DocumentManager) context.getBean("documentManager")).newDocument();
					}
				});
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private UserDetails getUserDetails(PreferencesStore preferences) {
		return new UserDetails(preferences.get(PreferencesStore.NICKNAME_KEY, 
				System.getProperty("user.name")));
	}
	
}
