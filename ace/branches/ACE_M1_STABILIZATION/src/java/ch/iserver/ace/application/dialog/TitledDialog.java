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

package ch.iserver.ace.application.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import ch.iserver.ace.application.LocaleMessageSource;
import ch.iserver.ace.util.ParameterValidator;

public abstract class TitledDialog extends JDialog {
	
	private final LocaleMessageSource messages;
	
	public TitledDialog(LocaleMessageSource messages, String titleText, Icon icon) {
		ParameterValidator.notNull("messages", messages);
		this.messages = messages;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		TitlePane title = new TitlePane(titleText, icon);
		getContentPane().add(title, BorderLayout.NORTH);
		
		JComponent content = createContent();
		content.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(8, 8, 8, 8),
				content.getBorder()));
		getContentPane().add(content, BorderLayout.CENTER);
		
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.setBorder(BorderFactory.createCompoundBorder(
				new EtchedLineBorder(),
				BorderFactory.createEmptyBorder(8,8,8,8)));
		JButton cancelButton = new JButton(messages.getMessage("dCancel"));
		buttonPane.add(cancelButton);
		JButton finishButton = new JButton(messages.getMessage("dFinish"));
		buttonPane.add(finishButton);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		getRootPane().setDefaultButton(finishButton);
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		finishButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (onFinish()) {
					setVisible(false);
					dispose();
				}
			}
		});		
	}
	
	protected LocaleMessageSource getMessages() {
		return messages;
	}
	
	public void showDialog() {
		init();
		setSize(500, 300);
		setVisible(true);
	}
	
	protected void init() {
		// nothing to be done here
	}
	
	protected abstract JComponent createContent();
	
	protected boolean onFinish() {
		return true;
	}
	
}
