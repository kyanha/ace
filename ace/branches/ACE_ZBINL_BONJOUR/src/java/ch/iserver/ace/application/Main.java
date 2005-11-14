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

import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.event.*;



public class Main {
	
	private static final String[] CONTEXT_FILES = new String[] {
		"application-context.xml", "actions-context.xml"
	};
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(CONTEXT_FILES);
		LocaleMessageSource messageSource = new LocaleMessageSourceImpl(context);

		// set look & feel
		/*try {
			UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
		} catch(Exception e) {}*/

		// create frame
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		ApplicationFactory applicationFactory = (ApplicationFactory)context.getBean("appFactory");
		frame.setJMenuBar(applicationFactory.createMenuBar());
		frame.getContentPane().add(BorderLayout.PAGE_START, applicationFactory.createToolBar());
		frame.getContentPane().add(BorderLayout.CENTER, applicationFactory.createComponentPane());
		frame.getContentPane().add(BorderLayout.PAGE_END, applicationFactory.createStatusBar());		
		frame.show();
	}
	
}
