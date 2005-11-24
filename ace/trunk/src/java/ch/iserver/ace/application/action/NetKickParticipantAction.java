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
import ch.iserver.ace.application.ParticipantItem;
import ch.iserver.ace.application.DocumentManager;
import ch.iserver.ace.application.ItemSelectionChangeEvent;
import ch.iserver.ace.application.LocaleMessageSource;
import ch.iserver.ace.application.DocumentViewController;
import ch.iserver.ace.application.ParticipantViewController;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;



public class NetKickParticipantAction extends DocumentItemSelectionChangeAction {

	private DocumentManager documentManager;
	private DocumentItem currentDocumentItem;
	private ParticipantItem currentParticipantItem;
	
	private DocumentViewController viewController;
	private ParticipantViewController participantController;

	public NetKickParticipantAction(LocaleMessageSource messageSource, DocumentManager documentManager,
			DocumentViewController viewController, ParticipantViewController participantController) {
		super(messageSource.getMessage("mNetKick"), messageSource.getIcon("iMenuNetKick"), viewController);
		putValue(SHORT_DESCRIPTION, messageSource.getMessage("mNetKickTT"));
		participantController.addItemSelectionChangeListener(this);
		this.documentManager = documentManager;
		this.viewController = viewController;
		this.participantController = participantController;
		setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		// need session here
		if(currentDocumentItem != null && currentParticipantItem != null) {
			System.out.println("NetKickParticipantAction: " + currentParticipantItem.getName());
		}
	}

	public void itemSelectionChanged(ItemSelectionChangeEvent e) {
		// HANDLE PARTICIPANT & DOCUMENT ITEM SELECTION CHANGES
		if(e.getSource() == viewController) {
			if(e.getItem() == null) {
				currentDocumentItem = null;
			} else {
				currentDocumentItem = (DocumentItem)e.getItem();
			}
		} else if(e.getSource() == participantController){
			if(e.getItem() == null) {
				currentParticipantItem = null;
			} else {
				currentParticipantItem = (ParticipantItem)e.getItem();
			}
		}
		
		if(currentDocumentItem != null && currentParticipantItem != null) {
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}

}
