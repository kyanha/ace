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
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventListModel;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;



public class BrowseView extends ViewImpl {

	private JToolBar browseToolBar;

	public BrowseView(LocaleMessageSource messageSource, BrowseViewController controller) {
		super(controller, messageSource);
		// create view toolbar & actions
		browseToolBar = new JToolBar();

		// create data list & filters
		JTextField browseFilterField = new JTextField();
		TextFilterator browseFilterator = new TextFilterator() {
			public void getFilterStrings(List baseList, Object element) {
				DocumentItem item = (DocumentItem)element;
				baseList.add(item.getPublisher());
			}
		};
		
		Matcher browseViewMatcher = new Matcher() {
			public boolean matches(Object item) {
				DocumentItem dItem = (DocumentItem)item;
				return (dItem.getType() == DocumentItem.REMOTE ||
						dItem.getType() == DocumentItem.AWAITING);
			}
		};
		
		MatcherEditor browseMatcherEditor = new TextComponentMatcherEditor(browseFilterField, browseFilterator);
				
		setSourceList(new FilterList(new ObservableElementList(controller.getSourceList(),
								GlazedLists.beanConnector(DocumentItem.class)),	browseViewMatcher));
		
		SortedList browseSortedList = new SortedList(new FilterList(getSourceList(), browseMatcherEditor));

		// create list & model
		setEventListModel(new EventListModel(browseSortedList));
		setEventSelectionModel(new EventSelectionModel(browseSortedList));

		setList(new JList(getEventListModel()));
		getList().setCellRenderer(new BrowseViewCellRenderer(messageSource));
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
		JPanel browseViewContent = new JPanel(new BorderLayout());
		browseViewContent.add(new JScrollPane(getList()), BorderLayout.CENTER);
		browseViewContent.add(browseFilterField, BorderLayout.SOUTH);
		SimpleInternalFrame browseView = new SimpleInternalFrame(null, messageSource.getMessage("vBrowseTitle"), browseToolBar, browseViewContent);
		setLayout(new BorderLayout());
		add(browseView);		
	}

	public Item getSelectedItem() {
		DocumentItem selectedItem = null;
		try {
			selectedItem = (DocumentItem)getEventListModel().getElementAt(getEventSelectionModel().getMinSelectionIndex());
		} catch(ArrayIndexOutOfBoundsException e) {}
		return selectedItem;
	}

	public void setToolBarActions(List toolBarActions) {
		for(int i = 0; i < toolBarActions.size(); i++) {
			JButton toolBarButton = browseToolBar.add(((AbstractAction)toolBarActions.get(i)));
			toolBarButton.setBorder(BorderFactory.createEmptyBorder());
			browseToolBar.addSeparator();
		}
	}
	
}