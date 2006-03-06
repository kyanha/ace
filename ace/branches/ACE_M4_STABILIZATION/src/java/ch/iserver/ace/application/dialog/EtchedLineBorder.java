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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

class EtchedLineBorder extends AbstractBorder {
	public Insets getBorderInsets(Component c) {
		return new Insets(2, 0, 0, 0);
	}
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		g.setColor(new Color(192, 192, 192));
		g.drawLine(0, 0, width - 1, 0);
		g.setColor(new Color(228, 228, 228));
		g.drawLine(0, 1, width -1, 1);
	}
	public boolean isBorderOpaque() {
		return false;
	}
}