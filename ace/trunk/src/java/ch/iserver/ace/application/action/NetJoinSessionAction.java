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

import ch.iserver.ace.application.DocumentItem;
import ch.iserver.ace.application.DocumentManager;
import ch.iserver.ace.application.ItemSelectionChangeEvent;
import ch.iserver.ace.application.ItemSelectionChangeListener;
import ch.iserver.ace.application.LocaleMessageSource;
import ch.iserver.ace.application.BrowseViewController;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.awt.event.*;



public class NetJoinSessionAction extends AbstractAction implements ItemSelectionChangeListener {
	
	private DocumentItem currentBrowseItem;

	public NetJoinSessionAction(LocaleMessageSource messageSource, BrowseViewController browseController) {
		super(messageSource.getMessage("mNetJoin"), messageSource.getIcon("iMenuNetJoin"));
		putValue(SHORT_DESCRIPTION, messageSource.getMessage("mNetJoinTT"));
		browseController.addItemSelectionChangeListener(this);
		browseController.getViewList().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					joinSession();
				}
			}
		});
		setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		joinSession();
	}
	
	private void joinSession() {
		if(currentBrowseItem != null) {
			currentBrowseItem.join();
		}
	}

	public void itemSelectionChanged(ItemSelectionChangeEvent e) {
		if(e.getItem() == null) {
			currentBrowseItem = null;
			setEnabled(false);
		} else {
			DocumentItem item = (DocumentItem)e.getItem();
			currentBrowseItem = item;
			if(item.getType() == DocumentItem.REMOTE) {
				setEnabled(true);
			} else {
				setEnabled(false);
			}
		}
		
	}

}
