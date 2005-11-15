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

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventListModel;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;



public class BrowseView extends ViewImpl {

	public BrowseView(BrowseViewController controller, LocaleMessageSource messageSource) {//, List toolBarActions) {
		super(controller, messageSource);
		// get view source
		setSourceList(controller.getBrowseSourceList());
		
		// create view toolbar & actions
		JToolBar browseToolBar = new JToolBar();
		//for(int i = 0; i < toolBarActions.size(); i++) {
			//browseToolBar.add()toolBarActions.get(i));
		//}

		// create list
		JTextField browseFilterField = new JTextField();
		TextFilterator browseFilterator = new TextFilterator() {
			public void getFilterStrings(List baseList, Object element) {
				BrowseItem item = (BrowseItem)element;
				baseList.add(item.getTitle());
			}
		};
		MatcherEditor browseMatcherEditor = new TextComponentMatcherEditor(browseFilterField, browseFilterator);
		SortedList browseSortedList = new SortedList(new ObservableElementList(new FilterList(getSourceList(), browseMatcherEditor), GlazedLists.beanConnector(BrowseItem.class)));
		setEventListModel(new EventListModel(browseSortedList));
		setEventSelectionModel(new EventSelectionModel(browseSortedList));

		setList(new JList(getEventListModel()));
		getList().setCellRenderer(new BrowseItemCellRenderer(messageSource));
		getList().setSelectionModel(getEventSelectionModel());
		getList().setSelectionMode(EventSelectionModel.SINGLE_SELECTION);
		
		// add mouse listener
		getList().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if(event.getValueIsAdjusting()) {
					return;
				}
				BrowseItem newItem = null;
				try {
					newItem = (BrowseItem)getEventListModel().getElementAt(getEventSelectionModel().getMinSelectionIndex());
				} catch(ArrayIndexOutOfBoundsException e) {}
				fireItemSelectionChange(newItem);
			}
		});

		// create frame
		JPanel browseViewContent = new JPanel(new BorderLayout());
		browseViewContent.add(new JScrollPane(getList()), BorderLayout.CENTER);
		browseViewContent.add(browseFilterField, BorderLayout.SOUTH);
		SimpleInternalFrame browseView = new SimpleInternalFrame(null, messageSource.getMessage("vBrowseTitle"), browseToolBar, browseViewContent);
		setLayout(new BorderLayout());
		add(browseView);		
	}

	public Item getSelectedItem() {
		BrowseItem selectedItem = null;
		try {
			selectedItem = (BrowseItem)getEventListModel().getElementAt(getEventSelectionModel().getMinSelectionIndex());
		} catch(ArrayIndexOutOfBoundsException e) {}
		return selectedItem;
	}
	
}