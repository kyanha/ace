/*
 * $Id:DocumentItem.java 1091 2005-11-09 13:29:05Z zbinl $
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import ch.iserver.ace.application.LocaleMessageSource;
import ch.iserver.ace.application.PropertyChangeHashMap;
import ch.iserver.ace.collaboration.Session;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;





public class CollaborativeEditor extends EditorImpl {

	private CollaborativeTextPane cTextPane;
	private CollaborativeEditorKit cEditorKit;
	private JToolBar editorToolBar;
	private SimpleInternalFrame editorPane;
	private LocaleMessageSource messageSource;
	
	public CollaborativeEditor(LocaleMessageSource messageSource) {
		this.messageSource = messageSource;
		// create toolbar
		editorToolBar = new JToolBar();
		editorToolBar.setFloatable(false);
		editorToolBar.setRollover(true);
		
		/*String[] defaultJoinValues = { "Auto", "Blubb", "hubahuba" };
		JComboBox defaultJoinRules = new JComboBox(defaultJoinValues);
		JPanel comboPanel = new JPanel();
		comboPanel.add(defaultJoinRules);
		comboPanel.setBorder(BorderFactory.createEmptyBorder());
		editorToolBar.add(comboPanel);*/

		// create editor
		cTextPane = new CollaborativeTextPane();
		cEditorKit = new CollaborativeEditorKit();
		cTextPane.setEditorKit(cEditorKit);

		// create editor pane
		JScrollPane scrollPane = new JScrollPane(cTextPane);
		//scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		editorPane = new SimpleInternalFrame(null, " ", editorToolBar, scrollPane);
		
		// add components		
		setLayout(new BorderLayout());
		add(editorPane);
	}

	public void setDocument(CollaborativeDocument document) {
		cTextPane.setDocument(document);
	}
	
	public void setCaretHandlerMap(PropertyChangeHashMap caretHandlerMap) {
		cTextPane.setCaretHandlerMap(caretHandlerMap);
	}
	
	public void setParticipationCursorColorMap(HashMap participationCursorColorMap) {
		cTextPane.setParticipationCursorColorMap(participationCursorColorMap);
	}
	public void setSession(Session session) {
		cTextPane.setSession(session);
	}
	
	public void setLocalEditing(boolean localEditing) {
		cTextPane.setLocalEditing(localEditing);
	}

	public void setEnabled(boolean enabled) {
		cTextPane.setEnabled(enabled);
	}

	public void setFontSize(int size) {
		cTextPane.setFont(new Font("Courier", Font.PLAIN, size));
	}

	public void setTitle(String title) {
		editorPane.setTitle(title);
	}
	
	public Action getCutAction() {
		return cEditorKit.getCutAction();
	}
	
	public Action getCopyAction() {
		return cEditorKit.getCopyAction();
	}
	
	public Action getPasteAction() {
		return cEditorKit.getPasteAction();
	}
	
	public Action getSelectAllAction() {
		return cEditorKit.getSelectAllAction();
	}

	public void setToolBarActions(List toolBarActions) {
		for(int i = 0; i < toolBarActions.size(); i++) {
			JButton toolBarButton = editorToolBar.add(((AbstractAction)toolBarActions.get(i)));
			toolBarButton.setBorder(BorderFactory.createEmptyBorder());
			toolBarButton.setBackground(Color.WHITE);
			editorToolBar.addSeparator(new Dimension(3, 0));
		}
	}

}
