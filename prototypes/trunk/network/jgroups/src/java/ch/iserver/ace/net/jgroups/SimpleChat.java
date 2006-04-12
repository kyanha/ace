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

package ch.iserver.ace.net.jgroups;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.MembershipListener;
import org.jgroups.Message;
import org.jgroups.MessageListener;
import org.jgroups.View;
import org.jgroups.blocks.PullPushAdapter;

/**
 * A SimpleChat application based on JGroups. It allows the user to
 * join a JGroup group by name and send messages to the joined group.
 */
public class SimpleChat extends JFrame {
	private JButton joinButton;
	private JButton leaveButton;
	
	private JTextArea messageArea;
	
	private JTextField messageField;
	private JButton sendButton;
	
	private Channel channel;
	private PullPushAdapter adapter;
	
	private String group;
	private ChatConfig config;
	
	/**
	 * Create new SimpleChat client. 
	 * 
	 * @param config the chat config
	 */
	public SimpleChat(ChatConfig config) {
		super();		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.config = config;
		setTitle(createTitle(config));

		// content pane
		JPanel pane = new JPanel(new BorderLayout());

		JPanel buttonPane = new JPanel(new FlowLayout());
		joinButton = new JButton("join");
		joinButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String name = JOptionPane.showInputDialog("Specify group name");
					if (name != null) {
						connect(name);
					}
				} catch (ChannelException ex) {
					handleException(ex);
				}
			}
		});
		leaveButton = new JButton("leave");
		leaveButton.setEnabled(false);
		leaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disconnect();
			}
		});
		buttonPane.add(joinButton);
		buttonPane.add(leaveButton);
		
		JPanel messagePane = new JPanel(new BorderLayout());
		messageArea = new JTextArea();
		messageArea.setEditable(false);
		messagePane.add(messageArea, BorderLayout.CENTER);
		
		JPanel inputPane = new JPanel(new BorderLayout());
		ActionListener sendListener = new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
				try {
					send(messageField.getText());
				} catch (ChannelException ex) {
					handleException(ex);
				} finally {
					messageField.setText("");
				}
			}
		};
		messageField = new JTextField();
		messageField.setEnabled(false);
		messageField.addActionListener(sendListener);
		sendButton = new JButton("send");
		sendButton.setEnabled(false);
		sendButton.addActionListener(sendListener);
		inputPane.add(messageField, BorderLayout.CENTER);
		inputPane.add(sendButton, BorderLayout.EAST);
		
		pane.add(buttonPane, BorderLayout.NORTH);
		pane.add(messagePane, BorderLayout.CENTER);
		pane.add(inputPane, BorderLayout.SOUTH);
		setContentPane(pane);
		
		setSize(500, 400);
		setVisible(true);
	}
	
	private String createTitle(ChatConfig config) {
		StringBuffer buf = new StringBuffer("SimpleChat - JGroups (");
		buf.append(config.getUserName());
		if (group != null) {
			buf.append(" @ ");
			buf.append(group);
		}
		buf.append(")");
		return buf.toString();
	}
	
	private void handleException(Exception e) {
		e.printStackTrace();
	}

	/**
	 * Enable/disable GUI.
	 * 
	 * @param enable whether to enable or disable GUI
	 */
	public void doEnable(boolean enable) {
		joinButton.setEnabled(!enable);
		leaveButton.setEnabled(enable);
		sendButton.setEnabled(enable);
		messageField.setEnabled(enable);
	}
	
	/**
	 * Connect to group specified by its <var>name</var>.
	 * 
	 * @param name the name of the group to connect to
	 * @throws ChannelException
	 */
	public void connect(String name) throws ChannelException {
		try {
			// remember the current group name
			this.group = name;
			
			// create the channel
			channel = new JChannel();
			// connect to channel
			channel.connect(name);
			// create PullPushAdapter to receive messages with MessageListener
			adapter = new PullPushAdapter(channel, new MyMessageListener(), new MyMembershipListener());
			
			// adapt the window title to the new group name
			setTitle(createTitle(config));
		} finally {
			doEnable(true);
		}
	}
	
	/**
	 * Sends the given <var>message</var> to the current group. All current
	 * members of the group will receive this message.
	 * 
	 * @param message the message text to send
	 * @throws ChannelException
	 */
	public void send(String message) throws ChannelException {
		if (channel != null) {
			// create the message object based on user name and given message
			String username = config.getUserName();
			Message msg = new Message(null, null, username + "> " + message);
			channel.send(msg);
		}
	}
	
	/**
	 * Close the current channel, leaving the current group. This
	 * method brings the application back to its initial state.
	 */
	public void disconnect() {
		try {
			// stop the PullPushAdapter
			adapter.stop();
			// disconnect from the channel
			channel.disconnect();
			// close the channel
			channel.close();
		} finally {
			// reset state variables
			group = null;
			adapter = null;
			channel = null;
			// disable buttons, textfields, ...
			doEnable(false);
		}
	}
	
	/**
	 * Simple MessageListener that prints appends the received message
	 * text to the text area.
	 */
	private class MyMessageListener implements MessageListener {
		public byte[] getState() {
			System.out.println("*** get state");
			return null;
		}
	
		public void receive(Message message) {
			Object obj = message.getObject();
			messageArea.append(obj.toString() + "\n");
		}
	
		public void setState(byte[] state) {
			System.out.println("*** set state");
		}
	}
	
	/**
	 * A MembershipListener that outputs any events to System.out.
	 */
	private class MyMembershipListener implements MembershipListener {
		public void block() {
			
		}
		public void suspect(Address addr) {
			System.out.println("... suspect " + addr);
		}
		public void viewAccepted(View view) {
			System.out.println("... view accepted " + view);
		}
	}
	
	public static void main(String[] args) {
		SimpleChat chat = new SimpleChat(new SimpleChatConfig());
	}
	
}
