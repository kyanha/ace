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

package ch.iserver.ace.application.editor;

import ch.iserver.ace.application.*;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;

import java.awt.*;
import javax.swing.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;



public class DummyEditorFactory implements EditorFactory, ApplicationContextAware {

	private ApplicationContext context;
	private LocaleMessageSource messageSource;

	public JPanel createEditor() {
		JPanel innerEditorPane = new JPanel();
		innerEditorPane.setLayout(new BorderLayout());
		// add components
		innerEditorPane.add(new JScrollPane(new DummyEditor()));
		SimpleInternalFrame editorFrame = new SimpleInternalFrame(null, " ", null, innerEditorPane);
		JPanel editorPane = new JPanel();
		editorPane.setLayout(new BorderLayout());
		editorPane.setPreferredSize(new Dimension(140, 200));
		editorPane.add(editorFrame);
		return editorPane;
	}
	
	public void setMessageSource(LocaleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public void setApplicationContext(ApplicationContext context) {
		this.context = context;
	}

}
