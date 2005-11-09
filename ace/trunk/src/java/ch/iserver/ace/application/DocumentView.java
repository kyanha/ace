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

import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventListModel;
import ca.odell.glazedlists.swing.EventSelectionModel;



public class DocumentView extends ViewImpl {

	private EventList documentSourceList;
	private EventListModel documentEventListModel;
	private EventSelectionModel documentEventSelectionModel;
	protected JList documentList;

	public DocumentView(DocumentViewController controller, LocaleMessageSource messageSource) {
		super(controller, messageSource);
		// get view source
		documentSourceList = controller.getDocumentSourceList();
		
		// create view toolbar & actions
		JToolBar viewToolBar = new JToolBar();

		// create list
		SortedList documentSortedList = new SortedList(new ObservableElementList(documentSourceList, GlazedLists.beanConnector(DocumentItem.class)));
		documentEventListModel = new EventListModel(documentSortedList);
		documentEventSelectionModel = new EventSelectionModel(documentSortedList);

		documentList = new JList(documentEventListModel);
		documentList.setCellRenderer(new DocumentItemCellRenderer(messageSource));
		documentList.setSelectionModel(documentEventSelectionModel);
		documentList.setSelectionMode(EventSelectionModel.SINGLE_SELECTION);

		// add mouse listener
		documentList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if(event.getValueIsAdjusting()) {
					return;
				}
				DocumentItem newItem = null;
				try {
					newItem = (DocumentItem)documentEventListModel.getElementAt(documentEventSelectionModel.getMinSelectionIndex());
				} catch(ArrayIndexOutOfBoundsException e) {}
				fireItemSelectionChange(newItem);
			}
		});
		
		// create frame
		JPanel documentViewContent = new JPanel(new BorderLayout());
		documentViewContent.add(new JScrollPane(documentList), BorderLayout.CENTER);
		SimpleInternalFrame documentView = new SimpleInternalFrame(null, messageSource.getMessage("vDocumentTitle"), viewToolBar, documentViewContent);
		setLayout(new BorderLayout());
		add(documentView);		
	}
	
	public Item getSelectedItem() {
		DocumentItem selectedItem = null;
		try {
			selectedItem = (DocumentItem)documentEventListModel.getElementAt(documentEventSelectionModel.getMinSelectionIndex());
		} catch(ArrayIndexOutOfBoundsException e) {}
		return selectedItem;
	}

}