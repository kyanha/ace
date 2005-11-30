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
import javax.swing.event.*;
import java.awt.*;
import java.util.HashMap;
import ch.iserver.ace.util.*;
import java.util.*;



public class CollaborativeTextPane extends JTextPane implements CaretListener {

	private boolean localEditing = true;
	Session session;
	HashMap caretHandlerMap;
	HashMap paintCaretHandlerMap;
	HashMap participationColorMap;

	public CollaborativeTextPane() {
		caretHandlerMap = new HashMap();
	}
	
	public void setCaretHandlerMap(HashMap caretHandlerMap) {
		this.caretHandlerMap = caretHandlerMap;
		paintCaretHandlerMap = new HashMap();
	}
	
	public HashMap getCaretHandlerMap() {
		return caretHandlerMap;
	}
	
	public void setParticipationColorMap(HashMap participationColorMap) {
		this.participationColorMap = participationColorMap;
	}
	
	public HashMap getParticipationColorMap() {
		return participationColorMap;
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
		if (localEditing) {
			removeCaretListener(this);
		} else {
			addCaretListener(this);
		}
	}
	
	public void caretUpdate(CaretEvent e) {
		if (localEditing) {
		} else {

			// check if caret moved from text manipulation
			CaretHandler pCaretHandler = (CaretHandler)caretHandlerMap.get("0");
			if(pCaretHandler.getDot() != e.getDot() || pCaretHandler.getMark() != e.getMark()) {
				// set new dot & mark for caret handler
				pCaretHandler.setDot(e.getDot());
				pCaretHandler.setMark(e.getMark());

				// send updates
				final int dot = e.getDot();
				final int mark = e.getMark();
				SessionTemplate template = new SessionTemplate(session);
				template.execute(new SessionTemplateCallback() {
					public void execute(Session session) {
						CaretUpdate cu = new CaretUpdate(dot, mark);
//						System.out.println(cu);
						session.sendCaretUpdate(cu);
					}
				});

			}
		}
	}

	public void replaceSelection(final String content) {
		if (localEditing) {
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
//								System.out.println(op);
								session.sendOperation(op);
								try {
									doc.remove(p0, p1 - p0);
								} catch(BadLocationException e) {}

							}
							if (content != null && content.length() > 0) {
								Operation op = new InsertOperation(p0, content);
//								System.out.println(op);
								session.sendOperation(op);

								StyledDocument styledDoc = (StyledDocument)doc;
								Style pStyle = styledDoc.getStyle("myStyle");
								try {
									styledDoc.insertString(p0, content, pStyle);
								} catch(BadLocationException e) {}

							}
						} catch (BadLocationException e) {
						}
					}
					// END COPY & PASTE
//					CollaborativeTextPane.super.replaceSelection(content);

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
	
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

/*
		
		if(!localEditing && caretHandlerMap.size() > 0) {// && participationColorMap.size() > 0) {

			try {


				String mpId = "" + session.getParticipantId();
				Iterator iter = caretHandlerMap.keySet().iterator();
				while(iter.hasNext()) {
					String pId = (String)iter.next(); 
					if(!pId.equals(mpId)) {
						// for all carets except the own one
						
						//System.out.println("mpId: " + mpId + "   pId: " + pId);
						CaretHandler pCaretHandler = (CaretHandler)caretHandlerMap.get(pId);
						
						// 1. get old positions to clear
						//if(paintCaretHandlerMap.containsKey(participantId + "oldDot")) {
						//	int oldDot = ((Integer)paintCaretHandlerMap.get(participantId + "oldDot")).intValue();
						//}
	
						// 2. draw new
						g.setColor((Color)participationColorMap.get(pId));
						Rectangle rect = modelToView(pCaretHandler.getDot());
						g.drawLine(rect.x-1, rect.y+rect.height-1, rect.x, rect.y+rect.height-2);
						g.drawLine(rect.x+1, rect.y+rect.height-1, rect.x, rect.y+rect.height-2);
						g.drawLine(rect.x-2, rect.y+rect.height-1, rect.x, rect.y+rect.height-3);
						g.drawLine(rect.x+2, rect.y+rect.height-1, rect.x, rect.y+rect.height-3);
	
						// 3. save new positions
	
					}
				}



			} catch(BadLocationException ble) {
				ble.printStackTrace();
			}



		}*/
	}
}