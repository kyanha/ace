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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import java.util.List;
import com.jgoodies.uif_lite.panel.SimpleInternalFrame;



public class DummyEditor extends JPanel implements Editor {

	private JTextPane textPane;
	private SimpleInternalFrame editorFrame;
	private LocaleMessageSource messageSource;
	private PersistentContentPane persistentContentPane;

	public DummyEditor(LocaleMessageSource messageSource, List toolBarActions) {
		this.messageSource = messageSource;
		// create toolbar
		JToolBar editorToolBar = new JToolBar();
		for(int i = 0; i < toolBarActions.size(); i++) {
			JButton toolBarButton = editorToolBar.add(((AbstractAction)toolBarActions.get(i)));
			toolBarButton.setBorder(BorderFactory.createEmptyBorder());
		}
		// create editor
		//JPanel innerEditorPane = new JPanel();
		textPane = new DummyTextPane();
		setFontSize(12);
		
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		// add components
		editorFrame = new SimpleInternalFrame(null, " ", editorToolBar, scrollPane);
		editorFrame.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					persistentContentPane.switchFullScreenEditing();
				}
			}
		});
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
	
	public JPanel getEditorComponent() {
		return this;
	}
	
	public void setPersistentContentPane(PersistentContentPane persistentContentPane) {
		this.persistentContentPane = persistentContentPane;
	}
	
	public class DummyTextPane extends JTextPane {
		public DummyTextPane() {
			//super();
		}

		/*public Dimension getPreferredScrollableViewportSize() {
			return new Dimension(1000, 1000);//getSize();
		}

		public boolean getScrollableTracksViewportHeight() {
        	return false;
    	}*/
		
		/*public boolean getScrollableTracksViewportWidth() {
        	return true;
    	}*/
	}

}
