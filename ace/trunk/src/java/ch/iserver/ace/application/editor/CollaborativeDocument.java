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

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.*;
import java.awt.*;



public class CollaborativeDocument extends DefaultStyledDocument {

	public CollaborativeDocument() {

	}
	
	protected void styleChanged(Style style) {
		//
		if(!style.getName().equals("default")) {
			System.out.println("Style Changed: reapplying styles to document");
			reapplyStyles(style);
		}
	}



	public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		/*Style pStyle = styledDoc.getStyle("myStyle");
		try {
			styledDoc.insertString(p0, content, pStyle);
		} catch(BadLocationException e) {}*/
		Style pStyle = getStyle("myStyle");
		//StyleConstants.setBackground(pStyle, Color.GREEN);
		//System.out.println("CollaborativeDocument::replace()");
		super.replace(offset, length, text, pStyle);
	}



	public void reapplyStyles(Style style) {
        // Get section element
        Element sectionElem = getDefaultRootElement();
    
        // Get number of paragraphs.
        int paraCount = sectionElem.getElementCount();
    
        for (int i=0; i<paraCount; i++) {
            Element paraElem = sectionElem.getElement(i);
            AttributeSet attr = paraElem.getAttributes();
    
            // Get the name of the style applied to this paragraph element; may be null
            String sn = (String)attr.getAttribute(StyleConstants.NameAttribute);
    
            // Check if style name match
            if (style.getName().equals(sn)) {
                // Reapply the paragraph style
                int rangeStart = paraElem.getStartOffset();
                int rangeEnd = paraElem.getEndOffset();
                setParagraphAttributes(rangeStart, rangeEnd-rangeStart, style, true);
            }
    
            // Enumerate the content elements
            for (int j=0; j<paraElem.getElementCount(); j++) {
                Element contentElem = paraElem.getElement(j);
                attr = contentElem.getAttributes();
    
                // Get the name of the style applied to this content element; may be null
                sn = (String)attr.getAttribute(StyleConstants.NameAttribute);
    
                // Check if style name match
                if (style.getName().equals(sn)) {
                    // Reapply the content style
                    int rangeStart = contentElem.getStartOffset();
                    int rangeEnd = contentElem.getEndOffset();
                    setCharacterAttributes(rangeStart, rangeEnd-rangeStart, style, true);
                }
            }
        }
	}
	
}