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
package ch.iserver.ace.net.bonjour;

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
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class EchoClient extends JFrame implements ActionListener, ServiceListener {

	private JComboBox targetPicker;

	private DefaultComboBoxModel targetModel;
	
	private JTextField textField;
	
	private JTextArea textArea;

	public EchoClient() throws IOException {
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		System.out.println("... browsing for services");
		targetModel = new DefaultComboBoxModel();
		
		JmDNS jmdns = new JmDNS();
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
	
	public void serviceAdded(ServiceEvent event) {
		System.out.println("service added: " + event);
	}
	
	public void serviceRemoved(ServiceEvent event) {
		System.out.println("service removed: " + event);
		this.targetModel.addElement(event.getInfo());
	}
	
	public void serviceResolved(ServiceEvent event) {
		System.out.println("service resolved: " + event);
		ServiceInfo info = event.getInfo();
		System.out.println("info is " + info);
		this.targetModel.addElement(info);
		System.out.println(targetModel.getSize());
	}

	public void actionPerformed(ActionEvent e) {
		ServiceInfo sel = (ServiceInfo) targetPicker.getSelectedItem();
		if (sel != null) {
			try {
				doRequest(sel.getAddress(), sel.getPort());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
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

	public static void main(String[] args) throws IOException {
		EchoClient client = new EchoClient();
	}

}
