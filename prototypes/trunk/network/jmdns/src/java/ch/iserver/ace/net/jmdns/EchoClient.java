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
package ch.iserver.ace.net.jmdns;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class EchoClient extends JFrame implements ActionListener, ServiceListener {
	private JComboBox targetPicker;

	private DefaultComboBoxModel targetModel;
	
	private JTextField textField;
	
	private JTextArea textArea;

	private JmDNS jmdns;

	public EchoClient() throws IOException {
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		System.out.println("... browsing for services");
		targetModel = new DefaultComboBoxModel();
		
		jmdns = new JmDNS();
		jmdns.addServiceListener(EchoConstants.REGISTRY_TYPE, this);

		JPanel pane = new JPanel(new BorderLayout());
		
		targetPicker = new JComboBox(targetModel);
		pane.add(targetPicker, BorderLayout.NORTH);

		textArea = new JTextArea();
		textArea.setEditable(false);
		pane.add(textArea, BorderLayout.CENTER);
		
		JPanel sendPane = new JPanel(new BorderLayout());
		textField = new JTextField();
		textField.addActionListener(this);
		sendPane.add(textField, BorderLayout.CENTER);
		pane.add(sendPane, BorderLayout.SOUTH);
		
		setContentPane(pane);
		setSize(600, 300);
		setVisible(true);
	}
	
	public void serviceAdded(final ServiceEvent event) {
		System.out.println("... added " + event.getType() + " / " + event.getName());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				targetModel.addElement(new ServiceEntry(event.getType(), event.getName()));
			}
		});
		
		// to resolve service immediately, do one of the following things
		// - call jmdns.requestServiceInfo(type, name) from another thread (i.e. async)
		// - call jmdns.requestServiceInfo(type, name, -1) from the current thread
	}
	
	public void serviceRemoved(final ServiceEvent event) {
		System.out.println("... removed " + event.getType() + " / " + event.getName());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				targetModel.removeElement(new ServiceEntry(event.getType(), event.getName()));
			}
		});
	}
	
	public void serviceResolved(ServiceEvent event) {
		System.out.println("... resolved " + event.getType() + " / " + event.getName());
		try {
			doRequest(event.getInfo().getInetAddress(), event.getInfo().getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		ServiceEntry entry = (ServiceEntry) targetPicker.getSelectedItem();
		if (entry != null) {
			jmdns.requestServiceInfo(entry.getType(), entry.getName());
		}
	}

	public void doRequest(InetAddress addr, int port) throws IOException {
		System.out.println("... requesting echo from " + addr + ":" + port);
		Socket echoSocket = new Socket(addr, port);
		PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket
				.getInputStream()));

		try {
			out.println(textField.getText());
			textArea.append(addr + ":" + port + "> "
					+ in.readLine());
			textArea.append("\n");
		} finally {
			out.close();
			in.close();
			echoSocket.close();
		}
	}
	
	protected static class ServiceEntry {
		private final String type;
		private final String name;
		
		protected ServiceEntry(String type, String name) {
			this.type = type;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public String getType() {
			return type;
		}
		
		public String toString() {
			return name + " / " + type;
		}
		
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (obj == null) {
				return false;
			} else if (getClass().equals(obj.getClass())) {
				ServiceEntry entry = (ServiceEntry) obj;
				return name.equals(entry.getName())
						&& type.equals(entry.getType());
			} else {
				return false;
			}
		}
		
		public int hashCode() {
			return toString().hashCode();
		}
	}

	public static void main(String[] args) throws Exception {
		EchoClient client = new EchoClient();
	}

}
