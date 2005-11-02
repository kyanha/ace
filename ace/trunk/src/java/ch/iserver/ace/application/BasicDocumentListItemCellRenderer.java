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

import java.awt.*;
import javax.swing.*;



public class BasicDocumentListItemCellRenderer extends JPanel implements ListCellRenderer {

	private BasicDocumentListItem value;
	private LocaleMessageSource source;
	protected ImageIcon iconLocalPrivate, iconLocalPublished, iconRemote;

	public BasicDocumentListItemCellRenderer(LocaleMessageSource source) {
		this.source = source;
		iconLocalPrivate = source.getIcon("iViewFileLocalPrivate");
		iconLocalPublished = source.getIcon("iViewFileLocalPublished");
		iconRemote = source.getIcon("iViewFileRemote");
	}

	public Component getListCellRendererComponent(JList list,
							Object value,
							int index,
							boolean isSelected,
							boolean cellHasFocus) {
		setOpaque(true);
		this.value = (BasicDocumentListItem)value;
		
		if(isSelected) {
			setForeground(list.getSelectionForeground());
			setBackground(list.getSelectionBackground());
			//setBorder(BorderFactory.createLineBorder(Color.black, 1));
		} else {
			setForeground(list.getForeground());
			setBackground(list.getBackground());
			//setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		}
		
		return this;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		/*// draw icon
		g.drawImage(icon.getImage(), 0, 0, 15, 15, this);
		
		// draw string
		g.drawString(value.getDocumentName(), 10, 10);*/
	}

	public Dimension getPreferredSize() {
		return new Dimension(0, 16);
	}
	
}