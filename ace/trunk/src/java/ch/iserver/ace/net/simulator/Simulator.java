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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.iserver.ace.application.ApplicationTerminator;
import ch.iserver.ace.application.Launcher;

/**
 *
 */
public class Simulator extends JFrame {
	
	private static final String[] CONTEXT_FILES = new String[] {
		"application-context.xml", "actions-context.xml" ,"collaboration-context.xml", "network-simulator-context.xml"
	};
	
	private JList instanceList;
	private DefaultListModel instanceModel;
	
	public Simulator() {
		super("Network Simulator");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel content = new JPanel(new BorderLayout());
		
		instanceModel = new DefaultListModel();
		instanceList = new JList(instanceModel);
		content.add(new JScrollPane(instanceList), BorderLayout.CENTER);
		
		JButton launcher = new JButton(new LaunchAction());
		content.add(launcher, BorderLayout.SOUTH);

		setContentPane(content);
		setLocationRelativeTo(null);
		setSize(400, 300);
		setVisible(true);
	}
	
	private static int id = 0;
	
	private class LaunchAction extends AbstractAction {
		private LaunchAction() {
			super("Launch Instance");
		}
		public void actionPerformed(ActionEvent e) {
			final String clientId = "" + (++id);
			Launcher launcher = new Launcher();
			launcher.setContextFiles(CONTEXT_FILES);
			launcher.setPreferencesPath("ch/iserver/ace/simulator/" + clientId);
			launcher.setApplicationTerminator(new ApplicationTerminator() {
				public void terminate() {
					instanceModel.removeElement("client " + clientId);
				}
			});
			try {
				launcher.launch();
				instanceModel.addElement("client " + clientId);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		new Simulator();
	}
	
}
