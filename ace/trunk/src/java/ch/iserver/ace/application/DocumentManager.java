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

import ch.iserver.ace.application.*;
import ch.iserver.ace.collaboration.*;



public class DocumentManager implements ItemSelectionChangeListener {

	private DocumentViewController controller;
	private CollaborationService collaborationService;
	private DocumentItem currentDocumentItem;

	public DocumentManager(DocumentViewController controller) {
		controller.addItemSelectionChangeListener(this);
		this.controller = controller;
	}
	
	public void closeDocument() {
		controller.removeDocument(controller.getSelectedDocumentItem());
		controller.setSelectedIndex(controller.getDocumentSourceList().size()-1);
	}
	
	public void newDocument() {
		//System.out.println("new");
		DocumentItem newItem = new DocumentItem("New Document");
		controller.addDocument(newItem);
		controller.setSelectedIndex(controller.indexOf(newItem));
	}
	
	public void openDocument() {
		System.out.println("open");
	}
	
	public void exitApplication() {
		System.out.println("exit application");
	}
	
	
	public void publishDocument() {
		currentDocumentItem.publish(collaborationService);
	}
	
	public void concealDocument() {
		currentDocumentItem.conceal();
	}
	
	public void itemSelectionChanged(ItemSelectionChangeEvent e) {
		// set actual document
		//System.out.println("DM: set current item");
		currentDocumentItem = (DocumentItem)e.getItem();
	}



	public void setCollaborationService(CollaborationService collaborationService) {
		this.collaborationService = collaborationService;
	}


}