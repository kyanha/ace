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
import java.awt.*;
import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import java.util.List;
import com.jgoodies.uif_lite.panel.SimpleInternalFrame;



public class DummyEditor extends JPanel implements Editor {

	JTextPane textPane;
	SimpleInternalFrame editorFrame;
	LocaleMessageSource messageSource;

	public DummyEditor(LocaleMessageSource messageSource, List toolBarActions) {
		this.messageSource = messageSource;
		// create toolbar
		JToolBar editorToolBar = new JToolBar();
		for(int i = 0; i < toolBarActions.size(); i++) {
			editorToolBar.add(((AbstractAction)toolBarActions.get(i)));
		}
		// create editor
		//JPanel innerEditorPane = new JPanel();
		textPane = new JTextPane();
		setFontSize(12);
		
		// add components
		editorFrame = new SimpleInternalFrame(null, " ", editorToolBar, new JScrollPane(textPane));
		setLayout(new BorderLayout());
		add(editorFrame);
	}
	
	public void setTitle(String title) {
		editorFrame.setTitle(title);
	}
	
	public void setDocument(StyledDocument document) {
		textPane.setDocument(document);
	}
	
	public void setEnabled(boolean enabled) {
		//
		textPane.setEnabled(enabled);
	}
	
	public void setFontSize(int size) {
		textPane.setFont(new Font("Courier", Font.PLAIN, size));
	}

}
