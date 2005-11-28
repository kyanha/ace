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

import ch.iserver.ace.collaboration.*;
import ch.iserver.ace.collaboration.util.*;
import ch.iserver.ace.*;
import ch.iserver.ace.text.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;



public class CollaborativeTextPane extends JTextPane {

	private boolean localEditing = true;
	Session session;
	

	public CollaborativeTextPane() {
	}
	
	public void setSession(Session session) {
		this.session = session;
	}
	
	public Session getSession() {
		return session;
	}
	
	public boolean isLocalEditing() {
		return localEditing;
	}

	public void setLocalEditing(boolean localEditing) {
		this.localEditing = localEditing;
	}

	public void replaceSelection(final String content) {
		if(localEditing) {
			super.replaceSelection(content);
		} else {
			final JTextComponent target = this;

			SessionTemplate template = new SessionTemplate(session);
			template.execute(new SessionTemplateCallback() {
				public void execute(Session session) {

					// COPY & PASTE FROM ORIGINAL
					Document doc = target.getDocument();
					Caret caret = target.getCaret();
					if (doc != null) {
						try {
							int p0 = Math.min(caret.getDot(), caret.getMark());
							int p1 = Math.max(caret.getDot(), caret.getMark());
							if (p0 != p1) {
								Operation op = new DeleteOperation(p0, doc.getText(p0, p1 - p0));
								System.out.println(op);
								session.sendOperation(op);
							}
							if (content != null && content.length() > 0) {
								Operation op = new InsertOperation(p0, content);
								System.out.println(op);
								session.sendOperation(op);
							}
						} catch (BadLocationException e) {
						}
					}
					// END COPY & PASTE
					CollaborativeTextPane.super.replaceSelection(content);
				}
			});

		}
	}
	
	public void setSize(Dimension d) {
		if(d.width < getParent().getSize().width) {
			d.width = getParent().getSize().width;
		}
		super.setSize(d);
	}
		
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

}