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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 *
 */
public class CharsetComboBoxModel extends AbstractListModel implements ComboBoxModel {

	private Map charsets;
	
	private List charsetNames;
	
	private Object selectedItem;
	
	public CharsetComboBoxModel() {
		charsets = Charset.availableCharsets();
		charsetNames = new ArrayList();
		charsetNames.addAll(charsets.keySet());
		Charset charset = (Charset) charsets.get("ISO-8859-1");
		selectedItem = charset != null ? charset.name() : null;
	}
	
	public Object getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Object anItem) {
		selectedItem = anItem;
	}

	public int getSize() {
		return charsets.size();
	}

	public Object getElementAt(int index) {
		return charsetNames.get(index);
	}	

}
