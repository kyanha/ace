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

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.QueryListener;
import com.apple.dnssd.ResolveListener;
import com.apple.dnssd.TXTRecord;

/**
 * 
 */
public class EchoClient extends JFrame implements ActionListener,
		ResolveListener {
	private DNSSDService browser;

	private DNSSDService resolver;

	private JComboBox targetPicker;

	private TargetListModel targetModel;
	
	private JTextField textField;
	
	private JTextArea textArea;

	public EchoClient() throws DNSSDException {
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		System.out.println("... browsing for services");
		targetModel = new TargetListModel();
		browser = DNSSD.browse(0, 0, EchoConstants.REGISTRY_TYPE, "",
				new SwingBrowseListener(targetModel));

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

	public void actionPerformed(ActionEvent e) {
		TargetListElement sel = (TargetListElement) targetPicker.getSelectedItem();
		if (sel != null) {
			try {
				resolver = DNSSD.resolve(0, sel.getIfidx(), sel.getServiceName(), 
						sel.getType(), sel.getDomain(), this);
			} catch (DNSSDException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void operationFailed(DNSSDService service, int errorCode) {
		System.err.println("Service reported error: " + errorCode);
	}

	public void serviceResolved(DNSSDService resolver, int flags, int ifidx,
			String fullName, String hostName, int port, TXTRecord txtrecord) {
		System.out.println("... resolved service: " + fullName + " @ "
				+ hostName + ":" + port);
		try {
			DNSSD.queryRecord(flags, ifidx, hostName, 1, 1,
					new SimpleQueryListener(port));
		} catch (DNSSDException e) {
			e.printStackTrace();
		} finally {
			resolver.stop();
		}
	}

	private class SimpleQueryListener implements QueryListener {
		private int port;

		public SimpleQueryListener(int port) {
			this.port = port;
		}

		public void operationFailed(DNSSDService service, int errorCode) {
			System.out.println("... operation failed: " + errorCode);
		}

		public void queryAnswered(DNSSDService query, int flags, int ifidx,
				String fullName, int rrtype, int rrclass, byte[] rdata, int ttl) {
			System.out.println("... query answered: " + fullName);
			try {
				InetAddress addr = InetAddress.getByAddress(rdata);
				doRequest(addr, port);
			} catch (IOException e) {
				e.printStackTrace();
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

	public static void main(String[] args) throws DNSSDException {
		EchoClient client = new EchoClient();
	}

}
