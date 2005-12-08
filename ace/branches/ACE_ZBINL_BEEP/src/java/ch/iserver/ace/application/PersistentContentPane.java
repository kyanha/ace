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

import ch.iserver.ace.application.editor.Editor;
import javax.swing.JPanel;
import java.awt.*;
import javax.swing.*;



public class PersistentContentPane extends JPanel {

	private JSplitPane dvbv, pvuv, dvbvce, dvbvcepvuv;
	private boolean fullScreenMode = false;
	private int lastDivider, lastDivider2;

	/*public PersistentContentPane() {
	}
	
	public void setDocumentView(DocumentView documentView) {
	}
	public void setBrowseView(BrowseView browseView) {
	}
	public void setEditor(Editor editor) {
	}
	public void setParticipantView(ParticipantView participantView) {
	}
	public void setUserView(UserView userView) {
	}*/

	public PersistentContentPane(DocumentView documentView, BrowseView browseView,
			Editor editor, ParticipantView participantView, UserView userView) {
		setLayout(new BorderLayout());
		
		//getDividerLocation
		
		// splitPane (DocumentView | BrowseView)
		dvbv = createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, documentView, browseView, 0.5);
		dvbv.setDividerLocation(220);

		// splitPane (ParticipantView | UserView)
		pvuv = createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, participantView,  userView, 0.5);
		pvuv.setDividerLocation(180);

		// splitPane (DocumentView/BrowseView | Editor)
		dvbvce = createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT, dvbv, editor.getEditorComponent(), 0.0);
		
		// splitPane (DocumentView/BrowseView/Editor | ParticipantView/UserView)
		dvbvcepvuv = createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT, dvbvce, pvuv, 1.0);

		// add
		add(dvbvcepvuv);
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 1));
	}
	
	public void switchFullScreenEditing() {
		if(fullScreenMode) {
			// set last divider location
			dvbvce.setDividerLocation(dvbvce.getLastDividerLocation());
			dvbvcepvuv.setDividerLocation(dvbvcepvuv.getLastDividerLocation());
			fullScreenMode = false;
		} else {
			// set full screen mode
			dvbvce.setDividerLocation(0);
			dvbvcepvuv.setDividerLocation(10000);
			fullScreenMode = true;
		}
	}

	private JSplitPane createStrippedSplitPane(int orientation, Component leftComponent,
			Component rightComponent, double value) {
		JSplitPane splitPane = new JSplitPane(orientation, leftComponent, rightComponent);
		splitPane.setBorder(BorderFactory.createEmptyBorder());
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(value);
		return splitPane;
	}

}