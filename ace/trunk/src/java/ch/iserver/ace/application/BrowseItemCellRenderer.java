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



public class BrowseItemCellRenderer extends JPanel implements ListCellRenderer {

	private BrowseItem value;
	private LocaleMessageSource messageSource;
	protected ImageIcon iconLocalPrivate, iconLocalPublished, iconRemote;

	public BrowseItemCellRenderer(LocaleMessageSource messageSource) {
		this.messageSource = messageSource;
//		iconLocalPrivate = messageSource.getIcon("iViewFileLocalPrivate");
//		iconLocalPublished = messageSource.getIcon("iViewFileLocalPublished");
//		iconRemote = messageSource.getIcon("iViewFileRemote");
	}

	public Component getListCellRendererComponent(JList list,
							Object value,
							int index,
							boolean isSelected,
							boolean cellHasFocus) {
		setOpaque(true);
		this.value = (BrowseItem)value;
		
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
		// draw document icon

		// draw document title
		g.drawString("browse: " + value.getTitle() + "(" + value.getPublisher() + ")", 10, 10);
		
		// draw owner		
	}

	public Dimension getPreferredSize() {
		return new Dimension(0, 16);
	}
	
}