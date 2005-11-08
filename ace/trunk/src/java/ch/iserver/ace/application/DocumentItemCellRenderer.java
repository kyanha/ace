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



public class DocumentItemCellRenderer extends JPanel implements ListCellRenderer {

	private DocumentItem value;
	private LocaleMessageSource messageSource;
	protected ImageIcon iconLocal, iconPublished, iconRemote;

	public DocumentItemCellRenderer(LocaleMessageSource messageSource) {
		this.messageSource = messageSource;
		iconLocal = messageSource.getIcon("iViewFileLocal");
		iconPublished = messageSource.getIcon("iViewFilePublished");
		iconRemote = messageSource.getIcon("iViewFileRemote");
	}

	public Component getListCellRendererComponent(JList list,
							Object value,
							int index,
							boolean isSelected,
							boolean cellHasFocus) {
		setOpaque(true);
		this.value = (DocumentItem)value;
		
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
		switch(value.getType()) {
			case DocumentItem.LOCAL:
				g.drawImage(iconLocal.getImage(), 2, 1, 14, 14, this);
			break;
			case DocumentItem.PUBLISHED:
				g.drawImage(iconPublished.getImage(), 2, 1, 14, 14, this);
			break;			
			case DocumentItem.REMOTE:
				g.drawImage(iconRemote.getImage(), 2, 1, 14, 14, this);
			break;
		}
		
		// draw document title
		g.drawString("document: " + value.getTitle(), 20, 10);
	}

	public Dimension getPreferredSize() {
		return new Dimension(0, 16);
	}
	
}