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
import java.util.List;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventListModel;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;



public class BrowseView extends ViewImpl {

	private EventList browseSourceList;
	private EventListModel browseEventListModel;
	private EventSelectionModel browseEventSelectionModel;
	protected JList browseList;

	public BrowseView(BrowseViewController controller, LocaleMessageSource messageSource) {
		super(controller, messageSource);
		// get view source
		browseSourceList = controller.getBrowseSourceList();
		
		// create view toolbar & actions
		JToolBar viewToolBar = new JToolBar();

		// create data list
		JTextField browseFilterField = new JTextField();
		TextFilterator browseFilterator = new TextFilterator() {
			public void getFilterStrings(List baseList, Object element) {
				BrowseItem item = (BrowseItem)element;
				baseList.add(item.getTitle());
			}
		};
		MatcherEditor browseMatcherEditor = new TextComponentMatcherEditor(browseFilterField, browseFilterator);
		SortedList browseSortedList = new SortedList(new ObservableElementList(new FilterList(browseSourceList, browseMatcherEditor), GlazedLists.beanConnector(BrowseItem.class)));
		browseEventListModel = new EventListModel(browseSortedList);
		browseEventSelectionModel = new EventSelectionModel(browseSortedList);

		browseList = new JList(browseEventListModel);
		browseList.setCellRenderer(new BrowseItemCellRenderer(messageSource));
		browseList.setSelectionModel(browseEventSelectionModel);
		browseList.setSelectionMode(EventSelectionModel.SINGLE_SELECTION);
		
		// create frame
		JPanel browseViewContent = new JPanel(new BorderLayout());
		browseViewContent.add(new JScrollPane(browseList), BorderLayout.CENTER);
		browseViewContent.add(browseFilterField, BorderLayout.SOUTH);
		SimpleInternalFrame browseView = new SimpleInternalFrame(null, messageSource.getMessage("vBrowseTitle"), viewToolBar, browseViewContent);
		setLayout(new BorderLayout());
		add(browseView);		
	}

	public Item getSelectedItem() {
		if(browseEventSelectionModel.getMinSelectionIndex() >= 0) {
			return (BrowseItem)browseEventListModel.getElementAt(browseEventSelectionModel.getMinSelectionIndex());
		}
		return null;
	}
}