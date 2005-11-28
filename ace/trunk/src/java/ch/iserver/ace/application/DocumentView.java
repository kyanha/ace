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
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.swing.EventListModel;
import ca.odell.glazedlists.swing.EventSelectionModel;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;



public class DocumentView extends ViewImpl {

	private JToolBar documentToolBar;
	private SortedList documentSortedList;
	
	public DocumentView(LocaleMessageSource messageSource, DocumentViewController controller) {
		super(controller, messageSource);
				
		// create view toolbar & actions
		documentToolBar = new JToolBar();
		documentToolBar.setFloatable(false);
		documentToolBar.setRollover(true);

		// create data list & filters
		Matcher documentViewMatcher = new Matcher() {
			public boolean matches(Object item) {
				DocumentItem dItem = (DocumentItem)item;
				return (dItem.getType() == DocumentItem.LOCAL ||
						dItem.getType() == DocumentItem.PUBLISHED ||
						dItem.getType() == DocumentItem.JOINED);
			}
		};
		
		setSourceList(new FilterList(new ObservableElementList(controller.getSourceList(),
								GlazedLists.beanConnector(DocumentItem.class)), documentViewMatcher));
		documentSortedList = new SortedList(getSourceList());

		// create list & model			
		setEventListModel(new EventListModel(documentSortedList));
		setEventSelectionModel(new EventSelectionModel(documentSortedList));

		setList(new JList(getEventListModel()));
		getList().setCellRenderer(new DocumentViewCellRenderer(messageSource));
		getList().setSelectionModel(getEventSelectionModel());
		getList().setSelectionMode(EventSelectionModel.SINGLE_SELECTION);

		// add mouse listener
		getList().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if(event.getValueIsAdjusting()) {
					return;
				}
				DocumentItem newItem = null;
				try {
					newItem = (DocumentItem)getEventListModel().getElementAt(getEventSelectionModel().getMinSelectionIndex());
				} catch(ArrayIndexOutOfBoundsException e) {}
				fireItemSelectionChange(newItem);
			}
		});
		
		// create frame
		JPanel documentViewContent = new JPanel(new BorderLayout());
		documentViewContent.add(new JScrollPane(getList()), BorderLayout.CENTER);
		SimpleInternalFrame documentView = new SimpleInternalFrame(null, messageSource.getMessage("vDocumentTitle"), documentToolBar, documentViewContent);
		setLayout(new BorderLayout());
		add(documentView);		
	}
	
	public Item getSelectedItem() {
		DocumentItem selectedItem = null;
		try {
			selectedItem = (DocumentItem)getEventListModel().getElementAt(getEventSelectionModel().getMinSelectionIndex());
		} catch(ArrayIndexOutOfBoundsException e) {}
		return selectedItem;
	}
	
	public void setSelectedItem(Item item) {
		getSourceList().getReadWriteLock().readLock().lock();
		try {
			int pos = documentSortedList.indexOf(item);
			getEventSelectionModel().setSelectionInterval(pos, pos);
			getList().ensureIndexIsVisible(pos);
		} finally {
			getSourceList().getReadWriteLock().readLock().unlock();
		}
	}

	public void setToolBarActions(List toolBarActions) {
		for(int i = 0; i < toolBarActions.size(); i++) {
			JButton toolBarButton = documentToolBar.add(((AbstractAction)toolBarActions.get(i)));
			toolBarButton.setBorder(BorderFactory.createEmptyBorder());
			documentToolBar.addSeparator();
		}
	}
}