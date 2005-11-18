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

package ch.iserver.ace.application.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;

public class TitlePane extends JPanel {
	
	private JLabel titleLabel;
	private JLabel messageLabel;

	public TitlePane(String title, Icon icon) {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		setBorder(new TitleBorder());
		
		titleLabel = new JLabel("<html><b>" + title + "</b></html>");
		messageLabel = new JLabel();
		messageLabel.setFont(messageLabel.getFont().deriveFont(messageLabel.getFont().getSize2D() * 0.8f));
		
		JPanel leftPane = new JPanel(new GridLayout(2, 1));
		leftPane.setBackground(Color.WHITE);
		leftPane.add(titleLabel);
		leftPane.add(messageLabel);
		
		JLabel iconLbl = new JLabel(icon);
		add(leftPane, BorderLayout.LINE_START);
		add(iconLbl, BorderLayout.LINE_END);
	}
	
	public void setMessage(String message) {
		messageLabel.setText(message);
	}
	
	private class TitleBorder extends AbstractBorder {
		public Insets getBorderInsets(Component c) {
			return new Insets(0, 15, 1, 1);
		}
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.setColor(new Color(127,127,127));
			g.drawLine(0, height - 1, width - 1, height - 1);
		}
		public boolean isBorderOpaque() {
			return false;
		}
	}
	
}
