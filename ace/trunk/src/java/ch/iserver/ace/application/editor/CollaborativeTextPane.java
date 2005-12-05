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

import ch.iserver.ace.application.*;
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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;



public class CollaborativeTextPane extends JTextPane implements CaretListener, PropertyChangeListener {

	private boolean localEditing = true;
	Session session;
	PropertyChangeHashMap caretHandlerMap;
	HashMap participationCursorColorMap;

	public CollaborativeTextPane() {
		caretHandlerMap = new PropertyChangeHashMapImpl();
		// ONLY for JAVA 1.5
		// DefaultCaret c = new DefaultCaret();
		// c.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		// setCaret(c);
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
		if (localEditing) {
		} else {
			// check if caret moved from text manipulation
			PropertyChangeCaretHandlerImpl pCaretHandler = (PropertyChangeCaretHandlerImpl)caretHandlerMap.get("" + session.getParticipantId());

//			System.out.println("caretUpdate(CaretEvent e): pCH.getDot(): " + pCaretHandler.getDot() + "     pCH.getMark(): " +
//									pCaretHandler.getMark() + "      e.getDot(): " + e.getDot() + "      e.getMark(): " + e.getMark());

			if(pCaretHandler.getDot() != e.getDot() || pCaretHandler.getMark() != e.getMark()) {
				// set new dot & mark for caret handler
				pCaretHandler.setCaret(e.getDot(), e.getMark());

				// send updates
				final int dot = e.getDot();
				final int mark = e.getMark();
				SessionTemplate template = new SessionTemplate(session);
				template.execute(new SessionTemplateCallback() {
					public void execute(Session session) {
						CaretUpdate cu = new CaretUpdate(dot, mark);
						//System.out.println(cu);
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
/*								try {
									doc.remove(p0, p1 - p0);
								} catch(BadLocationException e) {}*/

							}
							if (content != null && content.length() > 0) {
								Operation op = new InsertOperation(p0, content);
//								System.out.println(op);
								session.sendOperation(op);

/*								StyledDocument styledDoc = (StyledDocument)doc;
								Style pStyle = styledDoc.getStyle("myStyle");
								try {
									styledDoc.insertString(p0, content, pStyle);
								} catch(BadLocationException e) {}*/

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

			// set own caret
			String mpId = "" + session.getParticipantId();
			PropertyChangeCaretHandlerImpl pCaretHandler = (PropertyChangeCaretHandlerImpl)caretHandlerMap.get(mpId);
			/*System.out.println("propertyChange(): name=" + evt.getPropertyName() + "   getDot=" + pCaretHandler.getDot() + "   getMark=" + pCaretHandler.getMark());
			CaretUpdate oldCUT = (CaretUpdate)evt.getOldValue();
			System.out.println("propertyChange()::oldCUT: dot=" + oldCUT.getDot() + "   mark=" + oldCUT.getMark());
			CaretUpdate newCUT = (CaretUpdate)evt.getNewValue();
			System.out.println("propertyChange()::newCUT: dot=" + newCUT.getDot() + "   mark=" + newCUT.getMark());*/

			//TODO: find bug ;)
			//setCaretPosition(pCaretHandler.getDot());

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
							g.setColor(((Color)participationCursorColorMap.get(pId)));
							Rectangle rect = modelToView(pCaretHandler.getDot());
							g.drawLine(rect.x-1, rect.y+rect.height-1, rect.x, rect.y+rect.height-2);
							g.drawLine(rect.x+1, rect.y+rect.height-1, rect.x, rect.y+rect.height-2);
							g.drawLine(rect.x-2, rect.y+rect.height-1, rect.x, rect.y+rect.height-3);
							g.drawLine(rect.x+2, rect.y+rect.height-1, rect.x, rect.y+rect.height-3);
						} else {
							// draw selection
							
							int startPos = Math.min(pCaretHandler.getDot(), pCaretHandler.getMark());
							int endPos = Math.max(pCaretHandler.getDot(), pCaretHandler.getMark());

							Rectangle rectStart = modelToView(startPos);
							Rectangle rectEnd = modelToView(endPos);

							

/*							g.setColor(((Color)participationCursorColorMap.get(pId)));

							if(1==1) {//if(rectDot.y == rectMark.y) {
								// 1. single line


								// paint beginning border part
								Rectangle iRect = modelToView(startPos);
								Rectangle iNext = modelToView(startPos+1);
								if(iRect.y == iNext.y) {
									// on the same line
									g.drawLine(iRect.x, iRect.y, iRect.x, iRect.y + iRect.height);
									g.drawLine(iRect.x, iRect.y, iNext.x, iRect.y);
									g.drawLine(iRect.x, iRect.y + iRect.height, iNext.x, iRect.y + iRect.height);
								}

								// paint middle element
								for(int i = startPos+1; i < endPos-1 ; i++) {
									iRect = modelToView(i);
									iNext = modelToView(i+1);
									if(iRect.y == iNext.y) {
										// on the same line
										g.drawLine(iRect.x, iRect.y, iNext.x, iRect.y);
										g.drawLine(iRect.x, iRect.y + iRect.height, iNext.x, iRect.y + iRect.height);
									}
								}

								// paint end element
								iRect = modelToView(endPos);
								iNext = modelToView(endPos-1);
								if(iRect.y == iNext.y) {
									// on the same line
									//System.out.println(iRect + " - " + iNext);
									//System.out.println("" + iRect.x + "," + iRect.y + "," +iRect.x+"," +iRect.height);
									g.drawLine(iRect.x + iRect.width, iRect.y, iRect.x + iRect.width, iRect.y + iRect.height);
									g.drawLine(iRect.x, iRect.y, iNext.x, iRect.y);
									g.drawLine(iRect.x, iRect.y + iRect.height, iNext.x, iRect.y + iRect.height);
								}


							
							}

*/
						}

					}
				}
		
			} catch(BadLocationException e) {
				e.printStackTrace();
			}
		}

	}
}