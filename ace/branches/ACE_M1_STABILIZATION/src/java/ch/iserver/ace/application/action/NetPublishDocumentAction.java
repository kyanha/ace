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

import ch.iserver.ace.application.DocumentManager;
import ch.iserver.ace.application.DocumentItem;
import ch.iserver.ace.application.ItemSelectionChangeEvent;
import ch.iserver.ace.application.LocaleMessageSource;
import ch.iserver.ace.application.ViewController;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.util.List;



public class NetPublishDocumentAction extends ItemSelectionChangeAction {

	private DocumentManager documentManager;

	public NetPublishDocumentAction(LocaleMessageSource messageSource, DocumentManager documentManager, List viewControllers) {
		super(messageSource.getMessage("mNetPublish"), messageSource.getIcon("iMenuNetPublish"), viewControllers);
		this.documentManager = documentManager;
		setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		//System.out.println("NetPublishDocumentAction");
		documentManager.publishDocument();
	}

	public void itemSelectionChanged(ItemSelectionChangeEvent e) {
		if((e.getItem() != null) && ((DocumentItem)e.getItem()).getType() == DocumentItem.LOCAL) {
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}

}

