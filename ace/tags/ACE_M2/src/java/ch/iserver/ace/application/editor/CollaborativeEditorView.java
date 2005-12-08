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
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import ch.iserver.ace.util.*;
import java.util.HashMap;



public class CollaborativeEditorView extends LabelView {

	public CollaborativeEditorView(Element elem) {
		super(elem);
	}
	
	public void paint(Graphics g, Shape a) {
		super.paint(g, a);
		g.drawString(".", 10, 10);
		CollaborativeTextPane cTextPane = (CollaborativeTextPane)getContainer();
		PropertyChangeHashMap caretHandlerMap = cTextPane.getCaretHandlerMap();
		HashMap participationColorMap = cTextPane.getParticipationCursorColorMap();
		
		if(!cTextPane.isLocalEditing()) {

try{
						String pId = "0";
						
						CaretHandler pCaretHandler = (CaretHandler)caretHandlerMap.get(pId);
						g.setColor((Color)participationColorMap.get(pId));
						
						//Rectangle rect = modelToView(pCaretHandler.getDot());
						Shape shape = modelToView(pCaretHandler.getDot(), a, Position.Bias.Forward);
						System.out.println(a);
						Rectangle rect = shape.getBounds();
						System.out.println(rect);
						
						g.drawLine(rect.x-1, rect.y+rect.height-1, rect.x, rect.y+rect.height-2);
						g.drawLine(rect.x+1, rect.y+rect.height-1, rect.x, rect.y+rect.height-2);
						g.drawLine(rect.x-2, rect.y+rect.height-1, rect.x, rect.y+rect.height-3);
						g.drawLine(rect.x+2, rect.y+rect.height-1, rect.x, rect.y+rect.height-3);
} catch(BadLocationException e) {}



		}

	}


/*public float getTabbedSpan(float x, TabExpander e) {
float result = super.getTabbedSpan(x, e);
this.preferenceChanged(this, true, false);
return result;
}*/

}