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

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 */
public class Main {
	
	private static final String[] CONTEXT_FILES = new String[] {
		"application-context.xml", "actions-context.xml"
	};
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(CONTEXT_FILES);
		LocaleMessageSource messageSource = new LocaleMessageSourceImpl(context);

		// set look & feel
		try {
			UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
		} catch(Exception e) {}

		/*// create document controller & view
		DocumentViewController dvc = new DocumentViewController();
		DocumentView dv = new DocumentView(dvc, messageSource);
		dvc.setView(dv);

		// create browse controller & view
		BrowseViewController bvc = new BrowseViewController();
		BrowseView bv = new BrowseView(bvc, messageSource);
		bvc.setView(bv);

		// create participant controller & view
		ParticipantViewController pvc = new ParticipantViewController();
		ParticipantView pv = new ParticipantView(pvc, messageSource);
		pvc.setView(pv);

		// create user controller & view
		UserViewController uvc = new UserViewController();
		UserView uv = new UserView(uvc, messageSource);
		uvc.setView(uv);

		// create editor
		//JPanel editor = EditorFactory.createEditor();
		//JPanel editor = EditorFactory.createBasicEditor(messageSource);
		*/
		// create frame
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);	
		//frame.setJMenuBar(ApplicationFactory.createMenuBar(messageSource));
		//frame.getContentPane().add(BorderLayout.PAGE_START, ApplicationFactory.createToolBar(messageSource));
		//frame.getContentPane().add(BorderLayout.CENTER, ApplicationFactory.createComponentPane(dv, bv, editor, pv, uv));
		//frame.getContentPane().add(BorderLayout.PAGE_END, ApplicationFactory.createStatusBar());
		frame.show();
	}
	
}
