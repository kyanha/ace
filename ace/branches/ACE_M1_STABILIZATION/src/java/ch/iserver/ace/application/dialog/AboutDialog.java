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

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

import ch.iserver.ace.application.LocaleMessageSource;

/**
 *
 */
public class AboutDialog extends TitledDialog {
	
	private JEditorPane editor;
	
	public AboutDialog(Frame owner, LocaleMessageSource messages) {
		super(owner, messages, 
				messages.getMessage("dAboutTitle"),
				messages.getIcon("iAboutTitle"));
	}
	
	protected URL getURL() {
		return getMessages().getResource("uAboutHtml");
	}
	
	/**
	 * @see ch.iserver.ace.application.dialog.TitledDialog#createContent()
	 */
	protected JComponent createContent() {
		editor = new JEditorPane();
		editor.setEditable(false);
		return editor;
	}
	
	/**
	 * @see ch.iserver.ace.application.dialog.TitledDialog#createButtonPane()
	 */
	protected JPanel createButtonPane() {
		JPanel pane = new JPanel(new FlowLayout());
		JButton close = new JButton(getMessages().getMessage("dClose"));
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hideDialog();
			}
		});
		pane.add(close);
		return pane;
	}
	
	protected void init() {
		setSize(400, 400);
		try {
			editor.setPage(getURL());
		} catch (Exception e) {
			e.printStackTrace();
			editor.setText("failed to load about HTML file");
		}
	}

}
