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

package ch.iserver.ace.application.action;

import ch.iserver.ace.application.ItemSelectionChangeEvent;
import ch.iserver.ace.application.ItemSelectionChangeListener;
import ch.iserver.ace.application.ViewController;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Icon;



public abstract class ItemSelectionChangeAction extends AbstractAction implements ItemSelectionChangeListener {

	public ItemSelectionChangeAction(String name, List viewControllers) {
		super(name);
		for(int i = 0; i < viewControllers.size(); i++) {
			((ViewController)viewControllers.get(i)).addItemSelectionChangeListener(this);
		}
	}

	public ItemSelectionChangeAction(String name, Icon icon, List viewControllers) {
		super(name, icon);
		for(int i = 0; i < viewControllers.size(); i++) {
			((ViewController)viewControllers.get(i)).addItemSelectionChangeListener(this);
		}
	}
	
	public abstract void itemSelectionChanged(ItemSelectionChangeEvent e);

}

