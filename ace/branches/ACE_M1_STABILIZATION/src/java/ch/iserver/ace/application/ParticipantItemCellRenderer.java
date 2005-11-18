/*
 * $Id:ParticipantItemCellRenderer.java 1091 2005-11-09 13:29:05Z zbinl $
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.BorderFactory;



public class ParticipantItemCellRenderer extends JPanel implements ListCellRenderer {

	private ParticipantItem value;
	private LocaleMessageSource messageSource;
	protected ImageIcon iconParticipant;

	public ParticipantItemCellRenderer(LocaleMessageSource messageSource) {
		this.messageSource = messageSource;
		iconParticipant = messageSource.getIcon("iViewParticipant");
	}

	public Component getListCellRendererComponent(JList list,
							Object value,
							int index,
							boolean isSelected,
							boolean cellHasFocus) {
		setOpaque(true);
		this.value = (ParticipantItem)value;
		
		if(isSelected) {
			setForeground(list.getSelectionForeground());
			setBackground(list.getSelectionBackground());
			setBorder(BorderFactory.createLineBorder(list.getSelectionBackground().darker(), 1));
		} else {
			setForeground(list.getForeground());
			setBackground(list.getBackground());
			setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		}

		return this;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		int itemHeight = getHeight();
		int itemWidth = getWidth();

		// draw participant icon
		int imageHeight = 16; //iconLocal.getIconHeight();
		int imageWidth = 16; //iconLocal.getIconWidth();
		int imagePosX = 1;
		int imagePosY = (itemHeight / 2) - (imageHeight / 2);
		g.drawImage(iconParticipant.getImage(), imagePosX, imagePosY, imageHeight, imageWidth, this);
		
		// draw participant name (TODO: dynamic border)
		g.setColor(Color.BLACK);
		int textAscent = g.getFontMetrics().getAscent();
		int textDescent = g.getFontMetrics().getDescent();		
		int textPosX = imagePosX + imageWidth + 5;
		int textPosY = (itemHeight / 2) + (textAscent / 2) - textDescent + 1;
		g.drawString(value.getName(), textPosX, textPosY);
		
		// draw participant color
		int colorWidth = 20;
		int colorHeight = 10;
		int colorPosX = itemWidth - colorWidth - 5;
		int colorPosY = (itemHeight / 2) - (colorHeight / 2);
		g.setColor(value.getColor());
		g.fillRect(colorPosX, colorPosY, colorWidth, colorHeight);
		g.setColor(value.getColor().darker());
		g.drawRect(colorPosX, colorPosY, colorWidth, colorHeight);
		
	}
	
	public String getToolTipText() {
		return value.getName();
	}

	public Dimension getPreferredSize() {
		return new Dimension(0, 20);
	}
	
}