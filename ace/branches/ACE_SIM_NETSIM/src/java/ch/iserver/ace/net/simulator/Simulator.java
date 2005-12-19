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

package ch.iserver.ace.net.simulator;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ch.iserver.ace.application.Launcher;

/**
 *
 */
public class Simulator extends JFrame {
	
	private static final String[] CONTEXT_FILES = new String[] {
		"application-context.xml", "actions-context.xml" ,"collaboration-context.xml", "network-simulator-context.xml"
	};

	public Simulator() {
		super("Network Simulator");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel content = new JPanel();
		JButton launcher = new JButton(new LaunchAction());
		content.add(launcher);
		setContentPane(content);
		
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
	}
	
	private static int id = 0;
	
	private class LaunchAction extends AbstractAction {
		private LaunchAction() {
			super("Launch Instance");
		}
		public void actionPerformed(ActionEvent e) {
			Launcher launcher = new Launcher();
			launcher.setContextFiles(CONTEXT_FILES);
			launcher.setPreferencesPath("ch/iserver/ace/simulator/" + (++id));
			try {
				launcher.launch();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		new Simulator();
	}
	
}
