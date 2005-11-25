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

package ch.iserver.ace.application;

import ch.iserver.ace.application.editor.*;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import java.awt.*;
import javax.swing.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;



public class ApplicationFactoryImpl implements ApplicationFactory, ApplicationContextAware {

	private ApplicationContext context;
	private LocaleMessageSource messageSource;

	public JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		// add components		
		JMenu mFile = new JMenu(messageSource.getMessage("mFile"));
		mFile.add((AbstractAction)context.getBean("fileNewAction")).setToolTipText(null);
		mFile.add((AbstractAction)context.getBean("fileOpenAction")).setToolTipText(null);
		mFile.addSeparator();
		mFile.add((AbstractAction)context.getBean("fileSaveAction")).setToolTipText(null);
		mFile.add((AbstractAction)context.getBean("fileSaveAsAction")).setToolTipText(null);
		mFile.add((AbstractAction)context.getBean("fileSaveCopyAction")).setToolTipText(null);
		mFile.add((AbstractAction)context.getBean("fileSaveAllAction")).setToolTipText(null);
		mFile.add((AbstractAction)context.getBean("fileCloseAction")).setToolTipText(null);
		//mFile.add((AbstractAction)context.getBean("fileCloseAllAction")).setToolTipText(null);
		mFile.addSeparator();
		mFile.add((AbstractAction)context.getBean("appSettingsAction")).setToolTipText(null);
		mFile.addSeparator();
		mFile.add((AbstractAction)context.getBean("appExitAction")).setToolTipText(null);
		menuBar.add(mFile);

		JMenu mEdit = new JMenu(messageSource.getMessage("mEdit"));
		CollaborativeEditor cEditor = (CollaborativeEditor)context.getBean("collabEditor");
		Action cutAction = cEditor.getCutAction();
		cutAction.putValue(Action.NAME, messageSource.getMessage("mEditCut"));
		cutAction.putValue(Action.SMALL_ICON, messageSource.getIcon("iMenuEditCut"));
		cutAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mEdit.add(cutAction).setToolTipText(null);
		
		Action copyAction = cEditor.getCopyAction();
		copyAction.putValue(Action.NAME, messageSource.getMessage("mEditCopy"));
		copyAction.putValue(Action.SMALL_ICON, messageSource.getIcon("iMenuEditCopy"));
		copyAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mEdit.add(copyAction).setToolTipText(null);
		
		Action pasteAction = cEditor.getPasteAction();
		pasteAction.putValue(Action.NAME, messageSource.getMessage("mEditPaste"));
		pasteAction.putValue(Action.SMALL_ICON, messageSource.getIcon("iMenuEditPaste"));
		pasteAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('V', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mEdit.add(pasteAction).setToolTipText(null);
		
		Action selectAllAction = cEditor.getSelectAllAction();
		selectAllAction.putValue(Action.NAME, messageSource.getMessage("mEditSelectAll"));
		selectAllAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mEdit.add(selectAllAction).setToolTipText(null);
		menuBar.add(mEdit);

		JMenu mNet = new JMenu(messageSource.getMessage("mNet"));
		mNet.add((AbstractAction)context.getBean("netPublishDocumentAction")).setToolTipText(null);
		mNet.add((AbstractAction)context.getBean("netConcealDocumentAction")).setToolTipText(null);
		mNet.addSeparator();
		mNet.add((AbstractAction)context.getBean("netJoinSessionAction")).setToolTipText(null);
		mNet.add((AbstractAction)context.getBean("netLeaveSessionAction")).setToolTipText(null);
		mNet.addSeparator();
		mNet.add((AbstractAction)context.getBean("netDiscoverUserAction")).setToolTipText(null);
		mNet.add((AbstractAction)context.getBean("netInviteUserAction")).setToolTipText(null);
		mNet.add((AbstractAction)context.getBean("netKickParticipantAction")).setToolTipText(null);
		menuBar.add(mNet);

		JMenu mHelp = new JMenu(messageSource.getMessage("mHelp"));
		mHelp.add((AbstractAction)context.getBean("helpDebugAction")).setToolTipText(null);
		mHelp.add((AbstractAction)context.getBean("helpAboutAction")).setToolTipText(null);
		menuBar.add(mHelp);

		return menuBar;
	}
		
	public JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		// add components
		
		toolBar.add((AbstractAction)context.getBean("fileNewAction"));
		toolBar.add((AbstractAction)context.getBean("fileOpenAction"));
		toolBar.addSeparator(new Dimension(8, 0));
		toolBar.add((AbstractAction)context.getBean("fileSaveAction"));
		toolBar.add((AbstractAction)context.getBean("fileCloseAction"));
		toolBar.addSeparator(new Dimension(8, 0));
		toolBar.add((AbstractAction)context.getBean("netJoinSessionAction"));
		toolBar.add((AbstractAction)context.getBean("netLeaveSessionAction"));
		toolBar.addSeparator(new Dimension(8, 0));
		toolBar.add((AbstractAction)context.getBean("netInviteUserAction"));
		toolBar.add((AbstractAction)context.getBean("netKickParticipantAction"));
		//toolBar.addSeparator(new Dimension(8, 0));
		//toolBar.add((AbstractAction)context.getBean("toggleFullScreenEditingAction"));

		return toolBar;
	}

	public JPanel createComponentPane() {
		JPanel componentPane = new JPanel();
		componentPane.setLayout(new BorderLayout());
		// add components
		DocumentView documentView = (DocumentView)context.getBean("documentView");
		BrowseView browseView = (BrowseView)context.getBean("browseView");
		ParticipantView participantView = (ParticipantView)context.getBean("participantView");
		UserView userView = (UserView)context.getBean("userView");

		// create editor
		//EditorFactory editorFactory = (EditorFactory)context.getBean("editorFactory");
		//JPanel editorPane = editorFactory.createEditor();
		//EditorFactory editorFactory = (EditorFactory)context.getBean("editorFactory");
		JPanel editorPane = (JPanel)context.getBean("dummyEditor");

		JSplitPane dvSbv = createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, documentView, browseView, 0.0);
		JSplitPane pvSuv = createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, participantView,  userView, 0.0);
		JSplitPane dvbvSce = createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT, dvSbv, editorPane, 0.0);
		JSplitPane dvbvceSpvuv = createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT, dvbvSce, pvSuv, 1.0);
		componentPane.add(dvbvceSpvuv);

		return componentPane;
	}
	
	public JPanel createPersistentContentPane() {
		//return (PersistentContentPane)context.getBean("persistentContentPane");

		DocumentView documentView = (DocumentView)context.getBean("documentView");
		BrowseView browseView = (BrowseView)context.getBean("browseView");
		Editor editor = (Editor)context.getBean("dummyEditor");
		ParticipantView participantView = (ParticipantView)context.getBean("participantView");
		UserView userView = (UserView)context.getBean("userView");

		return new PersistentContentPane(documentView, browseView, editor, participantView, userView);
	}
	
	public JPanel createStatusBar() {
		JPanel statusBar = new JPanel();
		statusBar.setPreferredSize(new Dimension(0, 16));
		statusBar.setBorder(BorderFactory.createEmptyBorder());
		// add components
		
		return statusBar;
	}
	
	public void setMessageSource(LocaleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public void setApplicationContext(ApplicationContext context) {
		this.context = context;
	}
	
	private static JSplitPane createStrippedSplitPane(int orientation, Component leftComponent, Component rightComponent, double value) {
		JSplitPane splitPane = new JSplitPane(orientation, leftComponent, rightComponent);
		splitPane.setBorder(BorderFactory.createEmptyBorder());
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(value);
		return splitPane;
	}
	
}