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

import ch.iserver.ace.application.ApplicationFactory;
import ch.iserver.ace.application.LocaleMessageSource;
import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;



public class PersistentFrame extends JFrame {

	private LocaleMessageSource messageSource;

	public PersistentFrame(final Action exitAction) {
		setSize(920, 560);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//
				exitAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "ApplicationExit"));
			}
		});
	}
	
	public void setMenuBar(JMenuBar menuBar) {
		setJMenuBar(menuBar);
	}
	
	public void setToolBar(JToolBar toolBar) {
		getContentPane().add(toolBar, BorderLayout.PAGE_START);
	}
	
	public void setContentPane(JPanel contentPane) {
		getContentPane().add(contentPane, BorderLayout.CENTER);
	}
	
	public void setStatusBar(JPanel statusBar) {
		getContentPane().add(statusBar, BorderLayout.PAGE_END);
	}

	public void setMessageSource(LocaleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
}