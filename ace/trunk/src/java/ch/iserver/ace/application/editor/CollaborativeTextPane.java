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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.algorithm.Operation;
import ch.iserver.ace.algorithm.text.DeleteOperation;
import ch.iserver.ace.algorithm.text.InsertOperation;
import ch.iserver.ace.application.PropertyChangeCaretHandlerImpl;
import ch.iserver.ace.application.PropertyChangeHashMap;
import ch.iserver.ace.application.PropertyChangeHashMapImpl;
import ch.iserver.ace.collaboration.Session;
import ch.iserver.ace.collaboration.util.SessionTemplate;
import ch.iserver.ace.collaboration.util.SessionTemplateCallback;
import ch.iserver.ace.util.CaretHandler;



public class CollaborativeTextPane extends JTextPane implements CaretListener, PropertyChangeListener {

	private boolean localEditing = true;
	Session session;
	PropertyChangeHashMap caretHandlerMap;
	HashMap participationCursorColorMap;



	public CollaborativeTextPane() {
		caretHandlerMap = new PropertyChangeHashMapImpl();
		// ONLY for JAVA 1.5 (AsyncCaret is a JAVA 1.4.2 hack that allows that a caret is updated allways)
		AsyncCaret c = new AsyncCaret();
		c.setBlinkRate(1000);
		setCaret(c);
	}
	
	public void setCaretHandlerMap(PropertyChangeHashMap caretHandlerMap) {
		// unregister old map
		this.caretHandlerMap.removePropertyChangeListener(this);
		this.caretHandlerMap = caretHandlerMap;
		// register new map
		this.caretHandlerMap.addPropertyChangeListener(this);
	}
	
	public PropertyChangeHashMap getCaretHandlerMap() {
		return caretHandlerMap;
	}
	
	public void setParticipationCursorColorMap(HashMap participationCursorColorMap) {
		this.participationCursorColorMap = participationCursorColorMap;
	}
	
	public HashMap getParticipationCursorColorMap() {
		return participationCursorColorMap;
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
		if (!localEditing) {
			// check if caret moved from text manipulation
			PropertyChangeCaretHandlerImpl pCaretHandler = (PropertyChangeCaretHandlerImpl)caretHandlerMap.get("" + session.getParticipantId());

			if(pCaretHandler.getDot() != e.getDot() || pCaretHandler.getMark() != e.getMark()) {
				// set new dot & mark for caret handler
				pCaretHandler.setCaret(e.getDot(), e.getMark());

				// send updates
				Document doc = getDocument();
				if (doc instanceof CollaborativeDocument) {
					((CollaborativeDocument) doc).readLock();
				}
				try {
					final int dot = e.getDot();
					final int mark = e.getMark();
					CaretUpdate cu = new CaretUpdate(dot, mark);
					session.sendCaretUpdate(cu);
				} finally {
					if (doc instanceof CollaborativeDocument) {
						((CollaborativeDocument) doc).readUnlock();
					}
				}
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
								session.sendOperation(op);
							}
							if (content != null && content.length() > 0) {
								Operation op = new InsertOperation(p0, content);
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
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(!localEditing) {

			// delete old caret (only if position is in document)
			if(evt.getOldValue() != null) {
				CaretUpdate oldCU = (CaretUpdate)evt.getOldValue();
				try {
					Rectangle oldRect = modelToView(oldCU.getDot());
					//System.out.println("repaint(" + oldRect + ")");
					oldRect.x -= 2;
					oldRect.width = 5;
					repaint(oldRect);
				} catch(BadLocationException e) { }
				try {
					Rectangle oldRect = modelToView(oldCU.getMark());
					//System.out.println("repaint(" + oldRect + ")");
					oldRect.x -= 2;
					oldRect.width = 5;
					repaint(oldRect);
				} catch(BadLocationException e) { }
			}
			
			// draw new caret
			if(evt.getNewValue() != null) {
				CaretUpdate newCU = (CaretUpdate)evt.getNewValue();
				try {
					Rectangle newRect = modelToView(newCU.getDot());
					//System.out.println("repaint(" + newRect + ")");
					newRect.x -= 2;
					newRect.width = 5;
					repaint(newRect);
				} catch(BadLocationException e) { }
				try {
					Rectangle newRect = modelToView(newCU.getMark());
					//System.out.println("repaint(" + newRect + ")");
					newRect.x -= 2;
					newRect.width = 5;
					repaint(newRect);
				} catch(BadLocationException e) { }
			}
			
		}
	}




	public void paint(Graphics g) {
		super.paint(g);

		if(!localEditing) {
		
			try {

				String mpId = "" + session.getParticipantId();
				Iterator iter = caretHandlerMap.keySet().iterator();
				while(iter.hasNext()) {
					String pId = (String)iter.next(); 
					if(!pId.equals(mpId)) {
						// for all carets except the own one
						CaretHandler pCaretHandler = (CaretHandler)caretHandlerMap.get(pId);

						if(pCaretHandler.getDot() == pCaretHandler.getMark()) {
							Color curColor = ((Color)participationCursorColorMap.get(pId)); 
							g.setColor(curColor);
							Rectangle rect = modelToView(pCaretHandler.getDot());
//							g.drawLine(rect.x-1, rect.y+rect.height-1, rect.x, rect.y+rect.height-2);
//							g.drawLine(rect.x+1, rect.y+rect.height-1, rect.x, rect.y+rect.height-2);
//							g.drawLine(rect.x-2, rect.y+rect.height-1, rect.x, rect.y+rect.height-3);
//							g.drawLine(rect.x+2, rect.y+rect.height-1, rect.x, rect.y+rect.height-3);

							int curSize = 5;
							
							int curOffsetX = rect.x;
							int curOffsetY = rect.y - curSize - 1;
							Polygon curPoly = new Polygon();
							
							curPoly.addPoint(curOffsetX, curOffsetY);
							curPoly.addPoint(curOffsetX - curSize, curOffsetY + curSize);
							curPoly.addPoint(curOffsetX + curSize, curOffsetY + curSize);
							
							g.fillPolygon(curPoly);
							
							g.setColor(curColor.darker());
							g.drawPolygon(curPoly);
						
						} else {
							// draw selection
							int startPos = Math.min(pCaretHandler.getDot(), pCaretHandler.getMark());
							int endPos = Math.max(pCaretHandler.getDot(), pCaretHandler.getMark());

							g.setColor(((Color)participationCursorColorMap.get(pId)));
							Rectangle rectStart = modelToView(startPos);
							g.drawLine(rectStart.x, rectStart.y+rectStart.height-1, rectStart.x, rectStart.y+rectStart.height-3);
							g.drawLine(rectStart.x+1, rectStart.y+rectStart.height-1, rectStart.x, rectStart.y+rectStart.height-2);
							g.drawLine(rectStart.x+2, rectStart.y+rectStart.height-1, rectStart.x, rectStart.y+rectStart.height-3);

							Rectangle rectEnd = modelToView(endPos);
							g.drawLine(rectEnd.x, rectEnd.y+rectEnd.height-1, rectEnd.x, rectEnd.y+rectEnd.height-3);
							g.drawLine(rectEnd.x-1, rectEnd.y+rectEnd.height-1, rectEnd.x, rectEnd.y+rectEnd.height-2);
							g.drawLine(rectEnd.x-2, rectEnd.y+rectEnd.height-1, rectEnd.x, rectEnd.y+rectEnd.height-3);

						}
					}
				}
		
			} catch(BadLocationException e) {
				e.printStackTrace();
			}
		}

	}
}