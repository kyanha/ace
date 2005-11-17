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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import ch.iserver.ace.application.LocaleMessageSource;
import ch.iserver.ace.util.ParameterValidator;

public abstract class TitledDialog extends JDialog {
	
	public static int OK_OPTION = 1;
	
	public static int CANCEL_OPTION = 2;
	
	private LocaleMessageSource messages;
	
	private TitlePane titlePane;
	
	private int option;
	
	public TitledDialog(Frame owner, LocaleMessageSource messages, String titleText, Icon icon) {
		super(owner, titleText);
		ParameterValidator.notNull("messages", messages);
		this.messages = messages;
		
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		titlePane = new TitlePane(titleText, icon);
		getContentPane().add(titlePane, BorderLayout.NORTH);
		
		JComponent content = createContent();
		content.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(8, 8, 8, 8),
				content.getBorder()));
		getContentPane().add(content, BorderLayout.CENTER);
		
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.add(createButtonPane());
		buttonPane.setBorder(BorderFactory.createCompoundBorder(
				new EtchedLineBorder(),
				BorderFactory.createEmptyBorder(8,8,8,8)));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		// pack it together
		pack();
		setResizable(false);
	}
	
	public void setMessage(String message) {
		titlePane.setMessage(message);
	}
	
	protected void finish() {
		if (onFinish()) {
			hideDialog();
			option = OK_OPTION;
		}
	}
	
	protected void cancel() {
		option = CANCEL_OPTION;
		hideDialog();
	}
	
	protected LocaleMessageSource getMessages() {
		return messages;
	}
	
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	public int getOption() {
		return option;
	}
	
	public void showDialog() {
		init();
		setVisible(true);
	}
	
	public void hideDialog() {
		setVisible(false);
	}
	
	protected void init() {
		// nothing to be done here
	}
	
	protected abstract JComponent createContent();
	
	protected abstract JPanel createButtonPane();
	
	protected boolean onFinish() {
		return true;
	}
	
}
